

package Source.client;

public interface GUIClient {
   void updateStatistics();
   void updateInventory();
   void updateTransaction(String transactionID);
   
   String getTimeStamp();
}
