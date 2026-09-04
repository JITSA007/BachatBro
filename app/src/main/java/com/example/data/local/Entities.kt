package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val monthlySalary: Double = 85000.0,
    val currencySymbol: String = "₹",
    val currentAge: Int = 28,
    val retirementAge: Int = 60,
    val lifeExpectancy: Int = 82,
    val currentRetirementSavings: Double = 350000.0,
    val currentEmergencyFund: Double = 150000.0,
    val riskProfile: String = "BALANCED",
    val retirementContributionAnnual: Double = 150000.0,
    val healthInsuranceAnnual: Double = 25000.0,
    val homeLoanInterestAnnual: Double = 0.0,
    val otherDeductionsAnnual: Double = 50000.0
)

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val isRecurring: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String,
    val targetCost: Double,
    val downPaymentPercent: Double = 20.0,
    val useLoan: Boolean = true,
    val loanInterestRate: Double = 8.5,
    val loanTenureYears: Int = 20,
    val allocatedMonthlySavings: Double = 15000.0,
    val savedSoFar: Double = 0.0,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
