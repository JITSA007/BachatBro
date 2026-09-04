package com.example.data.repository

import com.example.data.local.ExpenseDao
import com.example.data.local.ExpenseEntity
import com.example.data.local.UserProfileDao
import com.example.data.local.UserProfileEntity
import com.example.data.local.WishlistDao
import com.example.data.local.WishlistItemEntity
import kotlinx.coroutines.flow.Flow

class FinancialRepository(
    private val userProfileDao: UserProfileDao,
    private val expenseDao: ExpenseDao,
    private val wishlistDao: WishlistDao
) {
    val userProfile: Flow<UserProfileEntity?> = userProfileDao.getUserProfile()
    val allExpenses: Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()
    val allWishlistItems: Flow<List<WishlistItemEntity>> = wishlistDao.getAllWishlistItems()

    suspend fun saveUserProfile(profile: UserProfileEntity) {
        userProfileDao.insertOrUpdateProfile(profile)
    }

    suspend fun addExpense(expense: ExpenseEntity): Long {
        return expenseDao.insertExpense(expense)
    }

    suspend fun deleteExpense(id: Long) {
        expenseDao.deleteExpenseById(id)
    }

    suspend fun addWishlistItem(item: WishlistItemEntity): Long {
        return wishlistDao.insertItem(item)
    }

    suspend fun updateWishlistItem(item: WishlistItemEntity) {
        wishlistDao.updateItem(item)
    }

    suspend fun deleteWishlistItem(id: Long) {
        wishlistDao.deleteItemById(id)
    }

    suspend fun prepopulateIfEmpty() {
        val defaultProfile = UserProfileEntity(
            id = 1,
            monthlySalary = 5500.0,
            currencySymbol = "$",
            currentAge = 29,
            retirementAge = 60,
            lifeExpectancy = 85,
            currentRetirementSavings = 18000.0,
            currentEmergencyFund = 6000.0,
            riskProfile = "BALANCED",
            retirementContributionAnnual = 6000.0,
            healthInsuranceAnnual = 1800.0,
            homeLoanInterestAnnual = 0.0,
            otherDeductionsAnnual = 500.0
        )
        userProfileDao.insertOrUpdateProfile(defaultProfile)

        // Sensible initial expenses to give an immediate polished experience
        val initialExpenses = listOf(
            ExpenseEntity(title = "Apartment Rent", amount = 1450.0, category = "HOUSING"),
            ExpenseEntity(title = "Groceries & Food", amount = 500.0, category = "FOOD"),
            ExpenseEntity(title = "Electricity & Internet", amount = 180.0, category = "UTILITIES"),
            ExpenseEntity(title = "Metro & Fuel", amount = 220.0, category = "TRANSPORTATION"),
            ExpenseEntity(title = "Streaming & Gym", amount = 95.0, category = "SUBSCRIPTIONS")
        )
        for (expense in initialExpenses) {
            expenseDao.insertExpense(expense)
        }

        // Initial sample wishlist goals
        val initialWishlist = listOf(
            WishlistItemEntity(
                title = "Compact SUV / Car",
                category = "CAR",
                targetCost = 28000.0,
                downPaymentPercent = 15.0,
                useLoan = true,
                loanInterestRate = 7.2,
                loanTenureYears = 5,
                allocatedMonthlySavings = 400.0,
                savedSoFar = 2500.0,
                notes = "Reliable hybrid car for commute and road trips"
            ),
            WishlistItemEntity(
                title = "First Home / Condo",
                category = "HOUSE",
                targetCost = 320000.0,
                downPaymentPercent = 20.0,
                useLoan = true,
                loanInterestRate = 6.8,
                loanTenureYears = 25,
                allocatedMonthlySavings = 800.0,
                savedSoFar = 15000.0,
                notes = "3-bed suburban townhouse with garden"
            )
        )
        for (item in initialWishlist) {
            wishlistDao.insertItem(item)
        }
    }
}
