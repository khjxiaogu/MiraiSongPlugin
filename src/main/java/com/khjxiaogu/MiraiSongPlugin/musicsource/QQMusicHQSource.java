package com.khjxiaogu.MiraiSongPlugin.musicsource;

import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.khjxiaogu.MiraiSongPlugin.Utils;

public class QQMusicHQSource extends QQMusicSource {
	String[] availenc = new String[] { "D00A", // .flac
			"A000", // .ape
			"F000", // .flac
			"M800", // .mp3
			"O600", // .ogg
			"C400", // .m4a
			"M500",// .mp3
	};
	String[] availext = new String[] { ".flac", ".ape", ".flac", ".mp3", ".ogg", ".m4a", ".mp3", };

	public QQMusicHQSource() {
	}

	@Override
	public String queryRealUrl(String songmid) {
		StringBuilder urlsb = new StringBuilder(
				"https://c.y.qq.com/base/fcgi-bin/fcg_music_express_mobile3.fcg?format=json&platform=yqq&cid=205361747&guid=6931742855&songmid=");
		urlsb.append(songmid).append("&filename=");
		JsonObject out = null;
		String iqual = "2";
		int i = 0;
		if (iqual != null)
			i = Integer.parseInt(iqual);
		try {
			if (i < availenc.length)
				for (; i < availenc.length; i++) {
					URL u = new URL(urlsb.toString() + availenc[i] + songmid + availext[i]);
					System.out.println("incoming" + u.toString());
					HttpURLConnection conn = (HttpURLConnection) u.openConnection();
					conn.setDoOutput(true);
					conn.setRequestMethod("GET");
					conn.setRequestProperty("Host", "c.y.qq.com");
					conn.setRequestProperty("Referer", "http://y.qq.com");
					conn.setRequestProperty("User-Agent",
							"Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
					conn.connect();
					byte[] bs = Utils.readAll(conn.getInputStream());
					out = JsonParser.parseString(new String(bs, "UTF-8")).getAsJsonObject().get("data")
							.getAsJsonObject().get("items").getAsJsonArray().get(0).getAsJsonObject();
					JsonElement je = out.get("subcode");
					if (je == null || je.getAsInt() != 0) {
						continue;
					}
					break;
				}
			StringBuilder sb = new StringBuilder("http://ws.stream.qqmusic.qq.com/")
					.append(out.get("filename").getAsString()).append("?vkey=").append(out.get("vkey").getAsString())
					.append("&uid=0&guid=6931742855&fromtag=66");
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
