package com.khjxiaogu.MiraiSongPlugin;

public class MusicInfo{
	public final String title;
	public final String desc;
	public final String purl;
	public final String murl;
	public final String jurl;
	public final String source;
	public final String icon;
	public final long appid;
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
		this.appid = 1234561234;
		this.icon = "";
		this.source = source;
		this.title = title;
		this.desc = desc;
		this.purl = purl;
		this.murl = murl;
		this.jurl = jurl;
	}
}