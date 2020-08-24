package com.khjxiaogu.MiraiSongPlugin.musicsource;

import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.MusicSource;
import com.khjxiaogu.MiraiSongPlugin.Utils;

public class BaiduMusicSource implements MusicSource {

	public BaiduMusicSource() {
	}

	@Override
	public MusicInfo get(String keyword) throws Exception {
		HttpURLConnection huc=(HttpURLConnection) new URL("http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&calback=&from=webapp_music&method=baidu.ting.search.catalogSug&query="+keyword).openConnection();
		huc.setRequestMethod("GET");
		huc.connect();
		String sid=JsonParser.parseString(new String(Utils.readAll(huc.getInputStream()), "UTF-8"))
		.getAsJsonObject().get("song").getAsJsonArray().get(0).getAsJsonObject().get("songid")
		.getAsString();
		huc=(HttpURLConnection) new URL("http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&calback=&from=webapp_music&method=baidu.ting.song.playAAC&songid="+sid).openConnection();
		JsonObject allinfo=JsonParser.parseString(new String(Utils.readAll(huc.getInputStream()), "UTF-8")).getAsJsonObject();
		JsonObject sif=allinfo.getAsJsonObject().get("songinfo").getAsJsonObject();
		return new MusicInfo(sif.get("title").getAsString(),sif.get("author").getAsString(),allinfo.get("pic_big").getAsString(),allinfo.get("bitrate").getAsJsonObject().get("file_link").getAsString(),sif.get("share_url").getAsString(),"千千静听","http://pic.17qq.com/uploads/epihigfbdz.jpeg");
	}

}
