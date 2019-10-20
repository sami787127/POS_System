/**

 * This interface defines the communication between the point of sale client
 * and the server. Messages to the server are sent as method calls, and
 * information is returned as return values.
 *
 */

package Source.server;

public interface POSServer {
   enum TransactionType {
      PURCHASE, RETURN, BACKORDER, RESTOCK
   }

   enum TransactionQuery {
      TYPE, ITEM_COUNT, TOTAL_QUANTITY, TOTAL_COST, IS_COMPLETE
   }

   enum ServerQuery {
      INVENTORY_COUNT, TRANSACTION_COMPLETED_COUNT, TRANSACTION_IN_PROGRESS_COUNT 
   }

   public enum ItemField {
     CODE, COST, DESCRIPTION, QUANTITY, BACKORDER_QUANTITY
   }

   /**
    * Construct a new transaction of the given type.
    *
    * @param  type the type of transaction
    * @param  time the date and time of the transaction
    * @return the transaction ID, or null on error
    */
   String createTransaction(TransactionType type, long time, int client);

   /**
    * Add a new item to a transaction, or change its quantity. If the item code is
    * already found in the transaction, add the given quantity to the item's
    * transaction quantity. The item's transaction quantity may be reduced (with a
    * negative quantity parameter) but it cannot be reduced below zero. If the
    * quantity reaches zero exactly, remove the item from the transaction.
    *
    * @param  id       the transaction ID
    * @param  item     the item code
    * @param  quantity the number of that item to add to the transaction 
    * @return an error message, or null on success
    */
   String addItemToTransaction(String id, String item, int quantity);

   /**
    * Complete a transaction.
    *
    * @param  id the transaction ID
    * @return an error message, or null on success
    */
   String completeTransaction(String id);

   /**
    * Query some feature of a transaction.
    *
    * @param  id    the transaction ID
    * @param  query identifies the value requested
    * @return determined by the requested value; or null on error
    */
   String queryTransaction(String id, TransactionQuery query);

   /**
    * Show the details of a transaction.
    *
    * @param  id the transaction ID
    * @return a string describing a transaction or null on error
    *         (ID, type, time, all the items, and whether or not it is complete)
    */
   String toString(String id);

   /**
    * Query some feature of the server.
    *
    * @param  query identifies the value requested
    * @return determined by the requested value; or null on error
    */
   String queryServer(ServerQuery query);

   /**
    * Describe the inventory of items for sale. Order the list alphabetically
    * by item code.
    *
    * @return a string describing the inventory on the server
    *         (one item per line), ordered by item code
    */
   String toString();

   /**
    * Begin a search through item codes and descriptions.
    *
    * @param  pattern the string to search for (partial matches OK)
    * @param  order   the order that the results will be presented by the iterator
    * @return the iterator ID
    */
   String search(String pattern, ItemField order);
   
   /**
    * Get the next match for the search. Call this until it returns
    * false; when it returns true, there is a matching item available.
    * Call it once before the first match is available, and repeatedly
    * for each subsequent match.
    *
    * @param  iID the iterator ID
    * @return true if the search has more matches; false if done
    */
   boolean next(String iID);
   
   /**
    * Query some feature of the currently-iterated item.
    *
    * @param  iID the iterator ID
    * @return determined by the requested value; or null on error
    */
   String queryMatch(String iID, ItemField query);
   
   /**
    * Cancel an existing transaction.
    * Must be cancelled by the client that made the transaction.
    *
    * @param  id the transaction ID
    * @param  client the client ID
    * @return an error message, or null on success
    */
   String cancelTransaction(String id, long time, int client);
}