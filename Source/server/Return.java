/**
 * A return transaction.
 */
package Source.server;

class Return extends Transaction {

   public Return(String id, long time, int clientID) {
      super(id, time, clientID);
   }

   @Override
   public POSServer.TransactionType getType() {
      return POSServer.TransactionType.RETURN;
   }
   /**
    *check if enough quantity exist in store to complete the transacion
    * @param  unit the unit to check
    * @return  true if can complete or false otherwise
    */
   @Override
   public boolean canCompleteTransaction(TransactionUnit unit) {
      // Can always complete a return
      return true;
   }
   /**
    * completes the transction
    * @param unit           one unit from the transaction
    * @param canCompleteAll true if all the units in the transaction returned true for canCompleteTransaction()
    */
   @Override
   public void completeTransaction(TransactionUnit unit, boolean canCompleteAll) {
      // has no effect on inventory
   }
   /**
    * cancel the transaction
    */
   @Override
   public void cancelTransaction() {
      cancel();
      if(isComplete()) {
         changeComplete();
      }
   }
   /**
    * @return summary of this transaction
    */
   @Override
   public String toString() {
      return "Return " + super.toString();
   }
}
