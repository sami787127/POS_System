/**
 * An iterator used on the server to traverse the list of inventory items.
 * Keeps a local copy of the list (because it specifies limited matches and ordering).
 */
package Source.server;
import java.util.*;


class ItemIterator {
   private static int lastID = 0;
   
   private int id;
   private int position;
   private Comparator<Item> comp;
   private ArrayList<Item> items;
   
   public ItemIterator(Comparator<Item> comp) {
      lastID++;
      id = lastID;

      position = -1;
      this.comp = comp;
      items = new ArrayList<Item>(); 
   }
   
   /**
    * Add an item to the list being iterated over. Ordered by the comparator.
    * 
    * @param item the item to add
    */
   public void add(Item item) {
      // Ordered insertion
      for (int i = 0; i < items.size(); i++)
         if (comp.compare(items.get(i), item) > 0) {
            items.add(i, item);
            return;
         }
      items.add(item);
   }

   public boolean hasNext() {
      return position < (items.size() - 1);
   }
   
   public Item next() {
      if (position >= items.size() - 1) {
         return null;
      }
      position++;
      return items.get(position);
   }
   
   public Item current() {
      if (position >= items.size() || position < 0) {
         return null;
      }
      return items.get(position);
   }
   
   public String getID() {
      return "" + id;
   }
}
