package com.example.domain

import com.example.data.model.InvestmentBucket
import com.example.data.model.RetirementCalculationResult
import com.example.data.model.RiskProfile
import com.example.data.model.TaxCalculationResult
import com.example.data.model.WealthMilestone
import com.example.data.model.WishlistSimulationResult
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.pow

object FinancialCalculators {

    // --- 1. INDIAN INCOME TAX CALCULATION (OLD vs NEW REGIME) ---
    fun calculateTaxAndRelaxations(
        monthlySalary: Double,
        retirementDeductionAnnual: Double, // Section 80C (PPF, EPF, ELSS, Life Insurance)
        healthDeductionAnnual: Double,     // Section 80D (Health Insurance)
        homeLoanInterestAnnual: Double,    // Section 24(b) (Home Loan Interest)
        otherDeductionsAnnual: Double      // Section 80CCD(1B) (NPS) + other
    ): TaxCalculationResult {
        val grossAnnualSalary = monthlySalary * 12.0

        // 1. OLD TAX REGIME DEDUCTIONS
        val standardDeductionOld = 50000.0
        val capped80C = retirementDeductionAnnual.coerceIn(0.0, 150000.0) // Max ₹1,50,000
        val capped80D = healthDeductionAnnual.coerceIn(0.0, 75000.0)      // Max ₹25,000 self + ₹50,000 parents
        val capped24b = homeLoanInterestAnnual.coerceIn(0.0, 200000.0)   // Max ₹2,00,000
        val cappedNps80CCD = otherDeductionsAnnual.coerceIn(0.0, 50000.0) // Max ₹50,000 under 80CCD(1B)

        val totalDeductionsOld = standardDeductionOld + capped80C + capped80D + capped24b + cappedNps80CCD
        val taxableIncomeOld = max(0.0, grossAnnualSalary - totalDeductionsOld)
        val taxOldRegime = computeOldRegimeTax(taxableIncomeOld)

        // 2. NEW TAX REGIME DEDUCTIONS (Budget FY 2024-25 / 2025-26)
        val standardDeductionNew = 75000.0 // Salaried standard deduction in New Regime
        val taxableIncomeNew = max(0.0, grossAnnualSalary - standardDeductionNew)
        val taxNewRegime = computeNewRegimeTax(taxableIncomeNew)

        // Baseline tax without any statutory relief/exemptions under Old Regime
        val taxBeforeDeductions = computeOldRegimeTax(max(0.0, grossAnnualSalary - 50000.0))

        // Compare regimes to find optimal tax
        val recommendedRegime = if (taxNewRegime <= taxOldRegime) "New Tax Regime" else "Old Tax Regime"
        val taxAfterDeductions = if (taxNewRegime <= taxOldRegime) taxNewRegime else taxOldRegime
        val regimeTaxDiff = kotlin.math.abs(taxOldRegime - taxNewRegime)
        val annualTaxSaved = max(0.0, taxBeforeDeductions - taxAfterDeductions)
        val monthlyTax = taxAfterDeductions / 12.0

        val effectiveTaxRateBefore = if (grossAnnualSalary > 0) (taxBeforeDeductions / grossAnnualSalary) * 100.0 else 0.0
        val effectiveTaxRateAfter = if (grossAnnualSalary > 0) (taxAfterDeductions / grossAnnualSalary) * 100.0 else 0.0

        // Indian Tax Planning Tips
        val taxTips = mutableListOf<String>()
        if (taxNewRegime <= taxOldRegime) {
            taxTips.add("New Tax Regime saves you ₹${FinancialFormatters.formatCurrency(regimeTaxDiff, "")} compared to Old Regime with zero paperwork!")
        } else {
            taxTips.add("Old Tax Regime saves you ₹${FinancialFormatters.formatCurrency(regimeTaxDiff, "")} thanks to Sec 80C, 80D & Home Loan deductions.")
        }
        if (capped80C < 150000.0) {
            val rem = 150000.0 - capped80C
            taxTips.add("Invest ₹${FinancialFormatters.formatCurrency(rem, "")} more in ELSS / PPF / EPF to exhaust your ₹1,50,000 Section 80C limit.")
        }
        if (cappedNps80CCD < 50000.0) {
            taxTips.add("Invest up to ₹50,000 in NPS (Tier-1) under Section 80CCD(1B) for exclusive extra tax relaxation.")
        }
        if (capped80D < 25000.0) {
            taxTips.add("Claim medical insurance premium for family and parents under Section 80D (up to ₹25,000 self + ₹50,000 senior parents).")
        }
        if (capped24b > 0.0) {
            taxTips.add("Your home loan interest provides up to ₹2,00,000 direct deduction under Section 24(b) in Old Regime.")
        } else {
            taxTips.add("Salaried standard deduction in New Regime is ₹75,000 with zero tax up to ₹7.75 Lakhs taxable income!")
        }

        return TaxCalculationResult(
            grossAnnualSalary = grossAnnualSalary,
            standardDeduction = if (recommendedRegime == "New Tax Regime") standardDeductionNew else standardDeductionOld,
            retirementDeduction = capped80C,
            healthInsuranceDeduction = capped80D,
            homeLoanInterestDeduction = capped24b,
            npsDeduction = cappedNps80CCD,
            totalDeductions = if (recommendedRegime == "New Tax Regime") standardDeductionNew else totalDeductionsOld,
            taxableIncome = if (recommendedRegime == "New Tax Regime") taxableIncomeNew else taxableIncomeOld,
            taxBeforeDeductions = taxBeforeDeductions,
            taxAfterDeductions = taxAfterDeductions,
            taxOldRegime = taxOldRegime,
            taxNewRegime = taxNewRegime,
            recommendedRegime = recommendedRegime,
            regimeTaxDiff = regimeTaxDiff,
            annualTaxSaved = annualTaxSaved,
            monthlyTax = monthlyTax,
            effectiveTaxRateBefore = effectiveTaxRateBefore,
            effectiveTaxRateAfter = effectiveTaxRateAfter,
            topTaxTips = taxTips
        )
    }

