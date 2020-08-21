package com.khjxiaogu.MusicToVoice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import net.mamoe.mirai.console.plugins.PluginBase;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.LightApp;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.ServiceMessage;

public class MusicToVoice extends PluginBase{
	private Executor exec=Executors.newFixedThreadPool(8);
	public String queryMusic(String songmid) {
		try {
			StringBuilder urlsb = new StringBuilder(
					"https://u.y.qq.com/cgi-bin/musicu.fcg?format=json&data=%7B%22req_0%22%3A%7B%22module%22%3A%22vkey.GetVkeyServer%22%2C%22method%22%3A%22CgiGetVkey%22%2C%22param%22%3A%7B%22guid%22%3A%22358840384%22%2C%22songmid%22%3A%5B%22");
			urlsb.append(songmid);
			urlsb.append(
					"%22%5D%2C%22songtype%22%3A%5B0%5D%2C%22uin%22%3A%221443481947%22%2C%22loginflag%22%3A1%2C%22platform%22%3A%2220%22%7D%7D%2C%22comm%22%3A%7B%22uin%22%3A%2218585073516%22%2C%22format%22%3A%22json%22%2C%22ct%22%3A24%2C%22cv%22%3A0%7D%7D");
			URL u = new URL(urlsb.toString());
			HttpURLConnection conn = (HttpURLConnection) u.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Host", "u.y.qq.com");
			conn.setRequestProperty("User-Agent",
					"Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
			conn.connect();
			byte[] bs = Utils.readAll(conn.getInputStream());

			JsonObject out = JsonParser.parseString(new String(bs, "UTF-8")).getAsJsonObject();
			System.out.println(out);
			if (out.get("code").getAsInt() != 0) {
				return null;
			}
			StringBuilder sb = new StringBuilder(out.get("req_0").getAsJsonObject().get("data").getAsJsonObject()
					.get("sip").getAsJsonArray().get(0).getAsString());

			sb.append(out.get("req_0").getAsJsonObject().get("data").getAsJsonObject().get("midurlinfo")
					.getAsJsonArray().get(0).getAsJsonObject().get("purl").getAsString());
			return sb.toString();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	public String queryNetEase(String id) throws Exception {
		JsonObject params = new JsonObject();
		params.add("ids", new JsonPrimitive("[" + id + "]"));
		params.add("br", new JsonPrimitive(999000));
		String[] encrypt = NetEaseCrypto.weapiEncrypt(params.toString());
		StringBuilder sb = new StringBuilder("params=");
		sb.append(encrypt[0]);
		sb.append("&encSecKey=");
		sb.append(encrypt[1]);
		byte[] towrite = sb.toString().getBytes("UTF-8");
		URL u = new URL("http://music.163.com/weapi/song/enhance/player/url?csrf_token=");
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setConnectTimeout(4000);
		conn.setReadTimeout(4000);
		conn.setFixedLengthStreamingMode(towrite.length);

		conn.setInstanceFollowRedirects(true);
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,gl;q=0.6,zh-TW;q=0.4");
		conn.setRequestProperty("Connection", "keep-alive");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Content-Length", Integer.toString(towrite.length));
		conn.setRequestProperty("Referer", "https://music.163.com");
		conn.setRequestProperty("Host", "music.163.com");
		conn.setRequestProperty("User-Agent", NetEaseCrypto.getUserAgent());
		conn.connect();
		conn.getOutputStream().write(towrite);
		// System.out.println(conn.getResponseCode());
		// conn.getOutputStream().close();
		if (conn.getResponseCode() == 200) {
			InputStream is = conn.getInputStream();
			byte[] bs = null;
			bs = Utils.readAll(is);
			is.close();
			conn.disconnect();
			// System.out.write(bs);
			// System.out.println();
			JsonObject main = JsonParser.parseString(new String(bs, "UTF-8")).getAsJsonObject();

			if (main.get("code").getAsInt() == 200) {
				JsonArray data = main.get("data").getAsJsonArray();
				JsonObject song = data.get(0).getAsJsonObject();
				if (song.get("code").getAsInt() == 200) {
					return song.get("url").getAsString().trim();
				}
			}
		}
		return null;
	}

	public void getNetEaseSong(Contact eg, String songname) throws Exception {
		URL url = new URL("http://music.163.com/api/search/pc");
		HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		huc.setDoInput(true);
		huc.setDoOutput(true);
		huc.setRequestMethod("POST");
		huc.setRequestProperty("Referer", "http://music.163.com/");
		huc.setRequestProperty("Cookie", "appver=1.5.0.75771;");
		huc.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
		huc.connect();
		huc.getOutputStream().write(("type=1&offset=0&limit=1&s=" + songname).getBytes(StandardCharsets.UTF_8));
		huc.getInputStream();
		JsonObject jo;
		if (huc.getResponseCode() == 200)
			jo = JsonParser.parseString(new String(Utils.readAll(huc.getInputStream()), StandardCharsets.UTF_8))
					.getAsJsonObject().get("result").getAsJsonObject().get("songs").getAsJsonArray().get(0)
					.getAsJsonObject();
		else
			throw new FileNotFoundException();
		JsonObject appmsg = new JsonObject();
		appmsg.addProperty("app", "com.tencent.structmsg");
		JsonObject cfg = new JsonObject();
		cfg.addProperty("autosize", true);
		cfg.addProperty("ctime", Utils.getTime() / 1000);
		cfg.addProperty("token", "a2c42c48922ed97efffe962a4072a6de");
		cfg.addProperty("type", "normal");
		cfg.addProperty("forward", true);
		appmsg.add("config", cfg);
		appmsg.addProperty("view", "music");
		appmsg.addProperty("ver", "0.0.0.1");
		appmsg.addProperty("desc", "音乐");
		appmsg.addProperty("prompt", jo.get("name").getAsString());
		JsonObject meta = new JsonObject();
		appmsg.add("meta", meta);
		JsonObject music = new JsonObject();
		meta.add("music", music);
		music.addProperty("action", "");
		music.addProperty("android_pkg_name", "");
		music.addProperty("app_type", 1);
		music.addProperty("appid", 100495085);
		music.addProperty("preview", jo.get("album").getAsJsonObject().get("picUrl").getAsString());
		music.addProperty("desc",
				jo.get("artists").getAsJsonArray().get(0).getAsJsonObject().get("name").getAsString());
		String jurl = "https://y.music.163.com/m/song?id=" + jo.get("id").getAsString();
		music.addProperty("jumpUrl", jurl);
		music.addProperty("musicUrl", queryNetEase(jo.get("id").getAsString()));
		music.addProperty("sourceMsgId", "0");
		music.addProperty("source_icon", "");
		music.addProperty("source_url", "");
		music.addProperty("tag", "网易云音乐");
		music.addProperty("title", jo.get("name").getAsString());
		eg.sendMessage(new LightApp(appmsg.toString()).plus(jurl));
	}

	public void getQQSong(Contact eg, String songname) throws Exception {
		URL url = new URL(
				"https://c.y.qq.com/soso/fcgi-bin/client_search_cp?p=1&cr=1&aggr=1&flag_qc=0&n=50&w="
						+ songname + "&format=json");
		HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		huc.setRequestProperty("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");
		huc.setRequestMethod("GET");
		huc.connect();
		JsonObject song = JsonParser
				.parseString(
						new String(Utils.readAll(huc.getInputStream()), StandardCharsets.UTF_8))
				.getAsJsonObject().get("data").getAsJsonObject().get("song").getAsJsonObject()
				.get("list").getAsJsonArray().get(0).getAsJsonObject();// .data.song.list
		String mid = song.get("songmid").getAsString();
		String musicURL = queryMusic(mid);
		if (musicURL == null) {
			throw new FileNotFoundException();
		}

		Message msg = new ServiceMessage(2,
				"<msg serviceID=\"2\" templateID=\"1\" action=\"web\" brief=\"[分享]"
						+ song.get("songname").getAsString()
						+ "\" sourceMsgId=\"0\" url=\"https://i.y.qq.com/v8/playsong.html?_wv=1&amp;songid="
						+ song.get("songid").getAsString()
						+ "&amp;souce=qqshae&amp;source=qqshare&amp;ADTAG=qqshare\" flag=\"0\" adverSign=\"0\" multiMsgFlag=\"0\">\r\n"
						+ "<item layout=\"2\">\r\n"
						+ "<audio cover=\"http://y.gtimg.cn/music/photo_new/T002R300x300M000"
						+ song.get("albummid").getAsString() + ".jpg\" src=\""
						+ musicURL.replaceAll("\\&", "&amp;") + "\"/>\r\n" + "<title>"
						+ song.get("songname").getAsString() + "</title>\r\n" + "<summary>"
						+ song.get("albumname").getAsString() + "</summary>\r\n" + "</item>\r\n"
						+ "<source name=\"QQ音乐\" icon=\"https://url.cn/PwqZ4Jpi\" url=\"https://url.cn/5MKvjPY\" action=\"app\" a_actionData=\"com.tencent.qqmusic\" i_actionData=\"tencent1101079856://\" appid=\"1101079856\"/>\r\n"
						+ "</msg>");

		eg.sendMessage(msg.plus("https://i.y.qq.com/v8/playsong.html?_wv=1&songid="
				+ song.get("songid").getAsString() + "&souce=qqshae&source=qqshare&ADTAG=qqshare"));
	}

	public void songToVoice(String musicURL, Consumer<? super InputStream> csm) {

		HttpURLConnection huc2 = null;
		try {
			huc2 = (HttpURLConnection) new URL(musicURL).openConnection();

			huc2.setRequestMethod("GET");
			huc2.connect();
		} catch (IOException e) {
			return;
		}
		File f = new File("./temp/", "wv" + System.currentTimeMillis() + ".m4a");
		File f2 = new File("./temp/", "wv" + System.currentTimeMillis() + ".silk");
		File ft = new File("./temp/", "wv" + System.currentTimeMillis() + ".pcm");
		// File f2=new File("./temp/","wv"+System.currentTimeMillis()+".amr");
		try {
			f.getParentFile().mkdirs();
			OutputStream os = new FileOutputStream(f);
			huc2.getInputStream().transferTo(os);
			os.close();
			//exeCmd(new File("ffmpeg.exe").getAbsolutePath() + " -i \"" + f.getAbsolutePath()
			//		+ "\" -ab 12.2k -ar 8000 -ac 1 -y " + f2.getAbsolutePath());
			exeCmd(new File("ffmpeg.exe").getAbsolutePath() + " -i \"" + f.getAbsolutePath()
					+ "\" -f s16le -ar 24000 -ac 1 -acodec pcm_s16le -y " + ft.getAbsolutePath());
			exeCmd(new File("silk_v3_encoder.exe").getAbsolutePath() + " \"" + ft.getAbsolutePath() + "\" \""
					+ f2.getAbsolutePath() + "\" -Fs_API 24000 -tencent");
			try (FileInputStream fis = new FileInputStream(f2)) {
				csm.accept(fis);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			f.delete();
			ft.delete();
			f2.delete();
		}

	}
	public void onEnable() {
        this.getEventListener().subscribeAlways(GroupMessageEvent.class, event -> {
        		PlainText pt=event.getMessage().first(PlainText.Key);
        		if(pt==null)
        			return;
        		String[] args=pt.getContent().trim().split(" ");
        		Group eg=event.getGroup();
				if (args[0].startsWith("#音乐")) {

					String sn = null;
					try {
						sn = URLEncoder.encode(String.join(" ", Arrays.copyOfRange(args, 1, args.length)), "UTF-8");
					} catch (UnsupportedEncodingException ignored) {
					}
					String songname = sn;
					exec.execute(() -> {
						try {getQQSong(eg,songname);} catch (Throwable e) {
							try {
								getNetEaseSong(eg, songname);
							} catch (Throwable e2) {
								eg.sendMessage("无法找到歌曲");
							}
						}
					});
				}else if (args[0].startsWith("#Q音")) {
					String sn = null;
					try {
						sn = URLEncoder.encode(String.join(" ", Arrays.copyOfRange(args, 1, args.length)), "UTF-8");
					} catch (UnsupportedEncodingException ignored) {
					}
					String songname = sn;
					exec.execute(() -> {
						try {getQQSong(eg,songname);} catch (Throwable e) {
								eg.sendMessage("无法找到歌曲");
						}
					});
				}else if (args[0].startsWith("#网易")) {
					String sn = null;
					try {
						sn = URLEncoder.encode(String.join(" ", Arrays.copyOfRange(args, 1, args.length)), "UTF-8");
					} catch (UnsupportedEncodingException ignored) {
					}
					String songname = sn;
					exec.execute(() -> {
						try {getNetEaseSong(eg,songname);} catch (Throwable e) {
							e.printStackTrace();
							eg.sendMessage("无法找到歌曲");
						}
					});
				}
        	
        });
        getLogger().info("插件加载完毕!");
    }
	private void exeCmd(String commandStr) {
		try {
			getLogger().info("executing " + commandStr);
			Process p = Runtime.getRuntime().exec(commandStr);
			getLogger().info("finished return" + p.waitFor());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
