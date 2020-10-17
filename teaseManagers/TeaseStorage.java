package teaseManagers;

import java.io.Serializable;
import java.util.TreeMap;

import org.graalvm.polyglot.HostAccess;

import system.TeaseViewer;

public class TeaseStorage {
	TreeMap<String, Object> storage;

	public TeaseStorage(TeaseViewer app) {
		storage=new TreeMap<>();
	}
	@HostAccess.Export
	public void setItem(String key, Object o) {
		storage.put(key, o);
	}
	@HostAccess.Export
	public Object getItem(String key) {
		System.out.println("getItem:"+key+":"+storage.get(key));
		return storage.get(key);
	}
	@HostAccess.Export
	public Serializable getStorage() {
		TreeMap<String, Serializable> serializableMap=new TreeMap<>();
		for(String key:storage.keySet()) {
			if(storage.get(key) instanceof Serializable) {
				serializableMap.put(key, (Serializable) storage.get(key));
			}
		}
		return serializableMap;
	}

}