    // New Tax Regime Slabs (Budget 2024-25 / 2025-26):
    // 0 - 3L: Nil
    // 3L - 7L: 5%
    // 7L - 10L: 10%
    // 10L - 12L: 15%
    // 12L - 15L: 20%
    // > 15L: 30%
    // Section 87A: If taxable income <= 7,00,000, tax is Nil.
    // 4% Health & Education Cess.
    private fun computeNewRegimeTax(taxableIncome: Double): Double {
        if (taxableIncome <= 300000.0) return 0.0
        var tax = 0.0

        if (taxableIncome > 1500000.0) {
            tax += (taxableIncome - 1500000.0) * 0.30
            tax += 300000.0 * 0.20 // 12L - 15L
            tax += 200000.0 * 0.15 // 10L - 12L
            tax += 300000.0 * 0.10 // 7L - 10L
            tax += 400000.0 * 0.05 // 3L - 7L
        } else if (taxableIncome > 1200000.0) {
            tax += (taxableIncome - 1200000.0) * 0.20
            tax += 200000.0 * 0.15 // 10L - 12L
            tax += 300000.0 * 0.10 // 7L - 10L
            tax += 400000.0 * 0.05 // 3L - 7L
        } else if (taxableIncome > 1000000.0) {
            tax += (taxableIncome - 1000000.0) * 0.15
            tax += 300000.0 * 0.10 // 7L - 10L
            tax += 400000.0 * 0.05 // 3L - 7L
        } else if (taxableIncome > 700000.0) {
            tax += (taxableIncome - 700000.0) * 0.10
            tax += 400000.0 * 0.05 // 3L - 7L
        } else {
            tax += (taxableIncome - 300000.0) * 0.05
        }

        // Section 87A rebate for income up to 7L
        if (taxableIncome <= 700000.0) {
            tax = 0.0
        }

        // 4% Health & Education Cess
        return tax * 1.04
    }

    // Old Tax Regime Slabs:
    // 0 - 2.5L: Nil
    // 2.5L - 5L: 5%
    // 5L - 10L: 20%
    // > 10L: 30%
    // Section 87A: If taxable income <= 5,00,000, tax is Nil.
    // 4% Health & Education Cess.
    private fun computeOldRegimeTax(taxableIncome: Double): Double {
        if (taxableIncome <= 250000.0) return 0.0
        var tax = 0.0

        if (taxableIncome > 1000000.0) {
            tax += (taxableIncome - 1000000.0) * 0.30
            tax += 500000.0 * 0.20 // 5L - 10L
            tax += 250000.0 * 0.05 // 2.5L - 5L
        } else if (taxableIncome > 500000.0) {
            tax += (taxableIncome - 500000.0) * 0.20
            tax += 250000.0 * 0.05 // 2.5L - 5L
        } else {
            tax += (taxableIncome - 250000.0) * 0.05
        }

        // Section 87A rebate for income up to 5L
        if (taxableIncome <= 500000.0) {
            tax = 0.0
        }

        // 4% Health & Education Cess
        return tax * 1.04
    }

