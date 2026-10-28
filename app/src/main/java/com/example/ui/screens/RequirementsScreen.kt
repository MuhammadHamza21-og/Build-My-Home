package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun RequirementsScreen(
    requirements: HouseRequirements,
    isUrdu: Boolean,
    isGenerating: Boolean,
    onRequirementsChanged: (HouseRequirements) -> Unit,
    onGenerate: () -> Unit
) {
    var buildingType by remember(requirements) { mutableStateOf(requirements.buildingType) }
    var bedrooms by remember(requirements) { mutableIntStateOf(requirements.rooms.bedrooms) }
    var attachedBaths by remember(requirements) { mutableIntStateOf(requirements.rooms.attachedBaths) }
    var commonBaths by remember(requirements) { mutableIntStateOf(requirements.rooms.commonBaths) }
    var kitchens by remember(requirements) { mutableIntStateOf(requirements.rooms.kitchens) }
    var carSlots by remember(requirements) { mutableIntStateOf(requirements.rooms.carParkingSlots) }
    var separateGuestEntrance by remember(requirements) { mutableStateOf(requirements.separateGuestEntrance) }
    var openPlanKitchen by remember(requirements) { mutableStateOf(requirements.openPlanKitchen) }
    var archStyle by remember(requirements) { mutableStateOf(requirements.architecturalStyle) }
    var quality by remember(requirements) { mutableStateOf(requirements.constructionQuality) }

    fun commit() {
        val updated = requirements.copy(
            buildingType = buildingType,
            rooms = requirements.rooms.copy(
                bedrooms = bedrooms,
                attachedBaths = attachedBaths,
                commonBaths = commonBaths,
                kitchens = kitchens,
                carParkingSlots = carSlots
            ),
            separateGuestEntrance = separateGuestEntrance,
            openPlanKitchen = openPlanKitchen,
            architecturalStyle = archStyle,
            constructionQuality = quality
        )
        onRequirementsChanged(updated)
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
                    text = if (isUrdu) "مرحلہ ۲ از ۲: کمروں کی تفصیلات" else "Step 2 of 2: Living Requirements",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ArchitecturalNavy
                )
                Surface(
                    color = Color(0xFFD1FAE5),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isUrdu) "۱۰۰ فیصد مکمل" else "Ready to Generate",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF065F46),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            LinearProgressIndicator(
                progress = { 1.0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = ArchitecturalEmerald,
                trackColor = Color(0xFFE2E8F0)
            )
        }

        // Section 1: Building Floors
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
                        text = if (isUrdu) "تعمیر کی قسم (منزلیں)" else "Building Storeys",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            BuildingType.SINGLE_STOREY to "Single Storey",
                            BuildingType.DOUBLE_STOREY to "Double Storey",
                            BuildingType.TRIPLE_STOREY to "Triple Storey"
                        ).forEach { (type, label) ->
                            FilterChip(
                                selected = buildingType == type,
                                onClick = {
                                    buildingType = type
                                    commit()
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

        // Section 2: Room Quantities Stepper
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
                        text = if (isUrdu) "کمروں اور جگہوں کی تعداد" else "Rooms & Space Requirements",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )

                    RoomStepperRow(
                        label = if (isUrdu) "بیڈ رومز (Bedrooms)" else "Bedrooms",
                        count = bedrooms,
                        min = 1,
                        max = 8,
                        onChanged = {
                            bedrooms = it
                            commit()
                        }
                    )
                    HorizontalDivider(color = BorderSubtle)

                    RoomStepperRow(
                        label = if (isUrdu) "اٹیچ باتھ رومز (Attached Baths)" else "Attached Bathrooms",
                        count = attachedBaths,
                        min = 1,
                        max = 8,
                        onChanged = {
                            attachedBaths = it
                            commit()
                        }
                    )
                    HorizontalDivider(color = BorderSubtle)

                    RoomStepperRow(
                        label = if (isUrdu) "پاؤڈر / مہمان واش روم" else "Powder / Common Bath",
                        count = commonBaths,
                        min = 0,
                        max = 3,
                        onChanged = {
                            commonBaths = it
                            commit()
                        }
                    )
                    HorizontalDivider(color = BorderSubtle)

                    RoomStepperRow(
                        label = if (isUrdu) "باورچی خانے (Kitchens)" else "Kitchens",
                        count = kitchens,
                        min = 1,
                        max = 3,
                        onChanged = {
                            kitchens = it
                            commit()
                        }
                    )
                    HorizontalDivider(color = BorderSubtle)

                    RoomStepperRow(
                        label = if (isUrdu) "گاڑیوں کی پارکنگ (Car Porch)" else "Car Porch Capacity",
                        count = carSlots,
                        min = 1,
                        max = 3,
                        onChanged = {
                            carSlots = it
                            commit()
                        }
                    )
                }
            }
        }

        // Section 3: Privacy & Kitchen Layout Preferences
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isUrdu) "خاندانی پرائیویسی و کچن ترجیحات" else "Family Privacy & Layout Preferences",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isUrdu) "ڈرائنگ روم کا الگ راستہ" else "Separate Guest Drawing Entrance",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isUrdu) "مہمان پورچ سے سیدھے ڈرائنگ میں داخل ہوں" else "Direct porch access without entering private living lounge",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = separateGuestEntrance,
                            onCheckedChange = {
                                separateGuestEntrance = it
                                commit()
                            }
                        )
                    }

                    HorizontalDivider(color = BorderSubtle)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isUrdu) "اوپن پلان کچن" else "Open Concept Kitchen",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (isUrdu) "بند کچن کے بجائے لاؤنج کے ساتھ اوپن کاؤنٹر" else "Breakfast island open to TV Lounge (Turn off for closed kitchen)",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                        Switch(
                            checked = openPlanKitchen,
                            onCheckedChange = {
                                openPlanKitchen = it
                                commit()
                            }
                        )
                    }
                }
            }
        }

        // Section 4: Architectural Style
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
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = if (isUrdu) "معماری ڈیزائن کا انداز" else "Architectural Design Style",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )

                    ArchitecturalStyle.values().forEach { style ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    archStyle = style
                                    commit()
                                },
                            color = if (archStyle == style) Color(0xFFE0F2FE) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = archStyle == style,
                                    onClick = {
                                        archStyle = style
                                        commit()
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = style.label,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${style.description} (${style.facadePalette})",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 5: Construction Quality Tier
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isUrdu) "تعمیراتی معیار اور مٹیریل گریڈ" else "Construction Quality Tier",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )

                    ConstructionQuality.values().forEach { q ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    quality = q
                                    commit()
                                },
                            color = if (quality == q) Color(0xFFFEF3C7) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = quality == q,
                                    onClick = {
                                        quality = q
                                        commit()
                                    }
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = q.label,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                    Text(
                                        text = "${q.description} • Grey: PKR ${q.greyRatePerSqFt.toInt()}/sqft",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Action Button: Generate Designs
        item {
            Button(
                onClick = {
                    commit()
                    onGenerate()
                },
                enabled = !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("generate_designs_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ArchitecturalNavy),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(if (isUrdu) "نقشے تیار ہو رہے ہیں..." else "Synthesizing Architectural Plans...")
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = ArchitecturalCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUrdu) "۳ مختلف متبادل نقشے جنریٹ کریں" else "Generate 3 House Layout Alternatives",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RoomStepperRow(
    label: String,
    count: Int,
    min: Int,
    max: Int,
    onChanged: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledIconButton(
                onClick = { if (count > min) onChanged(count - 1) },
                enabled = count > min,
                modifier = Modifier.size(32.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = SurfaceContainer)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = TextPrimary, modifier = Modifier.size(16.dp))
            }

            Text(
                text = count.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = ArchitecturalNavy,
                modifier = Modifier.widthIn(min = 20.dp)
            )

            FilledIconButton(
                onClick = { if (count < max) onChanged(count + 1) },
                enabled = count < max,
                modifier = Modifier.size(32.dp),
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = SurfaceContainer)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase", tint = TextPrimary, modifier = Modifier.size(16.dp))
            }
        }
    }
}
