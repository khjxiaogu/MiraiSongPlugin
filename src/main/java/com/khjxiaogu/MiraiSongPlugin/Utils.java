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

public final class Utils {
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

	public static byte[] readAll(File i) {
		try (FileInputStream fis = new FileInputStream(i)) {
			return Utils.readAll(fis);
		} catch (IOException ignored) {
			// TODO Auto-generated catch block
		}
		return new byte[0];
	}

	public static long getTime() {
		return new Date().getTime();
	}

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

	public static String getPlainText(MessageChain msg) {
		PlainText pt = msg.first(PlainText.Key);
		if (pt == null)
			return "";
		return pt.getContent().trim();
	}

	public static String removeLeadings(String leading, String orig) {
		return orig.replace(leading, "").trim();
	}

	public static Contact getRealSender(MessageEvent ev) {
		if (ev instanceof GroupMessageEvent)
			return ((GroupMessageEvent) ev).getGroup();
		return ev.getSender();
	}
	public static void exeCmd(String commandStr) {
		try {
			Process p = Runtime.getRuntime().exec(commandStr);
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
