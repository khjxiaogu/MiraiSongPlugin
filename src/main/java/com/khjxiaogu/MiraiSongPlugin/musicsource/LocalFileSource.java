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
		try(FileReader fr=new FileReader("SongPluginLocal.json")){
			JsonArray localfs = JsonParser.parseReader(fr).getAsJsonArray();
		
			JsonObject result = null;
			double min = Integer.MAX_VALUE;
			for (JsonElement je : localfs) {
				JsonObject cur = je.getAsJsonObject();
				String ckw = forceGetJsonString(cur, "title");
				double curm = Utils.compare(keyword, ckw);
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
