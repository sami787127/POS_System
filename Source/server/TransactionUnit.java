/**
 * An item in a transaction. Points to the item in the inventory and the
 * quantity.
 */
package Source.server;

class TransactionUnit {
   //item in the transaction
   private Item item;
   //quanitty this transaction includes
   private int quantity;
   
   public TransactionUnit(Item item, int quantity) {
      this.item = item;
      this.quantity = quantity;
   }

   /**
    *
    * @return item
    */
   public Item getItem() {
      return item;
   }

   /**
    *
    * @return quanitty in this transaction
    */
   public int getQuantity() {
      return quantity;
   }

   /**
    * change the quantity of the transaction
    * @param quantity quanity to change
    * @return true if success, false otherwise
    */
   public boolean changeQuantity(int quantity) {
      if (quantity < 0) {
         return false;
      }
      
      this.quantity = quantity;
      return true;
   }
   
   /**
    * Get the total value of this transaction item.
    * 
    * @return the cost of the item times the quantity
    */
   public int getTotalCost() {
      return item.getCost() * quantity;
   }

   /**
    * @return info about the transaction unit
    */
   @Override
   public String toString() {
      return item.getCode() + " (" + quantity + ")";
   }
}
