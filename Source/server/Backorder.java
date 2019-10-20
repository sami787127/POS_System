/**

 * An order transaction. In addition to the regular transaction features,
 * an order is like a purchase but it allows some items to be on backorder.
 */
package Source.server;

import java.util.ArrayList;

class Backorder extends Transaction {
   private ArrayList<TransactionUnit> backOrder;

   public Backorder(String id, long time, int clientID) {
      super(id, time, clientID);
      backOrder = new ArrayList<TransactionUnit>();
   }
   /**
    * return type of transaction it is
    * @return  type of transaction
    */
   @Override
   public POSServer.TransactionType getType() {
      return POSServer.TransactionType.BACKORDER;
   }

   /**
    *check if enough quantity exist in store to complete the transacion
    * @param  unit the unit to check
    * @return  true if can complete or false otherwise
    */
   @Override
   public boolean canCompleteTransaction(TransactionUnit unit) {
      // Can always complete a backorder
      return true;
   }
   /**
    * completes the transction
    * @param unit           one unit from the transaction
    * @param canCompleteAll true if all the units in the transaction returned true for canCompleteTransaction()
    */
   @Override
   public void completeTransaction(TransactionUnit unit, boolean canCompleteAll) {
      //get the quantity
      int transactionQuantity = unit.getQuantity();
      //get the item
      Item item = unit.getItem();
      int itemQuantity = item.getInStock();
      
      if (transactionQuantity <= itemQuantity) {
         //if enough item exist then purchase
         item.reduceInStock(transactionQuantity); 
      } else {
         // backorder if we do not have enough item
         int backorderQuantity = transactionQuantity - itemQuantity;
         item.reduceInStock(itemQuantity);
         item.increaseBackorder(backorderQuantity);
      }
   }
   
   /**
    * Make the leftovers from an unit in a transaction into a back order.
    * 
    * @param unit the unit that is back ordered
    * @param quantity the quantity of the back order
    * @return true if there is nothing left in the original unit; false otherwise
    */
   public boolean makeBackOrder(TransactionUnit unit, int quantity) {
      backOrder.add(new TransactionUnit(unit.getItem(), quantity));
      return unit.getQuantity() == 0;
   }

   /**
    * cancel the transaction
    */
   @Override
   public void cancelTransaction() {
      cancel();
      if(isComplete()) {
         //Reduce all the backorder quantity from the inventory
         for (TransactionUnit unit : backOrder) {
            unit.getItem().reduceBackorder(unit.getQuantity());
         }
         //return all the purchased items
         returnItems();
      }
   }

   /**
    * @return summary of this transaction
    */
   @Override
   public String toString() {
      return "Backorder " + super.toString() + (backOrder.size() > 0 ? "\n back order: " + backOrder : "");
   }
}
