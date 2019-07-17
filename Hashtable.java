//Brian Duong
//cssc1468

package data_structures;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import data_structures.BinarySearchTree.IteratorHelper;
import data_structures.BinarySearchTree.KeyIteratorHelper;
import data_structures.BinarySearchTree.ValueIteratorHelper;

public class Hashtable<K extends Comparable<K>,V> implements DictionaryADT<K,V> {
	
	private class DictionaryNode<K,V> implements Comparable<DictionaryNode<K,V>>{
		K key;
		V value;
		
		public DictionaryNode(K key, V value) {
			this.key = key;
			this.value = value;
		}
		
		public int compareTo(DictionaryNode<K,V> node) {
			return ((Comparable<K>)key).compareTo((K)node.key);
		}
	}
	
	private ListADT<DictionaryNode<K,V>> [] list;
	private int tableSize, maxSize;
	private final int DEFAULT_MAX_CAPACITY = 10000;
	protected long modificationCounter;
	
	public Hashtable(){
		modificationCounter = 0;
		tableSize = 0;	
		maxSize = DEFAULT_MAX_CAPACITY;
		list = new LinkedListDS[DEFAULT_MAX_CAPACITY];
	}
	
	public Hashtable(int maxSize) {
		modificationCounter = 0;
		tableSize = 0;
		this.maxSize = maxSize;
		list = new LinkedListDS[maxSize];
		for(int i = 0; i < maxSize; i++)
			list[i] = new LinkedListDS<DictionaryNode<K,V>>();
	}
	
	private int getHashCode(K key) {
		//forces the leading bit of the hash code to be zero, making the number positive
		return (key.hashCode() & 0x7FFFFFFF) % maxSize;
	}
	@Override
	public boolean contains(K key) {
		DictionaryNode<K,V> node = new DictionaryNode<K,V>(key,null);
		int hashVal = getHashCode(key);
		return list[hashVal].contains(node);
	}

	@Override
	public boolean add(K key, V value) {
		if(isFull() || contains(key)) {
			return false;
		}
		DictionaryNode<K,V> compare = new DictionaryNode<K,V>(key,value);
		int hashVal = getHashCode(key);
		list[hashVal].addFirst(compare);
		tableSize++;
		modificationCounter++;
		return true;
		
	}

	@Override
	public boolean delete(K key) {
		if(isEmpty() || !contains(key)) {
			return false;
		}
		DictionaryNode<K,V> compare = new DictionaryNode<K,V>(key,null);
		int hashVal = getHashCode(key);
		list[hashVal].remove(compare);
		tableSize--;
		modificationCounter++;
		return true;
	}

	@Override
	public V getValue(K key) {
		if(isEmpty() || !contains(key)) {
			return null;
		}
		DictionaryNode<K,V> compare = new DictionaryNode<K,V>(key,null);
		int hashVal = getHashCode(key);
		DictionaryNode<K,V> node = list[hashVal].find(compare);
		return node.value;
	}

	@Override
	public K getKey(V value) {
		if(isEmpty()) {
			return null;
		}
		for(int i = 0; i < maxSize; i++) {
			Iterator<DictionaryNode<K,V>> iter = list[i].iterator();
			while(iter.hasNext()) {
				DictionaryNode<K,V> compare = iter.next();
				if(((Comparable<V>)compare.value).compareTo(value) == 0)
					return compare.key;
			}			
		}
		return null;
	}

	@Override
	public int size() {
		return tableSize;
	}

	@Override
	public boolean isFull() {
		return (tableSize == maxSize);
	}

	@Override
	public boolean isEmpty() {
		return (size() == 0);
	}

	@Override
	public void clear() {
		for(int i = 0; i < tableSize; i++) {
			list[i].clear();
		}
		tableSize = 0;
		modificationCounter++;
	}
	private DictionaryNode<K,V>[] mergeSort(DictionaryNode<K,V>[] array){
		DictionaryNode<K,V>[] aux = new DictionaryNode[tableSize];
		for(int i = 0; i < tableSize; i++) {
			aux[i] = array[i];
		}
		mergeSortHelper(aux, 0, tableSize - 1);
		return aux;
	}

	private void mergeSortHelper(DictionaryNode<K,V>[] n, int low, int hi) {
		if(hi-low < 1) return;
		int mid = (low+hi)/2;
		mergeSortHelper(n, low, mid);
		mergeSortHelper(n, mid+1, hi);
		merge(n, low, mid, hi);
	}
	
