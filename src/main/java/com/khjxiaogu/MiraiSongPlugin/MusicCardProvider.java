package com.khjxiaogu.MiraiSongPlugin;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;

/**
 * 生成音乐卡片的接口
 * @author khjxiaogu
 * file: MusicCardProvider.java
 * time: 2020年8月26日
 */
@FunctionalInterface
public interface MusicCardProvider {
	
	/**
	 * 处理音乐信息并生成卡片.<br>
	 *
	 * @param mi 音乐信息
	 * @param ct 请求该信息的联系人，可能为群或者好友，用于上传图片等用，不要直接给他们发信息，应该通过返回值
	 * @return 返回要回传的音乐卡片信息
	 * @throws Exception 发生任意异常 
	 */
	public Message process(MusicInfo mi,Contact ct) throws Exception;
}
