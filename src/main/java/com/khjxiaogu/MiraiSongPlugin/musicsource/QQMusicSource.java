package com.khjxiaogu.MiraiSongPlugin.musicsource;

import java.io.FileNotFoundException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.MusicSource;
import com.khjxiaogu.MiraiSongPlugin.Utils;

public class QQMusicSource implements MusicSource {

	public QQMusicSource() {
	}
	public String queryRealUrl(String songmid) {
		try {
			StringBuilder urlsb = new StringBuilder(
					"https://u.y.qq.com/cgi-bin/musicu.fcg?format=json&data=%7B%22req_0%22%3A%7B%22module%22%3A%22vkey.GetVkeyServer%22%2C%22method%22%3A%22CgiGetVkey%22%2C%22param%22%3A%7B%22guid%22%3A%22358840384%22%2C%22songmid%22%3A%5B%22");
			urlsb.append(songmid);
			urlsb.append(
					"%22%5D%2C%22songtype%22%3A%5B0%5D%2C%22uin%22%3A%221443481947%22%2C%22loginflag%22%3A1%2C%22platform%22%3A%2220%22%7D%7D%2C%22comm%22%3A%7B%22uin%22%3A%2218585073516%22%2C%22format%22%3A%22json%22%2C%22ct%22%3A24%2C%22cv%22%3A0%7D%7D");
			URL u = new URL(urlsb.toString());
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Host", "u.y.qq.com");
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
			conn.connect();
			byte[] bs = Utils.readAll(conn.getInputStream());

			JsonObject out = JsonParser.parseString(new String(bs, "UTF-8")).getAsJsonObject();
			if (out.get("code").getAsInt() != 0) {
				return null;
			}
			StringBuilder sb = new StringBuilder(out.get("req_0").getAsJsonObject().get("data").getAsJsonObject()
					.get("sip").getAsJsonArray().get(0).getAsString());

			sb.append(out.get("req_0").getAsJsonObject().get("data").getAsJsonObject().get("midurlinfo")
					.getAsJsonArray().get(0).getAsJsonObject().get("purl").getAsString());
			return sb.toString();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public MusicInfo get(String keyword) throws Exception {
		URL url = new URL("https://c.y.qq.com/soso/fcgi-bin/client_search_cp?p=1&cr=1&aggr=1&flag_qc=0&n=3&w="
						+ keyword + "&format=json");
		HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		huc.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");
		huc.setRequestMethod("GET");
		huc.connect();
		JsonArray ss=JsonParser
				.parseString(
						new String(Utils.readAll(huc.getInputStream()), StandardCharsets.UTF_8))
				.getAsJsonObject().get("data").getAsJsonObject().get("song").getAsJsonObject()
				.get("list").getAsJsonArray();
		JsonObject song = ss.get(0).getAsJsonObject();// .data.song.list
		String mid = song.get("songmid").getAsString();
		String musicURL = queryRealUrl(mid);
		int i=0;
		while(!Utils.isExistent(musicURL)) {
			song = ss.get(++i).getAsJsonObject();
			mid = song.get("songmid").getAsString();
			musicURL = queryRealUrl(mid);
		}
		String desc;
		try {
			JsonArray singers=song.get("singer").getAsJsonArray();
			StringBuilder sgs=new StringBuilder();
			for(JsonElement je:singers) {
				sgs.append(je.getAsJsonObject().get("name").getAsString());
				sgs.append(";");
			}
			sgs.deleteCharAt(sgs.length()-1);
			desc=sgs.toString();
		}catch(Exception e) {
			desc=song.get("albumname").getAsString();
		}
		
		
		if (musicURL == null) {
			throw new FileNotFoundException();
		}
		return new MusicInfo(
				song.get("songname").getAsString(),
				desc,
				"http://y.gtimg.cn/music/photo_new/T002R300x300M000"+ song.get("albummid").getAsString() + ".jpg",
				musicURL,
				"https://i.y.qq.com/v8/playsong.html?_wv=1&songid="+ song.get("songid").getAsString() + "&source=qqshare&ADTAG=qqshare",
				"QQ音乐",
				"https://url.cn/PwqZ4Jpi",
				1101079856
			);
	}

}