	private void merge(DictionaryNode<K,V>[] n, int low, int mid, int hi) {
		@SuppressWarnings("unchecked")
		DictionaryNode<K,V>[] aux = new DictionaryNode[hi-low+1];
		int i = low;
		int j = mid+1;
		int k = 0;
		while(i <= mid && j <= hi) {
			if(n[i].compareTo(n[j]) <= 0)
				aux[k] = n[i++];
			else
				aux[k] = n[j++];
			k++;
		}
		if(i <= mid && j > hi) {
			while(i <= mid)
				aux[k++] = n[i++];
		}
			else {
				while(j <= hi)
					aux[k++] = n[j++];
			}
		for(k = 0; k < aux.length; k++) {
			n[k+low] = aux[k];
		}
	}
	
	abstract class IteratorHelper<E> implements Iterator<E> {
		protected DictionaryNode<K,V> [] nodes;
		protected int index;
		protected long stateCheck;
		
		public IteratorHelper() {
			nodes = new DictionaryNode[tableSize];
			index = 0;
			int j = 0;
			stateCheck = modificationCounter;
			for(int i=0; i < maxSize; i++) 
				for(DictionaryNode n : list[i]) 
					nodes[j++] = n;
			
			nodes = mergeSort(nodes);
		}
			
			public boolean hasNext() {
				if(stateCheck != modificationCounter)
					throw new ConcurrentModificationException();
				return index < tableSize;
			}
			
			public abstract E next();
			
			public void remove() {
				throw new UnsupportedOperationException();
			}
	}
	
	
	private class KeyIteratorHelper<K> extends IteratorHelper<K> {
		public KeyIteratorHelper() {
			super();
		}
		
		public K next() {
			return (K) nodes[index++].key;
		}
	}
	
	class ValueIteratorHelper<V> extends IteratorHelper<V> {
		public ValueIteratorHelper() {
			super();
		}
		
		public V next() {
			return (V) nodes[index++].value;
		}
	}
	

	@Override
	public Iterator<K> keys() {
		return new KeyIteratorHelper();
	}

	@Override
	public Iterator<V> values() {
		return new ValueIteratorHelper();
	}
	
	class LinkedListDS<E> implements ListADT<E> {
	    /////////////////////////////////////////////////////////////////
	    class Node<T> {
	        T data;
	        Node<T> next;
	        
	        public Node(T obj) {
	            data = obj;
	            next = null;
	            }
	        }
	    // END CLASS NODE ///////////////////////////////////////////////
	    
	    /////////////////////////////////////////////////////////////////
	    class ListIteratorHelper implements Iterator<E> {        
	        Node<E> index;
	        
	        public ListIteratorHelper() {
	            index = head;
	            }
	            
	        public boolean hasNext() {
	            return index != null;
	            }
	            
	        public E next() {
	            if(!hasNext())
	                throw new NoSuchElementException();
	            E tmp = index.data;
	            index = index.next;
	            return tmp;
	            }
	            
	        public void remove() {
	            throw new UnsupportedOperationException();
	            }
	            
	        }
	    // END CLASS LIST_ITERATOR_HELPER //////////////////////////////
	    
	    
	    private Node<E> head, tail;
	    private int currentSize;
	    
	    public LinkedListDS() {
	        head = tail = null;
	        currentSize = 0;
	        }

	    public void addFirst(E obj) {
	        Node<E> newNode = new Node<E>(obj);
	        if(isEmpty())
	            head = tail = newNode;
	        else {
	            newNode.next = head;            
	            head = newNode;
	            }
	        currentSize++;
	        }
	        
	    public void addLast(E obj) {
	        Node<E> newNode = new Node<E>(obj);
	        if(isEmpty())
	            head = tail = newNode;
	        else {
	            tail.next = newNode;
	            tail = newNode;
	            }
	        currentSize++;
	        }
	        
	    public E removeFirst() {
	        if(isEmpty())
	            return null;
	        E tmp = head.data;
	        head = head.next;
	        if(head == null)
	            head = tail = null;
	        currentSize--;
	        return tmp;
	        }
	        
