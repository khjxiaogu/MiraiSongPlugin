package com.khjxiaogu.MiraiSongPlugin.permission;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.khjxiaogu.MiraiSongPlugin.MiraiSongPlugin;
import com.khjxiaogu.MiraiSongPlugin.permission.BotMatcher.PermissionFactory;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;

public class GroupMatcher implements PermissionMatcher {
	PermissionResult wildcard=PermissionResult.UNSPECIFIED;
	LinkedHashMap<String,PermissionMatcher> restricted=new LinkedHashMap<>(5);
	Map<Long,PermissionResult> memberpermissions=new ConcurrentHashMap<>(10);
	
	@Override
	public PermissionResult match(Member m) {
		PermissionResult pr=wildcard;
		
		for(PermissionMatcher sp:restricted.values()) {
			pr=pr.and(sp.match(m));
		}
		//MiraiSongPlugin.getMLogger().info("brm"+pr.name());
		pr=pr.and(memberpermissions.getOrDefault(m.getId(),PermissionResult.UNSPECIFIED));
		//MiraiSongPlugin.getMLogger().info("arm"+pr.name());
		return pr;
	}
	@Override
	public PermissionResult match(User u, boolean temp) {
		return PermissionMatcher.super.match(u, temp);
	}
	@Override
	public PermissionResult match(long id, long group, Bot bot) {
		PermissionResult pr=wildcard;
		for(PermissionMatcher sp:restricted.values()) {
			pr=pr.and(sp.match(id, group,bot));
		}
		pr=pr.and(memberpermissions.getOrDefault(id,PermissionResult.UNSPECIFIED));
		return pr;
	}
	public List<String> getValue(){
		List<String> pl=new ArrayList<>();
		if(wildcard!=PermissionResult.UNSPECIFIED)
			pl.add(wildcard.getSymbol()+"*");
		for(PermissionMatcher sp:restricted.values())
			pl.addAll(sp.getValue());
		for(Entry<Long, PermissionResult> i:memberpermissions.entrySet()) {
			pl.add(i.getValue().getSymbol()+i.getKey().toString());
		}
		return pl;
	}
	void load(String param) {
		if(param.length()==0)return;
		char isr=param.charAt(0);
		if(Character.isDigit(isr)) {
			memberpermissions.put(Long.parseLong(param),PermissionResult.DISALLOW);
		}else {
			boolean result=false;
			String s;
			switch(isr) {
			case '#':return;
			case '+':result=true;s=param.substring(1);break;
			case '-':s=param.substring(1);break;
			default:s=param;break;
			}
			if(Character.isDigit(s.charAt(0))) {
				memberpermissions.put(Long.parseLong(s),PermissionResult.valueOf(result));
			}else if(s.charAt(0)=='*') {
				wildcard=PermissionResult.valueOf(result);
			}else {
				PermissionFactory pf=Matchers.get(s);
				if(pf!=null) {
					restricted.put(s,pf.create(result));
				}
			}
		}
	}
}
