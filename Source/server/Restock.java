/**
 * An order transaction. In addition to the regular transaction features,
 * an order is like a purchase but it allows some items to be on backorder.
 */

package Source.server;

public class Restock extends Transaction {
   public Restock(String id, long time, int clientID) {
      super(id, time, clientID);
   }

   /**
    * cancel the transaction
    */
   @Override
   public void cancelTransaction() {
      if(!isComplete()) {
         cancel();
      }
   }

   /**
    *
    * @param  unit the unit to check
    * @return true if yes, alse otherwise
    */
   @Override
   public boolean canCompleteTransaction(TransactionUnit unit) {
      return true;
   }

   /**
    * Complete the restock trnsaction
    * @param unit           one unit from the transaction
    * @param canCompleteAll true if all the units in the transaction returned true for canCompleteTransaction()
    */
   @Override
   public void completeTransaction(TransactionUnit unit, boolean canCompleteAll) {
      if(canCompleteAll){
         int quantity = unit.getQuantity();
         Item item = unit.getItem();
         int backorder = item.getBackorder();
         if(backorder>=quantity){
            item.reduceBackorder(quantity);
            quantity = 0;
         }
         else {
            item.reduceBackorder(backorder);
            quantity -= backorder;
         }

         if(quantity>0){
            item.increaseInStock(quantity);
         }
      }
   }

   @Override
   public POSServer.TransactionType getType() {
      return POSServer.TransactionType.RESTOCK;
   }
}
