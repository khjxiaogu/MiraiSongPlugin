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

// TODO: Auto-generated Javadoc
/**
 * Class MusicInfo.
 *
 * @author khjxiaogu
 * file: MusicInfo.java
 * @date 2022年8月22日
 */
public class MusicInfo {
	
	/** The title.<br>音乐名称 */
	public final String title;
	
	/** The desc.<br>音乐描述 */
	public final String desc;
	
	/** The purl.<br>图片地址，必须能够外界直接访问 */
	public final String purl;
	
	/** The murl.<br>音频地址，必须能够外界直接访问 */
	public final String murl;
	
	/** The jurl.<br>跳转地址，必须能够外界直接访问 */
	public final String jurl;
	
	/** The source.<br>音乐源名称 */
	public final String source;
	
	/** The icon.<br>音乐源图标地址 */
	public final String icon;
	
	/** The appid.<br>音乐软件appid，用于发送卡片 */
	public final long appid;
	
	/** The properties.<br>请求上述链接的而外http HEAD，可能为null */
	public Map<String, String> properties;

	/**
	 * Instantiates a new MusicInfo.<br>
	 *
	 * @param title the title<br>
	 * @param desc the desc<br>
	 * @param purl the purl<br>
	 * @param murl the murl<br>
	 * @param jurl the jurl<br>
	 * @param source the source<br>
	 * @param icon the icon<br>
	 * @param appid the appid<br>
	 */
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

	/**
	 * Instantiates a new MusicInfo.<br>
	 *
	 * @param title the title<br>
	 * @param desc the desc<br>
	 * @param purl the purl<br>
	 * @param murl the murl<br>
	 * @param jurl the jurl<br>
	 * @param source the source<br>
	 */
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

	/**
	 * Instantiates a new MusicInfo.<br>
	 *
	 * @param title the title<br>
	 * @param desc the desc<br>
	 * @param purl the purl<br>
	 * @param murl the murl<br>
	 * @param jurl the jurl<br>
	 * @param source the source<br>
	 * @param icon the icon<br>
	 */
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

	@Override
	public String toString() {
		return "歌曲信息\n歌名：" + title + "\n作者：" + desc + "\n封面：" + purl + "\n音频：" + murl + "\n链接：" + jurl
				+ "\n来自：" + source + "\n小图标：" + icon + "\nAPPID：" + appid;
	}
}