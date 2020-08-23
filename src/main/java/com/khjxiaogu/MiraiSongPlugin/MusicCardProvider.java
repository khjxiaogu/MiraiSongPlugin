package com.khjxiaogu.MiraiSongPlugin;

import net.mamoe.mirai.message.data.MessageChain;
@FunctionalInterface
public interface MusicCardProvider {
	public MessageChain process(MusicInfo mi);
}
