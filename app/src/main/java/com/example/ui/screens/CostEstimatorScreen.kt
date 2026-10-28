package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.cost.ConstructionCostCalculator
import com.example.data.model.ConstructionCostEstimate
import com.example.data.model.ConstructionQuality
import com.example.ui.components.SafetyDisclaimerBanner
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CostEstimatorScreen(
    currentEstimate: ConstructionCostEstimate?,
    isUrdu: Boolean,
    onRecalculate: (String, ConstructionQuality) -> Unit
) {
    var city by remember(currentEstimate) { mutableStateOf(currentEstimate?.city ?: "Lahore") }
    var quality by remember(currentEstimate) { mutableStateOf(currentEstimate?.quality ?: ConstructionQuality.STANDARD) }

    val estimate = currentEstimate ?: ConstructionCostCalculator.calculateCost(2400f, city, quality)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card with Grand Total
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ArchitecturalNavy),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isUrdu) "کل تخمینہ شدہ تعمیراتی لاگت" else "ESTIMATED TOTAL CONSTRUCTION COST",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalCyan,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = estimate.formatPKR(estimate.grandTotalPKR),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "PKR ${String.format("%,.0f", estimate.grandTotalPKR)} (incl. 5% contingency reserve)",
                        fontSize = 11.sp,
                        color = Color(0xFFCBD5E1)
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = if (isUrdu) "گرے اسٹرکچر" else "Grey Structure", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text(text = estimate.formatPKR(estimate.totalGreyCostPKR), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "@ PKR ${estimate.greyStructureRatePerSqFt.toInt()}/sqft", fontSize = 10.sp, color = ArchitecturalEmerald)
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(36.dp)
                                .background(Color(0xFF334155))
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = if (isUrdu) "فنشنگ لاگت" else "Finishing Cost", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text(text = estimate.formatPKR(estimate.totalFinishingCostPKR), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "@ PKR ${estimate.finishingRatePerSqFt.toInt()}/sqft", fontSize = 10.sp, color = ArchitecturalGold)
                        }
                    }
                }
            }
        }

        // City & Quality Selector Bar
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isUrdu) "شہر اور معیار کا انتخاب کریں" else "Location & Quality Parameters",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )

                    // City Dropdown
                    val cities = listOf("Lahore", "Islamabad", "Rawalpindi", "Karachi", "Peshawar", "Multan", "Faisalabad")
                    var cityExp by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = cityExp,
                        onExpandedChange = { cityExp = it }
                    ) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Construction City") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExp) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        )
                        ExposedDropdownMenu(
                            expanded = cityExp,
                            onDismissRequest = { cityExp = false }
                        ) {
                            cities.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c) },
                                    onClick = {
                                        city = c
                                        cityExp = false
                                        onRecalculate(city, quality)
                                    }
                                )
                            }
                        }
                    }

                    // Quality Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            ConstructionQuality.ECONOMY to "Economy",
                            ConstructionQuality.STANDARD to "Standard",
                            ConstructionQuality.PREMIUM_EXECUTIVE to "Premium"
                        ).forEach { (q, label) ->
                            FilterChip(
                                selected = quality == q,
                                onClick = {
                                    quality = q
                                    onRecalculate(city, quality)
                                },
                                label = { Text(label, fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ArchitecturalNavy,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section: Itemized Material Estimates (Steel, Cement, Bricks, etc.)
        item {
            Text(
                text = if (isUrdu) "ضروری مٹیریل کا تخمینہ (مقدار و ریٹ)" else "Estimated Material Quantities & Market Rates",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ArchitecturalNavy
            )
        }

        items(estimate.materialEstimates) { mat ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mat.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Estimated: ${String.format("%,.1f", mat.quantity)} ${mat.unit} • Rate: PKR ${mat.unitRatePKR.toInt()}/${mat.unit}",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "PKR ${String.format("%,.0f", mat.totalCostPKR)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ArchitecturalNavy,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Section: Work Breakdown
        item {
            Text(
                text = if (isUrdu) "شعبہ وار لاگت کی تقسیم" else "Trade-wise Cost Breakdown",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = ArchitecturalNavy
            )
        }

        items(estimate.costBreakdown) { item ->
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text(
                            text = "PKR ${String.format("%,.0f", item.costPKR)} (${item.percentage.toInt()}%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ArchitecturalEmerald
                        )
                    }
                    Text(text = item.description, fontSize = 10.sp, color = TextSecondary)
                }
            }
        }

        // Disclaimer
        item {
            SafetyDisclaimerBanner(isUrdu = isUrdu)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