    // --- 2. INDIAN INVESTMENT & SIP ALLOCATION ---
    fun calculateInvestmentAllocation(
        remainingMoney: Double,
        currentEmergencyFund: Double,
        monthlyExpenses: Double,
        riskProfile: RiskProfile
    ): List<InvestmentBucket> {
        if (remainingMoney <= 0.0) return emptyList()

        val emergencyTarget = monthlyExpenses * 6.0
        val emergencyFundShortfall = max(0.0, emergencyTarget - currentEmergencyFund)
        val isEmergencyUnderfunded = emergencyFundShortfall > 15000.0

        return when (riskProfile) {
            RiskProfile.CONSERVATIVE -> {
                val emergencyPct = if (isEmergencyUnderfunded) 35 else 20
                val debtPct = if (isEmergencyUnderfunded) 35 else 45
                val equityPct = if (isEmergencyUnderfunded) 20 else 25
                val goldPct = 10

                listOf(
                    InvestmentBucket(
                        name = "Emergency Buffer (Liquid Funds / Sweep FD)",
                        allocationPercent = emergencyPct,
                        monthlyAmount = remainingMoney * (emergencyPct / 100.0),
                        expectedCagr = 6.8,
                        description = "Instant liquidity in Auto-sweep Bank FD or Overnight / Liquid Mutual Funds.",
                        examples = "HDFC / ICICI Liquid Fund, Auto-Sweep Savings",
                        colorHex = 0xFF00B4D8
                    ),
                    InvestmentBucket(
                        name = "PPF, EPF & Sovereign Debt",
                        allocationPercent = debtPct,
                        monthlyAmount = remainingMoney * (debtPct / 100.0),
                        expectedCagr = 7.8,
                        description = "Guaranteed capital safety, tax-free interest under Section 80C.",
                        examples = "Public Provident Fund (PPF), Target Maturity Debt Funds, Corporate FDs",
                        colorHex = 0xFF3A86FF
                    ),
                    InvestmentBucket(
                        name = "Nifty 50 Index Fund",
                        allocationPercent = equityPct,
                        monthlyAmount = remainingMoney * (equityPct / 100.0),
                        expectedCagr = 12.0,
                        description = "Low-cost SIP in India's top 50 bluechip industry leaders.",
                        examples = "UTI / Nippon Nifty 50 Index Fund",
                        colorHex = 0xFF00B074
                    ),
                    InvestmentBucket(
                        name = "Sovereign Gold Bonds (SGB) / Gold ETF",
                        allocationPercent = goldPct,
                        monthlyAmount = remainingMoney * (goldPct / 100.0),
                        expectedCagr = 9.5,
                        description = "RBI Sovereign Gold Bonds with 2.5% p.a. coupon + tax-free capital appreciation.",
                        examples = "RBI SGB Tranches, Nippon Gold ETF",
                        colorHex = 0xFFFFB703
                    )
                )
            }
            RiskProfile.BALANCED -> {
                val emergencyPct = if (isEmergencyUnderfunded) 25 else 15
                val indexPct = if (isEmergencyUnderfunded) 40 else 50
                val debtPct = if (isEmergencyUnderfunded) 15 else 20
                val midcapPct = 10

                listOf(
                    InvestmentBucket(
                        name = "Nifty 50 & Flexi-Cap Mutual Funds",
                        allocationPercent = indexPct,
                        monthlyAmount = remainingMoney * (indexPct / 100.0),
                        expectedCagr = 13.0,
                        description = "Core Indian wealth engine. Blend of index stability and diversified multi-cap growth.",
                        examples = "Parag Parikh Flexi Cap, UTI Nifty 50 Index, Mirae Large & Midcap",
                        colorHex = 0xFF00B074
                    ),
                    InvestmentBucket(
                        name = "PPF & Debt Instruments",
                        allocationPercent = debtPct,
                        monthlyAmount = remainingMoney * (debtPct / 100.0),
                        expectedCagr = 7.5,
                        description = "Safe debt allocation providing portfolio stability and rebalancing cushion.",
                        examples = "Public Provident Fund (PPF), Short Duration Debt Funds",
                        colorHex = 0xFF3A86FF
                    ),
                    InvestmentBucket(
                        name = "Emergency Buffer (Liquid MFs)",
                        allocationPercent = emergencyPct,
                        monthlyAmount = remainingMoney * (emergencyPct / 100.0),
                        expectedCagr = 6.8,
                        description = "Maintains a 6-month safety buffer for unexpected medical or job needs.",
                        examples = "Liquid Mutual Funds, Sweep-in High Interest FDs",
                        colorHex = 0xFF00B4D8
                    ),
                    InvestmentBucket(
                        name = "Nifty Next 50 & Mid-Cap Funds",
                        allocationPercent = midcapPct,
                        monthlyAmount = remainingMoney * (midcapPct / 100.0),
                        expectedCagr = 15.5,
                        description = "High-octane growth from emerging market leaders and potential large-caps.",
                        examples = "Nifty Next 50 Index, Motilal Oswal Midcap Fund",
                        colorHex = 0xFF7B2CBF
                    )
                )
            }
            RiskProfile.AGGRESSIVE -> {
                val emergencyPct = if (isEmergencyUnderfunded) 15 else 10
                val flexiPct = if (isEmergencyUnderfunded) 50 else 55
                val smallcapPct = 25
                val goldDebtPct = 10

                listOf(
                    InvestmentBucket(
                        name = "Flexi-Cap & Large-Cap Equities",
                        allocationPercent = flexiPct,
                        monthlyAmount = remainingMoney * (flexiPct / 100.0),
                        expectedCagr = 13.5,
                        description = "Dominant domestic equity SIP compounding across bluechip leaders.",
                        examples = "Nifty 50 Index, Quant Active / Parag Parikh Flexi Cap",
                        colorHex = 0xFF00B074
                    ),
                    InvestmentBucket(
                        name = "Mid-Cap & Small-Cap Alpha Funds",
                        allocationPercent = smallcapPct,
                        monthlyAmount = remainingMoney * (smallcapPct / 100.0),
                        expectedCagr = 16.5,
                        description = "High alpha long-term compounders in India's expanding economy.",
                        examples = "Nippon India Small Cap, SBI Small Cap, HDFC Mid-Cap",
                        colorHex = 0xFF7B2CBF
                    ),
                    InvestmentBucket(
                        name = "Emergency Liquid Buffer",
                        allocationPercent = emergencyPct,
                        monthlyAmount = remainingMoney * (emergencyPct / 100.0),
                        expectedCagr = 6.8,
                        description = "Shock absorber to prevent breaking equity SIPs during market corrections.",
                        examples = "Liquid Mutual Funds, Bank Sweep Account",
                        colorHex = 0xFF00B4D8
                    ),
                    InvestmentBucket(
                        name = "Sovereign Gold Bonds (SGB)",
                        allocationPercent = goldDebtPct,
                        monthlyAmount = remainingMoney * (goldDebtPct / 100.0),
                        expectedCagr = 10.0,
                        description = "Inflation hedge backed by Govt of India with 2.5% extra annual coupon.",
                        examples = "RBI SGB Gold Bonds, Gold ETFs",
                        colorHex = 0xFFFFB703
                    )
                )
            }
        }
    }

