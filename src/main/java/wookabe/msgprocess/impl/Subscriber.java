package wookabe.msgprocess.impl;

import wookabe.msgprocess.subscription.Adjustment;
import wookabe.msgprocess.subscription.Message;
import wookabe.msgprocess.subscription.NotAcceptingNewMessages;
import wookabe.msgprocess.subscription.Receivable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Message processing and storing facility.
 */
public class Subscriber implements Receivable {
    /**
     * Number of received messages after which the sales report is generated
     */
    private static final int SALES_REPORT_TRIGGER = 10;

    /**
     * Number of received messages after which the adjustment report is generated
     */
    private static final int ADJUSTMENT_REPORT_TRIGGER = 50;

    private List<Message> messages;
    private Logger logger;
    private boolean paused;

    public Subscriber() {
        this(new ConsoleLogger());
    }

    public Subscriber(Logger logger) {
        this.logger = logger;
        messages = new ArrayList<>();
        paused = false;
    }

    /**
     * Reception of a message.
     *
     * @param msg message to process
     * @throws NotAcceptingNewMessages thrown if no new message is accepted
     */
    @Override
    public void receive(Message msg) throws NotAcceptingNewMessages {
        pauseSubscriberIfLimitReached();

        validateMessage(msg);
        storeMessage(msg);

        adjustMessagesIfApplicable(msg);
        reportMessagesIfApplicable();
    }

    /**
     * If limit of messages reached put the subscriber in paused mode.
     *
     * @throws NotAcceptingNewMessages
     */
    private void pauseSubscriberIfLimitReached() throws NotAcceptingNewMessages {
        if (paused)
            throw new NotAcceptingNewMessages(ADJUSTMENT_REPORT_TRIGGER);
        if (messages.size() >= ADJUSTMENT_REPORT_TRIGGER - 1) {
            paused = true; // no restart possible at the moment
        }
    }

    /**
     * Validate correctness of the message.
     * Could be done in Message constructor alternatively.
     *
     * @param msg message to validate
     */
    private void validateMessage(Message msg) {
        if (msg == null)
            throw new IllegalArgumentException("Message cannot be null");
        if (msg.getProductName() == null || msg.getProductName().isEmpty())
            throw new IllegalArgumentException("Product name cannot be null or empty");
        if (msg.getNumberOfSales() <= 0)
            throw new IllegalArgumentException("Number of sales must be greater than 0");
        if (msg.getTotalValue() <= 0)
            throw new IllegalArgumentException("Total value must be greater than 0");
    }

    /**
     * Store message.
     *
     * @param msg message to be stored
     */
    private void storeMessage(Message msg) {
        messages.add(msg);
    }

    /**
     * If the message is an adjustment message then apply the adjustment on all stored messages.
     *
     * @param msg possible adjustment messages
     */
    private void adjustMessagesIfApplicable(Message msg) {
        if (msg.getAdjustment() != null)
            messages.stream().
                    filter(m -> m.getProductName().equals(msg.getProductName())).
                    forEach(m -> m.adjust(msg.getAdjustment()));
    }

    /**
     * Report on messages if applicable.
     */
    private void reportMessagesIfApplicable() {
        if (messages.size() % SALES_REPORT_TRIGGER == 0)
            logSalesReport();
        if (messages.size() % ADJUSTMENT_REPORT_TRIGGER == 0) {
            logger.logPause();
            logAdjustmentReport();
        }
    }

    /**
     * Log adjustment report.
     */
    private void logAdjustmentReport() {
        Map<String, List<Adjustment>> productsToAdjustments = new HashMap<>();
        messages.stream().
                filter(m -> m.getAdjustment() != null).
                forEach(m -> {
                    String pName = m.getProductName();
                    productsToAdjustments.putIfAbsent(pName, new ArrayList<>());
                    productsToAdjustments.get(pName).add(m.getAdjustment());
                });
        logger.logAdjustmentReport(productsToAdjustments);
    }

    /**
     * Log sales report.
     */
    private void logSalesReport() {
        Map<String, Integer> numberOfSales = new HashMap<>();
        messages.stream().
                filter(m -> m.getAdjustment() == null).
                forEach(m -> {
                    String pName = m.getProductName();
                    numberOfSales.put(pName, numberOfSales.getOrDefault(pName, 0) + m.getNumberOfSales());
                });

        Map<String, Double> totalValues = new HashMap<>();
        messages.stream().
                filter(m -> m.getAdjustment() == null).
                forEach(m -> {
                    String pName = m.getProductName();
                    totalValues.put(pName, totalValues.getOrDefault(pName, .0) + m.getTotalValue());
                });

        logger.logSalesReport(numberOfSales, totalValues);
    }

    /**
     * Gets the list of messages for testing purposes at the moment (package-private).
     *
     * @return list of all received messages
     */
    List<Message> getMessages() {
        return messages;
    }
}
