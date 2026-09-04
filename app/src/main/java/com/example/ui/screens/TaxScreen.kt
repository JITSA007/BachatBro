package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FinancialUiState
import com.example.ui.FinancialViewModel
import com.example.ui.components.FinancialFormatters
import com.example.ui.components.MetricCard
import com.example.ui.components.SectionHeader

@Composable
fun TaxScreen(
    uiState: FinancialUiState,
    viewModel: FinancialViewModel,
    modifier: Modifier = Modifier
) {
    val tax = uiState.taxResult
    val profile = uiState.profile

    var retirementInput by remember(profile.retirementContributionAnnual) {
        mutableStateOf(profile.retirementContributionAnnual.toInt().toString())
    }
    var healthInput by remember(profile.healthInsuranceAnnual) {
        mutableStateOf(profile.healthInsuranceAnnual.toInt().toString())
    }
    var homeLoanInput by remember(profile.homeLoanInterestAnnual) {
        mutableStateOf(profile.homeLoanInterestAnnual.toInt().toString())
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "Tax Relaxation & Relief",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Statutory deductions, mortgage relief & tax savings engine",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tax Saved Hero Card
        if (tax != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tax_savings_hero_card"),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Annual Tax Relaxation Benefit",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Savings,
                                    contentDescription = "Tax Saved",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = FinancialFormatters.formatCurrency(tax.annualTaxSaved, uiState.currencySymbol),
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp
                                ),
                                color = Color.White
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${String.format("%.1f", tax.effectiveTaxRateAfter)}% Tax",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    ),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Monthly Saved: +${FinancialFormatters.formatCurrency(tax.annualTaxSaved / 12, uiState.currencySymbol)}/mo",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = Color.White
                            )
                            Text(
                                text = "Was ${String.format("%.1f", tax.effectiveTaxRateBefore)}% without relief",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Comparison Metrics Grid
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Tax Without Relief",
                        value = FinancialFormatters.formatCurrency(tax.taxBeforeDeductions, uiState.currencySymbol),
                        subtitle = "Standard liability",
                        icon = Icons.Default.Payments,
                        accentColor = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_tax_before"
                    )
                    MetricCard(
                        title = "Tax With Relief",
                        value = FinancialFormatters.formatCurrency(tax.taxAfterDeductions, uiState.currencySymbol),
                        subtitle = "Reduced liability",
                        icon = Icons.Default.CheckCircle,
                        accentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_tax_after"
                    )
                }
            }

            // Deductions & Relaxations Summary Table
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tax_deductions_summary_card")
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Deductions & Relaxations Breakdown",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        DeductionRow("Gross Annual Salary", FinancialFormatters.formatCurrency(tax.grossAnnualSalary, uiState.currencySymbol), isBold = true)
                        DeductionRow("Standard Statutory Deduction", "-${FinancialFormatters.formatCurrency(tax.standardDeduction, uiState.currencySymbol)}", isGreen = true)
                        DeductionRow("Retirement (401k / IRA / 80C)", "-${FinancialFormatters.formatCurrency(tax.retirementDeduction, uiState.currencySymbol)}", isGreen = true)
                        DeductionRow("Health Insurance (HSA / 80D)", "-${FinancialFormatters.formatCurrency(tax.healthInsuranceDeduction, uiState.currencySymbol)}", isGreen = true)
                        if (tax.homeLoanInterestDeduction > 0) {
                            DeductionRow("Home Loan Mortgage Interest", "-${FinancialFormatters.formatCurrency(tax.homeLoanInterestDeduction, uiState.currencySymbol)}", isGreen = true)
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                                .padding(vertical = 4.dp)
                        )

                        DeductionRow("Total Allowable Relaxations", FinancialFormatters.formatCurrency(tax.totalDeductions, uiState.currencySymbol), isBold = true, isGreen = true)
                        DeductionRow("Net Taxable Income", FinancialFormatters.formatCurrency(tax.taxableIncome, uiState.currencySymbol), isBold = true)
                    }
                }
            }
        }

        // Adjust Deductions Section
        item {
            SectionHeader(
                title = "Optimize Your Relaxations",
                subtitle = "Update eligible contributions to see tax reduction"
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("adjust_deductions_card")
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Retirement deduction input
                    OutlinedTextField(
                        value = retirementInput,
                        onValueChange = {
                            retirementInput = it.filter { ch -> ch.isDigit() }
                            val amt = retirementInput.toDoubleOrNull() ?: 0.0
                            viewModel.updateTaxDeductions(
                                retirementContribAnnual = amt,
                                healthInsuranceAnnual = healthInput.toDoubleOrNull() ?: profile.healthInsuranceAnnual,
                                homeLoanInterestAnnual = homeLoanInput.toDoubleOrNull() ?: profile.homeLoanInterestAnnual,
                                otherDeductionsAnnual = profile.otherDeductionsAnnual
                            )
                        },
                        label = { Text("Annual Retirement Savings (401k/IRA/80C)") },
                        supportingText = { Text("Max statutory deduction up to $23,000/yr") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Savings, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Health insurance input
                    OutlinedTextField(
                        value = healthInput,
                        onValueChange = {
                            healthInput = it.filter { ch -> ch.isDigit() }
                            val amt = healthInput.toDoubleOrNull() ?: 0.0
                            viewModel.updateTaxDeductions(
                                retirementContribAnnual = retirementInput.toDoubleOrNull() ?: profile.retirementContributionAnnual,
                                healthInsuranceAnnual = amt,
                                homeLoanInterestAnnual = homeLoanInput.toDoubleOrNull() ?: profile.homeLoanInterestAnnual,
                                otherDeductionsAnnual = profile.otherDeductionsAnnual
                            )
                        },
                        label = { Text("Annual Health & HSA Premiums") },
                        supportingText = { Text("Tax-deductible up to $4,150/yr") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.HealthAndSafety, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Home loan interest input
                    OutlinedTextField(
                        value = homeLoanInput,
                        onValueChange = {
                            homeLoanInput = it.filter { ch -> ch.isDigit() }
                            val amt = homeLoanInput.toDoubleOrNull() ?: 0.0
                            viewModel.updateTaxDeductions(
                                retirementContribAnnual = retirementInput.toDoubleOrNull() ?: profile.retirementContributionAnnual,
                                healthInsuranceAnnual = healthInput.toDoubleOrNull() ?: profile.healthInsuranceAnnual,
                                homeLoanInterestAnnual = amt,
                                otherDeductionsAnnual = profile.otherDeductionsAnnual
                            )
                        },
                        label = { Text("Home Loan Interest Paid (Mortgage)") },
                        supportingText = { Text("Tax relaxation up to $10,000/yr (Section 24/Mortgage)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Actionable Tax Tips & Relaxations Suggestions
        if (tax?.topTaxTips?.isNotEmpty() == true) {
            item {
                SectionHeader(
                    title = "Tax Relaxation Recommendations",
                    subtitle = "How to maximize your legal exemptions"
                )
            }

            items(tax.topTaxTips) { tip ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Tip",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = tip,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeductionRow(
    title: String,
    value: String,
    isBold: Boolean = false,
    isGreen: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = if (isBold) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold) else MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            style = if (isBold) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodySmall,
            color = if (isGreen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
