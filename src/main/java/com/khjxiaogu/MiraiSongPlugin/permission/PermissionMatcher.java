package com.khjxiaogu.MiraiSongPlugin.permission;

import java.util.List;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;

public interface PermissionMatcher {
	PermissionResult match(long id,long group, Bot bot);
	default PermissionResult match(Member m) {
		return match(m.getId(),m.getGroup().getId(),m.getBot());
	}
	/**
	 * @param temp  
	 */
	default PermissionResult match(User u,boolean temp) {
		return match(u.getId(),0,u.getBot());
	}
	List<String> getValue();
}
