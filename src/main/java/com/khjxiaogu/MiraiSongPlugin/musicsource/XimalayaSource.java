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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.khjxiaogu.MiraiSongPlugin.HttpRequestBuilder;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.MusicSource;
import com.khjxiaogu.MiraiSongPlugin.Utils;

public class XimalayaSource implements MusicSource {

	@Override
	public MusicInfo get(String keyword) throws Exception {
		JsonArray ja =HttpRequestBuilder.create("www.ximalaya.com")
		.url("/revision/search/main?page=1&spellchecker=true&paidFilter=true&condition=relation&rows=10&device=iPhone&core=track&kw=")
		.url(Utils.urlEncode(keyword))

		.get()
		.readJson()
		.get("data").getAsJsonObject()
		.get("track").getAsJsonObject()
		.get("docs").getAsJsonArray();
		JsonObject song;
		int i=-1;
		do {
			song=ja.get(++i).getAsJsonObject();
		}while(song.get("isPaid").getAsBoolean());
		String path=HttpRequestBuilder.create("www.ximalaya.com")
		.url("/tracks/")
		.url(song.get("id").getAsString())
		.url(".json")
		.get()
		.readJson().get("play_path").getAsString();
		
		return new MusicInfo(song.get("title").getAsString(),song.get("nickname").getAsString(),"https:"+song.get("coverPath").getAsString(),path,"https://www.ximalaya.com"+song.get("trackUrl").getAsString(),"喜马拉雅");
	}

	@Override
	public MusicInfo getId(String id) throws Exception {
		JsonObject out=HttpRequestBuilder.create("www.ximalaya.com")
				.url("/tracks/")
				.url(id)
				.url(".json")
				.get()
				.readJson();
		return new MusicInfo(out.get("title").getAsString(),
				String.valueOf(out.get("nickname")),
				out.get("cover_url").getAsString(),
				out.get("play_path").getAsString(),
				"https://www.ximalaya.com/sound/"+id,
				"喜马拉雅");
	}

}
