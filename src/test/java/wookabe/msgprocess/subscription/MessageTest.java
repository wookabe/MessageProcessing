package wookabe.msgprocess.subscription;

import org.junit.Before;
import org.junit.Test;
import wookabe.msgprocess.subscription.Message;

import static org.junit.Assert.assertEquals;
import static wookabe.msgprocess.Helper.assertTotalValue;

/**
 * Tests of Message.
 */
public class MessageTest {
    private Message oneSale;
    private Message multipleSales;

    @Before
    public void setUp() throws Exception {
        oneSale = new Message("apple", .10);
        multipleSales = new Message("apple", .10, 20);
    }

    @Test
    public void whenOneSale_thenNumberOfSalesIsOne() {
        assertEquals(1, oneSale.getNumberOfSales());
    }

    @Test
    public void whenOneSale_thenTotalValueEqualsPrice() {
        assertTotalValue(0.10, oneSale.getTotalValue());
    }

    @Test
    public void whenMultipleSales_thenNumberOfSalesIsMultiplier() {
        assertEquals(20, multipleSales.getNumberOfSales());
    }

    @Test
    public void whenMultipleSales_thenTotalValueEqualsPriceTimesMultiplier() {
        assertTotalValue(2.00, multipleSales.getTotalValue());
    }
}