    // Compounding growth projection over time (SIP Future Value)
    fun calculateWealthMilestones(monthlyInvestment: Double, expectedCagr: Double): List<WealthMilestone> {
        val yearsList = listOf(5, 10, 15, 20, 25, 30)
        val r = (expectedCagr / 100.0) / 12.0

        return yearsList.map { years ->
            val months = years * 12
            val totalInvested = monthlyInvestment * months
            val fv = if (r > 0) {
                monthlyInvestment * ((1.0 + r).pow(months.toDouble()) - 1.0) / r
            } else {
                totalInvested
            }
            val interest = max(0.0, fv - totalInvested)
            WealthMilestone(
                years = years,
                totalInvested = totalInvested,
                futureValue = fv,
                interestEarned = interest
            )
        }
    }

    // --- 3. WISHLIST GOAL SIMULATOR & LOAN CALCULATOR (INDIAN BANKING) ---
    fun calculateWishlistSimulation(
        targetCost: Double,
        downPaymentPercent: Double,
        useLoan: Boolean,
        loanInterestRate: Double,
        loanTenureYears: Int,
        allocatedMonthlySavings: Double,
        savedSoFar: Double,
        monthlySalary: Double,
        category: String
    ): WishlistSimulationResult {
        val safeDownPct = downPaymentPercent.coerceIn(0.0, 100.0)
        val downPaymentAmount = targetCost * (safeDownPct / 100.0)
        val loanAmount = if (useLoan) max(0.0, targetCost - downPaymentAmount) else 0.0

        // Loan EMI Formula: [P * r * (1+r)^n] / [(1+r)^n - 1]
        val monthlyLoanEmi: Double
        val totalLoanRepayment: Double
        val totalInterestPaid: Double

        if (useLoan && loanAmount > 0.0 && loanTenureYears > 0) {
            val n = loanTenureYears * 12
            val r = (loanInterestRate / 100.0) / 12.0
            if (r > 0.0) {
                val factor = (1.0 + r).pow(n.toDouble())
                monthlyLoanEmi = (loanAmount * r * factor) / (factor - 1.0)
            } else {
                monthlyLoanEmi = loanAmount / n
            }
            totalLoanRepayment = monthlyLoanEmi * n
            totalInterestPaid = max(0.0, totalLoanRepayment - loanAmount)
        } else {
            monthlyLoanEmi = 0.0
            totalLoanRepayment = 0.0
            totalInterestPaid = 0.0
        }

        // Timeline to accumulate Down Payment
        val remainingDownPayment = max(0.0, downPaymentAmount - savedSoFar)
        val safeAllocated = if (allocatedMonthlySavings > 0) allocatedMonthlySavings else 1000.0
        val monthsToDownPayment = if (remainingDownPayment <= 0.0) 0 else ceil(remainingDownPayment / safeAllocated).toInt()

        // Timeline if buying outright with cash (no loan)
        val remainingFullCash = max(0.0, targetCost - savedSoFar)
        val monthsToFullCash = if (remainingFullCash <= 0.0) 0 else ceil(remainingFullCash / safeAllocated).toInt()

        val emiSalaryRatioPercent = if (monthlySalary > 0 && useLoan) (monthlyLoanEmi / monthlySalary) * 100.0 else 0.0

        val affordabilityStatus: String
        val isAffordable: Boolean

        if (!useLoan) {
            affordabilityStatus = "Cash Purchase: Reached in $monthsToFullCash months at current SIP pace"
            isAffordable = true
        } else {
            when {
                emiSalaryRatioPercent <= 25.0 -> {
                    affordabilityStatus = "Comfortably Affordable (EMI is ${String.format("%.1f", emiSalaryRatioPercent)}% of salary)"
                    isAffordable = true
                }
                emiSalaryRatioPercent <= 40.0 -> {
                    affordabilityStatus = "Manageable (EMI is ${String.format("%.1f", emiSalaryRatioPercent)}% of salary)"
                    isAffordable = true
                }
                emiSalaryRatioPercent <= 50.0 -> {
                    affordabilityStatus = "Stretched (EMI is ${String.format("%.1f", emiSalaryRatioPercent)}% of salary - consider larger down payment)"
                    isAffordable = false
                }
                else -> {
                    affordabilityStatus = "High Debt Risk! (EMI exceeds ${String.format("%.1f", emiSalaryRatioPercent)}% of salary)"
                    isAffordable = false
                }
            }
        }

        val taxSavingNote = if (category.equals("HOUSE", ignoreCase = true) && useLoan) {
            "Section 24(b) Tax Relief: Home loan interest is deductible up to ₹2,00,000/year under Old Regime + Section 80C covers principal up to ₹1,50,000!"
        } else if (category.equals("EDUCATION", ignoreCase = true) && useLoan) {
            "Section 80E Tax Relief: 100% of interest paid on Higher Education loan is tax deductible without upper ceiling for up to 8 years!"
        } else {
            null
        }

        return WishlistSimulationResult(
            targetCost = targetCost,
            downPaymentAmount = downPaymentAmount,
            loanAmount = loanAmount,
            monthlyLoanEmi = monthlyLoanEmi,
            totalLoanRepayment = totalLoanRepayment,
            totalInterestPaid = totalInterestPaid,
            monthsToDownPayment = monthsToDownPayment,
            monthsToFullCash = monthsToFullCash,
            emiSalaryRatioPercent = emiSalaryRatioPercent,
            affordabilityStatus = affordabilityStatus,
            isAffordable = isAffordable,
            taxSavingNote = taxSavingNote
        )
    }

