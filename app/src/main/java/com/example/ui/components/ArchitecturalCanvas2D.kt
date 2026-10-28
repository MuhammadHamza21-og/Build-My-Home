package com.example.ui.components

import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.ArchitecturalNavy
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArchitecturalCanvas2D(
    floorPlan: FloorLevelPlan,
    plotInfo: PlotInfo,
    selectedRoom: RoomGeometry?,
    onRoomSelected: (RoomGeometry?) -> Unit,
    showDimensions: Boolean = true,
    showLabels: Boolean = true,
    showFurniture: Boolean = true,
    showGrid: Boolean = true,
    modifier: Modifier = Modifier,
    isUrdu: Boolean = false
) {
    var scale by remember { mutableFloatStateOf(1.0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val plotW = minOf(plotInfo.boundaries.front, plotInfo.boundaries.rear).coerceAtLeast(20f)
    val plotL = minOf(plotInfo.boundaries.left, plotInfo.boundaries.right).coerceAtLeast(35f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF07101B), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(16.dp))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.6f, 3.5f)
                        offset += pan
                    }
                }
                .pointerInput(floorPlan) {
                    detectTapGestures { tapOffset ->
                        // Convert tapOffset to plan coordinates
                        // Determine layout base scale
                        val margin = 32f
                        val baseScale = minOf(
                            (size.width - margin * 2) / plotW,
                            (size.height - margin * 2) / plotL
                        ) * scale

                        val planOriginX = (size.width - (plotW * baseScale)) / 2f + offset.x
                        val planOriginY = (size.height - (plotL * baseScale)) / 2f + offset.y

                        val clickX = (tapOffset.x - planOriginX) / baseScale
                        val clickY = (tapOffset.y - planOriginY) / baseScale

                        val tapped = floorPlan.rooms.find { it.containsPoint(clickX, clickY) }
                        onRoomSelected(tapped)
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height

            // 1. Draw Architectural Grid
            if (showGrid) {
                val gridSpacing = 28f * scale
                var gx = (offset.x % gridSpacing)
                while (gx < canvasW) {
                    drawLine(
                        color = Color(0x1200CEC9),
                        start = Offset(gx, 0f),
                        end = Offset(gx, canvasH),
                        strokeWidth = 1f
                    )
                    gx += gridSpacing
                }
                var gy = (offset.y % gridSpacing)
                while (gy < canvasH) {
                    drawLine(
                        color = Color(0x1200CEC9),
                        start = Offset(0f, gy),
                        end = Offset(canvasW, gy),
                        strokeWidth = 1f
                    )
                    gy += gridSpacing
                }
            }

            // 2. Base Scale & Plan Origin
            val margin = 40f
            val baseScale = minOf(
                (canvasW - margin * 2) / plotW,
                (canvasH - margin * 2) / plotL
            ) * scale

            val planOriginX = (canvasW - (plotW * baseScale)) / 2f + offset.x
            val planOriginY = (canvasH - (plotL * baseScale)) / 2f + offset.y

            // Draw Plot Boundary (Dotted Cyan/Gray line)
            drawRect(
                color = Color(0x4464748B),
                topLeft = Offset(planOriginX, planOriginY),
                size = Size(plotW * baseScale, plotL * baseScale),
                style = Stroke(width = 2f)
            )

            // 3. Draw Rooms
            val textPaint = Paint().apply {
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            for (room in floorPlan.rooms) {
                val rx = planOriginX + (room.x * baseScale)
                val ry = planOriginY + (room.y * baseScale)
                val rw = room.width * baseScale
                val rl = room.length * baseScale

                val isSelected = selectedRoom?.id == room.id

                // Room Floor fill
                val fillAlpha = when (room.roomType) {
                    RoomType.CAR_PORCH -> 0.18f
                    RoomType.FRONT_LAWN, RoomType.REAR_PASSAGE -> 0.22f
                    RoomType.OTS_SHAFT -> 0.10f
                    RoomType.TERRACE_BALCONY -> 0.15f
                    else -> 0.35f
                }
                val roomBaseColor = Color(room.roomType.colorHex)

                drawRect(
                    color = roomBaseColor.copy(alpha = if (isSelected) 0.55f else fillAlpha),
                    topLeft = Offset(rx, ry),
                    size = Size(rw, rl)
                )

                // Room Walls (Stroke)
                val wallStrokeColor = if (isSelected) Color(0xFF00CEC9) else Color(0xFFCBD5E1)
                val wallStrokeWidth = if (isSelected) 3.5f * scale else 2.0f * scale
                drawRect(
                    color = wallStrokeColor,
                    topLeft = Offset(rx, ry),
                    size = Size(rw, rl),
                    style = Stroke(width = wallStrokeWidth)
                )

                // 4. Draw Furniture if enabled
                if (showFurniture) {
                    drawFurnitureItems(room, rx, ry, baseScale)
                }

                // 5. Draw Doors
                for (door in room.doors) {
                    drawDoorSymbol(door, rx, ry, rw, rl, baseScale)
                }

                // 6. Draw Windows
                for (win in room.windows) {
                    drawWindowSymbol(win, rx, ry, rw, rl, baseScale)
                }

                // 7. Room Labels & Dimensions
                if (showLabels && rw > 30f && rl > 25f) {
                    textPaint.color = if (isSelected) android.graphics.Color.parseColor("#00CEC9") else android.graphics.Color.WHITE
                    textPaint.textSize = (11f * scale).coerceIn(10f, 18f)

                    val displayName = if (isUrdu) room.roomType.urduName else room.name
                    drawContext.canvas.nativeCanvas.drawText(
                        displayName,
                        rx + rw / 2f,
                        ry + rl / 2f - (if (showDimensions) 6f * scale else 0f),
                        textPaint
                    )

                    if (showDimensions) {
                        textPaint.textSize = (9f * scale).coerceIn(8f, 14f)
                        textPaint.color = android.graphics.Color.parseColor("#94A3B8")
                        drawContext.canvas.nativeCanvas.drawText(
                            room.formattedDimensions(),
                            rx + rw / 2f,
                            ry + rl / 2f + 10f * scale,
                            textPaint
                        )
                    }
                }
            }

            // 8. Architectural Dimension Line on top/left of plot if enabled
            if (showDimensions) {
                drawOuterDimensionGuides(planOriginX, planOriginY, plotW * baseScale, plotL * baseScale, plotW, plotL)
            }

            // 9. North Compass Rose
            drawNorthCompass(canvasW - 40f, 40f)

            // 10. Graphic Scale Bar (10 ft)
            val scaleBarLengthFt = 10f
            val scaleBarPixels = scaleBarLengthFt * baseScale
            val scaleBarX = 30f
            val scaleBarY = canvasH - 30f
            drawLine(
                color = Color.White,
                start = Offset(scaleBarX, scaleBarY),
                end = Offset(scaleBarX + scaleBarPixels, scaleBarY),
                strokeWidth = 3f
            )
            drawLine(
                color = Color.White,
                start = Offset(scaleBarX, scaleBarY - 6f),
                end = Offset(scaleBarX, scaleBarY + 6f),
                strokeWidth = 2f
            )
            drawLine(
                color = Color.White,
                start = Offset(scaleBarX + scaleBarPixels, scaleBarY - 6f),
                end = Offset(scaleBarX + scaleBarPixels, scaleBarY + 6f),
                strokeWidth = 2f
            )
            val scalePaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 18f
                textAlign = Paint.Align.LEFT
            }
            drawContext.canvas.nativeCanvas.drawText(
                "SCALE: 10 FT",
                scaleBarX,
                scaleBarY - 10f,
                scalePaint
            )
        }

        // Overlay Navigation Controls (Zoom in, Zoom out, Fit)
        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilledIconButton(
                onClick = { scale = (scale * 1.25f).coerceAtMost(3.5f) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xCC1E293B)),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = Color.White)
            }
            FilledIconButton(
                onClick = { scale = (scale / 1.25f).coerceAtLeast(0.6f) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xCC1E293B)),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = Color.White)
            }
            FilledIconButton(
                onClick = {
                    scale = 1.0f
                    offset = Offset.Zero
                },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color(0xCC1E293B)),
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Default.FitScreen, contentDescription = "Reset View", tint = Color.White)
            }
        }
    }
}

