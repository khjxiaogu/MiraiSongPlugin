package com.khjxiaogu.MiraiSongPlugin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;

public class SongIdRedirectMatcher implements ISongUrlMatcher {
	Pattern mc;
	public SongIdRedirectMatcher(String regex) {
		mc=Pattern.compile(regex);
	}
	@Override
	public Message test(String s,Contact ct) throws Exception {
		Matcher m=mc.matcher(s);
		if(m.find()) {
			String res=HttpRequestBuilder.create(m.group(1)).url(m.group(2)).noRedirect().defUA().get().getRedirect();
			if(res==null||res.isEmpty())return null;
			
			return MiraiSongPlugin.parseExternLinkIgnores(res, ct,this);
		}
		return null;
	}
}
