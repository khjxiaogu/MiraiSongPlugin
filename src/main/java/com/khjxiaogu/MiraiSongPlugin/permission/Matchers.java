/**
 * Mirai Song Plugin
 * Copyright (C) 2021  khjxiaogu
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.khjxiaogu.MiraiSongPlugin.permission;

import java.util.HashMap;
import java.util.Map;

import com.khjxiaogu.MiraiSongPlugin.permission.CommandMatcher.PermissionFactory;

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
