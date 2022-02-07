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
package com.khjxiaogu.MiraiSongPlugin;

import java.util.Map;

public class MusicInfo {
	public final String title;
	public final String desc;
	public final String purl;
	public final String murl;
	public final String jurl;
	public final String source;
	public final String icon;
	public final long appid;
	public Map<String, String> properties;

	public MusicInfo(String title, String desc, String purl, String murl, String jurl, String source, String icon,
			long appid) {
		this.title = title;
		this.desc = desc;
		this.purl = purl;
		this.murl = murl;
		this.jurl = jurl;
		this.source = source;
		this.icon = icon;
		this.appid = appid;
	}

	public MusicInfo(String title, String desc, String purl, String murl, String jurl, String source) {
		this.appid = 0;
		this.icon = "";
		this.source = source;
		this.title = title;
		this.desc = desc;
		this.purl = purl;
		this.murl = murl;
		this.jurl = jurl;
	}

	public MusicInfo(String title, String desc, String purl, String murl, String jurl, String source, String icon) {
		this.title = title;
		this.desc = desc;
		this.purl = purl;
		this.murl = murl;
		this.jurl = jurl;
		this.source = source;
		this.icon = icon;
		this.appid = 0;
	}
}