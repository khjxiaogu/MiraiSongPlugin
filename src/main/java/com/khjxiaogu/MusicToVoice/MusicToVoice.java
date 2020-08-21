package com.khjxiaogu.MusicToVoice;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mamoe.mirai.console.plugins.PluginBase;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.GroupMessageEvent;
import net.mamoe.mirai.message.data.Message;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.ServiceMessage;

public class MusicToVoice extends PluginBase{
	private Executor exec=Executors.newFixedThreadPool(8);
	public MusicToVoice() {
	} 
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
			if (out.get("code").getAsInt() != 0) {return null; }
			StringBuilder sb = new StringBuilder(out.get("req_0").getAsJsonObject().get("data").getAsJsonObject()
			        .get("sip").getAsJsonArray().get(0).getAsString());

			sb.append(out.get("req_0").getAsJsonObject().get("data").getAsJsonObject().get("midurlinfo")
			        .getAsJsonArray().get(0).getAsJsonObject().get("purl").getAsString());
			System.out.println(sb.toString());
			return sb.toString();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	public void onEnable() {
        this.getEventListener().subscribeAlways(GroupMessageEvent.class, event -> {
        		PlainText pt=event.getMessage().first(PlainText.Key);
        		String[] args=pt.getContent().trim().split(" ");
        		Group eg=event.getGroup();
        		if(args[0].startsWith("#音乐")) {
        			exec.execute(()->{try {
	        			URL url=new URL("https://c.y.qq.com/soso/fcgi-bin/client_search_cp?p=1&cr=1&aggr=1&flag_qc=0&n=50&w="+URLEncoder.encode(String.join(" ",Arrays.copyOfRange(args,1,args.length)),"UTF-8")+"&format=json");
	        			HttpURLConnection huc= (HttpURLConnection) url.openConnection();
	        			huc.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36");
	        			huc.setRequestMethod("GET");
	        			huc.connect();
	        			JsonObject song=JsonParser.parseString(new String(Utils.readAll(huc.getInputStream()),StandardCharsets.UTF_8)).getAsJsonObject().get("data").getAsJsonObject().get("song").getAsJsonObject().get("list").getAsJsonArray().get(0).getAsJsonObject();//.data.song.list
	        			String mid=song.get("songmid").getAsString();
	        			String musicURL=queryMusic(mid);
	        			if(musicURL==null) {
	        				eg.sendMessage("无法找到音乐");
	        				return;
	        			}
	        			Message msg=new ServiceMessage(2, "<msg serviceID=\"2\" templateID=\"1\" action=\"web\" brief=\"[分享]"+song.get("songname").getAsString()+"\" sourceMsgId=\"0\" url=\"https://i.y.qq.com/v8/playsong.html?_wv=1&amp;songid="+song.get("songid").getAsString()+"&amp;souce=qqshae&amp;source=qqshare&amp;ADTAG=qqshare\" flag=\"0\" adverSign=\"0\" multiMsgFlag=\"0\">\r\n" + 
	        					"<item layout=\"2\">\r\n" + 
	        					"<audio cover=\"http://y.gtimg.cn/music/photo_new/T002R300x300M000"+song.get("albummid").getAsString()+".jpg\" src=\""+musicURL.replaceAll("\\&","&amp;")+"\"/>\r\n" + 
	        					"<title>"+song.get("songname").getAsString()+"</title>\r\n" + 
	        					"<summary>"+song.get("albumname").getAsString()+"</summary>\r\n" + 
	        					"</item>\r\n" + 
	        					"<source name=\"QQ音乐\" icon=\"https://url.cn/PwqZ4Jpi\" url=\"https://url.cn/5MKvjPY\" action=\"app\" a_actionData=\"com.tencent.qqmusic\" i_actionData=\"tencent1101079856://\" appid=\"1101079856\"/>\r\n" + 
	        					"</msg>");
	        			
	        			eg.sendMessage(msg.plus("https://i.y.qq.com/v8/playsong.html?_wv=1&songid="+song.get("songid").getAsString()+"&souce=qqshae&source=qqshare&ADTAG=qqshare"));
	        			//event.getGroup().sendMessage(RichMessage.Templates.share(musicURL.replaceAll("\\&","&amp;"),song.get("songname").getAsString(),song.get("albumname").getAsString(),"http://y.gtimg.cn/music/photo_new/T002R300x300M000"+song.get("albummid").getAsString()+".jpg"));
					} catch (Throwable e) {
						event.getGroup().sendMessage("无法找到音乐");
						e.printStackTrace();
					}});
        		
        	}
        });
        getLogger().info("插件加载完毕!");
    }
}
