package com.telecom.inventorymanagement.util;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Utility functions for Inventory Management with comprehensive telecom inventory business logic.
 * Provides validation, calculations, forecasting, and analysis utilities for inventory operations.
 */
public class InventoryManagementUtils {

    private static final Pattern SERIAL_NUMBER_PATTERN = Pattern.compile("^[A-Z0-9]{8,20}$");
    private static final Pattern IMEI_PATTERN = Pattern.compile("^\\d{15}$");

    /**
     * Validate serial number format
     */
    public static boolean validateSerialNumber(String serialNumber) {
        if (serialNumber == null || serialNumber.isEmpty()) return false;
        return SERIAL_NUMBER_PATTERN.matcher(serialNumber).matches();
    }

    /**
     * Validate IMEI number format
     */
    public static boolean validateIMEI(String imei) {
        if (imei == null || imei.length() != 15) return false;
        return IMEI_PATTERN.matcher(imei).matches();
    }

    /**
     * Calculate reorder point using safety stock formula
     */
    public static int calculateReorderPoint(int averageDailyDemand, int leadTimeDays, int safetyStock) {
        return (averageDailyDemand * leadTimeDays) + safetyStock;
    }

    /**
     * Calculate economic order quantity (EOQ)
     */
    public static int calculateEOQ(double annualDemand, double orderingCost, double holdingCostPerUnit) {
        if (holdingCostPerUnit == 0) return 0;
        return (int) Math.sqrt((2 * annualDemand * orderingCost) / holdingCostPerUnit);
    }

    /**
     * Calculate stock turnover ratio
     */
    public static double calculateStockTurnover(double costOfGoodsSold, double averageInventoryValue) {
        if (averageInventoryValue == 0) return 0;
        return costOfGoodsSold / averageInventoryValue;
    }

    /**
     * Calculate days inventory outstanding (DIO)
     */
    public static double calculateDIO(int inventoryDays, double stockTurnover) {
        return inventoryDays / (stockTurnover > 0 ? stockTurnover : 1);
    }

    /**
     * Perform ABC analysis on inventory items
     */
    public static String performABCAnalysis(double itemValue, double totalInventoryValue) {
        double percentage = (itemValue / totalInventoryValue) * 100;
        if (percentage >= 70) return "A"; // 70% of value
        if (percentage >= 20) return "B"; // 20% of value
        return "C"; // 10% of value
    }

    /**
     * Calculate warehouse utilization percentage
     */
    public static double calculateWarehouseUtilization(double usedCapacity, double totalCapacity) {
        if (totalCapacity == 0) return 0;
        return (usedCapacity / totalCapacity) * 100;
    }

    /**
     * Calculate storage cost per unit
     */
    public static double calculateStorageCostPerUnit(double totalStorageCost, int totalUnits) {
        if (totalUnits == 0) return 0;
        return totalStorageCost / totalUnits;
    }

    /**
     * Calculate holding cost (carrying cost)
     */
    public static double calculateHoldingCost(double unitCost, double holdingCostRate, int quantityOnHand) {
        return (unitCost * holdingCostRate / 12) * (quantityOnHand / 2.0); // Monthly average
    }

    /**
     * Calculate order cost
     */
    public static double calculateOrderCost(double costPerOrder, int numberOfOrders) {
        return costPerOrder * numberOfOrders;
    }

    /**
     * Forecast demand using simple exponential smoothing
     */
    public static double forecastDemand(double previousDemand, double smoothingFactor, double previousForecast) {
        return (smoothingFactor * previousDemand) + ((1 - smoothingFactor) * previousForecast);
    }

    /**
     * Calculate depreciation using straight-line method
     */
    public static double calculateDepreciationStraightLine(double originalCost, double salvageValue, int usefulLifeYears) {
        return (originalCost - salvageValue) / usefulLifeYears;
    }

    /**
     * Calculate depreciation using declining balance method
     */
    public static double calculateDepreciationDecliningBalance(double bookValue, int depreciationRate) {
        return bookValue * (depreciationRate / 100.0);
    }

    /**
     * Calculate net realizable value
     */
    public static double calculateNetRealizableValue(double currentValue, double disposalCost) {
        return currentValue - disposalCost;
    }

    /**
     * Calculate inventory valuation using FIFO
     */
    public static double calculateFIFOValuation(List<Double> costHistory, int unitsSold) {
        double totalCost = 0;
        int unitsCounted = 0;
        
        for (double cost : costHistory) {
            if (unitsCounted + 1 <= unitsSold) {
                totalCost += cost;
                unitsCounted++;
            } else {
                break;
            }
        }
        
        return totalCost;
    }

    /**
     * Calculate inventory valuation using LIFO
     */
    public static double calculateLIFOValuation(List<Double> costHistory, int unitsSold) {
        double totalCost = 0;
        int unitsCounted = 0;
        
        for (int i = costHistory.size() - 1; i >= 0; i--) {
            if (unitsCounted < unitsSold) {
                totalCost += costHistory.get(i);
                unitsCounted++;
            } else {
                break;
            }
        }
        
        return totalCost;
    }

