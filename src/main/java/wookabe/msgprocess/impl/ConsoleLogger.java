package wookabe.msgprocess.impl;

import wookabe.msgprocess.subscription.Adjustment;

import java.util.List;
import java.util.Map;

/**
 * Simple console logger for reports.
 */
class ConsoleLogger implements Logger {
    @Override
    public void logSalesReport(Map<String, Integer> numberOfSales, Map<String, Double> totalValues) {
        System.out.println("Sales report:");
        numberOfSales.keySet().forEach(productName ->
                System.out.println("\tNumber of sales for product '" + productName + "' is '"
                        + numberOfSales.get(productName) + "' with total value at '"
                        + totalValues.get(productName) + "'")
        );
    }

    @Override
    public void logPause() {
        System.out.println("Subscriber is pausing...");
    }

    @Override
    public void logAdjustmentReport(Map<String, List<Adjustment>> productsToAdjustments) {
        System.out.println("Adjustment report:");
        productsToAdjustments.keySet().forEach(productName -> {
            System.out.println("\tList of adjustments per product '" + productName + "':");
            productsToAdjustments.get(productName).forEach(a -> System.out.print("\t\t" + a));
            System.out.println();
        });
    }
}
