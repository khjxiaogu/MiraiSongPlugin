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
