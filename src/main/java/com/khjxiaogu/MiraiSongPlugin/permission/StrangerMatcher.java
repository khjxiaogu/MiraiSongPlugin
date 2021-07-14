package com.khjxiaogu.MiraiSongPlugin.permission;

import java.util.Arrays;
import java.util.List;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;

public class StrangerMatcher implements PermissionMatcher {
	PermissionResult result;
	@Override
	public PermissionResult match(long id, long group, Bot bot) {
		if(bot.getFriend(id)==null) {
			return result;
		}
		return PermissionResult.UNSPECIFIED;
	}

	@Override
	public PermissionResult match(Member m) {
		if(m.getBot().getFriend(m.getId())==null) {
			return result;
		}
		return PermissionResult.UNSPECIFIED;
	}

	@Override
	public PermissionResult match(User u, boolean temp) {
		if(temp) {
			return result;
		}
		return PermissionResult.UNSPECIFIED;
	}
	public StrangerMatcher(PermissionResult result) {
		this.result = result;
	}
	public StrangerMatcher(boolean result) {
		this(PermissionResult.valueOf(result));
	}

	@Override
	public List<String> getValue() {
		return Arrays.asList(result.getSymbol()+"stranger");
	}

}
