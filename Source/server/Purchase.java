/**
 * A purchase transaction.
 */
package Source.server;

class Purchase extends Transaction {

   public Purchase(String id, long time, int clientID) {
      super(id, time, clientID);
   }

   /**
    * return type of transaction it is
    * @return  type of transaction
    */
   @Override
   public POSServer.TransactionType getType() {
      return POSServer.TransactionType.PURCHASE;
   }

   /**
    *check if enough quantity exist in store to complete the transacion
    * @param  unit the unit to check
    * @return  true if can complete or false otherwise
    */
   @Override
   public boolean canCompleteTransaction(TransactionUnit unit) {
      return unit.getQuantity() <= unit.getItem().getInStock();
   }

   /**
    * completes the transction
    * @param unit           one unit from the transaction
    * @param canCompleteAll true if all the units in the transaction returned true for canCompleteTransaction()
    */
   @Override
   public void completeTransaction(TransactionUnit unit, boolean canCompleteAll) {
      if (canCompleteAll) {
         unit.getItem().reduceInStock(unit.getQuantity());
      } else {
         // clear the transaction
         unit.changeQuantity(0);
      }
   }

   /**
    * Cancel the transaction
    */
   @Override
   public void cancelTransaction() {
      cancel(); //mark as cancelled
      if(isComplete()){
         //if it is completed transaciton then return the items
         returnItems();
      }
   }

   //print the info about this transction
   @Override
   public String toString() {
      return "Purchase " + super.toString();
   }
}
