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
package com.khjxiaogu.MiraiSongPlugin.cardprovider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import com.khjxiaogu.MiraiSongPlugin.MusicCardProvider;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.Utils;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.ExternalResource;
import net.mamoe.mirai.utils.OverFileSizeMaxException;

public class AmrVoiceProvider implements MusicCardProvider {
	public static String ffmpeg = "ffmpeg";
	public static boolean autoSize = false;
	public static boolean wideBrand = false;
	private final static String[] brs = new String[] { "23.05k", "19.85k", "18.25k", "15.85k", "14.25k", "12.65k",
			"8.85k", "6.6k" };
	public static String customCommand;

	public AmrVoiceProvider() {
	}

	@Override
	public Message process(MusicInfo mi, Contact ct) {
		URLConnection huc2 = null;
		try {
			huc2 =  new URL(mi.murl).openConnection();
			if (mi.properties != null)
				for (Map.Entry<String, String> me : mi.properties.entrySet())
					huc2.addRequestProperty(me.getKey(), me.getValue());
			huc2.connect();
		} catch (IOException e) {
			return new PlainText("获取音频失败");
		}
		File f = new File("temp/", "wv" + System.currentTimeMillis() + ".m4a");
		// File f2 = new File("./temp/", "wv" + System.currentTimeMillis() + ".silk");
		// File ft = new File("./temp/", "wv" + System.currentTimeMillis() + ".pcm");
		File f2 = new File("temp/", "wv" + System.currentTimeMillis() + ".amr");
		try {
			f.getParentFile().mkdirs();
			OutputStream os = new FileOutputStream(f);
			os.write(Utils.readAll(huc2.getInputStream()));
			os.flush();
			os.close();
			while(!new File(f.getAbsolutePath()).exists());
			if (customCommand != null) {
				Utils.exeCmd(customCommand.replace("%input%", f.getAbsolutePath()).replace("%output%",
						f2.getAbsolutePath()));
				try (FileInputStream fis = new FileInputStream(f2);ExternalResource ex=ExternalResource.create(fis)) {
					return Utils.uploadVoice(ex,ct);
				}
			} else if (wideBrand) {
				if (autoSize)
					Utils.exeCmd(ffmpeg, "-i", f.getAbsolutePath(), "-ab", "23.85k", "-ar", "16000",
							"-ac", "1", "-acodec", "amr_wb", "-fs", "1000000", "-y", f2.getAbsolutePath());
				else
					Utils.exeCmd(ffmpeg, "-i", f.getAbsolutePath(), "-ab", "23.85k", "-ar", "16000",
							"-ac", "1", "-acodec", "amr_wb", "-y", f2.getAbsolutePath());
				int i = 0;
				do {
					try {
							try (FileInputStream fis = new FileInputStream(f2);ExternalResource ex=ExternalResource.create(fis)) {
								return Utils.uploadVoice(ex,ct);
							}
					} catch (OverFileSizeMaxException ofse) {
						Utils.exeCmd(ffmpeg, "-i", f.getAbsolutePath(), "-ab", brs[i], "-ar", "16000",
								"-ac", "1", "-acodec", "amr_wb", "-y", f2.getAbsolutePath());
						i++;
					}
				} while (autoSize);
			} else {
				Utils.exeCmd(ffmpeg, "-i", f.getAbsolutePath(), "-ab", "12.2k", "-ar", "8000", "-ac",
						"1", "-fs", "1000000", "-y", f2.getAbsolutePath());
				
				try (FileInputStream fis = new FileInputStream(f2);ExternalResource ex=ExternalResource.create(fis)) {
					return Utils.uploadVoice(ex,ct);
				}
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			f.delete();
			// ft.delete();
			f2.delete();
		}
		return new PlainText("当前状态不支持音频");
	}

}
