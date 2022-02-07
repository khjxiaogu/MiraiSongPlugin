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

import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.MusicSource;
import com.khjxiaogu.MiraiSongPlugin.Utils;

public class XimalayaSource implements MusicSource {

	@Override
	public MusicInfo get(String keyword) throws Exception {
		HttpURLConnection huc = (HttpURLConnection) new URL(
				"https://www.ximalaya.com/revision/search/main?page=1&spellchecker=true&paidFilter=true&condition=relation&rows=10&device=iPhone&core=track&kw="
						+ keyword).openConnection();
		huc.setRequestMethod("GET");
		huc.setRequestProperty("Host", "www.ximalaya.com");
		huc.connect();
		
		JsonArray ja = JsonParser
				.parseString(new String(Utils.readAll(huc.getInputStream()), "UTF-8"))
				.getAsJsonObject().get("data").getAsJsonObject().get("track").getAsJsonObject().get("docs").getAsJsonArray();
		JsonObject song;
		int i=-1;
		do {
			song=ja.get(++i).getAsJsonObject();
		}while(song.get("isPaid").getAsBoolean());
		HttpURLConnection huc2 = (HttpURLConnection) new URL(
				"https://www.ximalaya.com/tracks/"+song.get("id").getAsString()+".json").openConnection();
		huc2.setRequestMethod("GET");
		huc2.setRequestProperty("Host", "www.ximalaya.com");
		huc2.connect();
		String out=new String(Utils.readAll(huc2.getInputStream()), "UTF-8");
		String path=JsonParser.parseString(out).getAsJsonObject().get("play_path").getAsString();
		
		return new MusicInfo(song.get("title").getAsString(),song.get("nickname").getAsString(),"https:"+song.get("coverPath").getAsString(),path,"https://www.ximalaya.com"+song.get("trackUrl").getAsString(),"喜马拉雅");
	}

}
