package clients.cashier;

import catalogue.Basket;
import catalogue.Product;
import debug.DEBUG;
import middle.*;

import java.util.Observable;
import javax.swing.JOptionPane;

/**
 * Implements the Model of the cashier client
 * Author: Mike Smith University of Brighton
 * Version: 2.0
 */
public class CashierModel extends Observable {
    private enum State { process, checked }

    private State       theState   = State.process;   // Current state
    private Product     theProduct = null;            // Current product
    private Basket      theBasket  = null;            // Bought items

    private String      pn = "";                      // Product being processed

    private StockReadWriter theStock     = null;
    private OrderProcessing theOrder     = null;

    /**
     * Construct the model of the Cashier
     * @param mf The factory to create the connection objects
     */
    public CashierModel(MiddleFactory mf) {
        try {
            theStock = mf.makeStockReadWriter();
            theOrder = mf.makeOrderProcessing();
        } catch (Exception e) {
            DEBUG.error("CashierModel.constructor\n%s", e.getMessage());
        }
        theState   = State.process;
    }

    public Basket getBasket() {
        return theBasket;
    }

    public void doCheck(String productNum) {
        String theAction = "";
        theState = State.process;                  // State process
        pn = productNum.trim();                    // Product no.
        int amount = 1;                            // & quantity
        try {
            if (theStock.exists(pn))               // Stock Exists?
            {                                      // T
                Product pr = theStock.getDetails(pn); // Get details
                if (pr.getQuantity() >= amount)      // In stock?
                {                                    // T
                    theAction =                           // Display
                            String.format("%s : %7.2f (%2d) ", //
                                    pr.getDescription(),          // description
                                    pr.getPrice(),                // price
                                    pr.getQuantity());            // quantity
                    theProduct = pr;                      // Remember prod.
                    theProduct.setQuantity(amount);       // & quantity
                    theState = State.checked;             // OK await BUY
                } else {                                // F
                    theAction =                           // Not in Stock
                            pr.getDescription() + " not in stock";
                }
            } else {                                  // F Stock exists
                theAction =                             // Unknown
                        "Unknown product number " + pn;       // product no.
            }
        } catch (StockException e) {
            DEBUG.error("%s\n%s",
                    "CashierModel.doCheck", e.getMessage());
            theAction = e.getMessage();
        }
        setChanged();
        notifyObservers(theAction);
    }

    public void doBuy() {
        String theAction = "";
        int amount = 1;                         // & quantity
        try {
            if (theState != State.checked)      // Not checked with customer
            {                                    // with customer
                theAction = "Check if OK with customer first";
            } else {
                boolean stockBought =            // Buy however
                        theStock.buyStock(        // may fail
                                theProduct.getProductNum(),   // product number
                                theProduct.getQuantity());   // quantity
                if (stockBought)                   // Stock bought
                {                                      // T
                    makeBasketIfReq();                    // new Basket ?
                    theBasket.add(theProduct);            // Add to bought
                    theAction = "Purchased " +           // details
                            theProduct.getDescription();   //
                } else {                                // F
                    theAction = "!!! Not in stock";       // Now no stock
                }
            }
        } catch (StockException e) {
            DEBUG.error("%s\n%s",
                    "CashierModel.doBuy", e.getMessage());
            theAction = e.getMessage();
        }
        theState = State.process;                // All Done
        setChanged();
        notifyObservers(theAction);
    }

    private void makeBasketIfReq() {
        if (theBasket == null) {
            try {
                int uon = theOrder.uniqueNumber();         // Unique order num.
                theBasket = makeBasket();                  // basket list
                theBasket.setOrderNum(uon);                // Add an order number
            } catch (OrderException e) {
                DEBUG.error("Comms failure\n" +
                        "CashierModel.makeBasket()\n%s", e.getMessage());
            }
        }
    }
    protected Basket makeBasket() {
        return new Basket();
    }


    public void doBought(String contactNumber) {
        try {
            if (theBasket != null && theBasket.size() >= 1) {
                theOrder.newOrder(theBasket);  // Updated: Removed contactNumber parameter
                theBasket = null;
            }
            setChanged();
            notifyObservers("Next customer");
            theState = State.process;
        } catch (OrderException e) {
            DEBUG.error("%s\n%s", "CashierModel.doBought", e.getMessage());
            setChanged();
            notifyObservers(e.getMessage());
        }
    }

    public void doRemove() {
        // Implementation for removing an item from the basket
    }

    public void askForUpdate() {
        setChanged();
        notifyObservers("Welcome");
    }
}
