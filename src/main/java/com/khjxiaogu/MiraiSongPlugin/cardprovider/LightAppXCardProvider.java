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
package com.khjxiaogu.MiraiSongPlugin.cardprovider;

import com.google.gson.JsonObject;
import com.khjxiaogu.MiraiSongPlugin.MusicCardProvider;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.Utils;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.LightApp;
import net.mamoe.mirai.message.data.Message;

public class LightAppXCardProvider implements MusicCardProvider {

	public LightAppXCardProvider() {
	}

	@Override
	public Message process(MusicInfo mi, Contact ct) throws Exception {
		JsonObject appmsg = new JsonObject();
		appmsg.addProperty("app", "com.tencent.miniapp_01");
		JsonObject cfg = new JsonObject();
		cfg.addProperty("autosize", 0);
		cfg.addProperty("ctime", Utils.getTime() / 1000);
		cfg.addProperty("token", "a95f83e736db2f94d88f9974c09717ad");
		cfg.addProperty("type", "normal");
		cfg.addProperty("forward", 1);
		appmsg.add("config", cfg);
		appmsg.addProperty("view", "music");
		appmsg.addProperty("ver", "1.0.0.19");
		appmsg.addProperty("desc", "音乐");
		appmsg.addProperty("view", "view_8C8E89B49BE609866298ADDFF2DBABA4");
		appmsg.addProperty("prompt", mi.title);
		JsonObject meta = new JsonObject();
		appmsg.add("meta", meta);
		JsonObject music = new JsonObject();
		meta.add("music", music);
		music.addProperty("action", "");
		music.addProperty("android_pkg_name", "");
		music.addProperty("app_type", 1);
		music.addProperty("appid", mi.appid);
		music.addProperty("preview", mi.purl);
		music.addProperty("desc", mi.desc);
		music.addProperty("jumpUrl", mi.jurl);
		music.addProperty("musicUrl", mi.murl);
		music.addProperty("sourceMsgId", "0");
		music.addProperty("source_icon", mi.icon);
		music.addProperty("source_url", "");
		music.addProperty("tag", mi.source);
		music.addProperty("title", mi.title);
		JsonObject detail = new JsonObject();
		meta.add("detail_1", detail);
		detail.addProperty("appid", mi.appid);
		detail.addProperty("desc", mi.desc);
		detail.addProperty("icon", mi.icon);
		detail.addProperty("preview", mi.purl);
		detail.addProperty("qqdocurl", mi.jurl);
		detail.addProperty("scene", 1036);
		detail.addProperty("shareTemplateId", "8C8E89B49BE609866298ADDFF2DBABA4");
		detail.add("shareTemplateData", new JsonObject());
		detail.addProperty("title", mi.title);
		return new LightApp(appmsg.toString()).plus(mi.jurl);
	}

}
