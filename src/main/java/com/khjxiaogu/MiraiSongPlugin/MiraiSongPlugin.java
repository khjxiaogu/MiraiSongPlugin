package com.khjxiaogu.MiraiSongPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.gson.JsonObject;
import com.khjxiaogu.MiraiSongPlugin.musicsource.KugouMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.NetEaseMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.QQMusicSource;

import net.mamoe.mirai.console.plugins.PluginBase;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.MessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.LightApp;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.ServiceMessage;

public class MiraiSongPlugin extends PluginBase {
	private Executor exec = Executors.newFixedThreadPool(8);
	public static Map<String, BiConsumer<MessageEvent, String[]>> commands = new ConcurrentHashMap<>();
	Map<String, MusicSource> sources = Collections.synchronizedMap(new LinkedHashMap<>());
	Map<String, MusicCardProvider> cards = new ConcurrentHashMap<>();
	{
		sources.put("QQ音乐", new QQMusicSource());
		// sources.put("QQ音乐HQ",new QQMusicHQSource());
		sources.put("网易", new NetEaseMusicSource());
		sources.put("酷狗", new KugouMusicSource());
		cards.put("LightApp", mi -> {
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
			appmsg.addProperty("prompt", mi.title);
			JsonObject meta = new JsonObject();
			appmsg.add("meta", meta);
			JsonObject music = new JsonObject();
			meta.add("music", music);
			music.addProperty("action", "");
			music.addProperty("android_pkg_name", "");
			music.addProperty("app_type", 1);
			music.addProperty("appid", mi.appid);
			music.addProperty("preview", mi.purl);
			music.addProperty("desc", mi.desc);
			music.addProperty("jumpUrl", mi.jurl);
			music.addProperty("musicUrl", mi.murl);
			music.addProperty("sourceMsgId", "0");
			music.addProperty("source_icon", mi.icon);
			music.addProperty("source_url", "");
			music.addProperty("tag", mi.source);
			music.addProperty("title", mi.title);
			return new LightApp(appmsg.toString()).plus(mi.jurl);
		});
		cards.put("XML", mi -> {
			StringBuilder xmb = new StringBuilder("<msg serviceID=\"2\" templateID=\"1\" action=\"web\" brief=\"[音乐]")
					.append(mi.title).append("\" sourceMsgId=\"0\" url=\"").append(mi.jurl.replaceAll("\\&", "&amp;"))
					.append("\" flag=\"0\" adverSign=\"0\" multiMsgFlag=\"0\">\r\n<item layout=\"2\">\r\n")
					.append("<audio cover=\"").append(mi.purl.replaceAll("\\&", "&amp;")).append("\" src=\"")
					.append(mi.murl.replaceAll("\\&", "&amp;")).append("\"/>\r\n").append("<title>").append(mi.title)
					.append("</title>\r\n<summary>").append(mi.desc).append("</summary>\r\n</item>\r\n<source name=\"")
					.append(mi.source).append("\" icon=\"").append(mi.icon)
					.append("\" url=\"\" action=\"\" a_actionData=\"\" i_actionData=\"\" appid=\"").append(mi.appid)
					.append("\"/>\r\n</msg>");
			Message msg = new ServiceMessage(2, xmb.toString());
			return msg.plus(mi.jurl);
		});
	}
	static {
		HttpURLConnection.setFollowRedirects(true);
	}

	public BiConsumer<MessageEvent, String[]> makeTemplate(String source, String card) {
		MusicCardProvider cb = cards.get(card);
		MusicSource mc = sources.get(source);
		return (event, args) -> {
			String sn;
			try {
				sn = URLEncoder.encode(String.join(" ", Arrays.copyOfRange(args, 1, args.length)), "UTF-8");
			} catch (UnsupportedEncodingException ignored) {return;}
			exec.execute(() -> {
				try {
					event.getSender().sendMessage(cb.process(mc.get(sn)));
				} catch (Throwable e) {
					event.getSender().sendMessage("无法找到歌曲");
				}
			});
		};
	}

	{
		commands.put("#音乐", (event, args) -> {
			String sn;
			try {
				sn = URLEncoder.encode(String.join(" ", Arrays.copyOfRange(args, 1, args.length)), "UTF-8");
			} catch (UnsupportedEncodingException ignored) {return;}
			exec.execute(() -> {
				MusicCardProvider mcp = cards.get("LightApp");
				for (MusicSource mc : sources.values()) {
					try {
						event.getSender().sendMessage(mcp.process(mc.get(sn)));
						return;
					} catch (Throwable t) {
					}
				}
				event.getSender().sendMessage("无法找到歌曲");
			});
		});
		commands.put("#QQ",makeTemplate("QQ音乐","XML"));
		commands.put("#网易",makeTemplate("网易","LightApp"));
		commands.put("#酷狗",makeTemplate("酷狗","LightApp"));
		commands.put("#点歌",(event,args)->{
			String sn;
			try {
				sn = URLEncoder.encode(String.join(" ", Arrays.copyOfRange(args,3, args.length)), "UTF-8");
			} catch (UnsupportedEncodingException ignored) {return;}
			exec.execute(() -> {
				try {
					MusicSource ms = sources.get(args[1]);
					if (ms == null) {
						event.getSender().sendMessage("无法找到源");
						return;
					}
					MusicCardProvider mcp = cards.get(args[2]);
					if (mcp == null) {
						event.getSender().sendMessage("无法找到模板");
						return;
					}
					event.getSender().sendMessage(mcp.process(ms.get(sn)));
				} catch (Throwable e) {
					event.getSender().sendMessage("无法找到歌曲");
				}
			});
		});
	}

	public void onEnable() {
		this.getEventListener().subscribeAlways(GroupMessageEvent.class, event -> {
			String[] args = Utils.getPlainText(event.getMessage()).split(" ");
			BiConsumer<MessageEvent, String[]> exec = commands.get(args[0]);
			if (exec != null)
				exec.accept(event, args);
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
