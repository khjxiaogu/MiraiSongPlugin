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
		keyword=Utils.urlEncode(keyword);
		JsonObject jo;
		HttpURLConnection huc;
		int requested = 0;
		do {
			huc = (HttpURLConnection) new URL(
					"http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&calback=&from=webapp_music&method=baidu.ting.search.catalogSug&query="
							+ keyword).openConnection();
			huc.setRequestProperty("Host", "tingapi.ting.baidu.com");
			huc.setRequestProperty("Referrer", "http://http://music.taihe.com/");
			huc.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36");
			huc.setRequestMethod("GET");
			huc.connect();
			jo = JsonParser.parseString(new String(Utils.readAll(huc.getInputStream()), "UTF-8")).getAsJsonObject();
		} while (jo.get("error_code").getAsInt() != 22000 && requested++ < 3);// 傻逼百度有时候会请求失败，不断请求直到成功。
		String sid = jo.getAsJsonObject().getAsJsonObject().get("song").getAsJsonArray().get(0).getAsJsonObject()
				.get("songid").getAsString();
		huc.disconnect();
		huc = (HttpURLConnection) new URL(
				"http://tingapi.ting.baidu.com/v1/restserver/ting?format=json&calback=&from=webapp_music&method=baidu.ting.song.play&songid="
						+ sid).openConnection();
		huc.setRequestProperty("Host", "tingapi.ting.baidu.com");
		huc.setRequestProperty("Referrer", "http://music.taihe.com/");
		huc.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/84.0.4147.125 Safari/537.36");
		huc.setRequestMethod("GET");
		huc.connect();
		JsonObject allinfo = JsonParser.parseString(new String(Utils.readAll(huc.getInputStream()), "UTF-8"))
				.getAsJsonObject();
		JsonObject sif = allinfo.getAsJsonObject().get("songinfo").getAsJsonObject();
		huc.disconnect();
		return new MusicInfo(sif.get("title").getAsString(), sif.get("author").getAsString(),
				sif.get("pic_big").getAsString(),
				allinfo.get("bitrate").getAsJsonObject().get("file_link").getAsString(),
				sif.get("share_url") != null ? sif.get("share_url").getAsString() : "", "千千静听");
	}

}
