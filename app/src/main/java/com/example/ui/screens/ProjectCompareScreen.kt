package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlternativeType
import com.example.data.model.FloorPlanDesign
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen

@Composable
fun ProjectCompareScreen(
    alternatives: List<FloorPlanDesign>,
    selectedDesign: FloorPlanDesign?,
    isUrdu: Boolean,
    onSelectAlternative: (AlternativeType) -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (isUrdu) "تینوں ڈیزائنز کا آمنے سامنے موازنہ" else "Compare Architectural Alternatives",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ArchitecturalNavy
            )
            Text(
                text = if (isUrdu)
                    "اپنی فیملی کی ترجیحات کے مطابق بہترین لے آؤٹ منتخب کریں"
                else
                    "Evaluate space utilization, circulation, and architectural trade-offs",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        items(alternatives) { design ->
            val isCurrent = selectedDesign?.alternative == design.alternative
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                border = if (isCurrent) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ArchitecturalEmerald), width = 2.dp) else null,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(if (isCurrent) ArchitecturalEmerald else ArchitecturalNavy, RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = design.alternative.code,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = design.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        if (isCurrent) {
                            Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(4.dp)) {
                                Text(
                                    text = "Selected",
                                    fontSize = 10.sp,
                                    color = Color(0xFF065F46),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Text(text = design.conceptDescription, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)

                    HorizontalDivider(color = BorderSubtle)

                    CompareMetricRow("Total Covered Area", "${design.totalCoveredAreaSqFt.toInt()} sq ft")
                    CompareMetricRow("Open Space / Lawn", "${design.openAreaSqFt.toInt()} sq ft")
                    CompareMetricRow(
                        "Staircase Placement",
                        when (design.alternative) {
                            AlternativeType.DESIGN_A -> "Foyer entrance (Rental/Independent)"
                            AlternativeType.DESIGN_B -> "Central living lounge (Scenic floating)"
                            AlternativeType.DESIGN_C -> "Double-height grand lobby"
                        }
                    )
                    CompareMetricRow(
                        "Kitchen Configuration",
                        when (design.alternative) {
                            AlternativeType.DESIGN_A -> "Closed compact functional kitchen"
                            AlternativeType.DESIGN_B -> "Spacious kitchen with breakfast counter"
                            AlternativeType.DESIGN_C -> "Dual Kitchen: Show Island + Spice Kitchen"
                        }
                    )
                    CompareMetricRow(
                        "Guest Drawing Privacy",
                        when (design.alternative) {
                            AlternativeType.DESIGN_A -> "Direct front access near entrance"
                            AlternativeType.DESIGN_B -> "Completely secluded formal guest suite"
                            AlternativeType.DESIGN_C -> "Executive salon overlooking garden"
                        }
                    )

                    Button(
                        onClick = {
                            onSelectAlternative(design.alternative)
                            onNavigate(AppScreen.FLOOR_PLAN_2D)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCurrent) ArchitecturalEmerald else ArchitecturalNavy
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Text(
                            text = if (isCurrent) "Viewing Active Plan →" else "Select Design ${design.alternative.code} & Open 2D →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompareMetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}
