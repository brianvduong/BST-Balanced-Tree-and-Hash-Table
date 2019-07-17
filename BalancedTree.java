//Brian Duong
//cssc1468

package data_structures;

import java.util.TreeMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class BalancedTree<K extends Comparable<K>,V> implements DictionaryADT<K,V> {
	
	private TreeMap<K,V> map;
	
	public BalancedTree() {
		map  = new TreeMap<K,V>();
	}
	
	@Override
	public boolean contains(K key) {
		return map.containsKey(key);
	}

	@Override
	public boolean add(K key, V value) {
		return (map.put(key, value) == null);
		
	}

	@Override
	public boolean delete(K key) {
		return (map.remove(key) != null);
	}

	@Override
	public V getValue(K key) {
		return map.get(key);
	}

	@Override
	public K getKey(V value) {
		for(Map.Entry<K,V> entry: map.entrySet()) {
			if(((Comparable<V>)value).compareTo(entry.getValue()) == 0)
				return entry.getKey();
		}
		return null;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isFull() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public void clear() {
		map.clear();
		
	}

	@Override
	public Iterator<K> keys() {
		return map.navigableKeySet().iterator();
	}

	@Override
	public Iterator<V> values() {
		return map.values().iterator();
	}
}
