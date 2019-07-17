//Brian Duong
//cssc1468

import data_structures.*;
import java.util.Iterator;
public class ProductLookup {

	private int maxSize;
	private DictionaryADT<String,StockItem> items;
	
 // Constructor. There is no argument-less constructor, or default size
 public ProductLookup(int maxSize) {
	 this.maxSize = maxSize;
	 items = new Hashtable(maxSize);
 }

 // Adds a new StockItem to the dictionary
 public void addItem(String SKU, StockItem item) {
	 items.add(SKU, item);
 }

 // Returns the StockItem associated with the given SKU, if it is
 // in the ProductLookup, null if it is not.
 public StockItem getItem(String SKU) {
	 return items.getValue(SKU);
 }

 // Returns the retail price associated with the given SKU value.
 // -.01 if the item is not in the dictionary
 public float getRetail(String SKU) {
	 if(items.contains(SKU)) {
		 return items.getValue(SKU).getRetail();
	 }
	 return -.01f;
 }

 // Returns the cost price associated with the given SKU value.
 // -.01 if the item is not in the dictionary
 public float getCost(String SKU) {
	 if(items.contains(SKU)) {
		 return items.getValue(SKU).getCost();
	 }
	 return -.01f;
 }

 // Returns the description of the item, null if not in the dictionary.
 public String getDescription(String SKU) {
	 if(items.contains(SKU)) {
		 return items.getValue(SKU).getDescription();
	 }
	 return null;
 }

 // Deletes the StockItem associated with the SKU if it is
 // in the ProductLookup. Returns true if it was found and
 // deleted, otherwise false.
 public boolean deleteItem(String SKU) {
	 return items.delete(SKU);
 }

 // Prints a directory of all StockItems with their associated
 // price, in sorted order (ordered by SKU).
 public void printAll() {
	 Iterator<StockItem> iterator = values();
	 while(iterator.hasNext()) {
		 System.out.println(":" +iterator.next().getCost());
	 }
 }

 // Prints a directory of all StockItems from the given vendor, 
 // in sorted order (ordered by SKU).
 public void print(String vendor) {
	 Iterator<StockItem> iterator = values();
	 while(iterator.hasNext()) {
		 if(((Comparable<String>)iterator.next().vendor).compareTo(vendor)==0)
				System.out.println(iterator.next());
	 }
 }

 // An iterator of the SKU keys.
 public Iterator<String> keys(){
	 return items.keys();
 }

 // An iterator of the StockItem values.
 public Iterator<StockItem> values(){
	 return items.values();
 }
}