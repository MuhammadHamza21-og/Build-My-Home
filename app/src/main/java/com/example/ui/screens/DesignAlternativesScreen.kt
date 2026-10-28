package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AlternativeType
import com.example.data.model.FloorPlanDesign
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen

@Composable
fun DesignAlternativesScreen(
    alternatives: List<FloorPlanDesign>,
    selectedDesign: FloorPlanDesign?,
    isUrdu: Boolean,
    onSelectDesign: (AlternativeType) -> Unit,
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
            Column {
                Text(
                    text = if (isUrdu) "۳ مختلف معماری لے آؤٹس تیار ہیں" else "3 Distinct Architectural Alternatives",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArchitecturalNavy
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isUrdu)
                        "ہر ڈیزائن مختلف طرزِ زندگی، پرائیویسی اور ہواداری کو مدنظر رکھ کر بنایا گیا ہے"
                    else
                        "Genuinely different circulation paths, staircase orientations, and living zones",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Action Row: Compare All 3
        item {
            OutlinedButton(
                onClick = { onNavigate(AppScreen.COMPARE_DESIGNS) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Compare, contentDescription = null, tint = ArchitecturalBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUrdu) "تینوں نقشوں کا آمنے سامنے موازنہ کریں" else "Compare All 3 Alternatives Side-by-Side",
                    fontWeight = FontWeight.Bold,
                    color = ArchitecturalBlue
                )
            }
        }

        items(alternatives) { design ->
            val isSelected = selectedDesign?.alternative == design.alternative
            AlternativeCard(
                design = design,
                isSelected = isSelected,
                isUrdu = isUrdu,
                onSelect = {
                    onSelectDesign(design.alternative)
                    onNavigate(AppScreen.FLOOR_PLAN_2D)
                }
            )
        }
    }
}

@Composable
private fun AlternativeCard(
    design: FloorPlanDesign,
    isSelected: Boolean,
    isUrdu: Boolean,
    onSelect: () -> Unit
) {
    val borderColor = if (isSelected) ArchitecturalEmerald else BorderSubtle
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(14.dp))
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Header Row: Badge & Alternative Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                if (isSelected) ArchitecturalEmerald else ArchitecturalNavy,
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = design.alternative.code,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = design.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = design.tagline,
                            fontSize = 11.sp,
                            color = ArchitecturalEmerald,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Surface(
                    color = if (isSelected) Color(0xFFD1FAE5) else SurfaceContainer,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (isSelected) "Active" else design.alternative.badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color(0xFF065F46) else TextSecondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = design.conceptDescription,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            // Metric Pills: Covered Area & Open Space
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricChip(
                    label = if (isUrdu) "کل کورڈ ایریا" else "Total Covered Area",
                    value = "${design.totalCoveredAreaSqFt.toInt()} sq ft",
                    color = Color(0xFFE0F2FE),
                    textColor = ArchitecturalBlue,
                    modifier = Modifier.weight(1f)
                )
                MetricChip(
                    label = if (isUrdu) "اوپن ایریا / لان" else "Open Lawn / Porch",
                    value = "${design.openAreaSqFt.toInt()} sq ft",
                    color = Color(0xFFFEF3C7),
                    textColor = Color(0xFF92400E),
                    modifier = Modifier.weight(1f)
                )
                MetricChip(
                    label = if (isUrdu) "منزلیں" else "Levels",
                    value = "${design.floors.size} Storeys",
                    color = Color(0xFFF1F5F9),
                    textColor = TextPrimary,
                    modifier = Modifier.weight(0.8f)
                )
            }

            // Advantages
            Text(
                text = if (isUrdu) "نمایاں خصوصیات:" else "Key Advantages:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = ArchitecturalNavy
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                design.advantages.take(3).forEach { adv ->
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = ArchitecturalEmerald,
                            modifier = Modifier.size(14.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = adv, fontSize = 11.sp, color = TextPrimary, lineHeight = 15.sp)
                    }
                }
            }

            // Feasibility Badge
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(6.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BorderSubtle))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = "Validated",
                        tint = ArchitecturalEmerald,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = design.feasibilityStatus,
                        fontSize = 10.sp,
                        color = TextSecondary
                    )
                }
            }

            // Button to open 2D blueprint
            Button(
                onClick = onSelect,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("select_design_${design.alternative.code.lowercase()}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSelected) ArchitecturalEmerald else ArchitecturalNavy
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Architecture, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUrdu) "اس نقشے کا ۲ ڈی بلیو پرنٹ دیکھیں →" else "Explore 2D Floor Plan & Blueprint →",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun MetricChip(
    label: String,
    value: String,
    color: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = color,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
            Text(text = label, fontSize = 9.sp, color = textColor.copy(alpha = 0.8f))
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}