	    public E removeLast() {
	        if(isEmpty())
	            return null;
	        E tmp = tail.data;
	        if(head == tail) // only one element in the list
	            head = tail = null;
	        else {    
	            Node<E> previous = null, current = head;
	            while(current != tail) {
	                previous = current;
	                current = current.next;
	                }
	            previous.next = null;
	            tail = previous;
	            }
	        
	        currentSize--;
	        return tmp;
	        }
	        
	    public E peekFirst() {
	        if(head == null)
	            return null;
	        return head.data;
	        }
	        
	    public E peekLast() {
	        if(tail == null)
	            return null;
	        return tail.data;
	        }
	        
	    public E find(E obj) {
	        if(head == null) return null;
	        Node<E> tmp = head;
	        while(tmp != null) {
	            if(((Comparable<E>)obj).compareTo(tmp.data) == 0)
	                return tmp.data;
	            tmp = tmp.next;
	            }
	        return null;
	        }        
	        
	    public boolean remove(E obj) {
	        if(isEmpty())
	            return false;
	        Node<E> previous = null, current = head;
	        while(current != null) {
	            if( ((Comparable<E>)current.data).compareTo(obj) == 0) {                
	                if(current == head) 
	                    removeFirst();
	                else if(current == tail) 
	                    removeLast();
	                else {
	                    previous.next = current.next;
	                    currentSize--;
	                    }
	                return true;
	                }
	            previous = current;
	            current = current.next;
	            }
	        return false;
	        }
	     
	// not in the interface.  Removes all instances of the key obj        
	    public boolean removeAllInstances(E obj) {
	        Node<E> previous = null, current = head;
	        boolean found = false;
	        while(current != null) {
	            if(((Comparable<E>)obj).compareTo(current.data) == 0) {
	                if(previous == null) { // node to remove is head
	                    head = head.next;
	                    if(head == null) tail = null;
	                    }
	                else if(current == tail) {
	                    previous.next = null;
	                    tail = previous;
	                    }
	                else 
	                    previous.next = current.next;
	                found = true;
	                currentSize--;
	                current = current.next;
	                }
	            else {
	                previous = current;
	                current = current.next;
	                }
	            } // end while
	        return found;
	        }
	        
	    public void makeEmpty() {
	        head = tail = null;
	        currentSize = 0;
	        }

	    public boolean contains(E obj) {
	        Node current = head;
	        while(current != null) {
	            if( ((Comparable<E>)current.data).compareTo(obj) == 0)
	                return true;
	            current = current.next;
	            }
	        return false;
	        }
	               
	    public boolean isEmpty() {
	        return head == null;
	        }
	        
	    public boolean isFull() {
	        return false;
	        }
	    
		public void clear() {
			currentSize = 0;
			head = tail = null;
			modificationCounter++;
		}
	        
	    public int size() {
	        return currentSize;
	        }
	        
	    public Iterator<E> iterator() {
	        return new ListIteratorHelper();
	        }
	}

	interface ListADT<E> extends Iterable<E> {


	//  Adds the Object obj to the beginning of the list
	    public void addFirst(E obj);

	//  Adds the Object obj to the end of the list
	    public void addLast(E o);

	//  Removes the first Object in the list and returns it.
	//  Returns null if the list is empty.
	    public E removeFirst();

	//  Removes the last Object in the list and returns it.
	//  Returns null if the list is empty.
	    public E removeLast();

	//  Returns the first Object in the list, but does not remove it.
	//  Returns null if the list is empty.
	    public E peekFirst();

	//  Returns the last Object in the list, but does not remove it.
	//  Returns null if the list is empty.
	    public E peekLast();
	    
	//  Finds and returns the Object obj if it is in the list, otherwise
	//  returns null.  Does not modify the list in any way
	    public E find(E obj);

	//  Removes the first instance of thespecific Object obj from the list, if it exists.
	//  Returns true if the Object obj was found and removed, otherwise false
	    public boolean remove(E obj);

	//  The list is returned to an empty state.
	    public void makeEmpty();

	//  Returns true if the list contains the Object obj, otherwise false
	    public boolean contains(E obj);

	//  Returns true if the list is empty, otherwise false
	    public boolean isEmpty();
	    
	    public void clear();

	//  Returns true if the list is full, otherwise false
	    public boolean isFull();

	//  Returns the number of Objects currently in the list.
	    public int size();

	//  Returns an Iterator of the values in the list, presented in
	//  the same order as the list.
	    public Iterator<E> iterator();

	}

}

