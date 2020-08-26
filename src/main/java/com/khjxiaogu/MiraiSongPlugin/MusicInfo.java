/*
 * 
 */
package com.khjxiaogu.MiraiSongPlugin;

// TODO: Auto-generated Javadoc
/**
 * Class MusicInfo.
 * 通用的音乐信息列表
 * @author khjxiaogu
 * file: MusicInfo.java
 * time: 2020年8月26日
 */
public class MusicInfo{
	
	/** The title.<br> 标题. */
	public final String title;
	
	/** The description.<br> 副标题，可以是歌手或者专辑. */
	public final String desc;
	
	/** The preview url.<br> 预览图URL. */
	public final String purl;
	
	/** The musci url.<br> 音乐URL. */
	public final String murl;
	
	/** The jump url.<br> 点击跳转URL. */
	public final String jurl;
	
	/** The source.<br> 来源名称. */
	public final String source;
	
	/** The icon.<br> 来源图标URL. */
	public final String icon;
	
	/** The appid.<br> 来源APPID. */
	public final long appid;
	
	/**
	 * Instantiates a new MusicInfo.<br>
	 * 新建一个MusicInfo类<br>
	 * 填入所有参数
	 * @param title 标题
	 * @param desc 描述
	 * @param purl 图片URL
	 * @param murl 音乐URL
	 * @param jurl 跳转URL
	 * @param source 来源名称
	 * @param icon 来源图标URL
	 * @param appid 来源APPID<br>
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
	 * 新建一个MusicInfo类<br>
	 * 只填入关键音乐参数
	 * @param title 标题
	 * @param desc 描述
	 * @param purl 图片URL
	 * @param murl 音乐URL
	 * @param jurl 跳转URL
	 * @param source 来源名称
	 */
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
	
	/**
	 * Instantiates a new MusicInfo.<br>
	 * 新建一个MusicInfo类<br>
	 * 关键参数+图标
	 * @param title 标题
	 * @param desc 描述
	 * @param purl 图片URL
	 * @param murl 音乐URL
	 * @param jurl 跳转URL
	 * @param source 来源名称
	 * @param icon 来源图标URL
	 */
	public MusicInfo(String title, String desc, String purl, String murl, String jurl, String source, String icon) {
		this.title = title;
		this.desc = desc;
		this.purl = purl;
		this.murl = murl;
		this.jurl = jurl;
		this.source = source;
		this.icon = icon;
		this.appid = 1234561234;
	}
}