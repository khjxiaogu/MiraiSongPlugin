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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.khjxiaogu.MiraiSongPlugin.cardprovider.AmrVoiceProvider;
import com.khjxiaogu.MiraiSongPlugin.cardprovider.LightAppXCardProvider;
import com.khjxiaogu.MiraiSongPlugin.cardprovider.MiraiMusicCard;
import com.khjxiaogu.MiraiSongPlugin.cardprovider.PlainMusicInfoProvider;
import com.khjxiaogu.MiraiSongPlugin.cardprovider.ShareCardProvider;
import com.khjxiaogu.MiraiSongPlugin.cardprovider.SilkVoiceProvider;
import com.khjxiaogu.MiraiSongPlugin.cardprovider.XMLCardProvider;
import com.khjxiaogu.MiraiSongPlugin.musicsource.BaiduMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.BiliBiliMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.KugouMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.LocalFileSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.NetEaseAdvancedRadio;
import com.khjxiaogu.MiraiSongPlugin.musicsource.NetEaseHQMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.NetEaseMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.NetEaseRadioSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.QQMusicHQSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.QQMusicSource;
import com.khjxiaogu.MiraiSongPlugin.musicsource.XimalayaSource;
import com.khjxiaogu.MiraiSongPlugin.permission.GlobalMatcher;
import com.khjxiaogu.MiraiSongPlugin.permission.MatchInfo;

import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.event.events.StrangerMessageEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import net.mamoe.yamlkt.Yaml;
import net.mamoe.yamlkt.YamlElement;
import net.mamoe.yamlkt.YamlLiteral;
import net.mamoe.yamlkt.YamlMap;

// TODO: Auto-generated Javadoc
/**
 * 插件主类
 * 
 * @author khjxiaogu
 *         file: MiraiSongPlugin.java
 *         time: 2020年8月26日
 */
public class MiraiSongPlugin extends JavaPlugin {
	public MiraiSongPlugin() {
		super(new JvmPluginDescriptionBuilder(PluginData.id, PluginData.ver).name(PluginData.name)
				.author(PluginData.auth).info(PluginData.info).build());
	}

	private final String spliter = " ";
	// 请求音乐的线程池。
	private Executor exec = Executors.newFixedThreadPool(8);

	/** 命令列表. */
	public static final Map<String, BiConsumer<MessageEvent, String[]>> commands = new ConcurrentHashMap<>();

	/** 音乐来源. */
	public static final Map<String, MusicSource> sources = Collections.synchronizedMap(new LinkedHashMap<>());

	/** 外观来源 */
	public static final Map<String, MusicCardProvider> cards = new ConcurrentHashMap<>();
	static {
		// 注册音乐来源
		sources.put("QQ音乐", new QQMusicSource());
		sources.put("QQ音乐HQ", new QQMusicHQSource());
		sources.put("网易", new NetEaseMusicSource());
		sources.put("网易电台节目", new NetEaseAdvancedRadio());
		sources.put("网易电台", new NetEaseRadioSource());
		sources.put("网易HQ", new NetEaseHQMusicSource());
		sources.put("酷狗", new KugouMusicSource());
		sources.put("千千", new BaiduMusicSource());
		sources.put("Bilibili", new BiliBiliMusicSource());
		sources.put("喜马拉雅", new XimalayaSource());
		sources.put("本地", new LocalFileSource());
		// 注册外观
		// cards.put("LightApp", new LightAppCardProvider());
		cards.put("LightApp", new MiraiMusicCard());
		cards.put("LightAppX", new LightAppXCardProvider());
		cards.put("XML", new XMLCardProvider());
		cards.put("Silk", new SilkVoiceProvider());
		cards.put("AMR", new AmrVoiceProvider());
		cards.put("Share", new ShareCardProvider());
		cards.put("Message", new PlainMusicInfoProvider());
		cards.put("Mirai", new MiraiMusicCard());
	}
	static {
		HttpURLConnection.setFollowRedirects(true);
	}
	private static MiraiSongPlugin plugin;

