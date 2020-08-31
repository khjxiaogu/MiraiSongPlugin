package com.khjxiaogu.MiraiSongPlugin.cardprovider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.khjxiaogu.MiraiSongPlugin.MusicCardProvider;
import com.khjxiaogu.MiraiSongPlugin.MusicInfo;
import com.khjxiaogu.MiraiSongPlugin.Utils;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;

public class AmrVoiceProvider implements MusicCardProvider {
	public static File ffmpeg=new File("ffmpeg.exe");
	public AmrVoiceProvider() {
	}

	@Override
	public Message process(MusicInfo mi,Contact ct) {
		HttpURLConnection huc2 = null;
		try {
			huc2 = (HttpURLConnection) new URL(mi.murl).openConnection();

			huc2.setRequestMethod("GET");
			huc2.connect();
		} catch (IOException e) {
			return new PlainText("获取音频失败");
		}
		File f = new File("./temp/", "wv" + System.currentTimeMillis() + ".m4a");
		//File f2 = new File("./temp/", "wv" + System.currentTimeMillis() + ".silk");
		//File ft = new File("./temp/", "wv" + System.currentTimeMillis() + ".pcm");
		File f2=new File("./temp/","wv"+System.currentTimeMillis()+".amr");
		try {
			f.getParentFile().mkdirs();
			OutputStream os = new FileOutputStream(f);
			huc2.getInputStream().transferTo(os);
			os.close();
			Utils.exeCmd('\"'+new File("ffmpeg.exe").getAbsolutePath() + "\" -i \"" + f.getAbsolutePath()
					+ "\" -ab 12.2k -ar 8000 -ac 1 -y " + f2.getAbsolutePath());
			try (FileInputStream fis = new FileInputStream(f2)) {
				if(ct instanceof Group)
					return ((Group) ct).uploadVoice(fis);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			f.delete();
			//ft.delete();
			f2.delete();
		}
		return new PlainText("当前状态不支持音频");
	}

}
