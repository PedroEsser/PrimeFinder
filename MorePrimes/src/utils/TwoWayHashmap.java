package utils;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TwoWayHashmap<K extends Object, V extends Object> {

	  private Map<K,V> forward = new Hashtable<K, V>();
	  private Map<V,K> backward = new Hashtable<V, K>();

	  public void put(K key, V value) {
		  forward.put(key, value);
		  backward.put(value, key);
	  }

	  public V getValue(K key) {
		  return forward.get(key);
	  }

	  public K getKey(V key) {
		  return backward.get(key);
	  }
	  
	  public boolean containsKey(K key) {
		  return forward.containsKey(key);
	  }
	  
	  public boolean containsValue(V value) {
		  return backward.containsKey(value);
	  }
	  
	  public Set<Entry<K, V>> entrySet(){
		  return forward.entrySet();
	  }
	  
	  public int size() {
		  return forward.size();
	  }
	  
}