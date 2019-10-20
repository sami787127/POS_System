/**
 * REMARKS: Connects the client with the server
 */

package Source.main;


import Source.client.GUIAdapter;
import Source.client.GUIClient;
import Source.server.POSServer;
import Source.server.ReadServer;

import java.util.ArrayList;
import java.util.HashMap;

public class Adapter implements GUIAdapter {
   private HashMap<Integer, GUIClient> ids; //hash map of client ids
   private ArrayList<GUIClient> clients;      //arraylist of client ids so we can traverse and update on all clients
   private POSServer server;
   public Adapter(){
      ids = new HashMap<>();
      clients = new ArrayList<>();
      server = ReadServer.getServer();
   }

   /**
    * @return inventory count by calling the server
    */
   @Override
   public String getInventoryCount() {
      return server.queryServer(POSServer.ServerQuery.INVENTORY_COUNT);
   }

   /**
    *
    * @return transaction completed count by calling the server
    */
   @Override
   public String getTransactionCompletedCount() {
      return server.queryServer(POSServer.ServerQuery.TRANSACTION_COMPLETED_COUNT);
   }

   /**
    * @return transaction progress count by calling the server
    */
   @Override
   public String getTransactionInProgressCount() {
      return server.queryServer(POSServer.ServerQuery.TRANSACTION_IN_PROGRESS_COUNT);
   }

   /**
    * search for a pattern in the inventory of items
    * @param pattern to be searched
    * @param order in which we will search
    * @return the arryay o all items list
    */
   @Override
   public String[] search(String pattern, String order) {
      if(pattern!=null && order!=null) {
         ArrayList<String> result = new ArrayList<>();
         String singleItem;
         POSServer.ItemField itemField = POSServer.ItemField.CODE;
         if (order.equals("COST")) { //if it iss cost
            itemField = POSServer.ItemField.COST;
         } else if (order.equals("DESCRIPTION")) {
            itemField = POSServer.ItemField.DESCRIPTION;
         } else if (order.equals("QUANTITY")) {
            itemField = POSServer.ItemField.QUANTITY;
         } else if (order.equals("BACKORDER_QUANTITY")) {
            itemField = POSServer.ItemField.BACKORDER_QUANTITY;
         }
         //search the server
         String id = server.search(pattern, itemField);
         //add the result to teh string
         while (server.next(id)) {
            singleItem = server.queryMatch(id, POSServer.ItemField.CODE);
            //add other infos
         singleItem += " ("+server.queryMatch(id, POSServer.ItemField.DESCRIPTION)+")";
         int cost = Integer.parseInt(server.queryMatch(id, POSServer.ItemField.COST));
         singleItem += " ($"+(cost / 100) + "." + (cost % 100 < 10 ? "0" : "") + (cost % 100)+")";
         singleItem+=" (Quantity: "+server.queryMatch(id, POSServer.ItemField.QUANTITY)+")";
         singleItem+=" (Backorder: "+server.queryMatch(id, POSServer.ItemField.BACKORDER_QUANTITY)+")";
            result.add(singleItem);
         }
         //get the string
         String[] info = new String[result.size()];
         result.toArray(info);
         //return the string
         return info;
      } else {
         //if inputs are null
         System.out.println("Invalid pattern or order");
         return new String[0];
      }
   }

   /**
    * get the transaction details
    * @param transactionID transaction id to be detailed
    * @return the details of the transaction
    */
   @Override
   public String getTransactionDetails(String transactionID) {

      if(transactionID!=null) {
         String result;
         result = server.toString(transactionID);
         return result;
      }
      else {
         return "";
      }
   }

   /**
    * get the transaction type from the server
    * @param transactionID of the transction
    * @return type of transaction it is
    */
   @Override
   public String getTransactionType(String transactionID) {
      if(transactionID!=null) {
         String result;
         result = server.queryTransaction(transactionID, POSServer.TransactionQuery.TYPE);
         return result;
      }else {
         return "";
      }
   }

   /**
    * How many items are in the transaction
    * @param transactionID of the transaction
    * @return item count
    */
   @Override
   public String getTransactionItemCount(String transactionID) {
      if(transactionID!=null) {
         String result;
         result = server.queryTransaction(transactionID, POSServer.TransactionQuery.ITEM_COUNT);
         return result;
      }
      else {
         return "";
      }
   }

   /**
    * Get the transaction quantity
    * @param transactionID of the transaction
    * @return total quantity
    */
   @Override
   public String getTransactionQuantity(String transactionID) {
      if(transactionID!=null) {
         String result = server.queryTransaction(transactionID, POSServer.TransactionQuery.TOTAL_QUANTITY);
         return result;
      }
      else {
         return "";
      }
   }

   /**
    * transaction cost of the trnsaction
    * @param transactionID of the transaction
    * @return total cost
    */
   @Override
   public String getTransactionCost(String transactionID) {
      if(transactionID!=null) {
         return server.queryTransaction(transactionID, POSServer.TransactionQuery.TOTAL_COST);
      }
      else {
         return "";
      }
   }

   /**
    * create a transaction with the given type
    * @param clientID of the transaction
    * @param type of the transaction
    * @return the id of the transaction
    */

   @Override
   public String createTransaction(int clientID, String type) {
      if(type!=null) {
         long time = Long.parseLong(ids.get(clientID).getTimeStamp());
         POSServer.TransactionType transactionType = POSServer.TransactionType.PURCHASE;
         if (type.equals("RETURN")) {
            transactionType = POSServer.TransactionType.RETURN;
         } else if (type.equals("BACKORDER")) {
            transactionType = POSServer.TransactionType.BACKORDER;
         } else if (type.equals("RESTOCK")) {
            transactionType = POSServer.TransactionType.RESTOCK;
         }
         //update client after adding
         String id = server.createTransaction(transactionType, time, clientID);
         for (GUIClient client : clients) {
            client.updateStatistics();
            client.updateInventory();
         }
         return id;
      }
      else {
         return "";
      }
   }

   /**
    * add an item to the trasacttion
    * @param clientID of the client
    * @param transactionID of the transaction
    * @param item code
    * @param quantity of the item that to be added
    * @return  error message
    */
   @Override
   public String addToTransaction(int clientID, String transactionID, String item, String quantity) {
      if(transactionID!=null && item!=null && quantity!=null) {
         String[] itemCode = item.split("\\s+");
         String result = server.addItemToTransaction(transactionID, itemCode[0], Integer.parseInt(quantity));
         //update the transaction
         ids.get(clientID).updateTransaction(transactionID);
         return result;
      }
      else {
         return "";
      }

   }

   /**
    * end a transaction (either complete or cancel)
    * @param clientID of the client
    * @param type cancel or complete
    * @param transactionID id of the transaction
    */
   @Override
   public void endTransaction(int clientID, String type, String transactionID) {
      if(type!=null && transactionID!=null) {
         long time = Long.parseLong(ids.get(clientID).getTimeStamp());
         if (type.equals("CANCEL")) {
            server.cancelTransaction(transactionID, time, clientID);
         } else {
            server.completeTransaction(transactionID);
         }
         for (GUIClient client: clients){
            client.updateStatistics();
            client.updateInventory();
         }
      }


   }

   /**
    * create a new client
    * @param clientID of teh client
    * @param client object
    */

   @Override
   public void newClient(int clientID, GUIClient client) {
      ids.put(clientID, client);
      clients.add(client);
   }
}
