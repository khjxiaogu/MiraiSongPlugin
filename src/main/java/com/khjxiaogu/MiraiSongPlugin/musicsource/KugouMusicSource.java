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

public class KugouMusicSource implements MusicSource {
	final static String COOKIE = "kg_mid=30f1713c23ab7bb496ab035b07dae834; ACK_SERVER_10015=%7B%22list%22%3A%5B%5B%22bjlogin-user.kugou.com%22%5D%5D%7D; ACK_SERVER_10016=%7B%22list%22%3A%5B%5B%22bjreg-user.kugou.com%22%5D%5D%7D; ACK_SERVER_10017=%7B%22list%22%3A%5B%5B%22bjverifycode.service.kugou.com%22%5D%5D%7D; Hm_lvt_aedee6983d4cfc62f509129360d6bb3d=1598198881; kg_dfid=1HZmYL0ngIYp0uu93N2m4s5P; kg_dfid_collect=d41d8cd98f00b204e9800998ecf8427e; Hm_lpvt_aedee6983d4cfc62f509129360d6bb3d=1598199021";

	public KugouMusicSource() {
	}

	@Override
	public MusicInfo get(String keyword) throws Exception {
		keyword=Utils.urlEncode(keyword);
		HttpURLConnection huc = (HttpURLConnection) new URL(
				"http://msearchcdn.kugou.com/api/v3/search/song?showtype=14&highlight=em&pagesize=1&tag_aggr=1&tagtype=%E5%85%A8%E9%83%A8&plat=0&sver=5&correct=1&api_ver=1&version=9108&page=1&area_code=1&tag=1&with_res_tag=1&keyword="
						+ keyword).openConnection();
		huc.setRequestMethod("GET");
		huc.setRequestProperty("Host", "msearchcdn.kugou.com");
		huc.connect();
		JsonObject je = JsonParser
				.parseString(new String(Utils.readAll(huc.getInputStream()), "UTF-8").replaceAll("<!--[_A-Z]+-->", ""))
				.getAsJsonObject();
		String song = je.get("data").getAsJsonObject().get("info").getAsJsonArray().get(0).getAsJsonObject().get("hash")
				.getAsString();
		String album_id = je.get("data").getAsJsonObject().get("info").getAsJsonArray().get(0).getAsJsonObject()
				.get("album_id").getAsString();
		HttpURLConnection ihuc = (HttpURLConnection) new URL(
				"https://wwwapi.kugou.com/yy/index.php?r=play/getdata&hash=" + song + "&album_id=" + album_id + "&_="
						+ Utils.getTime()).openConnection();
		ihuc.setRequestMethod("GET");
		ihuc.setRequestProperty("Host", "www.kugou.com");
		ihuc.setRequestProperty("Cookie", COOKIE);
		ihuc.connect();
		JsonObject info = JsonParser.parseString(new String(Utils.readAll(ihuc.getInputStream()), "UTF-8"))
				.getAsJsonObject().get("data").getAsJsonObject();
		return new MusicInfo(info.get("audio_name").getAsString(), info.get("author_name").getAsString(),
				info.get("img").getAsString(), info.get("play_url").getAsString(),
				"https://www.kugou.com/song/#hash=" + song + "&album_id=" + info.get("album_id").getAsString(), "酷狗","",205141);
	}

}
