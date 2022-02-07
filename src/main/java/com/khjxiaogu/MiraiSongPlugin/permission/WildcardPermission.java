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

import java.util.function.Predicate;

import net.mamoe.mirai.contact.MemberPermission;

public enum WildcardPermission {
	admin(m->m==MemberPermission.ADMINISTRATOR),
	admins(m->m!=MemberPermission.MEMBER),
	owner(m->m==MemberPermission.OWNER),
	member(m->m==MemberPermission.MEMBER),
	members(m->true);
	private final Predicate<MemberPermission> matcher;

	private WildcardPermission(Predicate<MemberPermission> matcher) {
		this.matcher = matcher;
	}
	public boolean isMatch(MemberPermission mp) {
		return matcher.test(mp);
	}
}
