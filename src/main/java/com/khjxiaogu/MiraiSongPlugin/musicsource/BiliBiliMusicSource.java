package com.khjxiaogu.MiraiSongPlugin.musicsource;

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.MusicSource;
import com.khjxiaogu.MiraiSongPlugin.Utils;

public class BiliBiliMusicSource implements MusicSource {

	@Override
	public MusicInfo get(String keyword) throws Exception {
		URL url = new URL("https://api.bilibili.com/audio/music-service-c/s?keyword=" + keyword);
		HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		huc.setDoInput(true);
		huc.setDoOutput(true);
		huc.setRequestMethod("GET");
		huc.connect();
		JsonArray ja;
		String murl;
		if (huc.getResponseCode() == 200) {
			ja = JsonParser.parseString(new String(Utils.readAll(huc.getInputStream()), StandardCharsets.UTF_8))
					.getAsJsonObject().get("data").getAsJsonObject().get("result").getAsJsonArray();
		} else
			throw new FileNotFoundException();
		JsonObject jo = ja.get(0).getAsJsonObject();
		JsonArray pl = jo.get("play_url_list").getAsJsonArray();
		int i = 0;
		while (pl.size() <= 0) {
			jo = ja.get(++i).getAsJsonObject();
			pl = jo.get("play_url_list").getAsJsonArray();
		}
		murl = pl.get(pl.size() > 1 ? 1 : 0).getAsJsonObject().get("url").getAsString();
		MusicInfo mi = new MusicInfo(jo.get("title").getAsString(),
				"UP:" + jo.get("up_name").getAsString() + "创作:" + jo.get("author").getAsString(),
				jo.get("cover").getAsString(), murl, "https://www.bilibili.com/audio/au" + jo.get("id").getAsString(),
				"哔哩哔哩(请打开后播放)");
		mi.properties = new HashMap<>();
		mi.properties.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36");
		mi.properties.put("referer", "https://www.bilibili.com/");
		return mi;
	}

}
