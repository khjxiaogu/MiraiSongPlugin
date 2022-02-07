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

import java.util.Arrays;
import java.util.List;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;

public class MemberPermissionMatcher implements PermissionMatcher {
	PermissionResult result;
	WildcardPermission perm;
	@Override
	public PermissionResult match(long id, long group, Bot bot) {
		if(group==0)return PermissionResult.UNSPECIFIED;
		Group g=bot.getGroup(group);
		if(g!=null) {
			try {
				Member m=g.get(id);
				if(m!=null)
					return match(m);
			}catch(Exception ignored) {}
		}
		return PermissionResult.UNSPECIFIED;
	}

	@Override
	public PermissionResult match(Member m) {
		return (perm.isMatch(m.getPermission())?result:PermissionResult.UNSPECIFIED);
	}

	@Override
	public PermissionResult match(User u, boolean temp) {
		return PermissionResult.UNSPECIFIED;
	}

	public MemberPermissionMatcher(WildcardPermission perm,PermissionResult result) {
		this.perm=perm;
		this.result = result;
	}
	public MemberPermissionMatcher(WildcardPermission perm,boolean result) {
		this(perm,PermissionResult.valueOf(result));
	}

	@Override
	public List<String> getValue() {
		return Arrays.asList(result.getSymbol()+perm.name());
	}
}
