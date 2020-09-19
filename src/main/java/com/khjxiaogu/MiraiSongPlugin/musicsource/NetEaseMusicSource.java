package com.khjxiaogu.MiraiSongPlugin.musicsource;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
				jo.get("artists").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString(),
				jo.get("album").getAsJsonObject().get("picUrl").getAsString(), murl,
				"https://y.music.163.com/m/song?id=" + jo.get("id").getAsString(), "网易云音乐", "", 100495085);
	}

}
