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
import com.khjxiaogu.MiraiSongPlugin.NetEaseCrypto;

public class NetEaseHQMusicSource extends NetEaseMusicSource {

	public NetEaseHQMusicSource() {
	}

	@Override
	public String queryRealUrl(String id) throws Exception {

		JsonObject main = HttpRequestBuilder.create("music.163.com")
		.url("/weapi/song/enhance/player/url?csrf_token=")
		.contenttype("application/x-www-form-urlencoded")
		.referer("https://music.163.com")
		.ua(NetEaseCrypto.getUserAgent())
		.post()
		.send(NetEaseCrypto.weapiEncryptParam(JsonBuilder.object().add("ids","[" + id + "]").add("br",999000).toString()))
		.readJson();
		if (main.get("code").getAsInt() == 200) {
			JsonArray data = main.get("data").getAsJsonArray();
			JsonObject song = data.get(0).getAsJsonObject();
			if (song.get("code").getAsInt() == 200) {
				return song.get("url").getAsString().trim();
			}
		}
		return null;
	}

	@Override
	public boolean isVisible() {
		return false;
	}
}
