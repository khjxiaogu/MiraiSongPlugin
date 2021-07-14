package com.khjxiaogu.MiraiSongPlugin.permission;

public enum PermissionResult {
	ALLOW(true,"+"),UNSPECIFIED(true,null),DISALLOW(false,"-");
	private final boolean allow;
	private final String symbol;
	private PermissionResult(boolean allow,String symbol) {
		this.allow = allow;
		this.symbol=symbol;
	}
	public String getSymbol() {
		return symbol;
	}
	public static PermissionResult valueOf(boolean b) {
		if(b)return ALLOW;
		return DISALLOW;
	}
	public boolean isAllowed() {
		return allow;
	}
	public PermissionResult and(PermissionResult oth) {
		if(oth==UNSPECIFIED)
			return this;
		return oth;
	}
}