	public static MiraiLogger getMLogger() {
		return plugin.getLogger();
	}

	GlobalMatcher matcher = new GlobalMatcher();
	String unfoundSong;
	String unavailableShare;
	String templateNotFound;
	String sourceNotFound;

	/**
	 * 使用现有的来源和外观制作指令执行器
	 * 
	 * @param source 音乐来源名称
	 * @param card   音乐外观名称
	 * @return return 返回一个指令执行器，可以注册到命令列表里面
	 */
	public BiConsumer<MessageEvent, String[]> makeTemplate(String source, String card) {
		if (source.equals("all"))
			return makeSearchesTemplate(card);
		MusicCardProvider cb = cards.get(card);
		if (cb == null)
			throw new IllegalArgumentException("card template not exists");
		MusicSource mc = sources.get(source);
		if (mc == null)
			throw new IllegalArgumentException("music source not exists");
		return (event, args) -> {
			String sn = String.join(spliter, Arrays.copyOfRange(args, 1, args.length));
			exec.execute(() -> {
				MusicInfo mi;
				try {
					mi = mc.get(sn);
				} catch (Throwable t) {
					this.getLogger().debug(t);
					Utils.getProperReceiver(event).sendMessage(unfoundSong);
					return;
				}
				try {
					Utils.getProperReceiver(event).sendMessage(cb.process(mi, Utils.getProperReceiver(event)));
				} catch (Throwable t) {
					this.getLogger().debug(t);
					// this.getLogger().
					Utils.getProperReceiver(event).sendMessage(unavailableShare);
					return;
				}
			});
		};
	}

	/**
	 * 自动搜索所有源并且以指定外观返回
	 * 
	 * @param card 音乐外观名称
	 * @return return 返回一个指令执行器，可以注册到命令列表里面
	 */
	public BiConsumer<MessageEvent, String[]> makeSearchesTemplate(String card) {
		MusicCardProvider cb = cards.get(card);
		if (cb == null)
			throw new IllegalArgumentException("card template not exists");
		return (event, args) -> {
			String sn = String.join(spliter, Arrays.copyOfRange(args, 1, args.length));

			exec.execute(() -> {
				for (MusicSource mc : sources.values()) {
					if (!mc.isVisible())
						continue;
					MusicInfo mi;
					try {
						mi = mc.get(sn);
					} catch (Throwable t) {
						this.getLogger().debug(t);
						continue;
					}
					try {
						Utils.getProperReceiver(event).sendMessage(cb.process(mi, Utils.getProperReceiver(event)));
					} catch (Throwable t) {
						this.getLogger().debug(t);
						Utils.getProperReceiver(event).sendMessage(unavailableShare);
					}
					return;
				}
				Utils.getProperReceiver(event).sendMessage(unfoundSong);
			});

		};
	}

