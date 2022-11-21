/**
 * Mirai Song Plugin
 * Copyright (C) 2021  khjxiaogu
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.khjxiaogu.MiraiSongPlugin;

// TODO: Auto-generated Javadoc

/**
 * 音乐来源接口.
 *
 * @author khjxiaogu
 *         file: MusicSource.java
 *         time: 2020年8月26日
 */
public interface MusicSource {

	/**
	 * 搜索对应关键词并返回音乐信息.<br>
	 * 返回音乐信息不能为null。
	 * 
	 * @param keyword 关键词
	 * @return return 返回音乐信息数据类
	 * @throws Exception 如果发生异常或者找不到音乐，都抛出异常。
	 */
	public MusicInfo get(String keyword) throws Exception;
	/**
	 * 搜索对应音乐ID并返回音乐信息.<br>
	 * 返回音乐信息不能为null。
	 * 
	 * @param keyword 音乐
	 * @return return 返回音乐信息数据类
	 * @throws Exception 如果发生异常或者找不到音乐，都抛出异常。
	 */
	public MusicInfo getId(String id) throws Exception;

	/**
	 * 返回是否对全部搜索可见<br>
	 *
	 * @return 如果是全部搜索可以搜索本来源，返回true.
	 */
	public default boolean isVisible() {
		return true;
	};
}