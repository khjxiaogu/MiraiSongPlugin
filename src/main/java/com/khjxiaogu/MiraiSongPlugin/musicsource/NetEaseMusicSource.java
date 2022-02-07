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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.MusicSource;
import com.khjxiaogu.MiraiSongPlugin.NetEaseCrypto;
import com.khjxiaogu.MiraiSongPlugin.Utils;

public class NetEaseMusicSource implements MusicSource {

	public NetEaseMusicSource() {
	}

	public String queryRealUrl(String id) throws Exception {
		return "http://music.163.com/song/media/outer/url?id=" + id + ".mp3";
	}

	@Override
	public MusicInfo get(String keyword) throws Exception {
		JsonObject params = new JsonObject();
		params.add("s", new JsonPrimitive(URLDecoder.decode(keyword, "UTF-8")));
		params.add("type",new JsonPrimitive(1));
		params.add("offset",new JsonPrimitive(0));
		params.add("limit",new JsonPrimitive(3));
		String[] encrypt = NetEaseCrypto.weapiEncrypt(params.toString());
		StringBuilder sb = new StringBuilder("params=");
		sb.append(encrypt[0]);
		sb.append("&encSecKey=");
		sb.append(encrypt[1]);
		URL url = new URL("https://music.163.com/weapi/cloudsearch/get/web?csrf_token=");
		HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		huc.setDoInput(true);
		huc.setDoOutput(true);
		huc.setRequestMethod("POST");
		huc.setRequestProperty("Referer", "http://music.163.com/");
		huc.setRequestProperty("Cookie", "appver=1.5.0.75771;");
		huc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
		huc.connect();
		huc.getOutputStream().write(sb.toString().getBytes(StandardCharsets.UTF_8));
		JsonArray ja;
		String murl;
		String data=new String(Utils.readAll(huc.getInputStream()), StandardCharsets.UTF_8);
		if (huc.getResponseCode() == 200) {
			ja = JsonParser.parseString(data)
					.getAsJsonObject().get("result").getAsJsonObject().get("songs").getAsJsonArray();
		} else
			throw new FileNotFoundException();
		JsonObject jo = ja.get(0).getAsJsonObject();
		murl = queryRealUrl(jo.get("id").getAsString());
		int i = 0;
		while (!Utils.isExistent(murl)) {
			jo = ja.get(++i).getAsJsonObject();
			murl = queryRealUrl(jo.get("id").getAsString());
		}
		return new MusicInfo(jo.get("name").getAsString(),
				jo.get("ar").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString(),
				jo.get("al").getAsJsonObject().get("picUrl").getAsString(), murl,
				"https://y.music.163.com/m/song?id=" + jo.get("id").getAsString(), "网易云音乐", "", 100495085);
	}

}
