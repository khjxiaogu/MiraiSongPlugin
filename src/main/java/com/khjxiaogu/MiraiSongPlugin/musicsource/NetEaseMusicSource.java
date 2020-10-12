package com.khjxiaogu.MiraiSongPlugin.musicsource;

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.MusicSource;
import com.khjxiaogu.MiraiSongPlugin.Utils;

public class NetEaseMusicSource implements MusicSource {

	public NetEaseMusicSource() {
	}
	public String queryRealUrl(String id) throws Exception {
		return "http://music.163.com/song/media/outer/url?id="+id+".mp3";
	}
	@Override
	public MusicInfo get(String keyword) throws Exception {
		URL url = new URL("http://music.163.com/api/search/pc");
		HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		huc.setDoInput(true);
		huc.setDoOutput(true);
		huc.setRequestMethod("POST");
		huc.setRequestProperty("Referer", "http://music.163.com/");
		huc.setRequestProperty("Cookie", "appver=1.5.0.75771;");
		huc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
		huc.connect();
		huc.getOutputStream().write(("type=1&offset=0&limit=3&s=" + keyword).getBytes(StandardCharsets.UTF_8));
		JsonArray ja;
		String murl;
		if (huc.getResponseCode() == 200) {
			ja = JsonParser.parseString(new String(Utils.readAll(huc.getInputStream()), StandardCharsets.UTF_8))
					.getAsJsonObject().get("result").getAsJsonObject().get("songs").getAsJsonArray();
		}else
			throw new FileNotFoundException();
		JsonObject jo=ja.get(0).getAsJsonObject();
		murl=queryRealUrl(jo.get("id").getAsString());
		int i=0;
		while(!Utils.isExistent(murl)) {
			jo=ja.get(++i).getAsJsonObject();
			murl=queryRealUrl(jo.get("id").getAsString());
		}
			return new MusicInfo(
				jo.get("name").getAsString(),
				jo.get("artists").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString(),
				jo.get("album").getAsJsonObject().get("picUrl").getAsString(),
				murl,
				"https://y.music.163.com/m/song?id=" + jo.get("id").getAsString(),
				"网易云音乐",
				"",
				 100495085
			);
	}

}
