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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import kotlin.math.max

@Composable
fun RetirementScreen(
    uiState: FinancialUiState,
    viewModel: FinancialViewModel,
    modifier: Modifier = Modifier
) {
    val retirement = uiState.retirementResult
    val profile = uiState.profile

    var currentAgeInput by remember(profile.currentAge) { mutableStateOf(profile.currentAge.toString()) }
    var retirementAgeInput by remember(profile.retirementAge) { mutableStateOf(profile.retirementAge.toString()) }
    var lifeExpInput by remember(profile.lifeExpectancy) { mutableStateOf(profile.lifeExpectancy.toString()) }
    var currentSavingsInput by remember(profile.currentRetirementSavings) {
        mutableStateOf(profile.currentRetirementSavings.toInt().toString())
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
                    text = "Retirement Savings Plan",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Target nest egg corpus & required monthly contributions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (retirement != null) {
            // Target Nest Egg Hero
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("retirement_hero_card"),
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
                                text = "Target Retirement Nest Egg",
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
                                    imageVector = Icons.Default.LockClock,
                                    contentDescription = "Nest Egg",
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
                                text = FinancialFormatters.formatCurrency(retirement.targetNestEggCorpus, uiState.currencySymbol),
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
                                    text = "${retirement.yearsToRetire}y left",
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

                        Text(
                            text = "Supports ${FinancialFormatters.formatCurrency(retirement.futureMonthlyExpenseAtRetirement, uiState.currencySymbol)}/month in retirement (inflation-adjusted) for ${retirement.retirementDurationYears} years (ages ${retirement.retirementAge} to ${profile.lifeExpectancy}).",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Retirement Readiness Score Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("retirement_readiness_card")
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Retirement Readiness",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = retirement.readinessStatus,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (retirement.readinessScorePercent >= 90) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (retirement.readinessScorePercent >= 90) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = "${retirement.readinessScorePercent}% Ready",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = if (retirement.readinessScorePercent >= 90) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { (retirement.readinessScorePercent / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = if (retirement.readinessScorePercent >= 90) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                            trackColor = Color(0xFFE6E1E5)
                        )
                    }
                }
            }

            // Monthly Savings Comparison Metrics
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Required to Save",
                        value = "${FinancialFormatters.formatCurrency(retirement.requiredMonthlySavings, uiState.currencySymbol)}/mo",
                        subtitle = "Target monthly pace",
                        icon = Icons.Default.Savings,
                        accentColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_required_monthly"
                    )
                    MetricCard(
                        title = "Your Monthly Surplus",
                        value = "${FinancialFormatters.formatCurrency(retirement.actualMonthlySavings, uiState.currencySymbol)}/mo",
                        subtitle = if (retirement.savingsDeficitOrSurplus >= 0) "+${FinancialFormatters.formatCurrency(retirement.savingsDeficitOrSurplus, uiState.currencySymbol)} surplus" else "${FinancialFormatters.formatCurrency(retirement.savingsDeficitOrSurplus, uiState.currencySymbol)} gap",
                        icon = if (retirement.savingsDeficitOrSurplus >= 0) Icons.Default.CheckCircle else Icons.Default.Warning,
                        accentColor = if (retirement.savingsDeficitOrSurplus >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f),
                        testTag = "metric_actual_monthly"
                    )
                }
            }

            // Milestone Roadmap Table
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("retirement_roadmap_card")
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
                            text = "Retirement Roadmap Summary",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        SummaryItem("Time Horizon", "${retirement.yearsToRetire} years remaining (Age ${retirement.currentAge} → ${retirement.retirementAge})")
                        SummaryItem("Current Retirement Savings", FinancialFormatters.formatCurrency(profile.currentRetirementSavings, uiState.currencySymbol))
                        SummaryItem("Existing Savings at Retirement", FinancialFormatters.formatCurrency(retirement.existingSavingsGrowthAtRetirement, uiState.currencySymbol), isGreen = true)
                        SummaryItem("Net Additional Corpus Needed", FinancialFormatters.formatCurrency(retirement.netCorpusNeeded, uiState.currencySymbol))
                        SummaryItem("Expected Retirement Years", "${retirement.retirementDurationYears} years (Age ${retirement.retirementAge} to ${profile.lifeExpectancy})")
                        SummaryItem("Monthly Income in Retirement", "${FinancialFormatters.formatCurrency(retirement.futureMonthlyExpenseAtRetirement, uiState.currencySymbol)}/mo")
                    }
                }
            }
        }

        // Adjust Retirement Parameters Card
        item {
            SectionHeader(
                title = "Adjust Retirement Parameters",
                subtitle = "Fine tune age, timeline, and existing corpus"
            )
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("adjust_retirement_card")
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        RoundedCornerShape(24.dp)
                    ),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = currentAgeInput,
                            onValueChange = {
                                currentAgeInput = it.filter { ch -> ch.isDigit() }
                                updateRetirementParams(currentAgeInput, retirementAgeInput, lifeExpInput, currentSavingsInput, profile, viewModel)
                            },
                            label = { Text("Current Age") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = retirementAgeInput,
                            onValueChange = {
                                retirementAgeInput = it.filter { ch -> ch.isDigit() }
                                updateRetirementParams(currentAgeInput, retirementAgeInput, lifeExpInput, currentSavingsInput, profile, viewModel)
                            },
                            label = { Text("Retire Age") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = lifeExpInput,
                            onValueChange = {
                                lifeExpInput = it.filter { ch -> ch.isDigit() }
                                updateRetirementParams(currentAgeInput, retirementAgeInput, lifeExpInput, currentSavingsInput, profile, viewModel)
                            },
                            label = { Text("Life Exp.") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = currentSavingsInput,
                        onValueChange = {
                            currentSavingsInput = it.filter { ch -> ch.isDigit() }
                            updateRetirementParams(currentAgeInput, retirementAgeInput, lifeExpInput, currentSavingsInput, profile, viewModel)
                        },
                        label = { Text("Current Retirement Savings (${uiState.currencySymbol})") },
                        supportingText = { Text("Funds currently in 401k, IRA, Superannuation, or stocks") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = { Icon(Icons.Default.Savings, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun updateRetirementParams(
    curAgeStr: String,
    retAgeStr: String,
    lifeExpStr: String,
    savingsStr: String,
    profile: com.example.data.local.UserProfileEntity,
    viewModel: FinancialViewModel
) {
    val cur = curAgeStr.toIntOrNull() ?: profile.currentAge
    val ret = retAgeStr.toIntOrNull() ?: profile.retirementAge
    val life = lifeExpStr.toIntOrNull() ?: profile.lifeExpectancy
    val sav = savingsStr.toDoubleOrNull() ?: profile.currentRetirementSavings

    if (cur in 18..75 && ret > cur && life > ret) {
        viewModel.updateRetirementDetails(
            currentAge = cur,
            retireAge = ret,
            lifeExp = life,
            savings = sav
        )
    }
}

@Composable
private fun SummaryItem(title: String, value: String, isGreen: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = if (isGreen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}
