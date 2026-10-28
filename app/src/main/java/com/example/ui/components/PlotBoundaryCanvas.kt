package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlotInfo
import com.example.data.model.PlotShape
import com.example.data.model.RoadFacingSide
import com.example.ui.theme.ArchitecturalBlue
import com.example.ui.theme.ArchitecturalCyan
import com.example.ui.theme.ArchitecturalNavy

@Composable
fun PlotBoundaryCanvas(
    plotInfo: PlotInfo,
    modifier: Modifier = Modifier,
    isUrdu: Boolean = false
) {
    val b = plotInfo.boundaries
    val front = b.front
    val rear = b.rear
    val left = b.left
    val right = b.right

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(Color(0xFF0F172A), RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height

            // Grid lines
            val gridSpacing = 24.dp.toPx()
            for (x in 0..(canvasW / gridSpacing).toInt()) {
                drawLine(
                    color = Color(0x1A64748B),
                    start = Offset(x * gridSpacing, 0f),
                    end = Offset(x * gridSpacing, canvasH),
                    strokeWidth = 1f
                )
            }
            for (y in 0..(canvasH / gridSpacing).toInt()) {
                drawLine(
                    color = Color(0x1A64748B),
                    start = Offset(0f, y * gridSpacing),
                    end = Offset(canvasW, y * gridSpacing),
                    strokeWidth = 1f
                )
            }

            // Margin for labels
            val margin = 48.dp.toPx()
            val availableW = canvasW - (margin * 2)
            val availableH = canvasH - (margin * 2)

            val maxPlotW = maxOf(front, rear, 10f)
            val maxPlotL = maxOf(left, right, 10f)

            val scale = minOf(availableW / maxPlotW, availableH / maxPlotL)

            val plotPixelW = front * scale
            val plotPixelL = left * scale

            val startX = margin + (availableW - plotPixelW) / 2f
            val startY = margin + (availableH - plotPixelL) / 2f

            // Polygon points for plot
            // Top-Left (0, 0), Top-Right (front, 0), Bottom-Right (rear, right), Bottom-Left (0, left)
            val pTopLeft = Offset(startX, startY)
            val pTopRight = Offset(startX + (front * scale), startY)
            val pBottomRight = Offset(startX + (rear * scale), startY + (right * scale))
            val pBottomLeft = Offset(startX, startY + (left * scale))

            val plotPath = Path().apply {
                moveTo(pTopLeft.x, pTopLeft.y)
                lineTo(pTopRight.x, pTopRight.y)
                if (plotInfo.plotShape == PlotShape.CORNER) {
                    // Chamfer corner at top right
                    val chamfer = 16f
                    reset()
                    moveTo(pTopLeft.x, pTopLeft.y)
                    lineTo(pTopRight.x - chamfer, pTopRight.y)
                    lineTo(pTopRight.x, pTopRight.y + chamfer)
                    lineTo(pBottomRight.x, pBottomRight.y)
                    lineTo(pBottomLeft.x, pBottomLeft.y)
                    close()
                } else {
                    lineTo(pBottomRight.x, pBottomRight.y)
                    lineTo(pBottomLeft.x, pBottomLeft.y)
                    close()
                }
            }

            // Fill with architectural subtle color
            drawPath(
                path = plotPath,
                color = Color(0x2E00B894)
            )

            // Outline boundary with architectural cyan/white
            drawPath(
                path = plotPath,
                color = Color(0xFF00CEC9),
                style = Stroke(
                    width = 3.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 0f), 0f)
                )
            )

            // Draw Road indicator on front side
            val roadY = startY - 14.dp.toPx()
            drawLine(
                color = Color(0xFFE5A93C),
                start = Offset(startX - 20f, roadY),
                end = Offset(startX + (front * scale) + 20f, roadY),
                strokeWidth = 4.dp.toPx()
            )

            // Boundary length texts using Android nativeCanvas
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 28f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            val roadPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#E5A93C")
                textSize = 24f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }

            // Road label
            drawContext.canvas.nativeCanvas.drawText(
                if (isUrdu) "مین روڈ (سامنے)" else "MAIN ROAD (FRONT)",
                startX + (plotPixelW / 2f),
                roadY - 10f,
                roadPaint
            )

            // Front label
            drawContext.canvas.nativeCanvas.drawText(
                "Front: ${front.toInt()}'",
                startX + (plotPixelW / 2f),
                startY + 35f,
                paint
            )

            // Rear label
            drawContext.canvas.nativeCanvas.drawText(
                "Rear: ${rear.toInt()}'",
                startX + ((rear * scale) / 2f),
                pBottomLeft.y - 15f,
                paint
            )

            // Left label
            val leftPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 26f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.RIGHT
            }
            drawContext.canvas.nativeCanvas.drawText(
                "Left: ${left.toInt()}'",
                startX - 15f,
                startY + (plotPixelL / 2f),
                leftPaint
            )

            // Right label
            val rightPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 26f
                isAntiAlias = true
                textAlign = android.graphics.Paint.Align.LEFT
            }
            drawContext.canvas.nativeCanvas.drawText(
                "Right: ${right.toInt()}'",
                startX + (front * scale) + 15f,
                startY + (plotPixelL / 2f),
                rightPaint
            )

            // North Compass Indicator at top right corner
            val compassCenterX = canvasW - 32.dp.toPx()
            val compassCenterY = 32.dp.toPx()
            val compassR = 18.dp.toPx()
            drawCircle(
                color = Color(0xFF1E293B),
                radius = compassR,
                center = Offset(compassCenterX, compassCenterY)
            )
            drawCircle(
                color = Color(0xFF475569),
                radius = compassR,
                center = Offset(compassCenterX, compassCenterY),
                style = Stroke(2f)
            )
            // Arrow pointing up
            val arrowPath = Path().apply {
                moveTo(compassCenterX, compassCenterY - (compassR - 4f))
                lineTo(compassCenterX - 6f, compassCenterY + 4f)
                lineTo(compassCenterX + 6f, compassCenterY + 4f)
                close()
            }
            drawPath(path = arrowPath, color = Color(0xFFEF4444))
            val nPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 20f
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            drawContext.canvas.nativeCanvas.drawText(
                "N",
                compassCenterX,
                compassCenterY + compassR + 18f,
                nPaint
            )
        }

        // Overlay Badge at bottom left
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color(0xCC0B192C), RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${plotInfo.getCalculatedAreaInSqFt().toInt()} Sq Ft (${String.format("%.2f", plotInfo.sqFtToMarla(plotInfo.getCalculatedAreaInSqFt()))} Marla)",
                color = Color(0xFF00CEC9),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            if (plotInfo.hasSignificantDiscrepancy()) {
                Text(
                    text = "Discrepancy: ${plotInfo.getAreaDiscrepancyPercentage().toInt()}% vs stated area",
                    color = Color(0xFFF59E0B),
                    fontSize = 10.sp
                )
            }
        }
    }
}
