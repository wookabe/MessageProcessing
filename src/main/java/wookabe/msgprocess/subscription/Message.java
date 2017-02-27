package wookabe.msgprocess.subscription;

/**
 * Representation of a message.
 */
public class Message {

    /**
     * Name of the product
     */
    private String productName;

    /**
     * Unit price of the product
     */
    private double price;

    /**
     * Number of sales for the product
     */
    private int numberOfSales;

    /**
     * Possible adjustment. Null if not applicable.
     */
    private Adjustment adjustment;

    public Message(String productName, double price) {
        this(productName, price, 1);
    }

    public Message(String productName, double price, int numberOfSales) {
        this.productName = productName;
        this.numberOfSales = numberOfSales;
        this.price = price;
        this.adjustment = null;
    }

    public Message(String productName, double price, Adjustment.Type adjustmentType) {
        this(productName, price);
        this.adjustment = new Adjustment(adjustmentType, price);
    }

    /**
     * Apply adjustment to the message price.
     *
     * @param adjustment adjustment to be applied
     */
    public void adjust(Adjustment adjustment) {
        if (this.adjustment == null) // don't adjust the adjustment
            this.price = adjustment.apply(this.price);
    }

    //
    // Properties
    //

    public String getProductName() {
        return productName;
    }

    public double getTotalValue() {
        return numberOfSales * price;
    }

    public int getNumberOfSales() {
        return numberOfSales;
    }

    public Adjustment getAdjustment() {
        return adjustment;
    }
}