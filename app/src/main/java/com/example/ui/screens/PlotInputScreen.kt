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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.PlotBoundaryCanvas
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlotInputScreen(
    plotInfo: PlotInfo,
    isUrdu: Boolean,
    onPlotInfoChanged: (PlotInfo) -> Unit,
    onProceed: () -> Unit
) {
    var projectName by remember(plotInfo) { mutableStateOf(plotInfo.projectName) }
    var city by remember(plotInfo) { mutableStateOf(plotInfo.city) }
    var statedAreaStr by remember(plotInfo) { mutableStateOf(plotInfo.statedAreaValue.toString()) }
    var selectedUnit by remember(plotInfo) { mutableStateOf(plotInfo.statedAreaUnit) }
    var marlaStandard by remember(plotInfo) { mutableStateOf(plotInfo.marlaStandard) }
    var plotShape by remember(plotInfo) { mutableStateOf(plotInfo.plotShape) }
    var roadFacing by remember(plotInfo) { mutableStateOf(plotInfo.roadFacing) }

    var frontStr by remember(plotInfo) { mutableStateOf(plotInfo.boundaries.front.toString()) }
    var rearStr by remember(plotInfo) { mutableStateOf(plotInfo.boundaries.rear.toString()) }
    var leftStr by remember(plotInfo) { mutableStateOf(plotInfo.boundaries.left.toString()) }
    var rightStr by remember(plotInfo) { mutableStateOf(plotInfo.boundaries.right.toString()) }

    fun commitChanges() {
        val statedArea = statedAreaStr.toFloatOrNull() ?: plotInfo.statedAreaValue
        val front = frontStr.toFloatOrNull() ?: plotInfo.boundaries.front
        val rear = rearStr.toFloatOrNull() ?: plotInfo.boundaries.rear
        val left = leftStr.toFloatOrNull() ?: plotInfo.boundaries.left
        val right = rightStr.toFloatOrNull() ?: plotInfo.boundaries.right

        val updated = plotInfo.copy(
            projectName = projectName,
            city = city,
            statedAreaValue = statedArea,
            statedAreaUnit = selectedUnit,
            marlaStandard = marlaStandard,
            plotShape = plotShape,
            roadFacing = roadFacing,
            boundaries = BoundaryMeasurements(front = front, rear = rear, left = left, right = right)
        )
        onPlotInfoChanged(updated)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step progress header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isUrdu) "مرحلہ ۱ از ۲: پلاٹ کی پیمائش" else "Step 1 of 2: Plot Specifications",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArchitecturalNavy
                )
                Surface(
                    color = Color(0xFFD1FAE5),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isUrdu) "۵۰ فیصد مکمل" else "50% Completed",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF065F46),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            LinearProgressIndicator(
                progress = { 0.5f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = ArchitecturalEmerald,
                trackColor = Color(0xFFE2E8F0),
            )
        }

        // Section 1: Project Identity
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
                        text = if (isUrdu) "منصوبے کا نام اور شہر" else "Project & Location",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )

                    OutlinedTextField(
                        value = projectName,
                        onValueChange = {
                            projectName = it
                            commitChanges()
                        },
                        label = { Text(if (isUrdu) "پروجیکٹ کا نام" else "Project Name") },
                        modifier = Modifier.fillMaxWidth().testTag("project_name_input"),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = ArchitecturalBlue) }
                    )

                    // City Selector
                    val cities = listOf("Lahore", "Islamabad", "Rawalpindi", "Karachi", "Peshawar", "Multan", "Faisalabad")
                    var cityExpanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = cityExpanded,
                        onExpandedChange = { cityExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isUrdu) "شہر" else "City") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cityExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("city_dropdown"),
                            leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = ArchitecturalBlue) }
                        )
                        ExposedDropdownMenu(
                            expanded = cityExpanded,
                            onDismissRequest = { cityExpanded = false }
                        ) {
                            cities.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c) },
                                    onClick = {
                                        city = c
                                        cityExpanded = false
                                        commitChanges()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Area & Pakistani Marla Standard
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
                        text = if (isUrdu) "پلاٹ کا کل رقبہ اور مرلہ معیار" else "Plot Area & Marla Standard",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = statedAreaStr,
                            onValueChange = {
                                statedAreaStr = it
                                commitChanges()
                            },
                            label = { Text(if (isUrdu) "رقبہ کی مقدار" else "Area Value") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1.2f).testTag("area_value_input"),
                            singleLine = true
                        )

                        // Unit Selector
                        var unitExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = unitExpanded,
                            onExpandedChange = { unitExpanded = it },
                            modifier = Modifier.weight(1.3f)
                        ) {
                            OutlinedTextField(
                                value = selectedUnit.label,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(if (isUrdu) "یونٹ" else "Unit") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            )
                            ExposedDropdownMenu(
                                expanded = unitExpanded,
                                onDismissRequest = { unitExpanded = false }
                            ) {
                                PlotAreaUnit.values().forEach { u ->
                                    DropdownMenuItem(
                                        text = { Text(u.label) },
                                        onClick = {
                                            selectedUnit = u
                                            unitExpanded = false
                                            commitChanges()
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Pakistani Marla Standard selection
                    Text(
                        text = if (isUrdu) "مرلہ کی پیمائش کا طریقہ کار منتخب کریں:" else "Select Regional Marla Standard:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    marlaStandard = MarlaStandard.STANDARD_225
                                    commitChanges()
                                }
                        ) {
                            RadioButton(
                                selected = marlaStandard == MarlaStandard.STANDARD_225,
                                onClick = {
                                    marlaStandard = MarlaStandard.STANDARD_225
                                    commitChanges()
                                }
                            )
                            Column {
                                Text(
                                    text = "225 Sq Ft / Marla (Most Societies)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Bahria Town, DHA, Gulberg, Lake City, etc.",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    marlaStandard = MarlaStandard.STANDARD_272_25
                                    commitChanges()
                                }
                        ) {
                            RadioButton(
                                selected = marlaStandard == MarlaStandard.STANDARD_272_25,
                                onClick = {
                                    marlaStandard = MarlaStandard.STANDARD_272_25
                                    commitChanges()
                                }
                            )
                            Column {
                                Text(
                                    text = "272.25 Sq Ft / Marla (Government Revenue)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Official LDA, CDA, Punjab Revenue standard (1 Kanal = 5445 sq ft)",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 3: Plot Shape & Road Facing
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
                        text = if (isUrdu) "پلاٹ کی ہیئت اور رخ" else "Plot Shape & Road Access",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )

                    // Plot Shape chips
                    Text(text = if (isUrdu) "شکل:" else "Shape:", fontSize = 12.sp, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            PlotShape.RECTANGLE to "Rectangle",
                            PlotShape.SQUARE to "Square",
                            PlotShape.CORNER to "Corner",
                            PlotShape.TRAPEZOID to "Trapezoid"
                        ).forEach { (shape, label) ->
                            FilterChip(
                                selected = plotShape == shape,
                                onClick = {
                                    plotShape = shape
                                    commitChanges()
                                },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ArchitecturalNavy,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Road Facing
                    Text(text = if (isUrdu) "روڈ فیسنگ:" else "Road Facing Side:", fontSize = 12.sp, color = TextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            RoadFacingSide.FRONT to "Front",
                            RoadFacingSide.CORNER_FRONT_LEFT to "Corner Left",
                            RoadFacingSide.FRONT_AND_REAR to "Double Road"
                        ).forEach { (rf, label) ->
                            FilterChip(
                                selected = roadFacing == rf,
                                onClick = {
                                    roadFacing = rf
                                    commitChanges()
                                },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Section 4: Boundary Dimensions (Front, Rear, Left, Right)
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
                        text = if (isUrdu) "چاروں حدود کی درست لمبائی (فٹ میں)" else "Plot Boundary Measurements (Feet)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = frontStr,
                            onValueChange = {
                                frontStr = it
                                commitChanges()
                            },
                            label = { Text(if (isUrdu) "سامنے (Front)" else "Front (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("front_boundary_input")
                        )
                        OutlinedTextField(
                            value = rearStr,
                            onValueChange = {
                                rearStr = it
                                commitChanges()
                            },
                            label = { Text(if (isUrdu) "پیچھے (Rear)" else "Rear (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("rear_boundary_input")
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = leftStr,
                            onValueChange = {
                                leftStr = it
                                commitChanges()
                            },
                            label = { Text(if (isUrdu) "بائیں (Left)" else "Left (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("left_boundary_input")
                        )
                        OutlinedTextField(
                            value = rightStr,
                            onValueChange = {
                                rightStr = it
                                commitChanges()
                            },
                            label = { Text(if (isUrdu) "دائیں (Right)" else "Right (ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("right_boundary_input")
                        )
                    }
                }
            }
        }

        // Section 5: Real-time Plot Boundary Canvas
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isUrdu) "پلاٹ کا نقشہ و حدود کا جائزہ" else "Plot Geometry & Road Preview",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArchitecturalNavy
                )
                PlotBoundaryCanvas(
                    plotInfo = plotInfo,
                    isUrdu = isUrdu
                )
            }
        }

        // Section 6: Discrepancy Alert if boundaries don't match stated area
        if (plotInfo.hasSignificantDiscrepancy()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFCA5A5)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = "Discrepancy Warning",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isUrdu) "رقبہ میں فرق پایا گیا ہے!" else "Area Discrepancy Detected!",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Stated Area: ${plotInfo.getStatedAreaInSqFt().toInt()} sq ft (${plotInfo.statedAreaValue} ${plotInfo.statedAreaUnit.shortLabel})\nCalculated from Dimensions: ${plotInfo.getCalculatedAreaInSqFt().toInt()} sq ft (~${String.format("%.2f", plotInfo.sqFtToMarla(plotInfo.getCalculatedAreaInSqFt()))} Marla).\nDiscrepancy: ${plotInfo.getAreaDiscrepancyPercentage().toInt()}%. Please check your boundary measurements.",
                                fontSize = 11.sp,
                                color = Color(0xFF7F1D1D),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Continue Button
        item {
            Button(
                onClick = {
                    commitChanges()
                    onProceed()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("proceed_to_requirements_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ArchitecturalNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isUrdu) "جاری رکھیں: کمروں کی ضروریات درج کریں →" else "Next: Specify Rooms & Requirements →",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