private fun DrawScope.drawDoorSymbol(
    door: DoorInfo,
    rx: Float,
    ry: Float,
    rw: Float,
    rl: Float,
    baseScale: Float
) {
    val doorWidthPx = door.widthFt * baseScale
    val doorColor = Color(0xFFE5A93C)

    when (door.wall) {
        WallSide.TOP -> {
            val dx = rx + (rw * door.offsetRatio)
            val dy = ry
            // Wall opening clear line
            drawLine(color = Color(0xFF07101B), start = Offset(dx, dy), end = Offset(dx + doorWidthPx, dy), strokeWidth = 3f)
            // Door leaf
            drawLine(color = doorColor, start = Offset(dx, dy), end = Offset(dx, dy + doorWidthPx), strokeWidth = 2.5f)
            // Door swing arc
            drawArc(
                color = doorColor.copy(alpha = 0.6f),
                startAngle = 0f,
                sweepAngle = 90f,
                useCenter = false,
                topLeft = Offset(dx - doorWidthPx, dy - doorWidthPx),
                size = Size(doorWidthPx * 2f, doorWidthPx * 2f),
                style = Stroke(width = 1.5f)
            )
        }
        WallSide.BOTTOM -> {
            val dx = rx + (rw * door.offsetRatio)
            val dy = ry + rl
            drawLine(color = doorColor, start = Offset(dx, dy), end = Offset(dx, dy - doorWidthPx), strokeWidth = 2.5f)
        }
        WallSide.LEFT -> {
            val dx = rx
            val dy = ry + (rl * door.offsetRatio)
            drawLine(color = doorColor, start = Offset(dx, dy), end = Offset(dx + doorWidthPx, dy), strokeWidth = 2.5f)
        }
        WallSide.RIGHT -> {
            val dx = rx + rw
            val dy = ry + (rl * door.offsetRatio)
            drawLine(color = doorColor, start = Offset(dx, dy), end = Offset(dx - doorWidthPx, dy), strokeWidth = 2.5f)
        }
    }
}