	@SuppressWarnings("resource")
	@Override
	public void onEnable() {
		plugin = this;
		if (!new File(this.getDataFolder(), "global.permission").exists()) {
			try (FileOutputStream fos = new FileOutputStream(new File(this.getDataFolder(), "global.permission"))) {
				fos.write("#global fallback permission file".getBytes());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!new File(this.getDataFolder(), "config.yml").exists()) {
			try {
				new FileOutputStream(new File(this.getDataFolder(), "config.yml"))
						.write(Utils.readAll(this.getResourceAsStream("config.yml")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		File local = new File("SongPluginLocal.json");
		if (!local.exists()) {
			try {
				Gson gs = new GsonBuilder().setPrettyPrinting().create();
				local.createNewFile();
				JsonArray datas = new JsonArray();
				JsonObject obj = new JsonObject();
				obj.addProperty("title", "标题");
				obj.addProperty("desc", "副标题");
				obj.addProperty("previewUrl", "专辑图片url");
				obj.addProperty("musicUrl", "音乐播放url");
				obj.addProperty("jumpUrl", "点击跳转url");
				obj.addProperty("source", "本地");
				datas.add(obj);
				try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(local), "UTF-8")) {
					gs.toJson(datas, fw);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		reload();
		GlobalEventChannel.INSTANCE.registerListenerHost(new SimpleListenerHost(this.getCoroutineContext()) {
			@EventHandler
			public void onGroup(GroupMessageEvent event) {
				String[] args = Utils.getPlainText(event.getMessage()).split(spliter);
				BiConsumer<MessageEvent, String[]> exec = commands.get(args[0]);
				if (exec != null)
					if (matcher.match(new MatchInfo(args[0], event.getSender())).isAllowed())
						exec.accept(event, args);

			}

			@EventHandler
			public void onFriend(FriendMessageEvent event) {
				String[] args = Utils.getPlainText(event.getMessage()).split(spliter);
				BiConsumer<MessageEvent, String[]> exec = commands.get(args[0]);
				if (exec != null)
					if (matcher.match(new MatchInfo(args[0], event.getSender(), false)).isAllowed())
						exec.accept(event, args);

			}

			@EventHandler
			public void onTemp(StrangerMessageEvent event) {
				String[] args = Utils.getPlainText(event.getMessage()).split(spliter);
				BiConsumer<MessageEvent, String[]> exec = commands.get(args[0]);
				if (exec != null)
					if (matcher.match(new MatchInfo(args[0], event.getSender(), true)).isAllowed())
						exec.accept(event, args);

			}
		});
		getLogger().info("插件加载完毕!");
	}

	public void reload() {
		YamlMap cfg = Yaml.getDefault().decodeYamlMapFromString(
				new String(Utils.readAll(new File(this.getDataFolder(), "config.yml")), StandardCharsets.UTF_8));
		matcher.load(this.getDataFolder());
		YamlMap excs = (YamlMap) cfg.get(new YamlLiteral("extracommands"));
		String addDefault = cfg.getStringOrNull("adddefault");
		commands.clear();
		if (addDefault == null || addDefault.equals("true")) {
			commands.put("#音乐", makeSearchesTemplate("Mirai"));
			commands.put("#外链", makeSearchesTemplate("Message"));
			commands.put("#语音", makeSearchesTemplate("AMR"));
			commands.put("#QQ", makeTemplate("QQ音乐", "Mirai"));// 标准样板
			commands.put("#网易", makeTemplate("网易", "Mirai"));
			commands.put("#网易电台", makeTemplate("网易电台节目", "Mirai"));
			commands.put("#酷狗", makeTemplate("酷狗", "Mirai"));
			commands.put("#千千", makeTemplate("千千", "XML"));
			commands.put("#点歌", (event, args) -> {
				String sn = String.join(spliter, Arrays.copyOfRange(args, 3, args.length));

				exec.execute(() -> {
					try {
						MusicSource ms = sources.get(args[1]);
						if (ms == null) {
							Utils.getProperReceiver(event).sendMessage("无法找到源");
							return;
						}
						MusicCardProvider mcp = cards.get(args[2]);
						if (mcp == null) {
							Utils.getProperReceiver(event).sendMessage("无法找到模板");
							return;
						}
						MusicInfo mi;
						try {
							mi = ms.get(sn);
						} catch (Throwable t) {
							this.getLogger().debug(t);
							Utils.getProperReceiver(event).sendMessage(unfoundSong);
							return;
						}
						try {
							Utils.getProperReceiver(event).sendMessage(mcp.process(mi, Utils.getProperReceiver(event)));
						} catch (Throwable t) {
							this.getLogger().debug(t);
							Utils.getProperReceiver(event).sendMessage(unavailableShare);
							return;
						}
					} catch (Throwable e) {
						e.printStackTrace();
						Utils.getProperReceiver(event).sendMessage(unfoundSong);
					}
				});
			});
		}
		if (excs != null)
			for (YamlElement cmd : excs.getKeys()) {
				commands.put(cmd.toString(), makeTemplate(((YamlMap) excs.get(cmd)).getString("source"),
						((YamlMap) excs.get(cmd)).getString("card")));
			}
		commands.put("/msp", (ev, args) -> {
			if (!matcher.match(new MatchInfo(args[0], ev.getSender(), true).mustMatchCommand()).and(matcher.match(new MatchInfo(args[0] + "." + args[1], ev.getSender(), true).mustMatchCommand())).isForceAllowed())
				return;
			if (args[1].equals("reload")) {

				reload();
				ev.getSender().sendMessage("重载成功！");
			} else if (args[1].equals("setperm")) {
				if (ev instanceof GroupMessageEvent) {
					GroupMessageEvent gev = (GroupMessageEvent) ev;
					try {
						if (matcher.loadString(args[2], gev.getGroup(), ev.getBot())) {
							ev.getSender().sendMessage("本群权限设置成功！");
							return;
						}
					} catch (Exception ex) {

						getLogger().warning(ex);
					}
					ev.getSender().sendMessage("本机权限设置失败！");
				}
			} else if (args[1].equals("setbperm")) {
				try {
					if (matcher.loadString(args[2], ev.getBot())) {
						ev.getSender().sendMessage("本机权限设置成功！");
						return;
					}
				} catch (Exception ex) {

					getLogger().warning(ex);
				}
				ev.getSender().sendMessage("本机权限设置失败！");
			} else if (args[1].equals("setgperm")) {
				try {
					if (matcher.loadString(args[2])) {
						ev.getSender().sendMessage("全局权限设置成功！");
						return;
					}
				} catch (Exception ex) {

					getLogger().warning(ex);
				}
				ev.getSender().sendMessage("全局权限设置失败！");
			} else if (args[1].equals("buildperm")) {
				try {
					matcher.rebuildConfig();
					ev.getSender().sendMessage("权限整理完成！");
				} catch (Exception ex) {
					ev.getSender().sendMessage("权限整理失败！");
					getLogger().warning(ex);
				}
			}
		});
		AmrVoiceProvider.ffmpeg = SilkVoiceProvider.ffmpeg = cfg.getString("ffmpeg_path");
		String amras = cfg.getStringOrNull("amrqualityshift");
		String amrwb = cfg.getStringOrNull("amrwb");
		String usecc = cfg.getStringOrNull("use_custom_ffmpeg_command");
		String ulocal = cfg.getStringOrNull("enable_local");
		String vb = cfg.getStringOrNull("verbose");
		unfoundSong = cfg.getStringOrNull("hintsongnotfound");
		if (unfoundSong == null)
			unfoundSong = "无法找到歌曲。";
		unavailableShare = cfg.getStringOrNull("hintcarderror");
		if (unavailableShare == null)
			unavailableShare = "分享歌曲失败。";
		templateNotFound = cfg.getStringOrNull("hintnotemplate");
		if (templateNotFound == null)
			templateNotFound = "无法找到卡片。";
		sourceNotFound = cfg.getStringOrNull("hintsourcenotfound");
		if (sourceNotFound == null)
			sourceNotFound = "无法找到来源。";
		LocalFileSource.autoLocal = ulocal != null && ulocal.equals("true");
		AmrVoiceProvider.autoSize = amras != null && amras.equals("true");
		AmrVoiceProvider.wideBrand = amrwb == null || amrwb.equals("true");
		AmrVoiceProvider.customCommand = (usecc != null && usecc.equals("true"))
				? cfg.getStringOrNull("custom_ffmpeg_command")
				: null;
		Utils.verbose = vb == null || vb.equals("true");
		SilkVoiceProvider.silk = cfg.getString("silkenc_path");
		if (AmrVoiceProvider.customCommand == null) {
			try {
				Utils.exeCmd(AmrVoiceProvider.ffmpeg, "-version");
			} catch (RuntimeException ex) {
				ex.printStackTrace();
				getLogger().warning("ffmpeg启动失败，语音功能失效！");
			}
			getLogger().info("当前配置项：宽域AMR:" + AmrVoiceProvider.wideBrand + " AMR自动大小:" + AmrVoiceProvider.autoSize);
		} else
			getLogger().info("当前配置项：自定义指令:" + AmrVoiceProvider.customCommand);
	}
}
