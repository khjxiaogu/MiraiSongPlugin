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
package com.khjxiaogu.MiraiSongPlugin.musicsource;

import java.net.URLDecoder;
import java.net.URLEncoder;

import com.khjxiaogu.MiraiSongPlugin.MusicInfo;

public class NetEaseAdvancedRadio extends NetEaseRadioSource {

	@Override
	public MusicInfo get(String keyword) throws Exception {
		String rkw = URLDecoder.decode(keyword, "UTF-8");
		int pos = rkw.indexOf('|');
		if (pos != -1) {
			String radio = rkw.substring(0, pos);
			String song = rkw.substring(pos + 1);
			if (song.length() > 0 && radio.length() > 0)
				return super.get(URLEncoder.encode(radio, "UTF-8"), song);
		}
		return super.get(keyword);
	}

}
