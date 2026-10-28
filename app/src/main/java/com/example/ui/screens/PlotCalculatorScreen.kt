package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.converters.PlotAreaConverter
import com.example.data.model.MarlaStandard
import com.example.data.model.Point2D
import com.example.ui.theme.*

@Composable
fun PlotCalculatorScreen(
    currentStandard: MarlaStandard,
    isUrdu: Boolean,
    onStandardChanged: (MarlaStandard) -> Unit
) {
    var standard by remember { mutableStateOf(currentStandard) }
    var inputValueStr by remember { mutableStateOf("10") }
    var inputUnitIndex by remember { mutableIntStateOf(0) } // 0: Marla, 1: Kanal, 2: Sq Ft, 3: Sq Yards, 4: Acres

    val units = listOf("Marla", "Kanal", "Square Feet", "Square Yards (Guz)", "Acres")

    val inputValue = inputValueStr.toFloatOrNull() ?: 0f

    // Calculate all equivalent units from input value
    val sqFt = when (inputUnitIndex) {
        0 -> PlotAreaConverter.marlaToSqFt(inputValue, standard)
        1 -> PlotAreaConverter.kanalToSqFt(inputValue, standard)
        2 -> inputValue
        3 -> PlotAreaConverter.sqYardsToSqFt(inputValue)
        4 -> PlotAreaConverter.acreToSqFt(inputValue, standard)
        else -> 0f
    }

    val marlaVal = PlotAreaConverter.sqFtToMarla(sqFt, standard)
    val kanalVal = PlotAreaConverter.marlaToKanal(marlaVal)
    val sqYardsVal = PlotAreaConverter.sqFtToSqYards(sqFt)
    val acresVal = PlotAreaConverter.sqFtToAcre(sqFt, standard)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = if (isUrdu) "پلاٹ رقبہ اور اراضی کیلکولیٹر" else "Pakistani Plot Area & Land Calculator",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ArchitecturalNavy
            )
            Text(
                text = if (isUrdu)
                    "مرلہ، کنال، مربع فٹ، گز اور ایکڑ میں فوری اور درست تبدیلیاں"
                else
                    "Official LDA / CDA and Private Society Marla conversion standards",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Standard Selector Card
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isUrdu) "مرلہ کا علاقائی معیار منتخب کریں:" else "Active Marla Conversion Standard:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                standard = MarlaStandard.STANDARD_225
                                onStandardChanged(standard)
                            }
                    ) {
                        RadioButton(
                            selected = standard == MarlaStandard.STANDARD_225,
                            onClick = {
                                standard = MarlaStandard.STANDARD_225
                                onStandardChanged(standard)
                            }
                        )
                        Column {
                            Text("225 Sq Ft / Marla (Private Societies)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Bahria, DHA, Lake City, Park View, Fazaia, etc.", fontSize = 11.sp, color = TextSecondary)
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                standard = MarlaStandard.STANDARD_272_25
                                onStandardChanged(standard)
                            }
                    ) {
                        RadioButton(
                            selected = standard == MarlaStandard.STANDARD_272_25,
                            onClick = {
                                standard = MarlaStandard.STANDARD_272_25
                                onStandardChanged(standard)
                            }
                        )
                        Column {
                            Text("272.25 Sq Ft / Marla (Govt Revenue / LDA / CDA)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Punjab Land Revenue, 1 Kanal = 5445 sq ft", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        // Input Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isUrdu) "رقبہ درج کریں" else "Enter Plot Area",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )

                    OutlinedTextField(
                        value = inputValueStr,
                        onValueChange = { inputValueStr = it },
                        label = { Text("Value") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Unit selector row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        units.forEachIndexed { index, name ->
                            FilterChip(
                                selected = inputUnitIndex == index,
                                onClick = { inputUnitIndex = index },
                                label = { Text(name.split(" ")[0], fontSize = 11.sp) },
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

        // Results Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ArchitecturalNavy),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isUrdu) "مساوی رقبہ جات (تمام اکائیوں میں)" else "EQUIVALENT MEASUREMENTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalCyan,
                        letterSpacing = 1.sp
                    )

                    ResultConversionRow("Square Feet (sq ft)", String.format("%,.1f", sqFt), ArchitecturalCyan)
                    ResultConversionRow("Marlas", String.format("%.3f", marlaVal), Color.White)
                    ResultConversionRow("Kanals (1 Kanal = 20 Marla)", String.format("%.3f", kanalVal), Color.White)
                    ResultConversionRow("Square Yards (Guz)", String.format("%,.1f", sqYardsVal), Color.White)
                    ResultConversionRow("Acres (1 Acre = 8 Kanal)", String.format("%.4f", acresVal), ArchitecturalGold)
                }
            }
        }

        // Polygon / Irregular Plot Formula Explanation
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (isUrdu) "بے ترتیب پلاٹ کا رقبہ نکالنے کا اصول:" else "Irregular & Trapezoid Plot Formula:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )
                    Text(
                        text = "1. For rectangular plots: Front × Left.\n2. For trapezoidal plots with unequal sides: [(Front + Rear) ÷ 2] × [(Left + Right) ÷ 2].\n3. For irregular polygon plots with 4+ corner points: The Shoelace coordinate survey formula is used:\nArea = 1/2 |∑ (x_i y_{i+1} - x_{i+1} y_i)|.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultConversionRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFFCBD5E1))
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
    HorizontalDivider(color = Color(0xFF1E293B))
}
