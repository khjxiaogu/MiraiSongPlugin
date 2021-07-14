package com.khjxiaogu.MiraiSongPlugin.permission;

import java.util.HashMap;
import java.util.Map;

import com.khjxiaogu.MiraiSongPlugin.permission.BotMatcher.PermissionFactory;

public class Matchers {
	static Map<String,PermissionFactory> subfactories=new HashMap<>();
	static {
		subfactories.put("admins",is->new MemberPermissionMatcher(WildcardPermission.admins,is));
		subfactories.put("friend",is->new FriendMatcher(is));
		subfactories.put("admin",is->new MemberPermissionMatcher(WildcardPermission.admin,is));
		subfactories.put("owner",is->new MemberPermissionMatcher(WildcardPermission.owner,is));
		subfactories.put("member",is->new MemberPermissionMatcher(WildcardPermission.member,is));
		subfactories.put("stranger",is->new StrangerMatcher(is));
	}
	public static PermissionMatcher create(String s,boolean result) {
		PermissionFactory pf=subfactories.get(s);
		if(pf!=null)
			return pf.create(result);
		return null;
	}
	public static PermissionFactory get(String s) {
		return subfactories.get(s);
	}
	public static PermissionFactory getWC(String s) {
		return subfactories.get(s);
	}
}
