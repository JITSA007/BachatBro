package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ExpenseEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.WishlistItemEntity
import com.example.data.model.InvestmentBucket
import com.example.data.model.RetirementCalculationResult
import com.example.data.model.RiskProfile
import com.example.data.model.TaxCalculationResult
import com.example.data.model.WealthMilestone
import com.example.data.model.WishlistSimulationResult
import com.example.data.repository.FinancialRepository
import com.example.domain.FinancialCalculators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.max

data class FinancialUiState(
    val profile: UserProfileEntity = UserProfileEntity(),
    val expenses: List<ExpenseEntity> = emptyList(),
    val wishlistItems: List<WishlistItemEntity> = emptyList(),
    val monthlySalary: Double = 5500.0,
    val currencySymbol: String = "$",
    val totalMonthlyExpenses: Double = 0.0,
    val monthlyTax: Double = 0.0,
    val remainingMoney: Double = 0.0,
    val savingsRatePercent: Double = 0.0,
    val taxResult: TaxCalculationResult? = null,
    val investmentBuckets: List<InvestmentBucket> = emptyList(),
    val wealthMilestones: List<WealthMilestone> = emptyList(),
    val retirementResult: RetirementCalculationResult? = null,
    val wishlistSimulations: Map<Long, WishlistSimulationResult> = emptyMap(),
    val isLoading: Boolean = false
)

class FinancialViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = FinancialRepository(
        userProfileDao = db.userProfileDao(),
        expenseDao = db.expenseDao(),
        wishlistDao = db.wishlistDao()
    )

    init {
        viewModelScope.launch {
            // Check if profile exists; if not, prepopulate with realistic starter values
            val initial = db.userProfileDao().getUserProfile()
            // Ensure repository has default items
            launch {
                repository.userProfile.collect { profile ->
                    if (profile == null) {
                        repository.prepopulateIfEmpty()
                    }
                }
            }
        }
    }

    val uiState: StateFlow<FinancialUiState> = combine(
        repository.userProfile,
        repository.allExpenses,
        repository.allWishlistItems
    ) { profileEntity, expensesList, wishlistList ->
        val profile = profileEntity ?: UserProfileEntity()
        val salary = profile.monthlySalary
        val totalExpenses = expensesList.sumOf { it.amount }

        // Tax calculation with relaxations
        val taxResult = FinancialCalculators.calculateTaxAndRelaxations(
            monthlySalary = salary,
            retirementDeductionAnnual = profile.retirementContributionAnnual,
            healthDeductionAnnual = profile.healthInsuranceAnnual,
            homeLoanInterestAnnual = profile.homeLoanInterestAnnual,
            otherDeductionsAnnual = profile.otherDeductionsAnnual
        )
        val monthlyTax = taxResult.monthlyTax

        // Remaining Money after expenses and taxes
        val remainingMoney = max(0.0, salary - monthlyTax - totalExpenses)
        val savingsRate = if (salary > 0) (remainingMoney / salary) * 100.0 else 0.0

        val riskProfileEnum = try {
            RiskProfile.valueOf(profile.riskProfile)
        } catch (_: Exception) {
            RiskProfile.BALANCED
        }

        // Where to invest remaining money
        val investmentBuckets = FinancialCalculators.calculateInvestmentAllocation(
            remainingMoney = remainingMoney,
            currentEmergencyFund = profile.currentEmergencyFund,
            monthlyExpenses = if (totalExpenses > 0) totalExpenses else 2000.0,
            riskProfile = riskProfileEnum
        )

        // Compound wealth projection
        val wealthMilestones = FinancialCalculators.calculateWealthMilestones(
            monthlyInvestment = remainingMoney,
            expectedCagr = riskProfileEnum.expectedReturnPercent
        )

        // Retirement planning
        val retirementResult = FinancialCalculators.calculateRetirement(
            currentAge = profile.currentAge,
            retirementAge = profile.retirementAge,
            lifeExpectancy = profile.lifeExpectancy,
            currentMonthlyExpense = if (totalExpenses > 0) totalExpenses else 2500.0,
            currentRetirementSavings = profile.currentRetirementSavings,
            actualMonthlySavings = remainingMoney
        )

        // Wishlist simulations
        val wishlistSimulations = wishlistList.associate { item ->
            val sim = FinancialCalculators.calculateWishlistSimulation(
                targetCost = item.targetCost,
                downPaymentPercent = item.downPaymentPercent,
                useLoan = item.useLoan,
                loanInterestRate = item.loanInterestRate,
                loanTenureYears = item.loanTenureYears,
                allocatedMonthlySavings = item.allocatedMonthlySavings,
                savedSoFar = item.savedSoFar,
                monthlySalary = salary,
                category = item.category
            )
            item.id to sim
        }

        FinancialUiState(
            profile = profile,
            expenses = expensesList,
            wishlistItems = wishlistList,
            monthlySalary = salary,
            currencySymbol = profile.currencySymbol,
            totalMonthlyExpenses = totalExpenses,
            monthlyTax = monthlyTax,
            remainingMoney = remainingMoney,
            savingsRatePercent = savingsRate,
            taxResult = taxResult,
            investmentBuckets = investmentBuckets,
            wealthMilestones = wealthMilestones,
            retirementResult = retirementResult,
            wishlistSimulations = wishlistSimulations,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = FinancialUiState(isLoading = true)
    )

    fun updateSalary(newSalary: Double) {
        viewModelScope.launch {
            val current = uiState.value.profile
            repository.saveUserProfile(current.copy(monthlySalary = max(0.0, newSalary)))
        }
    }

    fun updateCurrency(symbol: String) {
        viewModelScope.launch {
            val current = uiState.value.profile
            repository.saveUserProfile(current.copy(currencySymbol = symbol))
        }
    }

    fun updateRiskProfile(risk: RiskProfile) {
        viewModelScope.launch {
            val current = uiState.value.profile
            repository.saveUserProfile(current.copy(riskProfile = risk.name))
        }
    }

    fun updateEmergencyFund(amount: Double) {
        viewModelScope.launch {
            val current = uiState.value.profile
            repository.saveUserProfile(current.copy(currentEmergencyFund = max(0.0, amount)))
        }
    }

    fun updateRetirementDetails(
        currentAge: Int,
        retireAge: Int,
        lifeExp: Int,
        savings: Double
    ) {
        viewModelScope.launch {
            val current = uiState.value.profile
            repository.saveUserProfile(
                current.copy(
                    currentAge = currentAge,
                    retirementAge = retireAge,
                    lifeExpectancy = lifeExp,
                    currentRetirementSavings = max(0.0, savings)
                )
            )
        }
    }

    fun updateTaxDeductions(
        retirementContribAnnual: Double,
        healthInsuranceAnnual: Double,
        homeLoanInterestAnnual: Double,
        otherDeductionsAnnual: Double
    ) {
        viewModelScope.launch {
            val current = uiState.value.profile
            repository.saveUserProfile(
                current.copy(
                    retirementContributionAnnual = max(0.0, retirementContribAnnual),
                    healthInsuranceAnnual = max(0.0, healthInsuranceAnnual),
                    homeLoanInterestAnnual = max(0.0, homeLoanInterestAnnual),
                    otherDeductionsAnnual = max(0.0, otherDeductionsAnnual)
                )
            )
        }
    }

    fun addExpense(title: String, amount: Double, category: String) {
        viewModelScope.launch {
            repository.addExpense(
                ExpenseEntity(
                    title = title.trim(),
                    amount = max(0.0, amount),
                    category = category,
                    isRecurring = true
                )
            )
        }
    }

    fun deleteExpense(id: Long) {
        viewModelScope.launch {
            repository.deleteExpense(id)
        }
    }

    fun addWishlistItem(
        title: String,
        category: String,
        cost: Double,
        downPaymentPercent: Double,
        useLoan: Boolean,
        interestRate: Double,
        tenureYears: Int,
        allocatedSavings: Double,
        notes: String
    ) {
        viewModelScope.launch {
            repository.addWishlistItem(
                WishlistItemEntity(
                    title = title.trim(),
                    category = category,
                    targetCost = max(0.0, cost),
                    downPaymentPercent = downPaymentPercent,
                    useLoan = useLoan,
                    loanInterestRate = interestRate,
                    loanTenureYears = tenureYears,
                    allocatedMonthlySavings = max(10.0, allocatedSavings),
                    notes = notes.trim()
                )
            )
        }
    }

    fun updateWishlistItem(item: WishlistItemEntity) {
        viewModelScope.launch {
            repository.updateWishlistItem(item)
        }
    }

    fun deleteWishlistItem(id: Long) {
        viewModelScope.launch {
            repository.deleteWishlistItem(id)
        }
    }
}
