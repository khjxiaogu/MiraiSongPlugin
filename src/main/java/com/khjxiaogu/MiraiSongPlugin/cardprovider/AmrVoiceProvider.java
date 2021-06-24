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
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.utils.OverFileSizeMaxException;

public class AmrVoiceProvider implements MusicCardProvider {
	public static File ffmpeg = new File("ffmpeg.exe");
	public static boolean autoSize = false;
	public static boolean wideBrand = false;
	private final static String[] brs = new String[] { "23.05k", "19.85k", "18.25k", "15.85k", "14.25k", "12.65k",
			"8.85k", "6.6k" };

	public AmrVoiceProvider() {
	}

	@Override
	public Message process(MusicInfo mi, Contact ct) {
		URLConnection uc = null;
		try {
			uc =new URL(mi.murl).openConnection();
			if(mi.properties!=null)
				for(Map.Entry<String,String> me:mi.properties.entrySet())
					uc.addRequestProperty(me.getKey(),me.getValue());
			uc.connect();
		} catch (IOException e) {
			e.printStackTrace();
			return new PlainText("获取音频失败");
		}
		File f = new File("./temp/", "wv" + System.currentTimeMillis() + ".m4a");
		// File f2 = new File("./temp/", "wv" + System.currentTimeMillis() + ".silk");
		// File ft = new File("./temp/", "wv" + System.currentTimeMillis() + ".pcm");
		File f2 = new File("./temp/", "wv" + System.currentTimeMillis() + ".amr");
		try {
			f.getParentFile().mkdirs();
			OutputStream os = new FileOutputStream(f);
			os.write(Utils.readAll(uc.getInputStream()));
			os.close();
			if(wideBrand) {
				Utils.exeCmd('\"' + ffmpeg.getAbsolutePath() + "\" -i \"" + f.getAbsolutePath()
						+ "\" -ab 23.85k -ar 16000 -ac 1 -acodec libamr_wb "+(autoSize?"":"-fs 1000000")+" -y " + f2.getAbsolutePath());
				int i = 0;
				do {
					try {
						if (ct instanceof Group)
							try (FileInputStream fis = new FileInputStream(f2)) {
								return ((Group) ct).uploadVoice(fis);
							}
					} catch (OverFileSizeMaxException ofse) {
						Utils.exeCmd('\"' + ffmpeg.getAbsolutePath() + "\" -i \"" + f.getAbsolutePath()
								+ "\" -ab " + brs[i] + " -ar 16000 -ac 1 -acodec libamr_wb -y " + f2.getAbsolutePath());
						i++;
					}
				} while (autoSize);
			}
			else {
				Utils.exeCmd('\"'+ffmpeg.getAbsolutePath() + "\" -i \"" + f.getAbsolutePath()
				+ "\" -ab 12.2k -ar 8000 -ac 1 -fs 1000000 -y " + f2.getAbsolutePath());
				try (FileInputStream fis = new FileInputStream(f2)) {
					return ((Group) ct).uploadVoice(fis);
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
