package com.khjxiaogu.MiraiSongPlugin;
@FunctionalInterface
public interface MusicSource{
	public MusicInfo get(String keyword) throws Exception;
}