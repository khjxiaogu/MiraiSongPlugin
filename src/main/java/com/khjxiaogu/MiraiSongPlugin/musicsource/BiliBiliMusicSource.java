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
		keyword=Utils.urlEncode(keyword);
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
				"哔哩哔哩","https://open.gtimg.cn/open/app_icon/00/95/17/76/100951776_100_m.png?t=1624441854",100951776);
		mi.properties = new HashMap<>();
		mi.properties.put("user-agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.106 Safari/537.36");
		mi.properties.put("referer", "https://www.bilibili.com/");
		return mi;
	}

}
