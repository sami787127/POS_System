/**
 * Server class implementing the POSServer interface. Stores inventory
 * and transactions.
 */

package Source.server;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class Server implements POSServer {
   private TreeMap<String, Item> inventory;
   private HashMap<String, Transaction> transactions;
   private HashMap<String, ItemIterator> iterators;
   private static int lastID = 1;

   /**
    * Get the initial inventory from the given file.
    * 
    * @param inventoryFile the name of the file to read inventory from
    */
   public Server(File inventoryFile) {
      inventory = new TreeMap<>();
      transactions = new HashMap<>();
      iterators = new HashMap<>();
      
      String msg = readInventory(inventoryFile);
      if (msg != null) {
         System.out.println(msg);
      }
   }

   /**
    * Read the contents of a file into the inventory.
    * 
    * @param inventoryFile the name of the file
    * @return null if successful; otherwise, a message describing the error(s)
    */
   private String readInventory(File inventoryFile) {
      String msg = "";
      BufferedReader in;
      String line;
      String[] tokens;
      int cost, quantity;
      
      try {
         in = new BufferedReader(new FileReader(inventoryFile));
         
         line = in.readLine();
         while (line != null) {
            tokens = line.split(",");
            if (tokens.length != 4) {
               msg += "Invalid line: " + line + "\n";
            } else {
               cost = -1;
               quantity = -1;
               try {
                  cost = Integer.parseInt(tokens[2]);
                  quantity = Integer.parseInt(tokens[3]);
               } catch (NumberFormatException nfe) {
               }
               if (cost < 0)
                  msg += "Invalid cost: " + line + "\n";
               else if (quantity < 0)
                  msg += "Invalid quantity: " + line + "\n";
               else
                  inventory.put(tokens[0], new Item(tokens[0], tokens[1], cost, quantity));
            }
            line = in.readLine();
         }
      } catch (IOException ioe) {
         msg += ioe.getMessage();
      }
      
      return msg.length() == 0 ? null : msg;
   }

   /**
    * Construct a new transaction of the given type.
    *
    * @param  type the type of transaction
    * @param  time the date and time of the transaction
    * @param client the client id who created the transaction
    * @return the transaction ID, or null on error
    */
   public String createTransaction(TransactionType type, long time, int client) {
      Transaction t = null;
      //create the id
      String tID = "" + lastID;

      switch (type) {
      case PURCHASE:
         //create purchase
         t = new Purchase(tID, time, client);
         break;
      case RETURN:
         //create return
         t = new Return(tID, time, client);
         break;
      case BACKORDER:
         //create backorder
         t = new Backorder(tID, time, client);
         break;

      case RESTOCK:
         t= new Restock(tID, time, client);
      }
      lastID++;
      //add the id to the transaction
      transactions.put(tID, t);
      
      return t.getID();
   }
   /**
    * Add a new item to a transaction, or change its quantity. If the item code is
    * already found in the transaction, add the given quantity to the item's
    * transaction quantity. The item's transaction quantity may be reduced (with a
    * negative quantity parameter) but it cannot be reduced below zero. If the
    * quantity reaches zero exactly, remove the item from the transaction.
    *
    * @param  id       the transaction ID
    * @param code    the item id
    * @param  quantity the number of that item to add to the transaction
    * @return an error message, or null on success
    */
   public String addItemToTransaction(String id, String code, int quantity) {
      String result = null;
      Transaction trans = transactions.get(id);
      Item item;
      
      if (trans == null) {
         result = "Unable to find transaction " + id + " to add an item";
      } else if (code == null) {
         result = "Invalid item code";
      } else if (trans.isComplete()) {
         result = "Transaction " + id + " already completed";
      } else {
         item = inventory.get(code);
         if (item == null) {
            result = "Unable to find item " + code + " in inventory";
         } else {
            if (!trans.addItem(item, quantity)) {
               result = "Invalid quantity " + quantity + " of item " + code;
            }
         }
      }

      return result;
   }
   /**
    * Complete a transaction.
    *
    * @param  id the transaction ID
    * @return an error message, or null on success
    */
   public String completeTransaction(String id) {
      String result = null;
      Transaction t = transactions.get(id);
      
      if (t == null)
         result = "Unable to find transaction " + id;
      else if (t.isComplete())
         result = "Transaction already completed " + id;
      else
         t.complete();

      return result;
   }
   /**
    * Query some feature of a transaction.
    *
    * @param  id    the transaction ID
    * @param  query identifies the value requested
    * @return determined by the requested value; or null on error
    */
   public String queryTransaction(String id, TransactionQuery query) {
      String result = null;
      Transaction t = transactions.get(id);
      
      if (t != null) {
         switch (query) {
         case TYPE:
            result = t.getType().toString();
            break;
         case ITEM_COUNT:
            result = Integer.toString(t.itemCount());
            break;
         case TOTAL_QUANTITY:
            result = Integer.toString(t.totalQuantity());
            break;
         case TOTAL_COST:
            result = Integer.toString(t.totalCost());
            break;
         case IS_COMPLETE:
            result = Boolean.toString(t.isComplete());
            break;
         }
      }

      return result;
   }
   /**
    * Show the details of a transaction.
    *
    * @param  id the transaction ID
    * @return a string describing a transaction or null on error
    *         (ID, type, time, all the items, and whether or not it is complete)
    */
   public String toString(String id) {
      String result = null;
      Transaction t = transactions.get(id);
      
      if (t != null)
         result = t.toString();
      
      return result;
   }
   /**
    * Query some feature of the server.
    *
    * @param  query identifies the value requested
    * @return determined by the requested value; or null on error
    */
   public String queryServer(ServerQuery query) {
      String result = null;

      switch (query) {
      case INVENTORY_COUNT:
         result = Integer.toString(inventory.size());
         break;
      case TRANSACTION_COMPLETED_COUNT:
         result = Integer.toString(countCompleteTransactions());
         break;
      case TRANSACTION_IN_PROGRESS_COUNT:
         result = Integer.toString(transactions.size() - countCompleteTransactions()-countCancelTrans());
         break;
      }
      
      return result;
   }

   /**
    * Private method that counts all the completed transaction
    * @return number of completed transaction
    */
   private int countCompleteTransactions() {
      int count = 0;
      for (Map.Entry<String,Transaction> entry: transactions.entrySet()) {
         if (entry.getValue().isComplete()) {
            count++;
         }
      }
      return count;
   }

   private int countCancelTrans(){
      int count = 0;
      for(Map.Entry<String, Transaction> entry: transactions.entrySet()){
         if(entry.getValue().isCancelled()){
            count++;
         }
      }

      return count;
   }

   /**
    * Describe the inventory of items for sale. Order the list alphabetically
    * by item code.
    *
    * @return a string describing the inventory on the server
    *         (one item per line), ordered by item code
    */
   @Override
   public String toString() {
      String result = "";
      
      for (Map.Entry<String,Item> entry: inventory.entrySet()) {
         result += entry.getValue().toString() + "\n";
      }

      return result;
   }

   /**
    * Begin a search through item descriptions.
    *
    * @param pattern the string to search for (partial matches OK)
    * @param order the order that the results will be presented by the iterator
    * @return the iterator ID
    */
   @Override
   public String search(String pattern, ItemField order) {
      if (pattern == null) {
         return null;
      }
      
      ItemIterator ii = new ItemIterator(Item.getComparator(order));
      
      iterators.put(ii.getID(), ii);
      
      for (Map.Entry<String,Item> entry: inventory.entrySet()) {
         Item item = entry.getValue();
         if (item.matches(pattern))
            ii.add(item);
      }
      
      return ii.getID();
   }

   /**
    * Get the next match for the search. Call this until it returns
    * false; when it returns true, there is a matching item available.
    * Call it once before the first match is available, and repeatedly
    * for each subsequent match.
    *
    * @param iID the iterator ID
    * @return true if the search has more matches; false if done
    */
   @Override
   public boolean next(String iID) {
      ItemIterator ii = iterators.get(iID);
      if (ii != null && ii.hasNext()) {
         ii.next();
         return true;
      }
      iterators.remove(iID);
      return false;
   }

   /**
    * Query some feature of the currently-iterated item.
    *
    * @param  iID the iterator ID
    * @return determined by the requested value; or null on error
    */
   @Override
   public String queryMatch(String iID, ItemField query) {
      String result = null;
      ItemIterator ii = iterators.get(iID);

      //print out the
      if (ii != null && ii.current() != null) {
         Item item = ii.current();

         switch (query) {
         case CODE:
            result = item.getCode();
            break;
         case COST:
            result = "" + item.getCost();
            break;
         case DESCRIPTION:
            result = item.getDescription();
            break;
         case QUANTITY:
            result = "" + item.getInStock();
            break;
         case BACKORDER_QUANTITY:
            result = "" + item.getBackorder();
            break;
         }
      }
      
      return result;
   }

   /**
    * cancel a given transaction
    * @param  id the transaction ID
    * @param time time of the tranaction
    * @param  client the client ID
    * @return
    */

   @Override
   public String cancelTransaction(String id, long time, int client) {
      String message = "Can not find the transaction";
      //get the transaction
      Transaction trans = transactions.get(id);

      if(trans!=null){
         //if transaction is already cancelled print out the msg
         if(trans.isCancelled()){
            message = "Transaction is already cancelled";
         }
         else if(trans.getClientID()==client){
            //cancel transaction
            trans.cancelTransaction();
            //return null on success
            message = null;
         }

         else {
            message="Current client can not cancel this transaction";
         }
      }


      return message;
   }
}
