package com.khjxiaogu.MiraiSongPlugin.permission;

import java.util.Arrays;
import java.util.List;

import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;

public final class NullOperationMatcher implements PermissionMatcher {
	public static final NullOperationMatcher INSTANCE=new NullOperationMatcher();
	@Override
	public PermissionResult match(Member m) {
		return PermissionResult.UNSPECIFIED;
	}
	@Override
	public PermissionResult match(User u, boolean temp) {
		return PermissionResult.UNSPECIFIED;
	}
	@Override
	public PermissionResult match(long id, long group, Bot bot) {
		return PermissionResult.UNSPECIFIED;
	}
	@Override
	public List<String> getValue() {
		return Arrays.asList("#");
	}

}
