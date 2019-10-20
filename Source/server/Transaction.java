/**
 * An abstract transaction (currently, a purchase or return). Includes the transaction
 * ID, the time the transaction started, and a list of the items and their quantities.
 */
package Source.server;

import java.util.HashMap;
import java.util.Map;

abstract class Transaction {
   //id of the client who created this transaction
   private int clientID;
   //id of this transaction
   private String id;
   //time when trans was created
   private long time;
   //items in the transaction
   private Map<String, TransactionUnit> units;
   //complete mark
   private boolean complete;
   //cancel mark
   private boolean cancelled;

   public Transaction(String id, long time, int clientID) {
      this.id = id;
      this.time = time;
      this.units = new HashMap<>();
      this.complete = false;  //item is not completed at the beginnign
      this.clientID = clientID;
      cancelled = false;   //item is not cancelled at the beginning
   }

   /**
    * Add an item to the transaction.
    * 
    * @param  item the inventory item to add
    * @param  quantity the quantity of the item
    * @return true if the add was successful or false if not (invalid quantity)
    */
   public boolean addItem(Item item, int quantity) {
      boolean result = true;

      TransactionUnit unit = units.get(item.getCode());
      if (unit == null) {
         if (quantity < 0) {
            result = false;
         } else {
            units.put(item.getCode(), new TransactionUnit(item, quantity));
         }
      } else {
         if (unit.getQuantity() + quantity < 0) {
            result = false;
         } else if (unit.getQuantity() + quantity == 0) {
            units.remove(item.getCode());
         } else {
            unit.changeQuantity(unit.getQuantity() + quantity);
         }
      }
      
      return result;
   }

   public abstract void cancelTransaction();

   /**
    * Determine if it is possible to complete the transaction for this unit.
    * 
    * @param  unit the unit to check
    * @return true if the transaction can be completed, or false otherwise
    */
   public abstract boolean canCompleteTransaction(TransactionUnit unit);

   /**
    * Complete the transaction for this unit.
    * 
    * @param unit           one unit from the transaction
    * @param canCompleteAll true if all the units in the transaction returned true for canCompleteTransaction()
    */
   public abstract void completeTransaction(TransactionUnit unit, boolean canCompleteAll);


   /**
    * Complete the transaction.
    */
   public void complete() {
      assert !complete;
      
      boolean canComplete = true;
      
      for (TransactionUnit unit: units.values()) {
         if (!canCompleteTransaction(unit)) {
            canComplete = false;
            break;
         }
      }
      
      for (TransactionUnit unit: units.values()) {
         completeTransaction(unit, canComplete);
      }

      complete = true;
   }

   /**
    * Determine if the given ID matches the transaction ID.
    * 
    * @param id the id to check
    * @return true if it is the same as the transaction ID; false otherwise
    */
   public boolean matchID(String id) {
      return id.equals(this.id);
   }

   public String getID() {
      return id;
   }

   public boolean isComplete() {
      return complete;
   }

   public abstract POSServer.TransactionType getType();

   public int itemCount() {
      return units.size();
   }

   /**
    * Get the total quantity of the transaction.
    * 
    * @return the sum of the quantities of the items in the transaction
    */
   public int totalQuantity() {
      int count = 0;
      for (TransactionUnit unit: units.values()) {
         count += unit.getQuantity();
      }
      return count;
   }

   /**
    * Get the total cost of the transaction.
    * 
    * @return the sum of the cost of the items times quantities in the transaction
    */
   public int totalCost() {
      int total = 0;

      for (TransactionUnit item : units.values())
         total += item.getTotalCost();

      return total;
   }

   /**
    * Return a summmary of the transaction
    * @return summary of transaction
    */
   @Override
   public String toString() {
      int cost = totalCost();
      String result = "ID: " + id + "\nTime: " + time + "\nItems:";
      // "ID: " + id + " time: " + time + "\n items: " + units;
      //ArrayList<TransactionUnit> unitsTemp = new ArrayList<>();
      Object[] unitsTemp = units.values().toArray();
      for (int i = 0; i <unitsTemp.length ; i++) {
         result += "\t"+unitsTemp[i].toString()+"\n";
      }
      result += "Value: $" + (cost / 100) + "." + (cost % 100 < 10 ? "0" : "") + (cost % 100);
      return result;
   }

   //get the client id
   public int getClientID(){
      return clientID;
   }

   //get if this transction is cancelled
   public boolean isCancelled(){
      return cancelled;
   }
   //set cancel
   public void cancel(){
      cancelled = true;
   }

   /**
    * returns all the items in this transaction back to inventory
    */
   public void returnItems(){
      //set complete false since we are cancelling
      changeComplete();
      for (TransactionUnit unit: units.values()) {
         //increase the quantity in stock
         unit.getItem().increaseInStock(unit.getQuantity());
      }
   }
   //set the complete to false
   public void changeComplete(){
      complete = false;
   }
}