private fun DrawScope.drawWindowSymbol(
    win: WindowInfo,
    rx: Float,
    ry: Float,
    rw: Float,
    rl: Float,
    baseScale: Float
) {
    val winWidthPx = win.widthFt * baseScale
    val glassColor = Color(0xFF00CEC9)

    when (win.wall) {
        WallSide.TOP, WallSide.BOTTOM -> {
            val wx = rx + (rw * win.offsetRatio) - (winWidthPx / 2f)
            val wy = if (win.wall == WallSide.TOP) ry else ry + rl
            drawLine(color = glassColor, start = Offset(wx, wy - 2f), end = Offset(wx + winWidthPx, wy - 2f), strokeWidth = 2f)
            drawLine(color = glassColor, start = Offset(wx, wy + 2f), end = Offset(wx + winWidthPx, wy + 2f), strokeWidth = 2f)
        }
        WallSide.LEFT, WallSide.RIGHT -> {
            val wx = if (win.wall == WallSide.LEFT) rx else rx + rw
            val wy = ry + (rl * win.offsetRatio) - (winWidthPx / 2f)
            drawLine(color = glassColor, start = Offset(wx - 2f, wy), end = Offset(wx - 2f, wy + winWidthPx), strokeWidth = 2f)
            drawLine(color = glassColor, start = Offset(wx + 2f, wy), end = Offset(wx + 2f, wy + winWidthPx), strokeWidth = 2f)
        }
    }
}

