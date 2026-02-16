# Billing Invoicing Service

## Overview
The Billing Invoicing Service is responsible for managing the billing and invoicing processes in the telecom system. It now includes enterprise-grade functionalities controlled by feature flags for phased releases.

## Features
1. **Invoice Generation**
   - Feature Flag: `billing_enable_invoice_generation`
   - Description: Generates invoices in multiple formats.

2. **Tax Calculation**
   - Feature Flag: `billing_enable_tax_calculation`
   - Description: Calculates taxes based on regional rules.

3. **Discount Application**
   - Feature Flag: `billing_enable_discount`
   - Description: Applies discounts for enterprise customers.

4. **Payment Reconciliation**
   - Feature Flag: `billing_enable_payment_reconciliation`
   - Description: Reconciles payments with invoices.

5. **Invoice History Logging**
   - Feature Flag: `billing_enable_invoice_history`
   - Description: Logs invoice history for auditing purposes.

6. **Multi-Currency Support**
   - Feature Flag: `billing_enable_multi_currency`
   - Description: Supports billing in multiple currencies.

## Usage
- The `BillingInvoicingController` orchestrates the billing process by invoking the respective services.
- Each functionality is controlled by its respective feature flag, which can be toggled in the `feature-flags.json` file.

## Configuration
Update the `feature-flags.json` file to enable or disable specific features:
```json
{
  "featureFlags": {
    "billing_enable_invoice_generation": true,
    "billing_enable_tax_calculation": true,
    "billing_enable_discount": false,
    "billing_enable_payment_reconciliation": true,
    "billing_enable_invoice_history": true,
    "billing_enable_multi_currency": false
  }
}
```
