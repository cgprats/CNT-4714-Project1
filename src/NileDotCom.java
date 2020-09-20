/*  Name: Christopher Prats
    Course: CNT 4714 - Fall 2020
    Assignment title: Project 1 - Event-driven Enterprise Simulation
    Date: Sunday September 13, 2020
*/
import java.awt.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Integer.parseInt;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.TimeZone;

public class NileDotCom {
    private JButton itemProcessRequest;
    private JPanel Panel;
    private JButton confirmItemRequest;
    private JButton viewOrderRequest;
    private JButton finishOrderRequest;
    private JButton newOrderRequest;
    private JButton exitRequest;
    private JLabel itemNumberLabel;
    private JLabel itemIdLabel;
    private JLabel itemQuantityLabel;
    private JLabel itemInfoLabel;
    private JLabel orderSubtotalLabel;
    private JTextField itemNumberField;
    private JTextField itemIdField;
    private JTextField itemQuantityField;
    private JTextField itemInfoField;
    private JTextField orderSubtotalField;
    private String input;
    private int currentItem = 1;
    private int processedItems = 0;
    private int totalItems = 0;
    private String currentItemId;
    private int currentItemQuantity = 0;
    private int inventorySize = 0;
    private double orderSubtotal = 0.00;
    private boolean itemNotFound = false;
    private String currentItemSaleString;
    private double priceMultiplier;
    private int discountPercent;
    private double currentPrice;
    private ArrayList<String> currentOrder = new ArrayList<String>();
    private ArrayList<String> currentOrderDescription = new ArrayList<String>();
    double taxAmount;
    double orderTotal;
    int inventoryIndex;

