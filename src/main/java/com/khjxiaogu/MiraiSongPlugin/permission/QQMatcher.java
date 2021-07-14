package com.khjxiaogu.MiraiSongPlugin.permission;

import java.util.Arrays;
import java.util.List;

import net.mamoe.mirai.Bot;

public class QQMatcher implements PermissionMatcher {
	PermissionResult result;
	long qq;
	@Override
	public PermissionResult match(long id, long group, Bot bot) {
		if(id==qq)
			return result;
		return PermissionResult.UNSPECIFIED;
	}
	public QQMatcher(long qq,PermissionResult result) {
		this.result = result;
		this.qq=qq;
	}
	public QQMatcher(long qq,boolean result) {
		this(qq,PermissionResult.valueOf(result));
	}
	@Override
	public List<String> getValue() {
		return Arrays.asList(result.getSymbol()+String.valueOf(qq));
	}
}
