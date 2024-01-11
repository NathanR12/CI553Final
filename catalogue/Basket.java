package catalogue;
import java.util.Comparator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Formatter;
import java.util.Locale;
import java.util.Collections;
import java.util.Observer;
/**
 * A collection of products from the CatShop.
 *  used to record the products that are to be/
 *   wished to be purchased.
 * @author  Mike Smith University of Brighton
 * @version 2.2
 *
 */
public class Basket extends ArrayList<Product> implements Serializable
{
  private static final long serialVersionUID = 1;
  private int    theOrderNum = 0;          // Order number
  
  /**
   * Constructor for a basket which is
   *  used to represent a customer order/ wish list
   */
  public Basket()
  {
    theOrderNum  = 0;
  }
  
  /**
   * Set the customers unique order number
   * Valid order Numbers 1 .. N
   * @param anOrderNum A unique order number
   */
  public void setOrderNum( int anOrderNum )
  {
    theOrderNum = anOrderNum;
  }

  /**
   * Returns the customers unique order number
   * @return the customers order number
   */
  public int getOrderNum()
  {
    return theOrderNum;
  }
  
  /**
   * Add a product to the Basket.
   * Product is appended to the end of the existing products
   * in the basket.
   * @param pr A product to be added to the basket
   * @return true if successfully adds the product
   */
  // Will be in the Java doc for Basket
  @Override
  public boolean add( Product pr ) {
	  if (pr != null && pr.isValid()) {
		  int existingIndex = indexOfProduct(pr.getProductNum());
  if (existingIndex != -1) {
      // If the product exists, update the quantity
      Product existingProduct = get(existingIndex);
      existingProduct.setQuantity(existingProduct.getQuantity() + pr.getQuantity());
  } else {                              
    super.add( pr );     // Call add in ArrayList
  }
  // Sort the basket in ascending order based on product numbers
  Collections.sort(this, Comparator.comparing(Product::getProductNum));

  return true;
	  }
	  return false;
	  
}
  private int indexOfProduct(String productNum) {
      for (int i = 0; i < size(); i++) {
          Product pr = get(i);
          if (pr.getProductNum().equalsIgnoreCase(productNum)) {
              return i;
          }
      }
      return -1;
  }
  /**
   * Returns a description of the products in the basket suitable for printing.
   * @return a string description of the basket products
   */
  public String getDetails()
  {
    Locale uk = Locale.UK;
    StringBuilder sb = new StringBuilder(256);
    Formatter     fr = new Formatter(sb, uk);
    String csign = (Currency.getInstance( uk )).getSymbol();
    double total = 0.00;
    if ( theOrderNum != 0 )
      fr.format( "Order number: %03d\n", theOrderNum );
      
    if ( this.size() > 0 )
    {
      for ( Product pr: this )
      {
        int number = pr.getQuantity();
        fr.format("%-7s",       pr.getProductNum() );
        fr.format("%-14.14s ",  pr.getDescription() );
        fr.format("(%3d) ",     number );
        fr.format("%s%7.2f",    csign, pr.getPrice() * number );
        fr.format("\n");
        total += pr.getPrice() * number;
      }
      fr.format("----------------------------\n");
      fr.format("Total                       ");
      fr.format("%s%7.2f\n",    csign, total );
      fr.close();
    }
    return sb.toString();
  }
  public void doRemove() {
      if (!isEmpty()) {
          remove(size() - 1);  // Remove the last item
          Collections.sort(this, Comparator.comparing(Product::getProductNum)); // Sort the basket in ascending order based on product numbers
      }
  }
}