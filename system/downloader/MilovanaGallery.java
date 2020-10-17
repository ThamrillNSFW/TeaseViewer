package system.downloader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class MilovanaGallery implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String ID;
	private String name;
	private ArrayList<GalleryEntry> entries;

	public MilovanaGallery(String iD, String name) {
		super();
		ID = iD;
		this.name = name;
		entries = new ArrayList<GalleryEntry>();
	}

	public MilovanaGallery() {
		this((String)null,(String) null);
	}

	public MilovanaGallery(String iD, JSONObject parameters) {
		this(iD, (String) parameters.get("name"));
		JSONArray files=(JSONArray) parameters.get("images");
		for(Object object:files) {
			JSONObject jsono=(JSONObject) object;
			entries.add(new GalleryEntry(jsono.get("id").toString(), (String)jsono.get("hash"), ((Number)jsono.get("size")).longValue()));
		}
	}

	public static MilovanaGallery parseGallery(String ID, String name, String string) {
		MilovanaGallery gallery = new MilovanaGallery(ID, name);
		String list=string.substring(string.indexOf('[')+1);
		Pattern pattern = Pattern.compile("\\{\"id\":(\\d+),\"hash\":\"(.+?)\",\"size\":(\\d+).+?\\}");
		Matcher matcher = pattern.matcher(list);
		while (matcher.find()) {
			gallery.entries.add(new GalleryEntry(matcher.group(1), matcher.group(2), Long.parseLong(matcher.group(3))));
		}
		return gallery;
	}

	public static ArrayList<MilovanaGallery> parseGalleries(String string) {

		ArrayList<MilovanaGallery> galleries = new ArrayList<MilovanaGallery>();
		Pattern pattern = Pattern.compile("\"(.+?)\":\\{\"name\":\"(.+?)\",(.+?)\\]\\},*");
		Matcher matcher = pattern.matcher(string);
		while (matcher.find()) {
			galleries.add(MilovanaGallery.parseGallery(matcher.group(1), matcher.group(2), matcher.group(3)));
		}
		return galleries;
	}
	
	public ArrayList<GalleryEntry> getEntries() {
		return entries;
	}
	
	public String getID() {
		return ID;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return ID+"]["+name;
	}

	public static class GalleryEntry implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private String id;
		private String hash;
		Long size;
		public GalleryEntry(String id, String hash, Long size) {
			super();
			this.setId(id);
			this.setHash(hash);
			this.size = size;
		}
		public String getHash() {
			return hash;
		}
		public void setHash(String hash) {
			this.hash = hash;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
	}

}
