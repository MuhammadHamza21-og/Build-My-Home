package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.*
import com.example.ui.components.HouseVisualizer3D
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen

@Composable
fun Visualizer3DScreen(
    floorPlanDesign: FloorPlanDesign,
    plotInfo: PlotInfo,
    requirements: HouseRequirements,
    isUrdu: Boolean,
    onNavigate: (AppScreen) -> Unit
) {
    var showConceptPhoto by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight),
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
                        text = if (isUrdu) "۳ ڈی ہاؤس ماڈل و ویژولائزر" else "Interactive 3D House Visualizer",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )
                    Text(
                        text = "${floorPlanDesign.title} • ${requirements.architecturalStyle.label}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                FilledTonalButton(
                    onClick = { onNavigate(AppScreen.FLOOR_PLAN_2D) },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.ViewQuilt, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isUrdu) "۲ ڈی بلیو پرنٹ" else "2D Plan", fontSize = 11.sp)
                }
            }
        }

        // 3D Canvas Visualizer
        item {
            HouseVisualizer3D(
                floorPlanDesign = floorPlanDesign,
                plotInfo = plotInfo,
                style = requirements.architecturalStyle,
                isUrdu = isUrdu,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Gestures & Usage Guide Hint
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.TouchApp, contentDescription = null, tint = ArchitecturalBlue, modifier = Modifier.size(20.dp))
                    Text(
                        text = if (isUrdu)
                            "انگلی سے گھما کر ۳۶۰ ڈگری میں گھر کا معائنہ کریں۔ اوپر چھت والے بٹن سے اندرونی کمرے دیکھ سکتے ہیں۔"
                        else
                            "Drag in any direction to rotate 360° in 3D. Toggle 'Roof Cutaway' above to look directly into interior rooms.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Exterior Elevation Concept Photo Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isUrdu) "حقیقی بیرونی پیش منظر (Exterior Concept)" else "Exterior Elevation Concept",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ArchitecturalNavy
                            )
                            Text(
                                text = "Travertine stone cladding, wood louvers & LED cove lighting",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        IconButton(onClick = { showConceptPhoto = !showConceptPhoto }) {
                            Icon(
                                if (showConceptPhoto) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = "Toggle Photo"
                            )
                        }
                    }

                    if (showConceptPhoto) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.hero_house),
                                contentDescription = "Photorealistic House Concept Render",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp),
                                color = Color(0xCC0B192C),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Illustrative Architectural Render",
                                    fontSize = 9.sp,
                                    color = Color(0xFFCBD5E1),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Architectural Specification Details
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isUrdu) "تعمیراتی خصوصیات و تفصیلات" else "Elevation Specifications",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )

                    SpecRow("Style", requirements.architecturalStyle.label)
                    SpecRow("Total Covered Footprint", "${floorPlanDesign.totalCoveredAreaSqFt.toInt()} sq ft")
                    SpecRow("Facade Material", requirements.architecturalStyle.facadePalette)
                    SpecRow("Floor Heights", "Ground: 10'-6\", Upper: 10'-0\", Mumty: 8'-6\"")
                    SpecRow("Glass Railings", "12mm tempered safety glass with stainless steel posts")
                    SpecRow("Windows", "Thermal break aluminium sections with tinted double glazing")
                }
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = TextSecondary)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
    HorizontalDivider(color = BorderSubtle.copy(alpha = 0.5f))
}
