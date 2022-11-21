package com.khjxiaogu.MiraiSongPlugin;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HttpRequestBuilder {
	private StringBuilder url;
	
	List<String[]> headers=new ArrayList<>();
	
	public HttpRequestBuilder(String ourl) {
		url=new StringBuilder(ourl);
	}
	public static HttpRequestBuilder create(String host) {
		return new HttpRequestBuilder("https://"+host).host(host);
	}
	public static HttpRequestBuilder create(String protocol,String host) {
		return new HttpRequestBuilder(protocol+"://"+host).host(host);
	}
	public HttpRequestBuilder url(String v) {
		url.append(v);
		return this;
	}
	public HttpRequestBuilder url(int v) {
		url.append(v);
		return this;
	}
	public HttpRequestBuilder url(char v) {
		url.append(v);
		return this;
	}
	public HttpRequestBuilder header(String k,String v) {
		headers.add(new String[] {k,v});
		return this;
	}
	public HttpRequestBuilder referer(String v) {
		headers.add(new String[] {"referer",v});
		return this;
	}
	public HttpRequestBuilder contenttype(String v) {
		headers.add(new String[] {"content-type",v});
		return this;
	}
	public HttpRequestBuilder cookie(String v) {
		headers.add(new String[] {"cookie",v});
		return this;
	}
	public HttpRequestBuilder host(String v) {
		headers.add(new String[] {"Host",v});
		return this;
	}
	public HttpRequestBuilder defUA() {
		headers.add(new String[] {"User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.149 Safari/537.36"});
		return this;
	}
	public HttpRequestBuilder ua(String v) {
		headers.add(new String[] {"User-Agent",v});
		return this;
	}
	public static class OpenedConnectionBuilder {
		HttpURLConnection huc;
		public OutputStream os;
		public OpenedConnectionBuilder(HttpURLConnection huc,boolean doOut) throws IOException {
			super();
			this.huc = huc;
			if(doOut)
			this.os = huc.getOutputStream();
		}
		public OpenedConnectionBuilder(HttpURLConnection huc) {
			super();
			this.huc = huc;
		}
		public OpenedConnectionBuilder send(String s) throws IOException {
			if(os==null)
				os=huc.getOutputStream();
			os.write(s.getBytes(StandardCharsets.UTF_8));
			return this;
		}
		public OpenedConnectionBuilder send(byte[] s) throws IOException {
			if(os==null)
				os=huc.getOutputStream();
			os.write(s);
			return this;
		}
		public String readString() throws IOException {
			return new String(Utils.readAll(huc.getInputStream()), StandardCharsets.UTF_8);
		}
		public JsonObject readJson() throws IOException {
			return JsonParser.parseString(new String(Utils.readAll(huc.getInputStream()), StandardCharsets.UTF_8)).getAsJsonObject();
		}
	}
	private HttpURLConnection openConn() throws IOException {
		URL url = new URL(this.url.toString());
		HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		huc.setInstanceFollowRedirects(true);
		huc.setConnectTimeout(5000);
		huc.setReadTimeout(5000);
		for(String[] header:headers) {
			huc.setRequestProperty(header[0], header[1]);
		}
		
		return huc;
	}
	public OpenedConnectionBuilder post(boolean doOutput) throws IOException {
		HttpURLConnection huc=openConn();
		huc.setRequestMethod("POST");
		huc.setDoOutput(doOutput);
		huc.setDoInput(true);
		huc.connect();
		return new OpenedConnectionBuilder(huc,doOutput);
		
	}
	public OpenedConnectionBuilder post() throws IOException {
		HttpURLConnection huc=openConn();
		huc.setRequestMethod("POST");
		huc.setDoOutput(true);
		huc.setDoInput(true);
		huc.connect();
		return new OpenedConnectionBuilder(huc,true);
	}
	public OpenedConnectionBuilder get(boolean doOutput) throws IOException {
		HttpURLConnection huc=openConn();
		huc.setRequestMethod("GET");
		huc.setDoOutput(doOutput);
		huc.setDoInput(true);
		huc.connect();
		return new OpenedConnectionBuilder(huc,doOutput);
	}
	public OpenedConnectionBuilder get() throws IOException {
		HttpURLConnection huc=openConn();
		huc.setRequestMethod("GET");
		huc.connect();
		return new OpenedConnectionBuilder(huc,false);
	}
	public OpenedConnectionBuilder open(String met,boolean doInput,boolean doOutput) throws IOException {
		HttpURLConnection huc=openConn();
		huc.setRequestMethod(met);
		huc.setDoOutput(doOutput);
		huc.setDoInput(doInput);
		huc.connect();
		return new OpenedConnectionBuilder(huc,doOutput);
	}
	

}