    // --- 4. INDIAN RETIREMENT PLANNING ENGINE ---
    fun calculateRetirement(
        currentAge: Int,
        retirementAge: Int,
        lifeExpectancy: Int,
        currentMonthlyExpense: Double,
        currentRetirementSavings: Double,
        actualMonthlySavings: Double,
        inflationPercent: Double = 6.0,            // India long-term CPI inflation
        preRetirementReturnPercent: Double = 12.0, // India Equity + EPF blend
        postRetirementReturnPercent: Double = 7.5  // India SCSS + RBI Bonds + Annuity blend
    ): RetirementCalculationResult {
        val safeCurrentAge = currentAge.coerceIn(18, 75)
        val safeRetireAge = retirementAge.coerceIn(safeCurrentAge + 1, 80)
        val safeLifeExp = lifeExpectancy.coerceIn(safeRetireAge + 1, 100)

        val yearsToRetire = safeRetireAge - safeCurrentAge
        val retirementDurationYears = safeLifeExp - safeRetireAge

        // Retirement lifestyle assumption: 75% of pre-retirement expenses needed
        val replacementRatio = 0.75
        val baseMonthlyExpense = max(10000.0, currentMonthlyExpense * replacementRatio)

        // Inflation adjusted monthly expense at retirement year
        val inflationFactor = (1.0 + inflationPercent / 100.0).pow(yearsToRetire.toDouble())
        val futureMonthlyExpenseAtRetirement = baseMonthlyExpense * inflationFactor
        val futureAnnualExpense = futureMonthlyExpenseAtRetirement * 12.0

        // Real return during retirement (Fisher equation)
        val rPost = postRetirementReturnPercent / 100.0
        val inf = inflationPercent / 100.0
        val realReturn = (1.0 + rPost) / (1.0 + inf) - 1.0

        // Target Nest Egg Corpus using present value of annuity
        val targetNestEggCorpus = if (realReturn > 0.001) {
            futureAnnualExpense * (1.0 - (1.0 + realReturn).pow(-retirementDurationYears.toDouble())) / realReturn
        } else {
            futureAnnualExpense * retirementDurationYears
        }

        // Future value of existing retirement savings (EPF + PPF + MF corpus)
        val preReturnFactor = (1.0 + preRetirementReturnPercent / 100.0).pow(yearsToRetire.toDouble())
        val existingSavingsGrowthAtRetirement = currentRetirementSavings * preReturnFactor

        val netCorpusNeeded = max(0.0, targetNestEggCorpus - existingSavingsGrowthAtRetirement)

        // Monthly savings needed from today using sinking fund formula
        val rMonthly = (preRetirementReturnPercent / 100.0) / 12.0
        val totalMonths = yearsToRetire * 12
        val requiredMonthlySavings = if (rMonthly > 0 && totalMonths > 0) {
            val compFactor = (1.0 + rMonthly).pow(totalMonths.toDouble()) - 1.0
            if (compFactor > 0) (netCorpusNeeded * rMonthly) / compFactor else 0.0
        } else {
            if (totalMonths > 0) netCorpusNeeded / totalMonths else 0.0
        }

        val savingsDeficitOrSurplus = actualMonthlySavings - requiredMonthlySavings
        val readinessScorePercent = if (requiredMonthlySavings > 0) {
            ((actualMonthlySavings / requiredMonthlySavings) * 100.0).toInt().coerceIn(0, 150)
        } else {
            100
        }

        val readinessStatus = when {
            readinessScorePercent >= 115 -> "Ahead of Schedule (Financial Freedom Ahead)"
            readinessScorePercent >= 90 -> "On Track for Target Retirement"
            readinessScorePercent >= 65 -> "Moderate Progress (Needs Slight SIP Boost)"
            else -> "Deficit Alert: Increase monthly SIP or extend timeline"
        }

        return RetirementCalculationResult(
            currentAge = safeCurrentAge,
            retirementAge = safeRetireAge,
            yearsToRetire = yearsToRetire,
            retirementDurationYears = retirementDurationYears,
            currentMonthlyExpense = currentMonthlyExpense,
            futureMonthlyExpenseAtRetirement = futureMonthlyExpenseAtRetirement,
            targetNestEggCorpus = targetNestEggCorpus,
            existingSavingsGrowthAtRetirement = existingSavingsGrowthAtRetirement,
            netCorpusNeeded = netCorpusNeeded,
            requiredMonthlySavings = requiredMonthlySavings,
            actualMonthlySavings = actualMonthlySavings,
            savingsDeficitOrSurplus = savingsDeficitOrSurplus,
            readinessScorePercent = readinessScorePercent,
            readinessStatus = readinessStatus
        )
    }
}
