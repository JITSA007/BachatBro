package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.WishlistItemEntity
import com.example.data.model.WishlistCategory
import com.example.data.model.WishlistSimulationResult
import com.example.ui.FinancialUiState
import com.example.ui.FinancialViewModel
import com.example.ui.components.FinancialFormatters
import com.example.ui.components.SectionHeader
import kotlin.math.max

@Composable
fun WishlistScreen(
    uiState: FinancialUiState,
    viewModel: FinancialViewModel,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("fab_add_wishlist")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Goal")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Wishlist & Major Goals",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Loan modeling, timeline calculator & milestone roadmaps",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Overview Summary Card
            item {
                val totalGoalsCost = uiState.wishlistItems.sumOf { it.targetCost }
                val totalDownPayments = uiState.wishlistItems.sumOf { it.targetCost * (it.downPaymentPercent / 100.0) }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wishlist_overview_card")
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            RoundedCornerShape(24.dp)
                        ),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Active Goals",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${uiState.wishlistItems.size}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Total Wishlist Value",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = FinancialFormatters.formatCurrency(totalGoalsCost, uiState.currencySymbol),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(40.dp)
                                .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Down Payment Need",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = FinancialFormatters.formatCurrency(totalDownPayments, uiState.currencySymbol),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            )
                        }
                    }
                }
            }

            // Wishlist Goals List Header
            item {
                SectionHeader(
                    title = "Your Goals & Purchase Timelines",
                    subtitle = "How much you need and how to acquire them",
                    actionText = "+ Add Goal",
                    onActionClick = { showAddDialog = true }
                )
            }

            // Items List
            if (uiState.wishlistItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Empty",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Wishlist Goals Yet",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Add a Car, House, or Dream Vacation to calculate required loans, timelines, and monthly savings.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(uiState.wishlistItems, key = { it.id }) { item ->
                    val simulation = uiState.wishlistSimulations[item.id]
                    WishlistItemCard(
                        item = item,
                        simulation = simulation,
                        currencySymbol = uiState.currencySymbol,
                        onDelete = { viewModel.deleteWishlistItem(item.id) },
                        onUpdateSaved = { newSaved ->
                            viewModel.updateWishlistItem(item.copy(savedSoFar = newSaved))
                        }
                    )
                }
            }
        }
    }

    // Add Wishlist Item Dialog
    if (showAddDialog) {
        AddWishlistDialog(
            currencySymbol = uiState.currencySymbol,
            defaultSalary = uiState.monthlySalary,
            onDismiss = { showAddDialog = false },
            onConfirm = { title, category, cost, downPct, useLoan, rate, tenure, allocSavings, notes ->
                viewModel.addWishlistItem(
                    title = title,
                    category = category,
                    cost = cost,
                    downPaymentPercent = downPct,
                    useLoan = useLoan,
                    interestRate = rate,
                    tenureYears = tenure,
                    allocatedSavings = allocSavings,
                    notes = notes
                )
                showAddDialog = false
            }
        )
    }
}

