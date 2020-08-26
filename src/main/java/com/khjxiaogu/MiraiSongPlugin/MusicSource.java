package com.khjxiaogu.MiraiSongPlugin;

// TODO: Auto-generated Javadoc

/**
 * Interface MusicSource.
 * 音乐来源接口
 * @author khjxiaogu
 * file: MusicSource.java
 * time: 2020年8月26日
 */
@FunctionalInterface
public interface MusicSource{
	
	/**
	 * 搜索对应关键词并返回音乐信息.<br>
	 * 返回音乐信息不能为null。
	 * @param keyword 关键词
	 * @return return 返回音乐信息数据类
	 * @throws Exception 如果发生异常或者找不到音乐，都抛出异常。
	 */
	public MusicInfo get(String keyword) throws Exception;
}