private fun DrawScope.drawFurnitureItems(
    room: RoomGeometry,
    rx: Float,
    ry: Float,
    baseScale: Float
) {
    for (item in room.furniture) {
        val fx = rx + (room.width * item.relX * baseScale) - (item.widthFt * baseScale / 2f)
        val fy = ry + (room.length * item.relY * baseScale) - (item.lengthFt * baseScale / 2f)
        val fw = item.widthFt * baseScale
        val fl = item.lengthFt * baseScale

        when (item.type) {
            FurnitureType.CAR_SEDAN, FurnitureType.CAR_SUV -> {
                // Vehicle outline with windshield
                drawRoundRect(
                    color = Color(0xFF475569),
                    topLeft = Offset(fx, fy),
                    size = Size(fw, fl),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
                    style = Stroke(width = 2f)
                )
                // Windshield line
                drawLine(
                    color = Color(0xFF00CEC9),
                    start = Offset(fx + 4f, fy + fl * 0.25f),
                    end = Offset(fx + fw - 4f, fy + fl * 0.25f),
                    strokeWidth = 2f
                )
                // Rear glass
                drawLine(
                    color = Color(0xFF00CEC9),
                    start = Offset(fx + 4f, fy + fl * 0.75f),
                    end = Offset(fx + fw - 4f, fy + fl * 0.75f),
                    strokeWidth = 2f
                )
            }
            FurnitureType.DOUBLE_BED -> {
                // Mattress
                drawRect(
                    color = Color(0x33A29BFE),
                    topLeft = Offset(fx, fy),
                    size = Size(fw, fl)
                )
                drawRect(
                    color = Color(0xFFA29BFE),
                    topLeft = Offset(fx, fy),
                    size = Size(fw, fl),
                    style = Stroke(width = 1.5f)
                )
                // Pillows
                val pillowW = fw * 0.40f
                val pillowH = fl * 0.22f
                drawRect(color = Color(0xFFCBD5E1), topLeft = Offset(fx + fw * 0.08f, fy + 4f), size = Size(pillowW, pillowH))
                drawRect(color = Color(0xFFCBD5E1), topLeft = Offset(fx + fw * 0.52f, fy + 4f), size = Size(pillowW, pillowH))
            }
            FurnitureType.SOFA_L_SHAPE, FurnitureType.SOFA_3SEAT -> {
                drawRoundRect(
                    color = Color(0x33FDCB6E),
                    topLeft = Offset(fx, fy),
                    size = Size(fw, fl),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                )
                drawRoundRect(
                    color = Color(0xFFFDCB6E),
                    topLeft = Offset(fx, fy),
                    size = Size(fw, fl),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
                    style = Stroke(width = 1.5f)
                )
            }
            FurnitureType.STAIRS_FLIGHT -> {
                // Series of treads
                val treads = 10
                val stepH = fl / treads
                for (t in 0 until treads) {
                    val stepY = fy + (t * stepH)
                    drawLine(
                        color = Color(0xFFA29BFE),
                        start = Offset(fx, stepY),
                        end = Offset(fx + fw, stepY),
                        strokeWidth = 1.5f
                    )
                }
                // Arrow
                drawLine(
                    color = Color(0xFF00CEC9),
                    start = Offset(fx + fw / 2f, fy + fl - 4f),
                    end = Offset(fx + fw / 2f, fy + 4f),
                    strokeWidth = 2f
                )
            }
            else -> {
                drawRect(
                    color = Color(0x22CBD5E1),
                    topLeft = Offset(fx, fy),
                    size = Size(fw, fl)
                )
                drawRect(
                    color = Color(0x66CBD5E1),
                    topLeft = Offset(fx, fy),
                    size = Size(fw, fl),
                    style = Stroke(width = 1.2f)
                )
            }
        }
    }
}

private fun DrawScope.drawOuterDimensionGuides(
    originX: Float,
    originY: Float,
    pw: Float,
    pl: Float,
    widthFt: Float,
    lengthFt: Float
) {
    val dimColor = Color(0xFF64748B)
    val dimY = originY - 16f
    // Top dimension line
    drawLine(color = dimColor, start = Offset(originX, dimY), end = Offset(originX + pw, dimY), strokeWidth = 1.5f)
    drawLine(color = dimColor, start = Offset(originX, dimY - 4f), end = Offset(originX, dimY + 4f), strokeWidth = 1.5f)
    drawLine(color = dimColor, start = Offset(originX + pw, dimY - 4f), end = Offset(originX + pw, dimY + 4f), strokeWidth = 1.5f)

    val p = Paint().apply {
        color = android.graphics.Color.parseColor("#94A3B8")
        textSize = 20f
        textAlign = Paint.Align.CENTER
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }
    drawContext.canvas.nativeCanvas.drawText("${widthFt.toInt()}' - 0\"", originX + pw / 2f, dimY - 6f, p)
}

private fun DrawScope.drawNorthCompass(cx: Float, cy: Float) {
    val r = 16f
    drawCircle(color = Color(0xFF1E293B), radius = r, center = Offset(cx, cy))
    drawCircle(color = Color(0xFF475569), radius = r, center = Offset(cx, cy), style = Stroke(1.5f))
    val arrow = Path().apply {
        moveTo(cx, cy - r + 3f)
        lineTo(cx - 5f, cy + 3f)
        lineTo(cx + 5f, cy + 3f)
        close()
    }
    drawPath(arrow, Color(0xFFEF4444))
    val p = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 14f
        textAlign = Paint.Align.CENTER
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }
    drawContext.canvas.nativeCanvas.drawText("N", cx, cy + r + 14f, p)
}
