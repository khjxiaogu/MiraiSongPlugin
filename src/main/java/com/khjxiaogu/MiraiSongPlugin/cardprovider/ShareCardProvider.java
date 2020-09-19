package com.khjxiaogu.MiraiSongPlugin.cardprovider;

import com.khjxiaogu.MiraiSongPlugin.MusicCardProvider;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.RichMessage;

public class ShareCardProvider implements MusicCardProvider {

	public ShareCardProvider() {
	}

	@Override
	public Message process(MusicInfo mi, Contact ct) {
		return RichMessage.Templates.share(mi.jurl.replaceAll("\\&", "&amp;"), mi.title, mi.desc,
				mi.purl.replaceAll("\\&", "&amp;"));
	}

}
