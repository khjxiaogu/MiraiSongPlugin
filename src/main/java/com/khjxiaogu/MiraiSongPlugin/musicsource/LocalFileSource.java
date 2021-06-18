package com.khjxiaogu.MiraiSongPlugin.musicsource;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.MusicSource;
import com.khjxiaogu.MiraiSongPlugin.Utils;

public class LocalFileSource implements MusicSource {
	public static boolean autoLocal = false;

	@Override
	public boolean isVisible() {
		return autoLocal;
	}

	public LocalFileSource() {
	}

	String forceGetJsonString(JsonObject jo, String member) {
		return forceGetJsonString(jo, member, "");
	}

	String forceGetJsonString(JsonObject jo, String member, String def) {
		if (jo.has(member))
			return jo.get(member).getAsString();
		return def;
	}

	@Override
	public MusicInfo get(String keyword) throws Exception {
		String rkw = URLDecoder.decode(keyword, "UTF-8");
		JsonArray localfs = JsonParser.parseReader(new FileReader("SongPluginLocal.json")).getAsJsonArray();
		JsonObject result = null;
		int min = Integer.MAX_VALUE;
		for (JsonElement je : localfs) {
			JsonObject cur = je.getAsJsonObject();
			String ckw = forceGetJsonString(cur, "title");
			int curm = Utils.compare(rkw, ckw);
			if (curm < min) {
				min = curm;
				result = cur;
			}
		}
		return new MusicInfo(forceGetJsonString(result, "title"), forceGetJsonString(result, "desc"),
				forceGetJsonString(result, "previewUrl"), forceGetJsonString(result, "musicUrl"),
				forceGetJsonString(result, "jumpUrl"), forceGetJsonString(result, "source", "本地"));
	}

}
