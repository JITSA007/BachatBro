package com.example.data.model

enum class ExpenseCategory(
    val displayName: String,
    val iconName: String,
    val defaultColorHex: Long
) {
    HOUSING("Housing & Rent", "Home", 0xFF3A86FF),
    FOOD("Food & Groceries", "Restaurant", 0xFFFF006E),
    UTILITIES("Utilities & Bills", "Bolt", 0xFFFFBE0B),
    TRANSPORTATION("Transportation", "DirectionsCar", 0xFF8338EC),
    HEALTHCARE("Healthcare & Insurance", "LocalHospital", 0xFF06D6A0),
    ENTERTAINMENT("Entertainment & Leisure", "SportsEsports", 0xFFFF5964),
    SUBSCRIPTIONS("Subscriptions", "Subscriptions", 0xFF35A7FF),
    DEBT_EMI("Loan / Debt EMI", "AccountBalance", 0xFFE63946),
    MISCELLANEOUS("Miscellaneous", "MoreHoriz", 0xFF6C757D)
}

enum class WishlistCategory(
    val displayName: String,
    val iconName: String,
    val defaultDownPaymentPercent: Double,
    val defaultLoanTenureYears: Int,
    val defaultInterestRate: Double
) {
    HOUSE("Home / Flat / 2BHK", "Home", 20.0, 20, 8.5),
    CAR("Car / Vehicle", "DirectionsCar", 15.0, 5, 8.8),
    VACATION("Dream Vacation", "Flight", 100.0, 1, 0.0),
    TECH("Laptop / Gadget", "LaptopMac", 30.0, 1, 12.0),
    EDUCATION("Higher Education / MBA", "School", 15.0, 7, 8.5),
    WEDDING("Wedding / Celebration", "Favorite", 100.0, 2, 0.0),
    OTHER("Other Goal", "Star", 20.0, 3, 10.0)
}

enum class RiskProfile(
    val title: String,
    val subtitle: String,
    val expectedReturnPercent: Double
) {
    CONSERVATIVE("Conservative", "PPF, FDs, Sovereign Gold & Debt", 8.2),
    BALANCED("Balanced SIP", "Nifty 50 Index, Flexi-Cap MFs & PPF", 12.0),
    AGGRESSIVE("Aggressive Wealth", "Mid-Cap, Small-Cap & Direct Equities", 14.5)
}

data class InvestmentBucket(
    val name: String,
    val allocationPercent: Int,
    val monthlyAmount: Double,
    val expectedCagr: Double,
    val description: String,
    val examples: String,
    val colorHex: Long
)

data class WealthMilestone(
    val years: Int,
    val totalInvested: Double,
    val futureValue: Double,
    val interestEarned: Double
)

data class WishlistSimulationResult(
    val targetCost: Double,
    val downPaymentAmount: Double,
    val loanAmount: Double,
    val monthlyLoanEmi: Double,
    val totalLoanRepayment: Double,
    val totalInterestPaid: Double,
    val monthsToDownPayment: Int,
    val monthsToFullCash: Int,
    val emiSalaryRatioPercent: Double,
    val affordabilityStatus: String,
    val isAffordable: Boolean,
    val taxSavingNote: String?
)

data class TaxCalculationResult(
    val grossAnnualSalary: Double,
    val standardDeduction: Double,
    val retirementDeduction: Double, // 80C
    val healthInsuranceDeduction: Double, // 80D
    val homeLoanInterestDeduction: Double, // Sec 24(b)
    val npsDeduction: Double = 0.0, // Sec 80CCD(1B)
    val totalDeductions: Double,
    val taxableIncome: Double,
    val taxBeforeDeductions: Double,
    val taxAfterDeductions: Double,
    val taxOldRegime: Double = 0.0,
    val taxNewRegime: Double = 0.0,
    val recommendedRegime: String = "New Tax Regime",
    val regimeTaxDiff: Double = 0.0,
    val annualTaxSaved: Double,
    val monthlyTax: Double,
    val effectiveTaxRateBefore: Double,
    val effectiveTaxRateAfter: Double,
    val topTaxTips: List<String>
)

data class RetirementCalculationResult(
    val currentAge: Int,
    val retirementAge: Int,
    val yearsToRetire: Int,
    val retirementDurationYears: Int,
    val currentMonthlyExpense: Double,
    val futureMonthlyExpenseAtRetirement: Double,
    val targetNestEggCorpus: Double,
    val existingSavingsGrowthAtRetirement: Double,
    val netCorpusNeeded: Double,
    val requiredMonthlySavings: Double,
    val actualMonthlySavings: Double,
    val savingsDeficitOrSurplus: Double,
    val readinessScorePercent: Int,
    val readinessStatus: String
)
