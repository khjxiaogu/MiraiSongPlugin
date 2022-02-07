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

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.khjxiaogu.MiraiSongPlugin.NetEaseCrypto;
import com.khjxiaogu.MiraiSongPlugin.Utils;

public class NetEaseHQMusicSource extends NetEaseMusicSource {

	public NetEaseHQMusicSource() {
	}

	@Override
	public String queryRealUrl(String id) throws Exception {
		JsonObject params = new JsonObject();
		params.add("ids", new JsonPrimitive("[" + id + "]"));
		params.add("br", new JsonPrimitive(999000));
		String[] encrypt = NetEaseCrypto.weapiEncrypt(params.toString());
		StringBuilder sb = new StringBuilder("params=");
		sb.append(encrypt[0]);
		sb.append("&encSecKey=");
		sb.append(encrypt[1]);
		byte[] towrite = sb.toString().getBytes("UTF-8");
		URL u = new URL("http://music.163.com/weapi/song/enhance/player/url?csrf_token=");
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setConnectTimeout(4000);
		conn.setReadTimeout(4000);
		conn.setFixedLengthStreamingMode(towrite.length);
		conn.setInstanceFollowRedirects(true);
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", Integer.toString(towrite.length));
		conn.setRequestProperty("Referer", "https://music.163.com");
		conn.setRequestProperty("Host", "music.163.com");
		conn.setRequestProperty("User-Agent", NetEaseCrypto.getUserAgent());
		conn.connect();
		conn.getOutputStream().write(towrite);
		if (conn.getResponseCode() == 200) {
			InputStream is = conn.getInputStream();
			byte[] bs = null;
			bs = Utils.readAll(is);
			is.close();
			conn.disconnect();
			JsonObject main = JsonParser.parseString(new String(bs, "UTF-8")).getAsJsonObject();
			if (main.get("code").getAsInt() == 200) {
				JsonArray data = main.get("data").getAsJsonArray();
				JsonObject song = data.get(0).getAsJsonObject();
				if (song.get("code").getAsInt() == 200) {
					return song.get("url").getAsString().trim();
				}
			}
		}
		return null;
	}

	@Override
	public boolean isVisible() {
		return false;
	}
}
