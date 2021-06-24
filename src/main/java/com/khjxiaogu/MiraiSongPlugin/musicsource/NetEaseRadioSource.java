package com.khjxiaogu.MiraiSongPlugin.musicsource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.MusicSource;
import com.khjxiaogu.MiraiSongPlugin.NetEaseCrypto;
import com.khjxiaogu.MiraiSongPlugin.Utils;

public class NetEaseRadioSource implements MusicSource {

	public NetEaseRadioSource() {
	}
	public String queryRealUrl(String id) throws Exception {
		return "http://music.163.com/song/media/outer/url?id="+id+".mp3";
	}
	protected JsonObject getRadioSong(String id,String keyword) throws IOException {
		JsonObject params = new JsonObject();
		params.add("radioId", new JsonPrimitive(id));
		params.add("limit", new JsonPrimitive(100));
		params.add("offset", new JsonPrimitive(0));
		params.addProperty("asc",true);
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
			JsonObject main = JsonParser.parseString(new String(bs, "UTF-8")).getAsJsonObject();
			if (main.get("code").getAsInt() == 200) {
				JsonArray data = main.get("programs").getAsJsonArray();
				
				if(data.size()<=1||keyword==null) {
					return data.get(0).getAsJsonObject().get("mainSong").getAsJsonObject();
				}
				int curmax=Integer.MAX_VALUE;
				JsonObject result=null;
				for(JsonElement je:data) {
					JsonObject song=je.getAsJsonObject();
					int sim=Utils.compare(keyword,song.get("name").getAsString());
					if(sim<curmax) {
						curmax=sim;
						result=song;
					}
				}
				return result.get("mainSong").getAsJsonObject();
				
			}
		}
		return null;
	}
	@Override
	public MusicInfo get(String keyword) throws Exception {
		return get(keyword,null);
	}
	public MusicInfo get(String keyword,String songname) throws Exception {
		
		URL url = new URL("http://music.163.com/api/search/pc");
		HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		huc.setDoInput(true);
		huc.setDoOutput(true);
		huc.setRequestMethod("POST");
		huc.setRequestProperty("Referer", "http://music.163.com/");
		huc.setRequestProperty("Cookie", "appver=1.5.0.75771;");
		huc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
		huc.connect();
			
		huc.getOutputStream().write(("type=1009&offset=0&limit=1&s=" + keyword).getBytes(StandardCharsets.UTF_8));
		JsonArray ja;
		String murl;
		if (huc.getResponseCode() == 200) {
			String s=new String(Utils.readAll(huc.getInputStream()), StandardCharsets.UTF_8);
			ja = JsonParser.parseString(s)
					.getAsJsonObject().get("result").getAsJsonObject().get("djRadios").getAsJsonArray();
		}else
			throw new FileNotFoundException();
		System.out.println(ja);
		JsonObject detail=ja.get(0).getAsJsonObject();
		
		JsonObject jo=getRadioSong(detail.get("id").getAsString(),songname);
		murl=queryRealUrl(jo.get("id").getAsString());
		int i=0;
		JsonObject dj=detail.get("dj").getAsJsonObject();
			return new MusicInfo(
				jo.get("name").getAsString(),
				dj.get("nickname").getAsString(),
				detail.get("picUrl").getAsString(),
				murl,
				"http://music.163.com/program/" + detail.get("lastProgramId").getAsString()+"/" + dj.get("userId").getAsString()+"/?userid="+dj.get("userId").getAsString(),
				"网易云电台",
				"",
				 100495085
			);
	}
	@Override
	public boolean isVisible() {
		return false;
	}
}
