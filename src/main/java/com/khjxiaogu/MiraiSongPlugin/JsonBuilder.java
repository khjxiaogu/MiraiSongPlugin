package com.khjxiaogu.MiraiSongPlugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonBuilder {

	public JsonBuilder() {
	}
	public static JsonArrayBuilder<JsonArray> array(){
		return new JsonArrayBuilder<>(null);
	}
	public static JsonObjectBuilder<JsonObject> object(){
		return new JsonObjectBuilder<>(null);
	}
	public static class JsonArrayBuilder<T> {
		JsonArray jo=new JsonArray();
		T parent;
		private JsonArrayBuilder(T par) {
			parent=par;
		}
		public T end() {
			if(parent==null)
				return (T) jo;
			return parent;
		}
		public JsonArrayBuilder<T> add(String v){
			jo.add(v);
			return this;
		}
		public JsonArrayBuilder<T> add(Number v){
			jo.add(v);
			return this;
		}
		public JsonArrayBuilder<T> add(boolean v){
			jo.add(v);
			return this;
		}
		public JsonArrayBuilder<T> add(Character v){
			jo.add(v);
			return this;
		}
		public JsonArrayBuilder<T> add(JsonElement v){
			jo.add(v);
			return this;
		}
		public JsonObjectBuilder<JsonArrayBuilder<T>> object(){
			JsonObjectBuilder<JsonArrayBuilder<T>> job= new JsonObjectBuilder<>(this);
			this.add(job.get());
			return job;
		}
		public JsonArrayBuilder<JsonArrayBuilder<T>> array(){
			JsonArrayBuilder<JsonArrayBuilder<T>> job= new JsonArrayBuilder<>(this);
			this.add(job.get());
			return job;
		}
		public JsonArray get() {
			return jo;
		};
		public String toString() {
			return jo.toString();
		}
	}
	public static class JsonObjectBuilder<T> {
		JsonObject jo=new JsonObject();
		T parent;
		private JsonObjectBuilder(T par) {
			parent=par;
		}
		
		public T end() {
			if(parent==null)
				return (T) jo;
			return parent;
		}
		public JsonObjectBuilder<T> add(String k,String v){
			jo.addProperty(k,v);
			return this;
		}
		public JsonObjectBuilder<T> add(String k,Number v){
			jo.addProperty(k,v);
			return this;
		}
		public JsonObjectBuilder<T> add(String k,boolean v){
			jo.addProperty(k,v);
			return this;
		}
		public JsonObjectBuilder<T> add(String k,Character v){
			jo.addProperty(k, v);
			return this;
		}
		public JsonObjectBuilder<T> add(String k,JsonElement v){
			jo.add(k, v);
			return this;
		}
		public JsonObjectBuilder<JsonObjectBuilder<T>> object(String k){
			JsonObjectBuilder<JsonObjectBuilder<T>> job= new JsonObjectBuilder<JsonObjectBuilder<T>>(this);
			this.add(k, job.get());
			return job;
		}
		public JsonArrayBuilder<JsonObjectBuilder<T>> array(String k){
			JsonArrayBuilder<JsonObjectBuilder<T>> job= new JsonArrayBuilder<>(this);
			this.add(k,job.get());
			return job;
		}
		public JsonObject get() {
			return jo;
		};
		public String toString() {
			return jo.toString();
		}
	}
}
