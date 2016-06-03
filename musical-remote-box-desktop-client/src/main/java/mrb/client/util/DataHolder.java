package mrb.client.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class serves as a global storage for every kind of data, that needs to be shared.
 * @author Brigi
 *
 */
public class DataHolder implements Serializable{
	
	//Singleton mechanism
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static DataHolder instance = null;
	
	public static DataHolder getInstance() {
		if(instance == null)
			instance = new DataHolder();
		return instance;
	}
	
	// Actual object
	
	private Map<String, Object> objectMap;
	
	private DataHolder() {
		this.objectMap = new HashMap<String, Object>();
	}

	public Object get(String key) {
		return this.objectMap.get(key);
	}
	
	public void put(String key, Object object) {
		this.objectMap.put(key, object);
	}
}
