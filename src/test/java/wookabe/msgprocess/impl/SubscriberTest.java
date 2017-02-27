package wookabe.msgprocess.impl;

import org.junit.Before;
import org.junit.Test;
import wookabe.msgprocess.subscription.Adjustment;
import wookabe.msgprocess.subscription.Message;
import wookabe.msgprocess.subscription.NotAcceptingNewMessages;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static wookabe.msgprocess.Helper.assertTotalValue;

/**
 * Tests of Subscriber.
 */
public class SubscriberTest {
    private Subscriber s;
    private Message oneSale;
    private SalesReportTestLogger salesReportTestLogger;
    private Map<String, Integer> generatedNumberOfSales;
    private Map<String, Double> generatedTotalValues;
    private Message adjustmentAddType1;
    private Message adjustmentSubtractType2;
    private Message adjustmentMultiplyType3;

    @Before
    public void setUp() {
        salesReportTestLogger = new SalesReportTestLogger();
        s = new Subscriber(salesReportTestLogger);
        oneSale = new Message("apples", 100.00);
        generatedNumberOfSales = new HashMap<>();
        generatedTotalValues = new HashMap<>();
        adjustmentAddType1 = new Message("type1", 1000, Adjustment.Type.ADD);
        adjustmentSubtractType2 = new Message("type2", 1000, Adjustment.Type.SUBTRACT);
        adjustmentMultiplyType3 = new Message("type3", 1000, Adjustment.Type.MULTIPLY);
    }

    @Test
    public void whenCreated_thenNoMessages() {
        assertEquals(0, s.getMessages().size());
    }

    @Test
    public void whenAddedOne_thenGetsOne() throws NotAcceptingNewMessages {
        s.receive(oneSale);

        assertEquals(1, s.getMessages().size());
        assertEquals(oneSale, s.getMessages().get(0));
    }

    @Test
    public void when10received_thenLogTotalValuePerProduct() throws Exception {
        generateAndSendMessages("apple", 1);
        generateAndSendMessages("watch", 3);
        generateAndSendMessages("oil", 3);
        generateAndSendMessages("apple", 2);
        generateAndSendMessages("apple", 1);

        assertTrue(salesReportTestLogger.numberOfVisits == 1);
    }

    @Test
    public void when11received_thenLogOnlyOnce() throws Exception {
        generateAndSendMessages("generic", 11);

        assertTrue(salesReportTestLogger.numberOfVisits == 1);
    }

    @Test
    public void when35received_thenLogThreeTimes() throws Exception {
        generateAndSendMessages("generic", 35);

        assertTrue(salesReportTestLogger.numberOfVisits == 3);
    }

    @Test
    public void when50received_thenLogPause() throws Exception {
        generateAndSendMessages("generic", 50);
        assertTrue(salesReportTestLogger.pauseLogged);
    }

    @Test(expected = NotAcceptingNewMessages.class)
    public void when50received_thenStopProcessing() throws Exception {
        generateAndSendMessages("generic", 51);
    }

    @Test
    public void when50received_thenLogAdjustments() throws Exception {
        AdjustmentReportTestLogger logger = new AdjustmentReportTestLogger();
        s = new Subscriber(logger);

        generateAndSendMessages("type1", 10, 1000, 1);
        generateAndSendMessages("type2", 10, 1000, 1);
        generateAndSendMessages("type3", 10, 1000, 1);
        generateAndSendMessages("type4", 10);
        generateAndSendMessages("type5", 7);

        logger.checkSales = true;
        s.receive(adjustmentAddType1);
        s.receive(adjustmentSubtractType2);
        s.receive(adjustmentMultiplyType3);
    }

    /**
     * Helper method to generate messages of random price and number of sales.
     *
     * @param productName name of the product
     * @param n           number of messages to generate
     * @throws Exception needed to catch NotAcceptingNewMessages
     */
    private void generateAndSendMessages(String productName, int n) throws Exception {
        generateAndSendMessages(productName, n, 0, 0);
    }

    /**
     * Helper method to generate messages.
     *
     * @param productName   name of the product
     * @param n             number of messages to generate
     * @param price         fixed unit price
     * @param numberOfSales fixed number of sales
     * @throws Exception needed to catch NotAcceptingNewMessages
     */
    private void generateAndSendMessages(String productName, int n, double price, int numberOfSales) throws Exception {
        final Random random = new Random();
        price = price == 0 ? random.nextDouble() * random.nextInt(100) + 1: price;
        numberOfSales = numberOfSales == 0 ? random.nextInt(100) + 1 : numberOfSales;

        int sumOfNumberOfSales = Optional.ofNullable(generatedNumberOfSales.get(productName)).orElse(0);
        double sumOfTotalValues = Optional.ofNullable(generatedTotalValues.get(productName)).orElse(.0);

        for (int i = 0; i < n; i++) {
            sumOfNumberOfSales += numberOfSales;
            sumOfTotalValues += price * numberOfSales;
            generatedNumberOfSales.put(productName, sumOfNumberOfSales);
            generatedTotalValues.put(productName, sumOfTotalValues);
            s.receive(new Message(productName, price, numberOfSales));
        }
    }

    /**
     * Testing sales report through stub logger.
     */
    private class SalesReportTestLogger implements Logger {
        int numberOfVisits = 0;
        boolean pauseLogged;

        public void logSalesReport(Map<String, Integer> numberOfSales, Map<String, Double> totalValues) {
            numberOfVisits++;
            for (String productName : numberOfSales.keySet()) {
                assertEquals(generatedNumberOfSales.get(productName), numberOfSales.get(productName));
                assertTotalValue(generatedTotalValues.get(productName), totalValues.get(productName));
            }
        }

        public void logPause() {
            pauseLogged = true;
        }

        public void logAdjustmentReport(Map<String, List<Adjustment>> productsToAdjustments) {
            //not used
        }
    }

    /**
     * Testing adjustment report through stub logger.
     */
    private class AdjustmentReportTestLogger implements Logger {
        boolean checkSales;

        public void logAdjustmentReport(Map<String, List<Adjustment>> productsToAdjustments) {
            assertTrue(productsToAdjustments.get("type1").contains(adjustmentAddType1.getAdjustment()));
            assertTrue(productsToAdjustments.get("type2").contains(adjustmentSubtractType2.getAdjustment()));
            assertTrue(productsToAdjustments.get("type3").contains(adjustmentMultiplyType3.getAdjustment()));
        }

        public void logSalesReport(Map<String, Integer> numberOfSales, Map<String, Double> totalValues) {
            if (checkSales) {
                assertTotalValue(generatedTotalValues.get("type1") + 10000, totalValues.get("type1"));
                assertTotalValue(generatedTotalValues.get("type2") - 10000, totalValues.get("type2"));
                assertTotalValue(generatedTotalValues.get("type3") * 1000, totalValues.get("type3"));
            }
        }

        public void logPause() {
            //not used
        }
    }
}
