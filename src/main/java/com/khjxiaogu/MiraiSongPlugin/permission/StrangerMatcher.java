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

public class StrangerMatcher implements PermissionMatcher {
	PermissionResult result;
	@Override
	public PermissionResult match(MatchInfo info) {
		if(info.isTemp||info.bot.getFriend(info.callerid)==null) {
			return result;
		}
		return PermissionResult.UNSPECIFIED;
	}


	public StrangerMatcher(PermissionResult result) {
		this.result = result;
	}
	public StrangerMatcher(boolean result) {
		this(PermissionResult.valueOf(result));
	}

	@Override
	public List<String> getValue() {
		return Arrays.asList(result.getSymbol()+"stranger");
	}

}
