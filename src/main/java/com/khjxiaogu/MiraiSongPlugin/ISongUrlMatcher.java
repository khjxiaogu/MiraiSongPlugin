package com.khjxiaogu.MiraiSongPlugin;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;

public interface ISongUrlMatcher {

	Message test(String s, Contact ct) throws Exception;

}