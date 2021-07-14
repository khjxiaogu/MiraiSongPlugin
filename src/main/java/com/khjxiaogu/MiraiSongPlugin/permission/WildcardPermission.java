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
