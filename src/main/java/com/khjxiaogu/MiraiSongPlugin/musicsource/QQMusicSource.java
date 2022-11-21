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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.khjxiaogu.MiraiSongPlugin.HttpRequestBuilder;
import com.khjxiaogu.MiraiSongPlugin.JsonBuilder;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.MusicSource;
import com.khjxiaogu.MiraiSongPlugin.Utils;

public class QQMusicSource implements MusicSource {

	public QQMusicSource() {
	}

	public String queryRealUrl(String songmid) {
		try {
			JsonObject out =HttpRequestBuilder.create("u.y.qq.com")
			.url("/cgi-bin/musicu.fcg?format=json&data=%7B%22req_0%22%3A%7B%22module%22%3A%22vkey.GetVkeyServer%22%2C%22method%22%3A%22CgiGetVkey%22%2C%22param%22%3A%7B%22guid%22%3A%22358840384%22%2C%22songmid%22%3A%5B%22")
			.url(songmid)
			.url(
					"%22%5D%2C%22songtype%22%3A%5B0%5D%2C%22uin%22%3A%221443481947%22%2C%22loginflag%22%3A1%2C%22platform%22%3A%2220%22%7D%7D%2C%22comm%22%3A%7B%22uin%22%3A%2218585073516%22%2C%22format%22%3A%22json%22%2C%22ct%22%3A24%2C%22cv%22%3A0%7D%7D")
			.defUA()
			.get()
			.readJson();
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

	public String fetchMid(String songid) {
		try {
			JsonObject out =HttpRequestBuilder.create("c.y.qq.com")
			.url("/v8/fcg-bin/fcg_play_single_song.fcg?tpl=yqq_song_detail&format=json&songid=")
			.url(songid)
			.defUA()
			.get()
			.readJson();
			
			return out.get("data").getAsJsonArray().get(0).getAsJsonObject().get("mid").getAsString();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public JsonObject querySongDetail(String songmid) {
		try {
			JsonObject out =HttpRequestBuilder.create("u.y.qq.com")
			.url("/cgi-bin/musicu.fcg?format=json&data=%7B%22req_0%22%3A%7B%22module%22%3A%22music.pf_song_detail_svr%22%2C%22method%22%3A%22get_song_detail_yqq%22%2C%22param%22%3A%7B%22song_mid%22%3A%22")
			.url(songmid)
			.url("%22%7D%7D%2C%22comm%22%3A%7B%22uin%22%3A%221905222%22%2C%22format%22%3A%22json%22%2C%22ct%22%3A24%2C%22cv%22%3A0%7D%7D")
			.defUA()
			.get()
			.readJson();
			if (out.get("code").getAsInt() != 0) {
				return null;
			}
			return out.get("req_0").getAsJsonObject().get("data").getAsJsonObject().get("track_info").getAsJsonObject();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public MusicInfo get(String keyword) throws Exception {
		

		JsonArray ss =HttpRequestBuilder.create("u.y.qq.com")
		.url("/cgi-bin/musicu.fcg")
		.defUA()
		.referer("https://y.qq.com")
		.post()
		.send(JsonBuilder.object()
				.object("music.search.SearchCgiService")
					.add("module", "music.search.SearchCgiService")
					.add("method", "DoSearchForQQMusicDesktop")
					.object("param")
						.add("query", keyword)
						.add("num_per_page", 3)
						.add("search_type", 0)
						.add("page_num", 1)
					.end()
				.end()
			.toString())
		.readJson()
		.getAsJsonObject()
		.get("music.search.SearchCgiService").getAsJsonObject()
		.get("data").getAsJsonObject()
		.get("body").getAsJsonObject()
		.get("song").getAsJsonObject()
		.get("list").getAsJsonArray();
		JsonObject song = ss.get(0).getAsJsonObject();// .data.song.list
		String mid = song.get("mid").getAsString();
		String musicURL = queryRealUrl(mid);
		int i = 0;
		while (!Utils.isExistent(musicURL)) {
			song = ss.get(++i).getAsJsonObject();
			mid = song.get("mid").getAsString();
			musicURL = queryRealUrl(mid);
		}
		String desc;
		try {
			JsonArray singers = song.get("singer").getAsJsonArray();
			StringBuilder sgs = new StringBuilder();
			for (JsonElement je : singers) {
				sgs.append(je.getAsJsonObject().get("name").getAsString());
				sgs.append(";");
			}
			sgs.deleteCharAt(sgs.length() - 1);
			desc = sgs.toString();
		} catch (Exception e) {
			desc = song.get("album").getAsJsonObject().get("name").getAsString();
		}

		if (musicURL == null) 
			throw new FileNotFoundException();
		
		return new MusicInfo(song.get("title").getAsString(), desc,
				"http://y.gtimg.cn/music/photo_new/T002R300x300M000"
						+ song.get("album").getAsJsonObject().get("mid").getAsString() + ".jpg",
				musicURL, "https://i.y.qq.com/v8/playsong.html?_wv=1&songid=" + song.get("id").getAsString()
						+ "&source=qqshare&ADTAG=qqshare",
				"QQ音乐", "https://url.cn/PwqZ4Jpi", 100497308);
	}

	@Override
	public MusicInfo getId(String id) throws Exception {
		String mid=null;
		try {
			long l=Long.parseLong(id);
			if(id.length()>11)
				throw new RuntimeException();
			mid=fetchMid(id);
		}catch(RuntimeException ex) {
			mid=id;
		}
		if(mid==null)throw new IllegalArgumentException("错误的歌曲ID或MID");
		JsonObject song = querySongDetail(mid);
		String desc;
		JsonArray singers = song.get("singer").getAsJsonArray();
		StringBuilder sgs = new StringBuilder();
		for (JsonElement je : singers) {
			sgs.append(je.getAsJsonObject().get("name").getAsString());
			sgs.append(";");
		}
		sgs.deleteCharAt(sgs.length() - 1);
		desc = sgs.toString();
		return new MusicInfo(song.get("title").getAsString(), desc,
				"http://y.gtimg.cn/music/photo_new/T002R300x300M000"
						+ song.get("album").getAsJsonObject().get("mid").getAsString() + ".jpg",
				queryRealUrl(song.get("mid").getAsString()), "https://i.y.qq.com/v8/playsong.html?_wv=1&songid="
						+ song.get("id").getAsString() + "&source=qqshare&ADTAG=qqshare",
				"QQ音乐", "https://url.cn/PwqZ4Jpi", 100497308);
	}

}
