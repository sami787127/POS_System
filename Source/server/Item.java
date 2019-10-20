/**

 * A class representing an item in the inventory. Includes the item
 * code, its description, and its cost.
 */

package Source.server;
import java.util.Comparator;

class Item {

   private String code; //item code
   private String description;   //item description
   private int cost; //cost of the unit to purchase
   private int inStock; //in stock availability of the item
   private int backorder;  //backorder quantity of this item

   public Item(String code, String description, int cost, int quantity) {
      this.code = code;
      this.description = description;
      this.cost = cost;
      this.inStock = quantity;
   }

   /**
    * Determine if the given code matches this item.
    * 
    * @param code the code to check
    * @return true if the code matches this item; false otherwise
    */
   public boolean matchCode(String code) {
      return code.equals(this.code);
   }

   public void reduceInStock(int amount) {
      assert amount <= inStock;
      assert amount >= 0;
      inStock -= amount;
   }

   /**
    *increase ampunt in stock
    * @param amount to be increased
    */
   public void increaseInStock(int amount){
      assert amount>=0;
      inStock += amount;
   }

   /**
    * increase backorder
    * @param amount to be increased
    */
   public void increaseBackorder(int amount) {
      assert inStock == 0;
      assert amount >= 0;
      backorder += amount;
   }

   /**
    * reduce backorder from the item
    * @param amount  to be reduced
    */
   public void reduceBackorder(int amount){
      assert amount>=0;
      assert amount<=backorder;
      backorder -= amount;
   }

   /**
    * @return item code
    */
   public String getCode() {
      return code;
   }

   /**
    * @return get cost
    */
   public int getCost() {
      return cost;
   }

   /**
    * @return in stock quantity
    */
   public int getInStock() {
      return inStock;
   }

   /**
    * @return backorder quanitity
    */
   public int getBackorder() {
      return backorder;
   }

   /**
    * @return description of the item
    */
   public String getDescription() {
      return description;
   }

   /**
    * checks if a pattern is matched with this item
    * @param pattern to be macthed
    * @return  true if macthed, false otherwise
    */
   public boolean matches(String pattern) {
      return code.toLowerCase().contains(pattern.toLowerCase()) || description.toLowerCase().contains(pattern.toLowerCase());
   }

   @Override
   public String toString() {
      return "Code: " + code + " description: " + description + " cost: $" + (cost / 100) + "." + (cost % 100 < 10 ? "0" : "") + (cost % 100) + " (" + inStock + ")";
   }

   /**
    * get the iterator with matched pattern
    * @param order the order in which we will sort
    * @return iterator
    */
   public static Comparator<Item> getComparator(POSServer.ItemField order) {
      // This returns one of four comparators. To prevent class explosion,
      // the comparators are defined as "anonymous inner classes" (declared
      // and created on the fly).
      switch(order) {
      case CODE:
         return new Comparator<Item>() {
            public int compare(Item a, Item b) {
               return a.code.compareTo(b.code);
            }
         };
      case COST:
         return new Comparator<Item>() {
            public int compare(Item a, Item b) {
               return a.cost - b.cost;
            }
         };
      case DESCRIPTION:
         return new Comparator<Item>() {
            public int compare(Item a, Item b) {
               return a.description.compareTo(b.description);
            }
         };
      case QUANTITY:
         return new Comparator<Item>() {
            public int compare(Item a, Item b) {
               return a.inStock - b.inStock;
            }
         };
      case BACKORDER_QUANTITY:
         return new Comparator<Item>() {
            public int compare(Item a, Item b) {
               return a.backorder - b.backorder;
            }
         };
      }
      // Should never get here
      return null;
   }
}
