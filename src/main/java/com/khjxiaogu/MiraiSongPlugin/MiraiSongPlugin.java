package com.khjxiaogu.MiraiSongPlugin;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import com.khjxiaogu.MiraiSongPlugin.cardprovider.LightAppCardProvider;
import com.khjxiaogu.MiraiSongPlugin.cardprovider.XMLCardProvider;
import com.khjxiaogu.MiraiSongPlugin.musicsource.KugouMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.NetEaseMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.QQMusicSource;

import net.mamoe.mirai.console.plugins.PluginBase;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.MessageEvent;

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
		cards.put("LightApp",new LightAppCardProvider());
		cards.put("XML",new XMLCardProvider());
	}
	static {
		HttpURLConnection.setFollowRedirects(true);
	}

	public BiConsumer<MessageEvent, String[]> makeTemplate(String source, String card) {
		MusicCardProvider cb = cards.get(card);
		if(cb==null)
			throw new IllegalArgumentException("card template not exists");
		MusicSource mc = sources.get(source);
		if(mc==null)
			throw new IllegalArgumentException("music source not exists");
		return (event, args) -> {
			String sn;
			try {
				sn = URLEncoder.encode(String.join(" ", Arrays.copyOfRange(args, 1, args.length)), "UTF-8");
			} catch (UnsupportedEncodingException ignored) {
				return;
			}
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
			} catch (UnsupportedEncodingException ignored) {
				return;
			}
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
		commands.put("#QQ", makeTemplate("QQ音乐", "XML"));
		commands.put("#网易", makeTemplate("网易", "LightApp"));
		commands.put("#酷狗", makeTemplate("酷狗", "LightApp"));
		commands.put("#点歌", (event, args) -> {
			String sn;
			try {
				sn = URLEncoder.encode(String.join(" ", Arrays.copyOfRange(args, 3, args.length)), "UTF-8");
			} catch (UnsupportedEncodingException ignored) {
				return;
			}
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

	@Override
	public void onEnable() {
		this.getEventListener().subscribeAlways(GroupMessageEvent.class, event -> {
			String[] args = Utils.getPlainText(event.getMessage()).split(" ");
			BiConsumer<MessageEvent, String[]> exec = commands.get(args[0]);
			if (exec != null)
				exec.accept(event, args);
		});
		getLogger().info("插件加载完毕!");
	}

}
