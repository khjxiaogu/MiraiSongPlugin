package com.khjxiaogu.MiraiSongPlugin.cardprovider;

import com.khjxiaogu.MiraiSongPlugin.MusicCardProvider;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.ServiceMessage;

public class XMLCardProvider implements MusicCardProvider {

	public XMLCardProvider() {
	}

	@Override
	public MessageChain process(MusicInfo mi, Contact ct) {
		StringBuilder xmb = new StringBuilder("<msg serviceID=\"2\" templateID=\"1\" action=\"web\" brief=\"[音乐]")
				.append(escapeXmlTag(mi.title))
				.append("\" sourceMsgId=\"0\" url=\"")
				.append(escapeXmlTag(mi.jurl))
				.append("\" flag=\"0\" adverSign=\"0\" multiMsgFlag=\"0\">\r\n<item layout=\"2\">\r\n")
				.append("<audio cover=\"").append(escapeXmlTag(mi.purl))
				.append("\" src=\"").append(escapeXmlTag(mi.murl)).append("\"/>\r\n")
				.append("<title>").append(escapeXmlContent(mi.title))
				.append("</title>\r\n<summary>")
				.append(escapeXmlContent(mi.desc))
				.append("</summary>\r\n</item>\r\n<source name=\"")
				.append(escapeXmlTag(mi.source))
				.append("\" icon=\"").append(escapeXmlTag(mi.icon))
				.append("\" url=\"\" action=\"\" a_actionData=\"\" i_actionData=\"\" appid=\"").append(mi.appid)
				.append("\"/>\r\n</msg>");
		Message msg = new ServiceMessage(2, xmb.toString());
		return msg.plus(mi.jurl);
	}
	public String escapeXmlContent(String org) {
		return org.replaceAll("<","&lt;").replaceAll(">", "&gt;");
	}
	public String escapeXmlTag(String org) {
		return org.replaceAll("\\&", "&amp;").replaceAll("\"", "&quot;").replaceAll("'","&apos;").replaceAll("<","&lt;").replaceAll(">", "&gt;");
	}
}
