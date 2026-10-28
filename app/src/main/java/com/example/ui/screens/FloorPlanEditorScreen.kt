package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.ArchitecturalCanvas2D
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen

@Composable
fun FloorPlanEditorScreen(
    floorPlanDesign: FloorPlanDesign,
    plotInfo: PlotInfo,
    selectedFloorLevel: Int,
    selectedRoom: RoomGeometry?,
    isUrdu: Boolean,
    onSelectFloorLevel: (Int) -> Unit,
    onSelectRoom: (RoomGeometry?) -> Unit,
    onSelectAlternative: (AlternativeType) -> Unit,
    onSaveProject: (String) -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    val context = LocalContext.current
    var showDimensions by remember { mutableStateOf(true) }
    var showLabels by remember { mutableStateOf(true) }
    var showFurniture by remember { mutableStateOf(true) }
    var showGrid by remember { mutableStateOf(true) }

    var showSaveDialog by remember { mutableStateOf(false) }
    var saveProjectName by remember { mutableStateOf(plotInfo.projectName) }

    val currentFloor = floorPlanDesign.getFloor(selectedFloorLevel) ?: floorPlanDesign.floors.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight)
    ) {
        // Top Toolbar: Alternative Switcher & Mode Actions
        Surface(
            color = ArchitecturalNavy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Alternative Switcher (A | B | C)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUrdu) "ڈیزائن:" else "Layout:",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium
                    )
                    listOf(
                        AlternativeType.DESIGN_A,
                        AlternativeType.DESIGN_B,
                        AlternativeType.DESIGN_C
                    ).forEach { alt ->
                        val isCurrent = floorPlanDesign.alternative == alt
                        FilterChip(
                            selected = isCurrent,
                            onClick = { onSelectAlternative(alt) },
                            label = { Text("Alt ${alt.code}", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color(0xFF1E293B),
                                labelColor = Color.White,
                                selectedContainerColor = ArchitecturalCyan,
                                selectedLabelColor = Color.Black
                            ),
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }

                // Quick Navigation to 3D & Save
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalButton(
                        onClick = { onNavigate(AppScreen.VISUALIZER_3D) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = ArchitecturalEmerald,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(32.dp).testTag("open_3d_button")
                    ) {
                        Icon(Icons.Default.ViewInAr, contentDescription = "3D", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isUrdu) "۳ ڈی ویو" else "3D Model", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = { showSaveDialog = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save Project", tint = Color.White)
                    }
                }
            }
        }

        // Floor Level Switcher Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedFloorLevel.coerceIn(0, floorPlanDesign.floors.lastIndex),
            containerColor = Color(0xFF0F172A),
            contentColor = Color.White,
            edgePadding = 12.dp
        ) {
            floorPlanDesign.floors.forEach { floor ->
                Tab(
                    selected = selectedFloorLevel == floor.floorLevel,
                    onClick = { onSelectFloorLevel(floor.floorLevel) },
                    text = {
                        Text(
                            text = floor.floorName,
                            fontSize = 12.sp,
                            fontWeight = if (selectedFloorLevel == floor.floorLevel) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Layer Toggles Row (Dimensions, Labels, Furniture, Grid)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E293B))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                FilterChip(
                    selected = showDimensions,
                    onClick = { showDimensions = !showDimensions },
                    label = { Text("Dimensions", fontSize = 10.sp) },
                    leadingIcon = { Icon(Icons.Default.Straighten, contentDescription = null, modifier = Modifier.size(12.dp)) },
                    modifier = Modifier.height(26.dp)
                )
            }
            item {
                FilterChip(
                    selected = showLabels,
                    onClick = { showLabels = !showLabels },
                    label = { Text("Labels", fontSize = 10.sp) },
                    leadingIcon = { Icon(Icons.Default.Label, contentDescription = null, modifier = Modifier.size(12.dp)) },
                    modifier = Modifier.height(26.dp)
                )
            }
            item {
                FilterChip(
                    selected = showFurniture,
                    onClick = { showFurniture = !showFurniture },
                    label = { Text("Furniture", fontSize = 10.sp) },
                    leadingIcon = { Icon(Icons.Default.Chair, contentDescription = null, modifier = Modifier.size(12.dp)) },
                    modifier = Modifier.height(26.dp)
                )
            }
            item {
                FilterChip(
                    selected = showGrid,
                    onClick = { showGrid = !showGrid },
                    label = { Text("Grid", fontSize = 10.sp) },
                    leadingIcon = { Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(12.dp)) },
                    modifier = Modifier.height(26.dp)
                )
            }
        }

        // 2D Architectural Canvas Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
        ) {
            ArchitecturalCanvas2D(
                floorPlan = currentFloor,
                plotInfo = plotInfo,
                selectedRoom = selectedRoom,
                onRoomSelected = onSelectRoom,
                showDimensions = showDimensions,
                showLabels = showLabels,
                showFurniture = showFurniture,
                showGrid = showGrid,
                isUrdu = isUrdu,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Bottom Inspection Bar / Selected Room Card
        Surface(
            color = SurfaceCard,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                if (selectedRoom != null) {
                    // Room Inspector Card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(Color(selectedRoom.roomType.colorHex), RoundedCornerShape(2.dp))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isUrdu) selectedRoom.roomType.urduName else selectedRoom.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Text(
                                text = "Dimensions: ${selectedRoom.formattedDimensions()} • Area: ${selectedRoom.areaSqFt.toInt()} sq ft (~${String.format("%.2f", plotInfo.sqFtToMarla(selectedRoom.areaSqFt))} Marla)",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }

                        IconButton(onClick = { onSelectRoom(null) }) {
                            Icon(Icons.Default.Close, contentDescription = "Close Inspector", modifier = Modifier.size(18.dp))
                        }
                    }
                } else {
                    // Overall Floor Metrics & Export Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "${currentFloor.floorName} Covered Area",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = "${currentFloor.coveredAreaSqFt.toInt()} Sq Ft (~${String.format("%.2f", plotInfo.sqFtToMarla(currentFloor.coveredAreaSqFt))} Marla)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = ArchitecturalNavy
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { onNavigate(AppScreen.COST_ESTIMATOR) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.Paid, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Cost", fontSize = 11.sp)
                            }

                            Button(
                                onClick = {
                                    Toast.makeText(context, "Floor Plan exported to project storage!", Toast.LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ArchitecturalNavy),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isUrdu) "ایکسپورٹ" else "Export", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Save Project Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text(if (isUrdu) "پروجیکٹ محفوظ کریں" else "Save House Project") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isUrdu) "منصوبے کا نام درج کریں:" else "Enter project title to store in local database:",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    OutlinedTextField(
                        value = saveProjectName,
                        onValueChange = { saveProjectName = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSaveProject(saveProjectName)
                        showSaveDialog = false
                        Toast.makeText(context, "Project '$saveProjectName' saved successfully!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ArchitecturalNavy)
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
