package wookabe.msgprocess.impl;

import wookabe.msgprocess.subscription.Adjustment;

import java.util.List;
import java.util.Map;

/**
 * Logger interface for formatting and displaying reports.
 */
interface Logger {
    /**
     * Log sales report.
     *
     * @param numberOfSales number of sales per product
     * @param totalValues   total value of sales (sales * price) per product
     */
    void logSalesReport(Map<String, Integer> numberOfSales, Map<String, Double> totalValues);

    /**
     * Log moving into paused state.
     */
    void logPause();

    /**
     * Log adjustment report.
     *
     * @param productsToAdjustments list of adjustments per product
     */
    void logAdjustmentReport(Map<String, List<Adjustment>> productsToAdjustments);
}
