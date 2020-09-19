package com.khjxiaogu.MiraiSongPlugin.cardprovider;

import java.net.MalformedURLException;
import java.net.URL;

import com.khjxiaogu.MiraiSongPlugin.MusicCardProvider;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.utils.OverFileSizeMaxException;

public class PlainMusicInfoProvider implements MusicCardProvider {

	public PlainMusicInfoProvider() {
	}

	@Override
	public Message process(MusicInfo mi, Contact ct) throws OverFileSizeMaxException, MalformedURLException {
		Image im = ct.uploadImage(new URL(mi.purl));
		StringBuilder tsb = new StringBuilder().append("歌名：").append(mi.title).append("\n作者：").append(mi.desc)
				.append("\n封面：").append(mi.purl).append("\n外链：").append(mi.murl).append("\n链接：").append(mi.jurl)
				.append("\n来自：").append(mi.source);
		return im.plus(tsb.toString());
	}

}
