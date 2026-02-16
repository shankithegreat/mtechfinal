package com.telecom.billinginvoicing.controller;

import com.telecom.billinginvoicing.service.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/billing-invoicing")
public class BillingInvoicingController {

    private final InvoiceGenerationService invoiceGenerationService = new InvoiceGenerationService();
    private final TaxCalculationService taxCalculationService = new TaxCalculationService();
    private final DiscountService discountService = new DiscountService();
    private final PaymentReconciliationService paymentReconciliationService = new PaymentReconciliationService();
    private final InvoiceHistoryService invoiceHistoryService = new InvoiceHistoryService();
    private final MultiCurrencySupportService multiCurrencySupportService = new MultiCurrencySupportService();

    @PostMapping("/generate-invoices")
    public String generateInvoices() {
        invoiceGenerationService.generateInvoice();
        return "Invoice generation triggered.";
    }

    @PostMapping("/generate-invoice-summary")
    public String generateInvoiceSummary() {
        invoiceGenerationService.generateInvoiceSummary();
        return "Invoice summary generated.";
    }

    @PostMapping("/archive-invoices")
    public String archiveInvoices() {
        invoiceGenerationService.archiveOldInvoices();
        return "Invoice archival triggered.";
    }

    @PostMapping("/bulk-invoices")
    public String generateBulkInvoices() {
        invoiceGenerationService.generateBulkInvoices();
        return "Bulk invoice generation triggered.";
    }

    @PostMapping("/calculate-taxes")
    public String calculateTaxes() {
        taxCalculationService.calculateTax();
        return "Tax calculation triggered.";
    }

    @PostMapping("/tax-report")
    public String taxReport() {
        taxCalculationService.generateTaxReport();
        return "Tax report generated.";
    }

    @PostMapping("/apply-discounts")
    public String applyDiscounts() {
        discountService.applyDiscount();
        return "Discount application triggered.";
    }

    @PostMapping("/discount-report")
    public String discountReport() {
        discountService.generateDiscountReport();
        return "Discount report generated.";
    }

    @PostMapping("/reconcile-payments")
    public String reconcilePayments() {
        paymentReconciliationService.reconcilePayments();
        return "Payment reconciliation triggered.";
    }

    @PostMapping("/reconciliation-report")
    public String reconciliationReport() {
        // For demonstration, fetches dummy data
        List<Map<String, Object>> invoices = new ArrayList<>();
        List<Map<String, Object>> payments = new ArrayList<>();
        paymentReconciliationService.generateReconciliationReport(invoices, payments);
        return "Reconciliation report generated.";
    }

    @PostMapping("/log-invoice-history")
    public String logInvoiceHistory() {
        invoiceHistoryService.logInvoiceHistory();
        return "Invoice history logging triggered.";
    }

    @PostMapping("/history-report")
    public String historyReport() {
        invoiceHistoryService.generateHistoryReport();
        return "Invoice history report generated.";
    }

    @PostMapping("/archive-history")
    public String archiveHistory() {
        invoiceHistoryService.archiveHistory();
        return "Invoice history archival triggered.";
    }

    @PostMapping("/multi-currency")
    public String handleMultiCurrency() {
        multiCurrencySupportService.handleMultiCurrency();
        return "Multi-currency conversion triggered.";
    }

    @PostMapping("/currency-report")
    public String currencyReport() {
        multiCurrencySupportService.generateCurrencyReport();
        return "Currency conversion report generated.";
    }

    // Additional endpoints for dynamic updates and batch operations

    @PostMapping("/update-region-tax-rates")
    public String updateRegionTaxRates(@RequestBody Map<String, Double> newRates) {
        taxCalculationService.updateRegionTaxRates(newRates);
        return "Region tax rates updated.";
    }

    @PostMapping("/update-product-tax-rates")
    public String updateProductTaxRates(@RequestBody Map<String, Double> newRates) {
        taxCalculationService.updateProductTaxRates(newRates);
        return "Product tax rates updated.";
    }

    @PostMapping("/update-exchange-rates")
    public String updateExchangeRates(@RequestBody Map<String, Double> newRates) {
        multiCurrencySupportService.updateExchangeRates(newRates);
        return "Exchange rates updated.";
    }

    // Add more endpoints as needed for batch, dashboard, and validation features
}
