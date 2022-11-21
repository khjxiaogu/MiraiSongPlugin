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

import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.khjxiaogu.MiraiSongPlugin.HttpRequestBuilder;
import com.khjxiaogu.MiraiSongPlugin.JsonBuilder;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.MusicSource;
import com.khjxiaogu.MiraiSongPlugin.NetEaseCrypto;
import com.khjxiaogu.MiraiSongPlugin.Utils;

public class NetEaseRadioSource implements MusicSource {

	public NetEaseRadioSource() {
	}

	public String queryRealUrl(String id) throws Exception {
		return "http://music.163.com/song/media/outer/url?id=" + id + ".mp3";
	}
	protected JsonObject getSlice(String id,int offset,int limit) throws IOException {
		return HttpRequestBuilder.create("music.163.com")
				.url("/weapi/dj/program/byradio?csrf_token=")
				.defUA()
				.contenttype("application/x-www-form-urlencoded")
				.referer("https://music.163.com")
				.ua(NetEaseCrypto.getUserAgent())
				.post()
				.send(NetEaseCrypto.weapiEncryptParam(JsonBuilder.object()
						.add("radioId",id)
						.add("limit",limit)
						.add("offset",offset)
						.add("asc", true)
						.toString()))
				.readJson();
	}
	protected JsonObject getProgramSong(String id) throws IOException {
		JsonObject main=HttpRequestBuilder.create("music.163.com")
		.url("/weapi/dj/program/detail?csrf_token=")
		.defUA()
		.contenttype("application/x-www-form-urlencoded")
		.referer("https://music.163.com")
		.ua(NetEaseCrypto.getUserAgent())
		.post()
		.send(NetEaseCrypto.weapiEncryptParam(JsonBuilder.object().add("id",id).toString()))
		.readJson();
		if (main != null && main.get("code").getAsInt() == 200) {
			return main.get("program").getAsJsonObject();
		}
		return null;
	}
	protected JsonObject getRadioSong(String id, String keyword) throws IOException {
		JsonObject main = getSlice(id, 0, 10);
		int cur=10;
		if (main != null && main.get("code").getAsInt() == 200) {
			JsonArray data = main.get("programs").getAsJsonArray();
			double curmax = Integer.MAX_VALUE;
			JsonObject result = null;
			int count=main.get("count").getAsInt();
			
			if (data.size() <= 1 || keyword == null) {
				if (data.size() == 0)
					return null;
				return data.get(0).getAsJsonObject().get("mainSong").getAsJsonObject();
			}
			
			do{
				for (JsonElement je : data) {
					JsonObject song = je.getAsJsonObject();
					String sn = song.get("name").getAsString();
					double sim = Utils.compare(keyword, sn);
					if (sn.equals(keyword)) {
						return song.get("mainSong").getAsJsonObject();
					}
					if (sim < curmax) {
						curmax = sim;
						result = song;
					}
				}
				 main = getSlice(id,cur, 50);
				 cur+=50;
				 if (main == null|| main.get("code").getAsInt() != 200)
					 break;
				 data = main.get("programs").getAsJsonArray();
				 
				 if (data.size() < 1)
					 break;
			}while(cur<count);
			return result.get("mainSong").getAsJsonObject();

		}
		return null;
	}

	@Override
	public MusicInfo get(String keyword) throws Exception {
		return get(keyword, null);
	}

	public MusicInfo get(String keyword, String songname) throws Exception {


		JsonArray ja=HttpRequestBuilder.create("music.163.com")
		.url("/weapi/cloudsearch/get/web?csrf_token=")
		.ua(NetEaseCrypto.getUserAgent())
		.cookie("appver=1.5.0.75771;")
		.referer("http://music.163.com/")
		.contenttype("application/x-www-form-urlencoded")
		.post()
		.send(NetEaseCrypto.weapiEncryptParam(JsonBuilder.object()
				.add("s",keyword)
				.add("type",1009)
				.add("offset",0)
				.add("limit",3)
				.toString()))
		.readJson()
		.get("result").getAsJsonObject()
		.get("djRadios").getAsJsonArray();

		String murl;
		JsonObject detail = ja.get(0).getAsJsonObject();
		JsonObject jo = getRadioSong(detail.get("id").getAsString(), songname);
		murl = queryRealUrl(jo.get("id").getAsString());
		JsonObject dj = detail.get("dj").getAsJsonObject();
		return new MusicInfo(jo.get("name").getAsString(), dj.get("nickname").getAsString(),
				detail.get("picUrl").getAsString(), murl,
				"http://music.163.com/program?id=" + detail.get("lastProgramId").getAsString(),
				"网易云电台", "", 100495085);
	}

	@Override
	public boolean isVisible() {
		return false;
	}

	@Override
	public MusicInfo getId(String id) throws Exception {
		JsonObject jo = getProgramSong(id);
		String murl = queryRealUrl(jo.get("mainSong").getAsJsonObject().get("id").getAsString());
		JsonObject dj = jo.get("dj").getAsJsonObject();
		return new MusicInfo(jo.get("name").getAsString(), dj.get("nickname").getAsString(),
				jo.get("radio").getAsJsonObject().get("picUrl").getAsString(), murl,
				"http://music.163.com/program?id=" + id,
				"网易云电台", "", 100495085);
	}
}
