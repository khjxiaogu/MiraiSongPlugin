package com.khjxiaogu.MiraiSongPlugin.cardprovider;

import com.khjxiaogu.MiraiSongPlugin.MusicCardProvider;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.SimpleServiceMessage;

public class XMLCardProvider implements MusicCardProvider {

	public XMLCardProvider() {
	}

	@Override
	public MessageChain process(MusicInfo mi, Contact ct) {
		StringBuilder xmb = new StringBuilder("<msg serviceID=\"2\" templateID=\"1\" action=\"web\" brief=\"[音乐]")
				.append(mi.title).append("\" sourceMsgId=\"0\" url=\"").append(mi.jurl.replaceAll("\\&", "&amp;"))
				.append("\" flag=\"0\" adverSign=\"0\" multiMsgFlag=\"0\">\r\n<item layout=\"2\">\r\n")
				.append("<audio cover=\"").append(mi.purl.replaceAll("\\&", "&amp;")).append("\" src=\"")
				.append(mi.murl.replaceAll("\\&", "&amp;")).append("\"/>\r\n").append("<title>").append(mi.title)
				.append("</title>\r\n<summary>").append(mi.desc).append("</summary>\r\n</item>\r\n<source name=\"")
				.append(mi.source).append("\" icon=\"").append(mi.icon)
				.append("\" url=\"\" action=\"\" a_actionData=\"\" i_actionData=\"\" appid=\"").append(mi.appid)
				.append("\"/>\r\n</msg>");
		Message msg = new SimpleServiceMessage(2, xmb.toString());
		return msg.plus(mi.jurl);
	}

}
