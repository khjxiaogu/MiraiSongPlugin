package com.khjxiaogu.MiraiSongPlugin.test;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.MusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.BaiduMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.KugouMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.NetEaseAdvancedRadio;
import com.khjxiaogu.MiraiSongPlugin.musicsource.NetEaseMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.NetEaseRadioSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.QQMusicSource;

public class Main {
	public static final Map<String, MusicSource> sources = Collections.synchronizedMap(new LinkedHashMap<>());
	static {
		// 注册音乐来源
		sources.put("QQ音乐", new QQMusicSource());
		// sources.put("QQ音乐HQ",new QQMusicHQSource());//这个音乐源已被tx禁用。
		sources.put("网易", new NetEaseMusicSource());
		sources.put("网易电台", new NetEaseRadioSource());
		sources.put("酷狗", new KugouMusicSource());
		sources.put("千千", new BaiduMusicSource());
	}

	public Main() {
	}

	public static void main(String[] args) throws Exception {
		MusicInfo mi = sources.get("网易电台").get(URLEncoder.encode("先辈"));
		StringBuilder tsb = new StringBuilder().append("歌名：").append(mi.title).append("\n作者：").append(mi.desc)
				.append("\n封面：" + mi.purl).append("\n外链：" + mi.murl).append("\n链接：" + mi.jurl)
				.append("\n来自：" + mi.source);
		System.out.println(tsb);
	}

}