    //The GUI and Actual Program Processes
    public NileDotCom() throws FileNotFoundException {
        File inventoryFile = new File("inventory.txt");
        //Determine the Size of the Inventory
        Scanner inventorySizeCounter = new Scanner(inventoryFile);
        while (inventorySizeCounter.hasNext()) {
            inventorySize++;
            inventorySizeCounter.nextLine();
        }
        inventorySizeCounter.close();
        //Import Inventory Items from File
        Item[] inventory = new Item[inventorySize];
        Scanner inventoryImporter = new Scanner(inventoryFile);
        inventoryImporter.useDelimiter(", ");
        String tempData;
        for (int i = 0; i < inventorySize; i++) {
            tempData = inventoryImporter.nextLine();
            String[] splitTempData = tempData.split(", ");
            inventory[i] = new Item(splitTempData[0], splitTempData[1], Double.parseDouble(splitTempData[2]));
        }
        inventoryImporter.close();
        //GUI Operations
        itemProcessRequest.setText("Process Item #1");
        confirmItemRequest.setText("Confirm Item #1");
        viewOrderRequest.setText("View Order");
        finishOrderRequest.setText("Finish Order");
        newOrderRequest.setText("New Order");
        exitRequest.setText("Exit");
        itemIdLabel.setText("Enter item ID for Item #1");
        itemQuantityLabel.setText("Enter quantity for Item #1");
        itemInfoLabel.setText("Item #1 info: ");
        confirmItemRequest.setEnabled(false);
        viewOrderRequest.setEnabled(false);
        finishOrderRequest.setEnabled(false);
        itemProcessRequest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                itemNotFound = true;
                input = itemNumberField.getText();
                totalItems = parseInt(input);
                itemNumberField.setEditable(false);
                currentItemId = itemIdField.getText();
                input = itemQuantityField.getText();
                currentItemQuantity = parseInt(input);
                for (int i = 0; i < inventory.length; i++) {
                    if (inventory[i].GetItemId().compareTo(currentItemId) == 0) {
                        //Calculate Discount Rate
                        if (currentItemQuantity >= 1 && currentItemQuantity <= 4) {
                            priceMultiplier = 1.0;
                            discountPercent = 0;
                        } else if (currentItemQuantity >= 5 && currentItemQuantity <= 9) {
                            priceMultiplier = 0.9;
                            discountPercent = 10;
                        } else if (currentItemQuantity >= 10 && currentItemQuantity <= 14) {
                            priceMultiplier = 0.85;
                            discountPercent = 15;
                        } else if (currentItemQuantity >= 15) {
                            priceMultiplier = 0.8;
                            discountPercent = 20;
                        }
                        //Edge Case where User Enters Negative or 0 Items: Display Price as 0
                        else {
                            priceMultiplier = 0.0;
                            discountPercent = 0;
                        }
                        //Calculate Current Price
                        currentPrice = inventory[i].GetItemPrice() * currentItemQuantity * priceMultiplier;
                        currentItemSaleString = inventory[i].GetItemId() + " " + inventory[i].GetItemDescription() + " $" + inventory[i].GetItemPrice() + " " + currentItemQuantity + " " + discountPercent + "% " + String.format("%.2f", currentPrice);
                        itemInfoField.setText(currentItemSaleString);
                        inventoryIndex = i;
                        itemNotFound = false;
                    }
                }
                itemProcessRequest.setEnabled(false);
                confirmItemRequest.setEnabled(true);
            }
        });
        confirmItemRequest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //If the item exists, process the item and display success message
                if (!itemNotFound) {
                    JOptionPane.showMessageDialog(null, "Item #" + currentItem + " accepted");
                    currentItem++;
                    processedItems++;
                    if (processedItems > 0) {
                        viewOrderRequest.setEnabled(true);
                    }
                    itemProcessRequest.setEnabled(true);
                    finishOrderRequest.setEnabled(true);
                    confirmItemRequest.setEnabled(false);
                    itemProcessRequest.setText("Process Item #" + currentItem);
                    confirmItemRequest.setText("Process Item #" + currentItem);
                    itemIdLabel.setText("Enter item ID for Item #" + currentItem);
                    itemQuantityLabel.setText("Enter quantity for Item #" + currentItem);
                    itemInfoLabel.setText("Item #" + currentItem + " info: ");
                    orderSubtotalLabel.setText("Order subtotal for " + processedItems + " item(s): ");
                    currentOrder.add(currentItemSaleString);
                    currentOrderDescription.add(inventory[inventoryIndex].GetItemDescription());
                    orderSubtotal += currentPrice;
                    orderSubtotalField.setText(String.format("%.2f", orderSubtotal));
                }
                //If the item does not exist, display a message stating that it does not exist
                else {
                    JOptionPane.showMessageDialog(null, "item ID " + currentItemId + " not in file");
                    itemProcessRequest.setEnabled(true);
                    confirmItemRequest.setEnabled(false);
                }
                itemIdField.setText("");
                itemQuantityField.setText("");
                //Disable adding new items when order limit is reached
                if (currentItem > totalItems) {
                    itemProcessRequest.setEnabled(false);
                    itemIdField.setEditable(false);
                    itemQuantityField.setEditable(false);
                    itemIdLabel.setText("");
                    itemQuantityLabel.setText("");
                    itemProcessRequest.setText("Process Item");
                    confirmItemRequest.setText("Confirm Item");
                }
            }
        });
        //Actions for the view order button
        viewOrderRequest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Create the string to display
                String showOrder = "";
                for (int i = 0; i < currentOrder.size(); i++) {
                    showOrder += (i + 1) + ". " + currentOrder.get(i) + "\n";
                }
                //Display the String
                JOptionPane.showMessageDialog(null, showOrder);
            }
        });
        //Actions for the finish order button
        finishOrderRequest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Disable itemIdField, itemQuantityField, and itemProcessRequest button
                itemIdField.setEditable(false);
                itemQuantityField.setEditable(false);
                itemProcessRequest.setEnabled(false);
                //Display the invoice
                DateTimeFormatter invoiceFormat = DateTimeFormatter.ofPattern("dd/MM/YY hh:mm:ss a");
                String showOrder = "Date: " + invoiceFormat.format(LocalDateTime.now()) + " " + TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT) + "\n";
                showOrder += "Number of line items: " + currentOrder.size() + "\n";
                showOrder += "Item # / ID / Title / Price /Qty / Disc % / Subtotal: \n";
                for (int i = 0; i < currentOrder.size(); i++) {
                    showOrder += (i + 1) + ". " + currentOrder.get(i) + "\n";
                }
                showOrder += "Order Subtotal: $" + String.format("%.2f", orderSubtotal) + "\n";
                showOrder += "Tax rate: 6%" + "\n";
                taxAmount = orderSubtotal * 0.06;
                orderTotal = orderSubtotal + taxAmount;
                showOrder += "Tax amount: $" + String.format("%.2f", taxAmount) + "\n";
                showOrder += "Order total: $" + String.format("%.2f", orderTotal) + "\n";
                showOrder += "Thanks for shopping at Nile Dot Com!";
                JOptionPane.showMessageDialog(null, showOrder);
                //Write the transaction file
                FileWriter transactionFile = null;
                try {
                    transactionFile = new FileWriter("transaction.txt", true);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                DateTimeFormatter transactionFileFormat = DateTimeFormatter.ofPattern("ddMMYYHHmm");
                DateTimeFormatter dateTransactionFile = DateTimeFormatter.ofPattern("dd/MM/YY, hh:mm:ss a");
                String transactionString = "";
                int length;
                for (int i = 0; i < currentOrder.size(); i++) {
                    transactionString = "";
                    String[] splitTempData = currentOrder.get(i).split(" ");
                    length = splitTempData.length;
                    transactionString += transactionFileFormat.format(LocalDateTime.now()) + ", ";
                    transactionString += splitTempData[0] + ", " + currentOrderDescription.get(i) + ", " + splitTempData[length - 4] + ", " + splitTempData[length - 3] + ", " + splitTempData[length - 2] + ", " + splitTempData[length - 1] + ", ";
                    transactionString += dateTransactionFile.format(LocalDateTime.now()) + " " + TimeZone.getDefault().getDisplayName(true, TimeZone.SHORT) + "\n";
                    try {
                        transactionFile.write(transactionString);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                try {
                    transactionFile.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        //Reset the GUI and revert Changes to Variables Caused by Program Operation
        newOrderRequest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentOrder.clear();
                currentOrderDescription.clear();
                itemNumberField.setText("");
                itemNumberField.setEditable(true);
                itemIdField.setEditable(true);
                itemQuantityField.setEditable(true);
                itemProcessRequest.setText("Process Item #1");
                confirmItemRequest.setText("Confirm Item #1");
                itemIdLabel.setText("Enter item ID for Item #1");
                itemQuantityLabel.setText("Enter quantity for Item #1");
                itemInfoLabel.setText("Item #1 info: ");
                itemInfoField.setText("");
                orderSubtotalField.setText("");
                itemProcessRequest.setEnabled(true);
                confirmItemRequest.setEnabled(false);
                viewOrderRequest.setEnabled(false);
                finishOrderRequest.setEnabled(false);
                currentItem = 1;
                processedItems = 0;
                orderSubtotalLabel.setText("Order subtotal for " + processedItems + " item(s): ");
                totalItems = 0;
                orderSubtotal = 0.00;
            }
        });
        //Exit the Program when the User Presses the Exit Button
        exitRequest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    //The Main Program (Purpose is solely to launch NileDotCom GUI and Processes
    public static void main(String[] args) throws FileNotFoundException {
        JFrame frame = new JFrame("Nile Dot Com - Fall 2020");
        frame.setContentPane(new NileDotCom().Panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setMinimumSize(new Dimension(1024, 256));
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        Panel = new JPanel();
        Panel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(6, 6, new Insets(0, 0, 0, 0), -1, -1));
        itemProcessRequest = new JButton();
        itemProcessRequest.setText("Button");
        Panel.add(itemProcessRequest, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        confirmItemRequest = new JButton();
        confirmItemRequest.setText("Button");
        Panel.add(confirmItemRequest, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        viewOrderRequest = new JButton();
        viewOrderRequest.setText("Button");
        Panel.add(viewOrderRequest, new com.intellij.uiDesigner.core.GridConstraints(5, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        finishOrderRequest = new JButton();
        finishOrderRequest.setText("Button");
        Panel.add(finishOrderRequest, new com.intellij.uiDesigner.core.GridConstraints(5, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        newOrderRequest = new JButton();
        newOrderRequest.setText("Button");
        Panel.add(newOrderRequest, new com.intellij.uiDesigner.core.GridConstraints(5, 4, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exitRequest = new JButton();
        exitRequest.setText("Button");
        Panel.add(exitRequest, new com.intellij.uiDesigner.core.GridConstraints(5, 5, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        itemNumberLabel = new JLabel();
        itemNumberLabel.setText("Enter number of items in this order: ");
        Panel.add(itemNumberLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        itemIdLabel = new JLabel();
        itemIdLabel.setText("Enter item ID for item #: ");
        Panel.add(itemIdLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        itemQuantityLabel = new JLabel();
        itemQuantityLabel.setText("Enter quantity for item #: ");
        Panel.add(itemQuantityLabel, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        itemInfoLabel = new JLabel();
        itemInfoLabel.setText("Item # Info: ");
        Panel.add(itemInfoLabel, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        orderSubtotalLabel = new JLabel();
        orderSubtotalLabel.setText("Order subtotal for 0 item(s): ");
        Panel.add(orderSubtotalLabel, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        itemNumberField = new JTextField();
        Panel.add(itemNumberField, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        itemIdField = new JTextField();
        Panel.add(itemIdField, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        itemQuantityField = new JTextField();
        Panel.add(itemQuantityField, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        itemInfoField = new JTextField();
        itemInfoField.setEditable(false);
        Panel.add(itemInfoField, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        orderSubtotalField = new JTextField();
        orderSubtotalField.setEditable(false);
        Panel.add(orderSubtotalField, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return Panel;
    }

}
//Class for Inventory Items
class Item {
    String _itemId;
    String _itemDescription;
    double _itemPrice;
    //Construct an Inventory Item
    Item(String itemId, String itemDescription, double itemPrice) {
        _itemId = itemId;
        _itemDescription = itemDescription;
        _itemPrice = itemPrice;
    }
    //Retrieve the Item Id
    public String GetItemId() {
        return _itemId;
    }
    //Retrieve the Item Description
    public String GetItemDescription() {
        return _itemDescription;
    }
    //Retrieve the Item Price
    public double GetItemPrice() {
        return _itemPrice;
    }
}