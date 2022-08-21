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
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.MusicSource;
import com.khjxiaogu.MiraiSongPlugin.NetEaseCrypto;
import com.khjxiaogu.MiraiSongPlugin.Utils;

public class NetEaseRadioSource implements MusicSource {

	public NetEaseRadioSource() {
	}

	public String queryRealUrl(String id) throws Exception {
		return "http://music.163.com/song/media/outer/url?id=" + id + ".mp3";
	}
	protected JsonObject getSlice(String id,int offset,int limit) throws IOException {
		JsonObject params = new JsonObject();
		params.addProperty("radioId",id);
		params.addProperty("limit",limit);
		params.addProperty("offset",offset);
		params.addProperty("asc", true);
		String[] encrypt = NetEaseCrypto.weapiEncrypt(params.toString());
		StringBuilder sb = new StringBuilder("params=");
		sb.append(encrypt[0]);
		sb.append("&encSecKey=");
		sb.append(encrypt[1]);
		byte[] towrite = sb.toString().getBytes("UTF-8");
		URL u = new URL("https://music.163.com/weapi/dj/program/byradio?csrf_token=");
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
			return JsonParser.parseString(new String(bs, "UTF-8")).getAsJsonObject();
			
		}
		return null;
	}

	protected JsonObject getRadioSong(String id, String keyword) throws IOException {
		JsonObject main = getSlice(id, 0, 10);
		int cur=10;
		if (main != null && main.get("code").getAsInt() == 200) {
			JsonArray data = main.get("programs").getAsJsonArray();
			double curmax = Integer.MAX_VALUE;
			JsonObject result = null;
			int count=main.get("count").getAsInt();
			
			if (data.size() <= 1 || keyword == null) {
				if (data.size() == 0)
					return null;
				return data.get(0).getAsJsonObject().get("mainSong").getAsJsonObject();
			}
			
			do{
				for (JsonElement je : data) {
					JsonObject song = je.getAsJsonObject();
					String sn = song.get("name").getAsString();
					double sim = Utils.compare(keyword, sn);
					if (sn.equals(keyword)) {
						return song.get("mainSong").getAsJsonObject();
					}
					if (sim < curmax) {
						curmax = sim;
						result = song;
					}
				}
				 main = getSlice(id,cur, 50);
				 cur+=50;
				 if (main == null|| main.get("code").getAsInt() != 200)
					 break;
				 data = main.get("programs").getAsJsonArray();
				 
				 if (data.size() < 1)
					 break;
			}while(cur<count);
			return result.get("mainSong").getAsJsonObject();

		}
		return null;
	}

	@Override
	public MusicInfo get(String keyword) throws Exception {
		return get(keyword, null);
	}

	public MusicInfo get(String keyword, String songname) throws Exception {

		JsonObject params = new JsonObject();
		params.addProperty("s",keyword);
		params.addProperty("type",1009);
		params.addProperty("offset",0);
		params.addProperty("limit",3);
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
		if (huc.getResponseCode() == 200) {
			String s = new String(Utils.readAll(huc.getInputStream()), StandardCharsets.UTF_8);
			ja = JsonParser.parseString(s).getAsJsonObject().get("result").getAsJsonObject().get("djRadios")
					.getAsJsonArray();
		} else
			throw new FileNotFoundException();

		JsonObject detail = ja.get(0).getAsJsonObject();

		JsonObject jo = getRadioSong(detail.get("id").getAsString(), songname);
		murl = queryRealUrl(jo.get("id").getAsString());
		JsonObject dj = detail.get("dj").getAsJsonObject();
		return new MusicInfo(jo.get("name").getAsString(), dj.get("nickname").getAsString(),
				detail.get("picUrl").getAsString(), murl,
				"http://music.163.com/program/" + detail.get("lastProgramId").getAsString() + "/"
						+ dj.get("userId").getAsString() + "/?userid=" + dj.get("userId").getAsString(),
				"网易云电台", "", 100495085);
	}

	@Override
	public boolean isVisible() {
		return false;
	}
}
