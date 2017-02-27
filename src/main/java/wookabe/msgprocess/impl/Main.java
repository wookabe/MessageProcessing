package wookabe.msgprocess.impl;

import wookabe.msgprocess.subscription.Adjustment;
import wookabe.msgprocess.subscription.Message;
import wookabe.msgprocess.subscription.NotAcceptingNewMessages;

import java.util.Random;

/**
 * Simple usage of Subscriber.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Subscriber subscriber = new Subscriber();

        generateAndSendMessages(subscriber, "apple", 10);
        generateAndSendMessages(subscriber, "watch", 10);
        generateAndSendMessages(subscriber, "oil", 10);

        sendAdjustment(subscriber, "apple", 10, Adjustment.Type.MULTIPLY);

        generateAndSendMessages(subscriber, "watch", 9);

        sendAdjustment(subscriber, "watch", 1000, Adjustment.Type.ADD);

        generateAndSendMessages(subscriber, "apple", 9);
    }

    /**
     * Generate basic messages using random values.
     *
     * @param subscriber  subscriber to push the messages to
     * @param productName product to create a message about
     * @param n           number of messages to generate
     * @throws NotAcceptingNewMessages thrown if pushing messages subscriber goes to paused state
     */
    private static void generateAndSendMessages(Subscriber subscriber, String productName, int n)
            throws NotAcceptingNewMessages {
        final Random random = new Random();
        double price;
        int numberOfSales;
        for (int i = 0; i < n; i++) {
            price = random.nextDouble() * random.nextInt(100) + 1;
            numberOfSales = random.nextInt(100) + 1;
            subscriber.receive(new Message(productName, price, numberOfSales));
        }
    }

    /**
     * Send adjustment.
     *
     * @param subscriber subscriber to push the adjustment to
     * @param productName product to adjust
     * @param price adjustment price
     * @param adjustmentType type of adjustment
     * @throws NotAcceptingNewMessages thrown if pushing messages subscriber goes to paused state
     */
    private static void sendAdjustment(Subscriber subscriber, String productName, double price, Adjustment.Type adjustmentType)
            throws NotAcceptingNewMessages {
        subscriber.receive(new Message(productName, price, adjustmentType));
    }
}