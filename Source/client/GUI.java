/**
 * Graphical user interface for the Point of sale system
 */
package Source.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class GUI {
   //The window where everything will be displayed
   private static Window client;


   public static GUIClient getClient(GUIAdapter adapter) {
      if (client == null) {
         client = new Window(adapter);
         client.setTitle("POS Client #1");
         client.pack();
         client.setVisible(true);
      }

      return client;
   }

   /**
    * Private class window
    * Sets up the window interface
    */
   private static class Window extends JFrame implements GUIClient {
      //Search orders
      private static final String[] SEARCH_ORDERS = new String[]{"Code", "Cost", "Description", "Quantity", "Backorder Quantity"};
      //client id
      private final int id;
      private final GUIAdapter client;
      //remember previous search
      private String previousSearch;
      private String transactionID;
      private final JList<String> list;
      //text panels
      private final Window.LabelledTextPanel inventoryCount;
      private final Window.LabelledTextPanel transactionCompletedCount;
      private final Window.LabelledTextPanel tranasctionInProgressCount;
      private final JTextField searchText;
      private final JComboBox<String> searchOrder;
      private final Window.LabelledTextPanel selectedItem;

      /**
       * -------------
       * Buttons
       * --------------
       */
      private final JButton purchaseButton;
      private final JButton returnButton;
      private final JButton backorderButton;
      private final JButton restockButton;
      private final JButton completeButton;
      private final JButton cancelButton;
      private final Window.LabelledTextPanel addQuantity;
      private final JButton addToTransaction;
      private final JTextArea transactionText;
      private final Window.LabelledTextPanel transID;
      private final Window.LabelledTextPanel transType;
      private final Window.LabelledTextPanel transCount;
      private final Window.LabelledTextPanel transQuantity;
      private final Window.LabelledTextPanel transCost;
      private static int lastID = 1;
      private static SimpleDateFormat sdf;

      private Window(GUIAdapter adapter) {
         this.previousSearch = null;
         this.transactionID = null;
         this.id = lastID++;
         this.client = adapter;
         JPanel border = new JPanel(new BorderLayout());
         this.list = new JList<>();
         this.list.setSelectionMode(0);
         this.list.setVisibleRowCount(8);
         JScrollPane scrollPane = new JScrollPane(this.list);
         JPanel flowLayOut = new JPanel(new FlowLayout());
         this.inventoryCount = new LabelledTextPanel("Inventory:", 5, false);
         this.transactionCompletedCount = new LabelledTextPanel("Transactions Completed:", 5, false);
         this.tranasctionInProgressCount = new LabelledTextPanel("Transactions In Progress:", 5, false);
         //
         JButton newClient = new JButton("New Client");
         flowLayOut.add(this.inventoryCount);
         flowLayOut.add(this.transactionCompletedCount);
         flowLayOut.add(this.tranasctionInProgressCount);
         flowLayOut.add(newClient);
         border.add(flowLayOut, "North");
         flowLayOut = new JPanel(new BorderLayout());
         TitledBorder titledBorder = BorderFactory.createTitledBorder("  Inventory  ");
         titledBorder.setTitleJustification(2);
         flowLayOut.setBorder(titledBorder);
         JPanel rightLayOut = new JPanel(new FlowLayout());
         this.searchText = new JTextField(40);
         rightLayOut.add(this.searchText);
         JButton searchButton = new JButton("Search");
         rightLayOut.add(searchButton);
         this.searchOrder = new JComboBox<>(SEARCH_ORDERS);
         rightLayOut.add(this.searchOrder);
         flowLayOut.add(rightLayOut, "North");
         flowLayOut.add(scrollPane, "South");
         border.add(flowLayOut, "Center");
         JPanel bottomBorder = new JPanel(new BorderLayout());
         titledBorder = BorderFactory.createTitledBorder("  Transaction  ");
         titledBorder.setTitleJustification(2);
         bottomBorder.setBorder(titledBorder);
         flowLayOut = new JPanel(new BorderLayout());
         rightLayOut = new JPanel(new FlowLayout());
         rightLayOut.add(new JLabel("Begin"));
         this.purchaseButton = new JButton("Purchase");
         this.returnButton = new JButton("Return");
         this.backorderButton = new JButton("Backorder");
         this.restockButton = new JButton("Restock");
         rightLayOut.add(this.purchaseButton);
         rightLayOut.add(this.returnButton);
         rightLayOut.add(this.backorderButton);
         rightLayOut.add(this.restockButton);
         flowLayOut.add(rightLayOut, "West");
         rightLayOut = new JPanel(new FlowLayout());
         rightLayOut.add(new JLabel("End"));
         this.completeButton = new JButton("Complete");
         this.completeButton.setEnabled(false);
         this.cancelButton = new JButton("Cancel");
         this.cancelButton.setEnabled(false);
         rightLayOut.add(this.completeButton);
         rightLayOut.add(this.cancelButton);
         flowLayOut.add(rightLayOut, "East");
         bottomBorder.add(flowLayOut, "North");
         flowLayOut = new JPanel(new BorderLayout());
         this.selectedItem = new LabelledTextPanel("Selected:", 40, false);
         flowLayOut.add(this.selectedItem, "North");
         rightLayOut = new JPanel(new FlowLayout());
         this.addQuantity = new LabelledTextPanel("Quantity:", 5, true);
         this.addQuantity.setEnabled(false);
         rightLayOut.add(this.addQuantity);
         this.addToTransaction = new JButton("Add");
         this.addToTransaction.setEnabled(false);
         rightLayOut.add(this.addToTransaction);
         flowLayOut.add(rightLayOut, "Center");
         this.transactionText = new JTextArea();
         this.transactionText.setEditable(false);
         this.transactionText.setRows(8);
         flowLayOut.add(new JScrollPane(this.transactionText), "South");
         bottomBorder.add(flowLayOut, "Center");
         flowLayOut = new JPanel(new FlowLayout());
         this.transID = new LabelledTextPanel("ID:", 10, false);
         this.transType = new LabelledTextPanel("Type:", 10, false);
         this.transCount = new LabelledTextPanel("Item Count:", 5, false);
         this.transQuantity = new LabelledTextPanel("Total Quantity:", 5, false);
         this.transCost = new LabelledTextPanel("Total Cost:", 10, false);
         flowLayOut.add(this.transType);
         flowLayOut.add(this.transID);
         flowLayOut.add(this.transCount);
         flowLayOut.add(this.transQuantity);
         flowLayOut.add(this.transCost);
         bottomBorder.add(flowLayOut, "South");
         border.add(bottomBorder, "South");
         this.add(border);
         newClient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
               Window window = new Window(Window.this.client);
               window.setTitle("POS Client #" + window.id);
               window.pack();
               window.setVisible(true);
            }
         });
         ActionListener listener = new ActionListener() {
            public void actionPerformed(ActionEvent var1) {
               if (Window.this.client != null) {
                  Window.this.previousSearch = Window.this.searchText.getText();
                  Window.this.list.setListData(Window.this.client.search(Window.this.previousSearch, Window.this.searchOrder.getSelectedItem().toString().toUpperCase().replace(' ', '_')));
                  Window.this.selectedItem.updateText("");
               }

            }
         };
         searchButton.addActionListener(listener);
         this.searchOrder.addActionListener(listener);
         this.list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
               String value = "";
               int index = Window.this.list.getSelectedIndex();
               if (index >= 0) {
                  value = (Window.this.list.getModel().getElementAt(index)).toString();
               }

               Window.this.selectedItem.updateText(value);
               Window.this.addToTransaction.setEnabled(value.length() > 0 && Window.this.transactionID != null);
            }
         });
         ActionListener listener1 = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
               if (Window.this.client != null) {
                  Window.this.transactionID = Window.this.client.createTransaction(Window.this.id, event.getActionCommand().toUpperCase());
                  Window.this.updateTransaction(Window.this.transactionID);
                  Window.this.purchaseButton.setEnabled(false);
                  Window.this.returnButton.setEnabled(false);
                  Window.this.backorderButton.setEnabled(false);
                  Window.this.restockButton.setEnabled(false);
                  Window.this.completeButton.setEnabled(true);
                  Window.this.cancelButton.setEnabled(true);
                  Window.this.addQuantity.updateText("1");
                  Window.this.addQuantity.setEnabled(true);
                  Window.this.addToTransaction.setEnabled(Window.this.selectedItem.getText().length() > 0);
               }

            }
         };
         this.purchaseButton.addActionListener(listener1);
         this.returnButton.addActionListener(listener1);
         this.backorderButton.addActionListener(listener1);
         this.restockButton.addActionListener(listener1);
         listener1 = new ActionListener() {
            public void actionPerformed(ActionEvent var1) {
               if (Window.this.client != null) {
                  Window.this.client.endTransaction(Window.this.id, var1.getActionCommand().toUpperCase(), Window.this.transactionID);
                  Window.this.updateTransaction((String)null);
                  Window.this.purchaseButton.setEnabled(true);
                  Window.this.returnButton.setEnabled(true);
                  Window.this.backorderButton.setEnabled(true);
                  Window.this.restockButton.setEnabled(true);
                  Window.this.completeButton.setEnabled(false);
                  Window.this.cancelButton.setEnabled(false);
                  Window.this.addQuantity.updateText("");
                  Window.this.addQuantity.setEnabled(false);
                  Window.this.addToTransaction.setEnabled(false);
               }

            }
         };
         this.completeButton.addActionListener(listener1);
         this.cancelButton.addActionListener(listener1);
         listener1 = new ActionListener() {
            public void actionPerformed(ActionEvent var1) {
               if (Window.this.client != null) {
                  String var2 = Window.this.client.addToTransaction(Window.this.id, Window.this.transactionID, Window.this.selectedItem.getText(), Window.this.addQuantity.getText());
                  if (var2 != null) {
                     JOptionPane.showMessageDialog(Window.this.rootPane, var2, "Error", 0);
                  }

                  Window.this.updateTransaction(Window.this.transactionID);
               }

            }
         };
         this.addToTransaction.addActionListener(listener1);
         this.updateStatistics();
         this.client.newClient(this.id, this);
      }

      public void updateStatistics() {
         if (this.client != null) {
            this.inventoryCount.updateText(this.client.getInventoryCount());
            this.transactionCompletedCount.updateText(this.client.getTransactionCompletedCount());
            this.tranasctionInProgressCount.updateText(this.client.getTransactionInProgressCount());
         }

      }

      public void updateInventory() {
         if (this.client != null && this.previousSearch != null) {
            this.list.setListData(this.client.search(this.previousSearch, Objects.requireNonNull(this.searchOrder.getSelectedItem()).toString().toUpperCase().replace(' ', '_')));
         }

      }

      public void updateTransaction(String input) {
         if (this.client != null) {
            this.transactionText.setText(this.client.getTransactionDetails(input));
            this.transID.updateText(input);
            this.transType.updateText(this.client.getTransactionType(input));
            this.transCount.updateText(this.client.getTransactionItemCount(input));
            this.transQuantity.updateText(this.client.getTransactionQuantity(input));
            String cost = this.client.getTransactionCost(input);

            try {
               cost = String.format("%.2f", (double)Integer.parseInt(cost) / 100.0D);
            } catch (Exception exception) {
               System.out.println("Format error!");
            }

            if (cost.length() > 0) {
               cost = "$" + cost;
            }

            this.transCost.updateText(cost);
         }

      }

      public String getTimeStamp() {
         if (sdf == null) {
            sdf = new SimpleDateFormat("yyyymmddHHmmss");
         }

         return sdf.format(Calendar.getInstance().getTime());
      }

      private static class LabelledTextPanel extends JPanel {
         private JTextField textField;

         public LabelledTextPanel(String inputString, int inputValue, boolean didSelect) {
            this.add(new JLabel(inputString), "West");
            this.textField = new JTextField(inputValue);
            if (!didSelect) {
               this.textField.setEditable(false);
               this.textField.setHorizontalAlignment(0);
            }

            this.add(this.textField, "East");
         }

         public void updateText(String text) {
            this.textField.setText(text);
         }

         public String getText() {
            return this.textField.getText();
         }

         public void setEnabled(boolean isEnbled) {
            this.textField.setEnabled(isEnbled);
         }
      }
   }

}
