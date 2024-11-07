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
import com.khjxiaogu.MiraiSongPlugin.JsonBuilder;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.MusicSource;
import com.khjxiaogu.MiraiSongPlugin.NetEaseCrypto;
import com.khjxiaogu.MiraiSongPlugin.Utils;

public class NetEaseMusicSource implements MusicSource {

	public NetEaseMusicSource() {
	}

	public String queryRealUrl(String id) throws Exception {
		return "http://music.163.com/song/media/outer/url?id=" + id + ".mp3";
	}
	

	@Override
	public MusicInfo get(String keyword) throws Exception {
		String murl;
		JsonObject jom=HttpRequestBuilder.create("music.163.com")
		.url("/weapi/cloudsearch/get/web?csrf_token=")
		.referer("http://music.163.com/")
		.cookie("appver=1.5.0.75771;"+NetEaseCrypto.cookie)
		.contenttype("application/x-www-form-urlencoded")
		.post()
		.send(NetEaseCrypto.weapiEncryptParam(JsonBuilder.object().add("s", keyword).add("type", 1).add("offset", 0).add("limit", 3).toString()))
		.readJson();
		JsonArray ja=jom
		.get("result").getAsJsonObject().get("songs").getAsJsonArray();
		JsonObject jo = ja.get(0).getAsJsonObject();
		murl = queryRealUrl(jo.get("id").getAsString());
		int i = 0;
		while (!Utils.isExistent(murl)) {
			jo = ja.get(++i).getAsJsonObject();
			murl = queryRealUrl(jo.get("id").getAsString());
		}
		return new MusicInfo(jo.get("name").getAsString(),
				jo.get("ar").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString(),
				jo.get("al").getAsJsonObject().get("picUrl").getAsString(), murl,
				"https://y.music.163.com/m/song?id=" + jo.get("id").getAsString(), "网易云音乐", "", 100495085);
	}

	@Override
	public MusicInfo getId(String id) throws Exception {
		String murl;
		JsonObject jo =HttpRequestBuilder.create("music.163.com")
				.url("/weapi/song/detail?csrf_token=")
				.referer("http://music.163.com/")
				.cookie("appver=1.5.0.75771;"+NetEaseCrypto.cookie)
				.contenttype("application/x-www-form-urlencoded")
				.post()
				.send(NetEaseCrypto.weapiEncryptParam(JsonBuilder.object().array("ids").add(id).end().toString()))
				.readJson()
				.get("songs").getAsJsonArray()
				.get(0).getAsJsonObject();
		murl = queryRealUrl(id);
		return new MusicInfo(jo.get("name").getAsString(),
				jo.get("artists").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString(),
				jo.get("album").getAsJsonObject().get("picUrl").getAsString(), murl,
				"https://y.music.163.com/m/song?id=" + jo.get("id").getAsString(), "网易云音乐", "", 100495085);
	}

}
