package com.khjxiaogu.MiraiSongPlugin.musicsource;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.khjxiaogu.MiraiSongPlugin.Utils;

public class QQMusicHQSource extends QQMusicSource {
	String[] availenc = new String[] { "D00A", // .flac
			"A000", // .ape
			"F000", // .flac
			"M800", // .mp3
			//"O600", // .ogg
			"C400", // .m4a
			"M500",// .mp3
	};
	String[] availext = new String[] { ".flac", ".ape", ".flac", ".mp3",/* ".ogg",*/ ".m4a", ".mp3", };

	public QQMusicHQSource() {
	}
	public static void main(String[] args) {
		System.out.println(new QQMusicHQSource().queryRealUrl("000wXPFa1voP29"));
	}
	//static String cookie=null;
	//static long qq=0;
	@Override
	public String queryRealUrl(String songmid) {
		JsonObject main=new JsonObject();
		JsonObject r0=new JsonObject();
		JsonObject comm=new JsonObject();
		JsonObject param=new JsonObject();
		JsonArray fns=new JsonArray();
		JsonArray ids=new JsonArray();
		JsonArray types=new JsonArray();
		String guid=String.valueOf((int)(Math.random()*9000000+1000000));
		main.add("req_0",r0);
		r0.addProperty("module","vkey.GetVkeyServer");
		r0.addProperty("method", "CgiGetVkey");
		r0.add("param", param);
		//param.addProperty("uin",qq);
		param.addProperty("loginflag",1);
		param.addProperty("platform", "20");
		param.addProperty("guid",guid);
		types.add(0);
		param.add("songtype",types);
		param.add("songmid",ids);
		ids.add(songmid);
		param.add("filename",fns);
		main.add("comm",comm);
		//comm.addProperty("uin",qq);
		comm.addProperty("format","json");
		comm.addProperty("ct",19);
		comm.addProperty("cv", 0);
		//comm.addProperty("authst","");
		StringBuilder urlsb = new StringBuilder("https://u.y.qq.com/cgi-bin/musicu.fcg?-=getplaysongvkey&g_tk=5381");
		//urlsb.append("&loginUin="+qq);
		urlsb.append("&format=json&platform=yqq.json&data=");
		String out = null;
		String iqual = "2";
		int i = 0;
		if (iqual != null)
			i = Integer.parseInt(iqual);
		try {
			if (i < availenc.length)
				for (; i < availenc.length; i++) {
					if(fns.size()>0)
						fns.remove(0);
					fns.add(availenc[i] + songmid +songmid + availext[i]);
					URL u = new URL(urlsb.toString() + URLEncoder.encode(main.toString()));
					//System.out.println("incoming " + u.toString());
					HttpURLConnection conn = (HttpURLConnection) u.openConnection();
					conn.setDoOutput(true);
					conn.setRequestMethod("GET");
					conn.setRequestProperty("Host", "u.y.qq.com");
					conn.setRequestProperty("Referer", "http://y.qq.com");
					conn.setRequestProperty("User-Agent",
							"Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
					//conn.setRequestProperty("cookie",cookie);
					conn.connect();
					byte[] bs = Utils.readAll(conn.getInputStream());
					//System.out.println(new String(bs, "UTF-8"));
					out = JsonParser.parseString(new String(bs, "UTF-8")).getAsJsonObject().get("req_0")
							.getAsJsonObject().get("data").getAsJsonObject().get("midurlinfo").getAsJsonArray().get(0).getAsJsonObject().get("purl").getAsString();
			
					if (out.length()==0) {
						continue;
					}
					break;
				}
			StringBuilder sb = new StringBuilder("http://ws.stream.qqmusic.qq.com/")
					.append(out);
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean isVisible() {
		return false;
	}

}
