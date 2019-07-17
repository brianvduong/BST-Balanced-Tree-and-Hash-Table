//Brian Duong
//cssc1468

package data_structures;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class BinarySearchTree<K extends Comparable<K>,V> implements DictionaryADT<K,V>{

	private class Node<K,V>{
		private K key;
		private V value;
		private Node<K,V> leftChild;
		private Node<K,V> rightChild;
		
		public Node(K k, V v) {
			key = k;
			value = v;
			leftChild = rightChild = null;
		}
		
		public int compareTo(Node<K,V> node) {		//compareTo of Nodes for mergesort
			return ((Comparable<K>)key).compareTo((K)node.key);
		}
	}
	
	private Node<K,V> root;
	private int currentSize;
	private boolean usedSuccessorLast = false;
	protected long modificationCounter;
	
	public BinarySearchTree() {
		root = null;
		currentSize = 0;
	}
	
	public BinarySearchTree(Node<K,V> r, int s) {
		root = r;
		currentSize = s;
	}
	
	@Override
	public boolean contains(K key) {
		return (findValue(key, root) != null);

	}

	@Override
	public boolean add(K key, V value) {		
		if(isFull() || contains(key)) {		//checks for duplicates
			return false;
		}
		if(root == null)
			root = new Node<K,V>(key,value);
		else
			insert(key,value,root,null,false);
		currentSize++;
		modificationCounter++;
		return true;
	}
	
	private void insert(K k, V v, Node<K,V> n, Node<K,V> parent, boolean wasLeft) {
		if (n == null) {
			if(wasLeft)
				parent.leftChild = new Node<K,V>(k,v);
			else
				parent.rightChild = new Node<K,V>(k,v);
		}
		else if(((Comparable<K>)k).compareTo((K)n.key) < 0)
			insert (k,v,n.leftChild,n,true);		// go left
		else
			insert (k,v,n.rightChild,n,false);		// go right
	}

	@Override
	public boolean delete(K key) {
		return delete(key,root,null,false);
	}
	
    private boolean delete(K key, Node<K,V> n, Node<K,V> parent, boolean left){	
        if(n == null) 
        	return false;
        if(((Comparable<K>)key).compareTo(n.key) < 0) {			//go left
        	parent = n;
			left = true;										//left determines the direction of traversal
			return delete(key, n.leftChild, parent, left);
		}	
		else if(((Comparable<K>)key).compareTo(n.key) > 0) {	//go right 
			parent = n;
			left = false;
			return delete(key, n.rightChild, parent, left);
		}
        else {					
            if(n.leftChild == null && n.rightChild == null) {	//if there are no children
            	if(parent == null)
            		root = null;
            	else if(left)
            		parent.leftChild = null;
            	else
            		parent.rightChild = null;
                currentSize--;
                modificationCounter++;
                return true;
            }
            else if(n.leftChild == null) {						//if there is a right child
            	if(parent == null)
            		root = n.rightChild;
            	else if(left)
            		parent.leftChild = n.rightChild;
            	else
            		parent.rightChild = n.rightChild;
                currentSize--;
                modificationCounter++;
                return true;
            }
            else if(n.rightChild == null) {						//if there is left child
            	if(parent == null)
            		root = n.leftChild;
            	else if(left)
            		parent.leftChild = n.leftChild;
            	else
            		parent.rightChild = n.leftChild;
                currentSize--;
                modificationCounter++;
                return true;
            }
            else {		
            	Node<K,V> successor = max(n.rightChild);		//if there are 2 children, gets successor
        		if(successor == null){
        		    n.key = n.rightChild.key;
        		    n.value = n.rightChild.value;
        		    n.rightChild = n.rightChild.rightChild;
        		}
        		else{
        		    n.key = successor.key;
        		    n.value = successor.value;
        		}
                currentSize--;
                modificationCounter++;
                return true;
           	}      
        }
     }
	
    private Node<K,V> max(Node<K,V> n){		//finds the successor for 2 children case
    	Node<K,V> parent = null;
    	while(n.leftChild != null){
    		parent = n;
    		n = n.leftChild;
    	}
    	if(parent == null)
    		return null;
    	else
    		parent.leftChild = n.rightChild;
    	return n;
    }
	
	@Override
	public V getValue(K key) {
		return findValue(key,root);
	}
	
	private V findValue(K key, Node<K,V> n) {
		if(n == null)
			return null;
		if(((Comparable<K>)key).compareTo(n.key) < 0)
			return findValue(key, n.leftChild);			//go left
		if(((Comparable<K>)key).compareTo(n.key) > 0)
			return findValue(key, n.rightChild);		//go right
		return (V) n.value;								//found!
	}

	@Override
	public K getKey(V value) {
		return findKey(value, root);
	}
	
	private K findKey(V value, Node<K,V> n) {
		if(n == null)
			return null;
		if(((Comparable<V>)value).compareTo(n.value) < 0)	//go left
			return findKey(value, n.leftChild);
		if(((Comparable<V>)value).compareTo(n.value) > 0)	//go right
			return findKey(value, n.rightChild);
		return (K) n.key;									//found
	}

	@Override
	public int size() {
		return currentSize;
	}

	@Override
	public boolean isFull() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return currentSize == 0;
	}

	@Override
	public void clear() {
		root = null;
		currentSize = 0;
		modificationCounter++;
	}
	
	private void preOrder(Node<K,V> n, Node<K,V>[]array, int i) {
		if(n != null) {						//returns the list of nodes in preorder traversal
			array[i] = n;
			preOrder(n.leftChild, array, ++i);
			preOrder(n.rightChild,array, ++i);
		}
	}
	
	private Node<K,V>[] mergeSort(Node<K,V>[] array){	//mergesort for nodes
		Node<K,V>[] aux = new Node[currentSize];
		for(int i = 0; i < currentSize; i++) {
			aux[i] = array[i];
		}
		mergeSortHelper(aux, 0, currentSize - 1);
		return aux;
	}

	private void mergeSortHelper(Node<K,V>[] n, int low, int hi) {
		if(hi-low < 1) return;
		int mid = (low+hi)/2;
		mergeSortHelper(n, low, mid);
		mergeSortHelper(n, mid+1, hi);
		merge(n, low, mid, hi);
	}
	
	private void merge(Node<K,V>[] n, int low, int mid, int hi) {
		@SuppressWarnings("unchecked")
		Node<K,V>[] aux = new Node[hi-low+1];
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

	@Override
	public Iterator<K> keys() {
		return new KeyIteratorHelper();
	}

	@Override
	public Iterator<V> values() {
		return new ValueIteratorHelper();
	}
	
	abstract class IteratorHelper<E> implements Iterator<E> {
		protected Node<K,V>[] nodes;
		protected int index;
		protected long stateCheck;
		
		public IteratorHelper() {
			nodes = (Node<K,V>[]) new Node[currentSize];
			index = 0;
			stateCheck = modificationCounter;
			preOrder(root, nodes, 0);
			nodes = mergeSort(nodes);	
		}
		
		public boolean hasNext() {
			if (stateCheck != modificationCounter)
				throw new ConcurrentModificationException();
			return index < currentSize;
		}
		
		public E next() {
			if(!hasNext())
				throw new NoSuchElementException();
			return (E) nodes[index++];
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	
	class KeyIteratorHelper<K> extends IteratorHelper<K> {
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

}
