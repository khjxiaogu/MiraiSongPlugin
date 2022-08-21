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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

import net.mamoe.mirai.contact.AudioSupported;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.Audio;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.utils.ExternalResource;

// TODO: Auto-generated Javadoc
/**
 * 工具类
 * 
 * @author khjxiaogu
 *         file: Utils.java
 *         time: 2020年8月26日
 */
public final class Utils {
	static boolean verbose = true;
	public static At getAt(MessageChain msg) {
		for(Message m:msg) {
			if(m instanceof At)
				return (At) m;
		}
		return null;
	}
	/**
	 * Read all content from input stream.<br>
	 * 从数据流读取全部数据
	 * 
	 * @param i the input stream<br>
	 *          数据流
	 * @return return all read data <br>
	 *         返回读入的所有数据
	 * @throws IOException Signals that an I/O exception has occurred.<br>
	 *                     发生IO错误
	 */
	public static byte[] readAll(InputStream i) throws IOException {
		ByteArrayOutputStream ba = new ByteArrayOutputStream(16384);
		int nRead;
		byte[] data = new byte[4096];

		try {
			while ((nRead = i.read(data, 0, data.length)) != -1) {
				ba.write(data, 0, nRead);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw e;
		}

		return ba.toByteArray();
	}

	/**
	 * Read all content from File.<br>
	 * 从文件读取全部数据
	 * 
	 * @param i the file<br>
	 *          文件
	 * @return return all read data <br>
	 *         返回读入的所有数据
	 */
	public static byte[] readAll(File i) {
		try (FileInputStream fis = new FileInputStream(i)) {
			return Utils.readAll(fis);
		} catch (IOException ignored) {
			// TODO Auto-generated catch block
		}
		return new byte[0];
	}
	public static String urlEncode(String text) {
		try {
			return URLEncoder.encode(text,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return URLEncoder.encode(text);
		}
	}
	/**
	 * Gets current time.<br>
	 * 获取当前时间.
	 *
	 * @return time<br>
	 */
	public static long getTime() {
		return new Date().getTime();
	}

	/**
	 * byte array to hex string.<br>
	 * 字节串转换为十六进制字符串。
	 * 
	 * @param hash the byte array<br>
	 *             字节串。
	 * @return return hex string<br>
	 *         返回十六进制字符串。
	 */
	public static String bytesToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	/**
	 * Gets the first plain text of the message.<br>
	 * 获取消息中的第一条文本.
	 *
	 * @param msg the message to get<br>
	 *            需要获取文本的消息
	 * @return first trimmed plain text<br>
	 *         第一条文本，去除首尾空格
	 */
	public static String getPlainText(MessageChain msg) {
		return msg.contentToString().trim();
	}

	/**
	 * Removes specific leadings from a string.<br>
	 * 替换掉目标字符串的指定开头
	 * 
	 * @param leading the specific leading<br>
	 *                指定的开头
	 * @param orig    the original string<br>
	 *                源字符串
	 * @return return result <br>
	 *         返回去除的结果
	 */
	public static String removeLeadings(String leading, String orig) {
		if (orig.startsWith(leading))
			return orig.substring(leading.length()).replace(leading, "").trim();
		return orig;
	}

	/**
	 * Gets the sender to send message.<br>
	 * 获取应该发信息的联系人.
	 *
	 * @param ev the message event<br>
	 *           消息事件
	 * @return sender to send message to<br>
	 *         应该发信息的联系人
	 */
	public static Contact getRealSender(MessageEvent ev) {
		if (ev instanceof GroupMessageEvent)
			return ((GroupMessageEvent) ev).getGroup();
		return ev.getSender();
	}

	/**
	 * Execute command,wait until finished.<br>
	 * 执行操作平台命令，等待完成。
	 * 
	 * @param commandStr the command string<br>
	 *                   命令字符串
	 */
	public static void exeCmd(String... commandStr) {
		try {
			ProcessBuilder pb = new ProcessBuilder(commandStr);
			// Process p = Runtime.getRuntime().exec(commandStr);
			if (verbose)
				pb.inheritIO();
			pb.start().waitFor();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Execute command,wait until finished.<br>
	 * 执行操作平台命令，等待完成。
	 * 
	 * @param commandStr the command string<br>
	 *                   命令字符串
	 */
	public static void exeCmd(String commandStr) {
		try {
			if (verbose)
				System.out.println("executing " + commandStr);
			Process p = Runtime.getRuntime().exec(commandStr);
			transferTo(p.getInputStream(), System.out);
			p.waitFor();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static long transferTo(InputStream in, OutputStream out) throws IOException {
		Objects.requireNonNull(out, "out");
		long transferred = 0;
		byte[] buffer = new byte[2048];
		int read;
		while ((read = in.read(buffer, 0, 2048)) >= 0) {
			out.write(buffer, 0, read);
			transferred += read;
		}
		return transferred;
	}

	public static boolean isExistent(String urlstr) throws IOException {
		try {
			URL url = new URL(urlstr);
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			huc.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");
			huc.setRequestMethod("HEAD");
			huc.connect();
			return huc.getResponseCode() == 200;
		} catch (Exception ex) {
			
			return false;
		}
	}

	public static InputStream getFromHttp(String url) throws IOException {
		try {
			HttpURLConnection huc2 = (HttpURLConnection) new URL(url).openConnection();

			huc2.setRequestMethod("GET");
			huc2.connect();
			return huc2.getInputStream();
		} catch (IOException e) {
			throw e;
		}
	}
	public static String fetchHttp(String url) throws IOException {
		try {
			HttpURLConnection huc2 = (HttpURLConnection) new URL(url).openConnection();

			huc2.setRequestMethod("GET");
			huc2.connect();
			return new String(Utils.readAll(huc2.getInputStream()),StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw e;
		}
	}
	public static Audio uploadVoice(ExternalResource ex,Contact ct) {
		if(ct instanceof AudioSupported) {
			return ((AudioSupported) ct).uploadAudio(ex);
		}
		return null;
	}
	static class Pair {
		int first;
		int second;

		public Pair(int first, int second) {
			this.first = first;
			this.second = second;
		}
	}
	/*
	 * String Compare method by alphagem
	 * */
	public static double compare(String s, String t) {
		int n = s.length();
		int m = t.length();
		boolean[] match = new boolean[m+n+2];
		int[][] cost = new int[s.length()+1][t.length()+1];
		Pair[][] from = new Pair[s.length()+1][t.length()+1];
		int count = 0;
		for (int i = 0; i <= n; ++i)
			for (int j = 0; j <= m; ++j)
				if (i != 0 || j != 0) {
					int best_cost = 999;
					Pair best_from = null;
					if (i > 0 && j > 0 && s.charAt(i-1) == t.charAt(j-1) && cost[i - 1][j - 1] < best_cost) {
						best_cost = cost[i - 1][j - 1];
						best_from = new Pair(i - 1, j - 1);
					}
					if (i > 0 && cost[i - 1][j] + 1 < best_cost) {
						best_cost = cost[i - 1][j] + 1;
						best_from = new Pair(i - 1, j);
					}
					if (j > 0 && cost[i][j - 1] + 1 < best_cost) {
						best_cost = cost[i][j - 1] + 1;
						best_from = new Pair(i, j - 1);
					}
					cost[i][j] = best_cost;
					from[i][j] = best_from;
				}
		for (int i = n, j = m, ii, jj; i != 0 || j != 0; i = ii, j = jj) {
			ii = from[i][j].first;
			jj = from[i][j].second;
			match[count++] = (i > ii && j > jj);
		}
		double ans = 0, total = count;
		for (int i = 0; i < count && !match[i]; ++i)
			total -= 0.5;
		for (int i = 0, tmp = 0; i <= count; ++i) {
			if (match[i])
				++tmp;
			else {
				ans += (1D * tmp / total) * (1D * tmp / total);
				tmp = 0;
			}
		}
		return 1-ans;
	}
}