    /**
     * Calculate weighted average cost
     */
    public static double calculateWeightedAverageCost(List<Double> costs, List<Integer> quantities) {
        if (costs.isEmpty() || quantities.isEmpty()) return 0;
        
        double totalCost = 0;
        int totalQuantity = 0;
        
        for (int i = 0; i < costs.size(); i++) {
            totalCost += costs.get(i) * quantities.get(i);
            totalQuantity += quantities.get(i);
        }
        
        return totalQuantity > 0 ? totalCost / totalQuantity : 0;
    }

    /**
     * Check if item is obsolete
     */
    public static boolean isObsolete(long lastMovementDate, long currentDate) {
        long ageInDays = (currentDate - lastMovementDate) / (24 * 60 * 60 * 1000L);
        return ageInDays > 365; // Over 1 year without movement
    }

    /**
     * Calculate supplier performance score
     */
    public static double calculateSupplierScore(int onTimeDeliveryRate, int qualityScore, double priceCompetitiveness) {
        return (onTimeDeliveryRate * 0.4) + (qualityScore * 0.4) + (priceCompetitiveness * 0.2);
    }

    /**
     * Calculate lead time variability
     */
    public static double calculateLeadTimeVariability(List<Integer> leadTimes) {
        if (leadTimes.isEmpty()) return 0;
        
        double average = leadTimes.stream().mapToInt(Integer::intValue).average().orElse(0);
        double variance = leadTimes.stream()
            .mapToDouble(lt -> Math.pow(lt - average, 2))
            .average().orElse(0);
        
        return Math.sqrt(variance); // Standard deviation
    }

    /**
     * Validate equipment manufacturer
     */
    public static boolean validateManufacturer(String manufacturer) {
        if (manufacturer == null || manufacturer.isEmpty()) return false;
        return manufacturer.length() >= 2 && manufacturer.length() <= 100;
    }

    /**
     * Format serial number for display
     */
    public static String formatSerialNumber(String serialNumber) {
        if (serialNumber == null || serialNumber.length() < 8) return serialNumber;
        return serialNumber.substring(0, 4) + "-" + serialNumber.substring(4, 8) + "-" + serialNumber.substring(8);
    }

    /**
     * Generate warehouse code
     */
    public static String generateWarehouseCode(String city, int warehouseIndex) {
        String cityPrefix = city.substring(0, Math.min(2, city.length())).toUpperCase();
        return "WH-" + cityPrefix + String.format("%03d", warehouseIndex);
    }

    /**
     * Calculate safety stock
     */
    public static int calculateSafetyStock(double maxDailyUsage, double maxLeadTime, 
                                           double averageDailyUsage, double averageLeadTime) {
        return (int) ((maxDailyUsage * maxLeadTime) - (averageDailyUsage * averageLeadTime));
    }

    /**
     * Calculate optimal order quantity considering bulk discounts
     */
    public static int calculateOptimalOrderQuantity(int eoq, double unitPrice, double bulkDiscountThreshold,
                                                    double discountPercentage) {
        if (unitPrice * eoq < bulkDiscountThreshold) {
            return (int) (bulkDiscountThreshold / unitPrice);
        }
        return eoq;
    }

    /**
     * Check if batch requires quality control
     */
    public static boolean requiresQualityControl(int batchSize, double samplingRate) {
        return batchSize >= 50; // Batches over 50 units require QC
    }

    /**
     * Calculate acceptable quality limit (AQL)
     */
    public static int calculateAQL(int batchSize, double acceptanceNumber) {
        return (int) Math.ceil(batchSize * acceptanceNumber);
    }

    /**
     * Validate warehouse location
     */
    public static boolean validateWarehouseLocation(double latitude, double longitude) {
        return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180;
    }

    /**
     * Calculate inventory age for obsolescence check
     */
    public static int calculateInventoryAgeMonths(long registrationDate) {
        return (int) ((System.currentTimeMillis() - registrationDate) / (30L * 24 * 60 * 60 * 1000));
    }

    /**
     * Determine delivery risk level
     */
    public static String determineDeliveryRisk(int leadTimeDays, int standardLeadTime, int variance) {
        if (leadTimeDays > standardLeadTime + variance) return "HIGH";
        if (leadTimeDays > standardLeadTime) return "MEDIUM";
        return "LOW";
    }

    /**
     * Calculate inventory carrying cost percentage
     */
    public static double calculateCarryingCostPercentage(double annualHoldingCost, double averageInventoryValue) {
        if (averageInventoryValue == 0) return 0;
        return (annualHoldingCost / averageInventoryValue) * 100;
    }

    /**
     * Check if supplier meets minimum standards
     */
    public static boolean meetsSupplierStandards(double supplierRating, int qualityScore, int onTimeRate) {
        return supplierRating >= 3.5 && qualityScore >= 75 && onTimeRate >= 85;
    }
}
