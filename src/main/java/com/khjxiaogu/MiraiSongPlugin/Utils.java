package com.khjxiaogu.MiraiSongPlugin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.PlainText;

// TODO: Auto-generated Javadoc
/**
 * Class Utils.
 * 工具类
 * @author khjxiaogu
 * file: Utils.java
 * time: 2020年8月26日
 */
public final class Utils {
	
	/**
	 * Read all content from input stream.<br>
	 * 从数据流读取全部数据
	 * @param i the input stream<br>数据流
	 * @return return all read data <br>返回读入的所有数据
	 * @throws IOException Signals that an I/O exception has occurred.<br>发生IO错误
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
	 * @param i the file<br>文件
	  @return return all read data <br>返回读入的所有数据
	 */
	public static byte[] readAll(File i) {
		try (FileInputStream fis = new FileInputStream(i)) {
			return Utils.readAll(fis);
		} catch (IOException ignored) {
			// TODO Auto-generated catch block
		}
		return new byte[0];
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
	 * @param hash the byte array<br>字节串。
	 * @return return hex string<br>返回十六进制字符串。
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
	 * @param msg the message to get<br>需要获取文本的消息
	 * @return first trimmed plain text<br>第一条文本，去除首尾空格
	 */
	public static String getPlainText(MessageChain msg) {
		PlainText pt = msg.first(PlainText.Key);
		if (pt == null)
			return "";
		return pt.getContent().trim();
	}

	/**
	 * Removes specific leadings from a string.<br>
	 * 替换掉目标字符串的指定开头
	 * @param leading the specific leading<br>指定的开头
	 * @param orig the original string<br>源字符串
	 * @return return result <br>返回去除的结果
	 */
	public static String removeLeadings(String leading, String orig) {
		if(orig.startsWith(leading))
			return orig.substring(leading.length()).replace(leading, "").trim();
		return orig;
	}

	/**
	 * Gets the sender to send message.<br>
	 * 获取应该发信息的联系人.
	 *
	 * @param ev the message event<br>消息事件
	 * @return sender to send message to<br>应该发信息的联系人
	 */
	public static Contact getRealSender(MessageEvent ev) {
		if (ev instanceof GroupMessageEvent)
			return ((GroupMessageEvent) ev).getGroup();
		return ev.getSender();
	}
	
	/**
	 * Execute command,wait until finished.<br>
	 * 执行操作平台命令，等待完成。
	 * @param commandStr the command string<br>命令字符串
	 */
	public static void exeCmd(String commandStr) {
		try {
			Process p = Runtime.getRuntime().exec(commandStr);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
