package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.RiskProfile
import com.example.domain.FinancialCalculators
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("WealthPlan", appName)
  }

  @Test
  fun `verify tax calculations with relaxations`() {
    val result = FinancialCalculators.calculateTaxAndRelaxations(
      monthlySalary = 6000.0,
      retirementDeductionAnnual = 6000.0,
      healthDeductionAnnual = 2400.0,
      homeLoanInterestAnnual = 3000.0,
      otherDeductionsAnnual = 0.0
    )
    assertTrue(result.annualTaxSaved > 0.0)
    assertTrue(result.taxAfterDeductions < result.taxBeforeDeductions)
    assertTrue(result.effectiveTaxRateAfter < result.effectiveTaxRateBefore)
  }

  @Test
  fun `verify investment allocation sums to surplus`() {
    val surplus = 1500.0
    val buckets = FinancialCalculators.calculateInvestmentAllocation(
      remainingMoney = surplus,
      currentEmergencyFund = 10000.0,
      monthlyExpenses = 2000.0,
      riskProfile = RiskProfile.BALANCED
    )
    val totalAllocated = buckets.sumOf { it.monthlyAmount }
    assertEquals(surplus, totalAllocated, 1.0)
  }

  @Test
  fun `verify wishlist loan and timeline calculations`() {
    val sim = FinancialCalculators.calculateWishlistSimulation(
      targetCost = 30000.0,
      downPaymentPercent = 20.0,
      useLoan = true,
      loanInterestRate = 6.5,
      loanTenureYears = 5,
      allocatedMonthlySavings = 500.0,
      savedSoFar = 1000.0,
      monthlySalary = 5000.0,
      category = "CAR"
    )
    assertEquals(6000.0, sim.downPaymentAmount, 0.01)
    assertEquals(24000.0, sim.loanAmount, 0.01)
    assertTrue(sim.monthlyLoanEmi > 0.0)
    assertEquals(10, sim.monthsToDownPayment)
  }

  @Test
  fun `verify retirement nest egg calculation`() {
    val res = FinancialCalculators.calculateRetirement(
      currentAge = 30,
      retirementAge = 65,
      lifeExpectancy = 85,
      currentMonthlyExpense = 2500.0,
      currentRetirementSavings = 20000.0,
      actualMonthlySavings = 1200.0
    )
    assertTrue(res.targetNestEggCorpus > 0.0)
    assertTrue(res.requiredMonthlySavings > 0.0)
    assertTrue(res.yearsToRetire == 35)
  }
}

