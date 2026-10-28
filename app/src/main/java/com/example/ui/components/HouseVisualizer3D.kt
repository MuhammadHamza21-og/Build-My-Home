package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import kotlin.math.*

enum class ViewPreset(val label: String) {
    PERSPECTIVE("3D Perspective"),
    TOP_VIEW("Top View"),
    FRONT_ELEVATION("Front View"),
    SIDE_ELEVATION("Side View"),
    ISOMETRIC("Isometric 45°")
}

enum class LightingMode(val label: String, val bgTop: Color, val bgBottom: Color) {
    DAYLIGHT("Daylight", Color(0xFFE2E8F0), Color(0xFF94A3B8)),
    EVENING_GLOW("Evening Glow", Color(0xFF0F172A), Color(0xFF020617)),
    BLUEPRINT("Blueprint", Color(0xFF07101B), Color(0xFF0B192C)),
    CLAY_MODEL("Clay Model", Color(0xFFF1F5F9), Color(0xFFCBD5E1))
}

@Composable
fun HouseVisualizer3D(
    floorPlanDesign: FloorPlanDesign,
    plotInfo: PlotInfo,
    style: ArchitecturalStyle = ArchitecturalStyle.MODERN_PAKISTANI,
    modifier: Modifier = Modifier,
    isUrdu: Boolean = false
) {
    var yaw by remember { mutableFloatStateOf(45f) } // horizontal rotation in degrees
    var pitch by remember { mutableFloatStateOf(35f) } // vertical elevation angle in degrees
    var zoom by remember { mutableFloatStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    var showRoof by remember { mutableStateOf(false) } // Cutaway mode by default so interior rooms are visible!
    var selectedFloorIndex by remember { mutableIntStateOf(-1) } // -1 = All floors, 0 = Ground, 1 = First
    var lightingMode by remember { mutableStateOf(LightingMode.EVENING_GLOW) }

    val plotW = minOf(plotInfo.boundaries.front, plotInfo.boundaries.rear).coerceAtLeast(20f)
    val plotL = minOf(plotInfo.boundaries.left, plotInfo.boundaries.right).coerceAtLeast(35f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(lightingMode.bgTop, lightingMode.bgBottom)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    yaw = (yaw + dragAmount.x * 0.5f) % 360f
                    pitch = (pitch - dragAmount.y * 0.4f).coerceIn(10f, 85f)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            val centerX = canvasW / 2f + panOffset.x
            val centerY = canvasH / 2f + panOffset.y + 30f

            val baseScale = (minOf(canvasW, canvasH) / (plotL * 1.8f)) * zoom

            // Convert degrees to radians
            val yawRad = (yaw * PI / 180.0).toFloat()
            val pitchRad = (pitch * PI / 180.0).toFloat()

            val cosYaw = cos(yawRad)
            val sinYaw = sin(yawRad)
            val cosPitch = cos(pitchRad)
            val sinPitch = sin(pitchRad)

            // 3D to 2D projection function
            fun project(x: Float, y: Float, z: Float): Offset {
                // Center coordinates relative to plot center
                val cx = x - (plotW / 2f)
                val cy = y - (plotL / 2f)
                val cz = z

                // 1. Rotate around Z (Yaw)
                val x1 = cx * cosYaw - cy * sinYaw
                val y1 = cx * sinYaw + cy * cosYaw
                val z1 = cz

                // 2. Rotate around X (Pitch)
                val x2 = x1
                val y2 = y1 * cosPitch - z1 * sinPitch
                val z2 = y1 * sinPitch + z1 * cosPitch

                // Isometric / Weak perspective scale
                val perspective = 1.0f / (1.0f + (z2 * 0.002f))
                val px = centerX + (x2 * baseScale * perspective)
                val py = centerY - (y2 * baseScale * perspective)

                return Offset(px, py)
            }

            // Draw Ground Plot Base Slab & Front Lawn
            val groundY = 0f
            val p00 = project(0f, 0f, groundY)
            val pw0 = project(plotW, 0f, groundY)
            val pwl = project(plotW, plotL, groundY)
            val p0l = project(0f, plotL, groundY)

            val basePlotPath = Path().apply {
                moveTo(p00.x, p00.y)
                lineTo(pw0.x, pw0.y)
                lineTo(pwl.x, pwl.y)
                lineTo(p0l.x, p0l.y)
                close()
            }

            val groundColor = when (lightingMode) {
                LightingMode.BLUEPRINT -> Color(0xFF0F172A)
                LightingMode.DAYLIGHT -> Color(0xFFE2E8F0)
                LightingMode.EVENING_GLOW -> Color(0xFF0B192C)
                LightingMode.CLAY_MODEL -> Color(0xFFE2E8F0)
            }
            drawPath(basePlotPath, groundColor)
            drawPath(basePlotPath, Color(0x3300CEC9), style = Stroke(width = 1.5f))

            // Draw Floors and 3D Extruded Walls
            val wallHeight = 10.0f // 10 ft story height
            val floorsToRender = when (selectedFloorIndex) {
                0 -> floorPlanDesign.floors.filter { it.floorLevel == 0 }
                1 -> floorPlanDesign.floors.filter { it.floorLevel == 1 }
                else -> floorPlanDesign.floors
            }

            for (floor in floorsToRender) {
                val floorElevation = floor.floorLevel * (wallHeight + 1.2f)

                // Floor Slab
                val s00 = project(0f, 0f, floorElevation)
                val sw0 = project(plotW, 0f, floorElevation)
                val swl = project(plotW, plotL, floorElevation)
                val s0l = project(0f, plotL, floorElevation)
                val slabPath = Path().apply {
                    moveTo(s00.x, s00.y)
                    lineTo(sw0.x, sw0.y)
                    lineTo(swl.x, swl.y)
                    lineTo(s0l.x, s0l.y)
                    close()
                }
                drawPath(slabPath, Color(0x22CBD5E1))

                // Sort rooms roughly by distance to avoid inside-out z-order artifacts
                val sortedRooms = floor.rooms.sortedByDescending {
                    val rCenter = project(it.x + it.width / 2f, it.y + it.length / 2f, floorElevation)
                    rCenter.y
                }

                for (room in sortedRooms) {
                    if (room.roomType == RoomType.FRONT_LAWN || room.roomType == RoomType.REAR_PASSAGE) {
                        // Draw flat green lawn area
                        val p1 = project(room.x, room.y, floorElevation)
                        val p2 = project(room.x + room.width, room.y, floorElevation)
                        val p3 = project(room.x + room.width, room.y + room.length, floorElevation)
                        val p4 = project(room.x, room.y + room.length, floorElevation)
                        val lawnPath = Path().apply {
                            moveTo(p1.x, p1.y)
                            lineTo(p2.x, p2.y)
                            lineTo(p3.x, p3.y)
                            lineTo(p4.x, p4.y)
                            close()
                        }
                        drawPath(lawnPath, Color(0x4400B894))
                        continue
                    }

                    if (room.roomType == RoomType.CAR_PORCH) {
                        // Car Porch ceiling columns
                        val h = wallHeight * 0.9f
                        val col1Bottom = project(room.x + 1f, room.y + 1f, floorElevation)
                        val col1Top = project(room.x + 1f, room.y + 1f, floorElevation + h)
                        drawLine(color = Color(0xFF94A3B8), start = col1Bottom, end = col1Top, strokeWidth = 3f)

                        val col2Bottom = project(room.x + room.width - 1f, room.y + 1f, floorElevation)
                        val col2Top = project(room.x + room.width - 1f, room.y + 1f, floorElevation + h)
                        drawLine(color = Color(0xFF94A3B8), start = col2Bottom, end = col2Top, strokeWidth = 3f)
                        continue
                    }

                    // Extrude Room 3D Cube (4 walls)
                    val x = room.x
                    val y = room.y
                    val w = room.width
                    val l = room.length
                    val zBase = floorElevation
                    val zTop = floorElevation + wallHeight

                    // 8 vertices of room box
                    val v0 = project(x, y, zBase)
                    val v1 = project(x + w, y, zBase)
                    val v2 = project(x + w, y + l, zBase)
                    val v3 = project(x, y + l, zBase)

                    val v4 = project(x, y, zTop)
                    val v5 = project(x + w, y, zTop)
                    val v6 = project(x + w, y + l, zTop)
                    val v7 = project(x, y + l, zTop)

                    // Wall Palette based on lighting mode
                    val wallFill = when (lightingMode) {
                        LightingMode.BLUEPRINT -> Color(0x3300CEC9)
                        LightingMode.EVENING_GLOW -> Color(0xAA1E293B)
                        LightingMode.DAYLIGHT -> Color(0xDDFFFFFF)
                        LightingMode.CLAY_MODEL -> Color(0xEEF8FAFC)
                    }

                    val wallStroke = when (lightingMode) {
                        LightingMode.BLUEPRINT -> Color(0xFF00CEC9)
                        LightingMode.EVENING_GLOW -> Color(0xFFE2E8F0)
                        LightingMode.DAYLIGHT -> Color(0xFF475569)
                        LightingMode.CLAY_MODEL -> Color(0xFF64748B)
                    }

                    // Draw Room Floor Plate
                    val floorPath = Path().apply {
                        moveTo(v0.x, v0.y)
                        lineTo(v1.x, v1.y)
                        lineTo(v2.x, v2.y)
                        lineTo(v3.x, v3.y)
                        close()
                    }
                    drawPath(floorPath, Color(room.roomType.colorHex).copy(alpha = 0.35f))

                    // Front Wall (v0 -> v1 -> v5 -> v4)
                    val frontWall = Path().apply {
                        moveTo(v0.x, v0.y)
                        lineTo(v1.x, v1.y)
                        lineTo(v5.x, v5.y)
                        lineTo(v4.x, v4.y)
                        close()
                    }
                    drawPath(frontWall, wallFill)
                    drawPath(frontWall, wallStroke, style = Stroke(width = 1.5f))

                    // Side Wall (v1 -> v2 -> v6 -> v5)
                    val sideWall = Path().apply {
                        moveTo(v1.x, v1.y)
                        lineTo(v2.x, v2.y)
                        lineTo(v6.x, v6.y)
                        lineTo(v5.x, v5.y)
                        close()
                    }
                    drawPath(sideWall, wallFill.copy(alpha = 0.8f))
                    drawPath(sideWall, wallStroke, style = Stroke(width = 1.5f))

                    // 3D Glass Windows with warm glow
                    if (room.windows.isNotEmpty()) {
                        val winZ1 = zBase + 3f
                        val winZ2 = zBase + 7f
                        val winP1 = project(x + w * 0.3f, y, winZ1)
                        val winP2 = project(x + w * 0.7f, y, winZ1)
                        val winP3 = project(x + w * 0.7f, y, winZ2)
                        val winP4 = project(x + w * 0.3f, y, winZ2)

                        val winPath = Path().apply {
                            moveTo(winP1.x, winP1.y)
                            lineTo(winP2.x, winP2.y)
                            lineTo(winP3.x, winP3.y)
                            lineTo(winP4.x, winP4.y)
                            close()
                        }
                        val windowTint = if (lightingMode == LightingMode.EVENING_GLOW) Color(0xFFFDE047) else Color(0xFF38BDF8)
                        drawPath(winPath, windowTint.copy(alpha = 0.85f))
                        drawPath(winPath, Color.White, style = Stroke(width = 1.2f))
                    }

                    // Render Roof Slab if showRoof is true and on top floor
                    if (showRoof) {
                        val roofPath = Path().apply {
                            moveTo(v4.x, v4.y)
                            lineTo(v5.x, v5.y)
                            lineTo(v6.x, v6.y)
                            lineTo(v7.x, v7.y)
                            close()
                        }
                        val roofColor = when (lightingMode) {
                            LightingMode.EVENING_GLOW -> Color(0xFF334155)
                            LightingMode.DAYLIGHT -> Color(0xFFCBD5E1)
                            LightingMode.BLUEPRINT -> Color(0x5500CEC9)
                            LightingMode.CLAY_MODEL -> Color(0xFFE2E8F0)
                        }
                        drawPath(roofPath, roofColor)
                        drawPath(roofPath, wallStroke, style = Stroke(width = 1.5f))
                    }
                }
            }
        }

        // Top Control Overlay: View Presets & Lighting
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lighting switcher chip
            AssistChip(
                onClick = {
                    lightingMode = when (lightingMode) {
                        LightingMode.EVENING_GLOW -> LightingMode.DAYLIGHT
                        LightingMode.DAYLIGHT -> LightingMode.BLUEPRINT
                        LightingMode.BLUEPRINT -> LightingMode.CLAY_MODEL
                        LightingMode.CLAY_MODEL -> LightingMode.EVENING_GLOW
                    }
                },
                label = { Text(lightingMode.label, fontSize = 11.sp) },
                leadingIcon = {
                    Icon(
                        if (lightingMode == LightingMode.EVENING_GLOW) Icons.Default.NightsStay else Icons.Default.WbSunny,
                        contentDescription = "Lighting",
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = Color(0xCC0F172A),
                    labelColor = Color.White
                )
            )

            // Roof Cutaway / Interior Inspection Toggle Button
            FilterChip(
                selected = showRoof,
                onClick = { showRoof = !showRoof },
                label = {
                    Text(
                        if (showRoof) "Roof: Closed" else "Roof: Open (Cutaway)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                leadingIcon = {
                    Icon(
                        if (showRoof) Icons.Default.Roofing else Icons.Default.Visibility,
                        contentDescription = "Roof Mode",
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(0xCC1E293B),
                    labelColor = Color.White,
                    selectedContainerColor = Color(0xFF00CEC9),
                    selectedLabelColor = Color.Black
                )
            )
        }

        // Bottom Controls: Quick Angle Presets & Reset
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ElevatedButton(
                onClick = {
                    yaw = 0f
                    pitch = 12f
                },
                colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xCC1E293B)),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Front", fontSize = 11.sp, color = Color.White)
            }
            ElevatedButton(
                onClick = {
                    yaw = 90f
                    pitch = 12f
                },
                colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xCC1E293B)),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Side", fontSize = 11.sp, color = Color.White)
            }
            ElevatedButton(
                onClick = {
                    yaw = 0f
                    pitch = 85f
                },
                colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xCC1E293B)),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text("Top", fontSize = 11.sp, color = Color.White)
            }
            ElevatedButton(
                onClick = {
                    yaw = 45f
                    pitch = 35f
                    zoom = 1.0f
                    panOffset = Offset.Zero
                },
                colors = ButtonDefaults.elevatedButtonColors(containerColor = Color(0xFF00CEC9)),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset 3D", tint = Color.Black, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reset", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
