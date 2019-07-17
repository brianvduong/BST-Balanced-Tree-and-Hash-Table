//Brian Duong
//cssc1468

import java.util.Iterator;
import data_structures.*;
public class StockItem implements Comparable<StockItem> {
	
 String SKU;
 String description;
 String vendor;
 float cost;
 float retail;

 // Constructor. Creates a new StockItem instance.
 public StockItem(String SKU, String description, String vendor,
 float cost, float retail) {
	 this.SKU = SKU;
	 this.description = description;
	 this.vendor = vendor;
	 this.cost = cost;
	 this.retail = retail;
 }

 // Follows the specifications of the Comparable Interface.
 // The SKU is always used for comparisons, in dictionary order.
 public int compareTo(StockItem n) {
	 if(SKU.compareTo(n.SKU) < 0) {
		 return -1;
	 }
	 else if(SKU.compareTo(n.SKU) > 0) {
		 return 1;
	 }
	 else
		 return 0;
 }

 // Returns an int representing the hashCode of the SKU.
 public int hashCode() {
	 byte [] b = SKU.getBytes();
	 long hash, part1 = 0, part2 = 0;
	 long offset = 1;
	 
	 for(int i=0; i < 3; i++) {
		 part1 += (int) b[i]*offset;
		 part2 += (int) b[i+3]*offset;
		 offset <<= 3;
	 }
	 hash = part1 + part2;
	 return (int) hash;
}

 // standard get methods
 public String getDescription() {
	 return description;
 }

 public String getVendor() {
	 return vendor;
 }

 public float getCost() {
	 return cost;
 }

 public float getRetail() {
	 return retail;
 }

 // All fields in one line, in order
 public String toString() {
 	 return SKU + 
 			 ", " + getDescription() + 
 			 ", " + getVendor() + 
 			 ", " + getCost() +
 			 ", " + getRetail();
 }
} 