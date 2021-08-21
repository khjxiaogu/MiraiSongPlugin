package com.khjxiaogu.MiraiSongPlugin.musicsource;

import java.io.FileReader;
import java.net.URLDecoder;

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
		try(FileReader fr=new FileReader("SongPluginLocal.json")){
			JsonArray localfs = JsonParser.parseReader(fr).getAsJsonArray();
		
			JsonObject result = null;
			double min = Integer.MAX_VALUE;
			for (JsonElement je : localfs) {
				JsonObject cur = je.getAsJsonObject();
				String ckw = forceGetJsonString(cur, "title");
				double curm = Utils.compare(rkw, ckw);
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

}
