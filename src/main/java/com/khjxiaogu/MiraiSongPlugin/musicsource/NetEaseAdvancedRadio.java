package com.khjxiaogu.MiraiSongPlugin.musicsource;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.khjxiaogu.MiraiSongPlugin.MusicInfo;

public class NetEaseAdvancedRadio extends NetEaseRadioSource {

	@Override
	public MusicInfo get(String keyword) throws Exception {
		String rkw=URLDecoder.decode(keyword,"UTF-8");
		int pos=rkw.indexOf('|');
		if(pos!=-1) {
			String radio=rkw.substring(0,pos);
			String song=rkw.substring(pos+1);
			if(song.length()>0&&radio.length()>0)
			return super.get(URLEncoder.encode(radio,"UTF-8"),song);
		}
		return super.get(keyword);
	}

}
