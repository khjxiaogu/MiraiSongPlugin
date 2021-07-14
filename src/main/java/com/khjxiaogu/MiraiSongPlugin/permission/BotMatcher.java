package com.khjxiaogu.MiraiSongPlugin.permission;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.khjxiaogu.MiraiSongPlugin.MiraiSongPlugin;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.User;

public class BotMatcher implements PermissionMatcher {
	PermissionResult wildcard = PermissionResult.UNSPECIFIED;
	LinkedHashMap<String, PermissionMatcher> restricted = new LinkedHashMap<>(5);
	Map<Long, GroupMatcher> groupmatchers = new ConcurrentHashMap<>(10);
	Map<Long, PermissionResult> friendpermissions = new ConcurrentHashMap<>(10);
	Map<WildcardPermission, GroupMatcher> permmatcher = new ConcurrentHashMap<>();

	@FunctionalInterface
	interface PermissionFactory {
		PermissionMatcher create(PermissionResult is);

		default PermissionMatcher create(boolean is) {
			return this.create(PermissionResult.valueOf(is));
		};
	}

	@Override
	public PermissionResult match(long id, long group, Bot bot) {
		PermissionResult pr = wildcard;
		for (PermissionMatcher pm : restricted.values()) {
			pr=pr.and(pm.match(id, group, bot));
		}
		if (group != 0) {
			MemberPermission mp = bot.getGroup(group).getBotPermission();
			for (Entry<WildcardPermission, GroupMatcher> me : permmatcher.entrySet()) {
				if (me.getKey().isMatch(mp)) {
					pr=pr.and(me.getValue().match(id, group, bot));
				}
			}
			PermissionMatcher pm = groupmatchers.get(group);
			if (pm != null) {
				pr=pr.and(pm.match(id, group, bot));
			}
		} else {
			pr=pr.and(friendpermissions.getOrDefault(id, PermissionResult.UNSPECIFIED));
		}
		return pr;
	}

	@Override
	public PermissionResult match(Member m) {
		PermissionResult pr = wildcard;
		for (PermissionMatcher pm : restricted.values()) {
			pr=pr.and(pm.match(m));
		}
		MemberPermission mp = m.getGroup().getBotPermission();
		for (Entry<WildcardPermission, GroupMatcher> me : permmatcher.entrySet()) {
			if (me.getKey().isMatch(mp)) {
				pr=pr.and(me.getValue().match(m));
			}
		}
		//MiraiSongPlugin.getMLogger().info("bgm"+pr.name());
		PermissionMatcher pm = groupmatchers.get(m.getGroup().getId());
		if (pm != null) {
			pr=pr.and(pm.match(m));
			//MiraiSongPlugin.getMLogger().info("gm");
		}
		//MiraiSongPlugin.getMLogger().info("agm"+pr.name());
		return pr;
	}

	@Override
	public PermissionResult match(User m, boolean temp) {
		PermissionResult pr = wildcard;
		for (PermissionMatcher pm : restricted.values()) {
			pr=pr.and(pm.match(m, temp));
		}
		pr=pr.and(friendpermissions.getOrDefault(m.getId(), PermissionResult.UNSPECIFIED));
		return pr;
	}

	void loadMatcher(String param) {
		if (param.length() == 0)
			return;
		param = param.split("#")[0].trim();
		if (param.length() == 0)
			return;
		String[] args = param.split("@");
		if (args.length == 1) {
			loadWildCard(args[0]);
		} else if (args.length == 2) {
			if (args[1].equals("*")) {
				loadWildCard(args[0]);
				return;
			}
			if (Character.isDigit(args[1].charAt(0))) {
				long group = Long.parseLong(args[1]);
				GroupMatcher gm = groupmatchers.get(group);
				if (gm == null) {
					gm = new GroupMatcher();
					groupmatchers.put(group, gm);
				}
				gm.load(args[0]);
			} else {
				WildcardPermission wp = WildcardPermission.valueOf(args[1]);
				GroupMatcher gm = permmatcher.get(wp);
				if (gm == null) {
					gm = new GroupMatcher();
					permmatcher.put(wp, gm);
				}
				gm.load(args[0]);
			}
		}
	}

	private void loadWildCard(String param) {
		if (param.length() == 0)
			return;
		char isr = param.charAt(0);
		if (Character.isDigit(isr)) {
			friendpermissions.put(Long.parseLong(param), PermissionResult.DISALLOW);
		} else {
			boolean result = false;
			String s;
			switch (isr) {
			case '+':
				result = true;
				s = param.substring(1);
				break;
			case '-':
				s = param.substring(1);
				break;
			default:
				s = param;
				break;
			}
			if (Character.isDigit(s.charAt(0))) {
				friendpermissions.put(Long.parseLong(s), PermissionResult.valueOf(result));
			} else if (s.charAt(0) == '*') {
				wildcard = PermissionResult.valueOf(result);
			} else {
				PermissionFactory pf = Matchers.get(s);
				if (pf != null)
					restricted.put(s, pf.create(result));
			}
		}
	}

	@Override
	public List<String> getValue() {
		List<String> pl = new ArrayList<>();
		if (wildcard != PermissionResult.UNSPECIFIED)
			pl.add(wildcard.getSymbol() + "*");
		for (PermissionMatcher sp : restricted.values())
			pl.addAll(sp.getValue());
		for (Entry<Long, PermissionResult> i : friendpermissions.entrySet()) {
			pl.add(i.getValue().getSymbol() + i.getKey().toString());
		}
		for (Entry<WildcardPermission, GroupMatcher> i : permmatcher.entrySet()) {
			String gn = "@" + i.getKey().name();
			for (String s : i.getValue().getValue()) {
				pl.add(s + gn);
			}
		}
		for (Entry<Long, GroupMatcher> i : groupmatchers.entrySet()) {
			String gn = "@" + i.getKey();
			for (String s : i.getValue().getValue()) {
				pl.add(s + gn);
			}
		}
		return pl;
	}
}