@Composable
fun WishlistItemCard(
    item: WishlistItemEntity,
    simulation: WishlistSimulationResult?,
    currencySymbol: String,
    onDelete: () -> Unit,
    onUpdateSaved: (Double) -> Unit
) {
    var showDepositDialog by remember { mutableStateOf(false) }
    val category = WishlistCategory.entries.find { it.name == item.category } ?: WishlistCategory.OTHER
    val categoryIcon = getCategoryIcon(category)

    val targetDownPayment = simulation?.downPaymentAmount ?: (item.targetCost * (item.downPaymentPercent / 100.0))
    val savedRatio = if (targetDownPayment > 0) (item.savedSoFar / targetDownPayment).coerceIn(0.0, 1.0) else 1.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("wishlist_item_${item.id}")
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(24.dp)
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = category.displayName,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${category.displayName} • ${FinancialFormatters.formatCurrency(item.targetCost, currencySymbol)} Total",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (item.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // How to Get It: Roadmap Grid
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        RoundedCornerShape(16.dp)
                    )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ACQUISITION ROADMAP & LOAN MODEL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (item.useLoan && simulation != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Down Payment (${item.downPaymentPercent.toInt()}%)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    FinancialFormatters.formatCurrency(simulation.downPaymentAmount, currencySymbol),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Column {
                                Text("Loan Principal", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    FinancialFormatters.formatCurrency(simulation.loanAmount, currencySymbol),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Monthly EMI", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    "${FinancialFormatters.formatCurrency(simulation.monthlyLoanEmi, currencySymbol)}/mo",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tenure: ${item.loanTenureYears} yrs @ ${item.loanInterestRate}% interest",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Total Interest: ${FinancialFormatters.formatCurrency(simulation.totalInterestPaid, currencySymbol)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Timeline to reach down payment
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (simulation.isAffordable) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (simulation.isAffordable) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (simulation.isAffordable) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Time to Down Payment: ${formatMonths(simulation.monthsToDownPayment)} (at ${FinancialFormatters.formatCurrency(item.allocatedMonthlySavings, currencySymbol)}/mo)",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = simulation.affordabilityStatus,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        // Cash purchase
                        val months = simulation?.monthsToFullCash ?: 12
                        Column {
                            Text(
                                text = "Cash Purchase: Full upfront payment of ${FinancialFormatters.formatCurrency(item.targetCost, currencySymbol)}",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Time to purchase: ${formatMonths(months)} saving ${FinancialFormatters.formatCurrency(item.allocatedMonthlySavings, currencySymbol)}/month.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Tax note if eligible
                    if (simulation?.taxSavingNote != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = simulation.taxSavingNote,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Down payment savings progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved so far: ${FinancialFormatters.formatCurrency(item.savedSoFar, currencySymbol)} / ${FinancialFormatters.formatCurrency(targetDownPayment, currencySymbol)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(
                    onClick = { showDepositDialog = true },
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("+ Add Savings", style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { savedRatio.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color(0xFFE6E1E5)
            )
        }
    }

    if (showDepositDialog) {
        var depositAmount by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDepositDialog = false },
            title = { Text("Add Saved Money to ${item.title}") },
            text = {
                Column {
                    Text(
                        "Enter the amount you have set aside or deposited towards this goal:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = depositAmount,
                        onValueChange = { depositAmount = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Amount ($currencySymbol)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val added = depositAmount.toDoubleOrNull() ?: 0.0
                        onUpdateSaved(item.savedSoFar + added)
                        showDepositDialog = false
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDepositDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun formatMonths(months: Int): String {
    if (months <= 0) return "Ready now!"
    val years = months / 12
    val remMonths = months % 12
    return when {
        years > 0 && remMonths > 0 -> "$years yrs $remMonths mos"
        years > 0 -> "$years years"
        else -> "$months months"
    }
}

private fun getCategoryIcon(category: WishlistCategory): ImageVector {
    return when (category) {
        WishlistCategory.HOUSE -> Icons.Default.Home
        WishlistCategory.CAR -> Icons.Default.DirectionsCar
        WishlistCategory.VACATION -> Icons.Default.Flight
        WishlistCategory.TECH -> Icons.Default.LaptopMac
        WishlistCategory.EDUCATION -> Icons.Default.School
        WishlistCategory.WEDDING -> Icons.Default.AccountBalance
        WishlistCategory.OTHER -> Icons.Default.Star
    }
}

@Composable
fun AddWishlistDialog(
    currencySymbol: String,
    defaultSalary: Double,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        category: String,
        cost: Double,
        downPct: Double,
        useLoan: Boolean,
        rate: Double,
        tenure: Int,
        allocatedSavings: Double,
        notes: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(WishlistCategory.CAR) }
    var costText by remember { mutableStateOf("25000") }
    var downPaymentPct by remember { mutableStateOf(selectedCategory.defaultDownPaymentPercent) }
    var useLoan by remember { mutableStateOf(true) }
    var interestRateText by remember { mutableStateOf(selectedCategory.defaultInterestRate.toString()) }
    var tenureYearsText by remember { mutableStateOf(selectedCategory.defaultLoanTenureYears.toString()) }
    var monthlyAllocatedText by remember { mutableStateOf("400") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Wishlist Goal") },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Goal Title (e.g. Electric Sedan, House)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("wishlist_title_input")
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Category", style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(WishlistCategory.entries) { cat ->
                            val isSelected = cat == selectedCategory
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable {
                                    selectedCategory = cat
                                    downPaymentPct = cat.defaultDownPaymentPercent
                                    interestRateText = cat.defaultInterestRate.toString()
                                    tenureYearsText = cat.defaultLoanTenureYears.toString()
                                }
                            ) {
                                Text(
                                    text = cat.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = costText,
                        onValueChange = { costText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Estimated Total Cost ($currencySymbol)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Finance with Loan", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = useLoan,
                            onCheckedChange = { useLoan = it }
                        )
                    }

                    if (useLoan) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Down Payment: ${downPaymentPct.toInt()}% (${FinancialFormatters.formatCurrency((costText.toDoubleOrNull() ?: 0.0) * (downPaymentPct / 100.0), currencySymbol)})",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Slider(
                            value = downPaymentPct.toFloat(),
                            onValueChange = { downPaymentPct = it.toDouble() },
                            valueRange = 5f..80f,
                            steps = 15
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = interestRateText,
                                onValueChange = { interestRateText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text("Interest %") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = tenureYearsText,
                                onValueChange = { tenureYearsText = it.filter { ch -> ch.isDigit() } },
                                label = { Text("Tenure (Yrs)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = monthlyAllocatedText,
                        onValueChange = { monthlyAllocatedText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text("Monthly Savings Allocated to Goal ($currencySymbol)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes / Specifications (Optional)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cost = costText.toDoubleOrNull() ?: 0.0
                    val rate = interestRateText.toDoubleOrNull() ?: 7.0
                    val tenure = tenureYearsText.toIntOrNull() ?: 5
                    val alloc = monthlyAllocatedText.toDoubleOrNull() ?: 300.0
                    if (title.isNotBlank() && cost > 0) {
                        onConfirm(
                            title,
                            selectedCategory.name,
                            cost,
                            downPaymentPct,
                            useLoan,
                            rate,
                            tenure,
                            alloc,
                            notes
                        )
                    }
                },
                enabled = title.isNotBlank() && (costText.toDoubleOrNull() ?: 0.0) > 0.0
            ) {
                Text("Add Goal")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
