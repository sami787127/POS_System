/**
 * COMP 2150 Summer 2019: Assignment 5 Question 1 "Point of Sale"
 */

package Source.client;

public interface GUIAdapter {
   // These are called by updateStatistics()
   String getInventoryCount();
   String getTransactionCompletedCount();
   String getTransactionInProgressCount();

   // This is called by the Search button and updateInventory() 
   String[] search(String pattern, String order);
   
   // These are called by updateTransaction()
   String getTransactionDetails(String transactionID);
   String getTransactionType(String transactionID);
   String getTransactionItemCount(String transactionID);
   String getTransactionQuantity(String transactionID);
   String getTransactionCost(String transactionID);
   
   // These are called during a transaction:
   String createTransaction(int clientID, String type);
   String addToTransaction(int clientID, String transactionID, String item, String quantity);
   void endTransaction(int clientID, String type, String transactionID);
   
   // This is called when a new client is created
   void newClient(int clientID, GUIClient client);
}
