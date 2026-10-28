package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArchitecturalNavy

@Composable
fun SafetyDisclaimerBanner(
    modifier: Modifier = Modifier,
    isUrdu: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFEF3C7), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Safety Disclaimer",
                tint = Color(0xFFD97706),
                modifier = Modifier.size(22.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isUrdu) "اہم قانونی اور اسٹرکچرل ڈسکلیمر" else "Architectural & Structural Disclaimer",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = if (isUrdu)
                        "یہ ایپلی کیشن صرف ابتدائی تصوراتی اور منصوبہ بندی کے نقشے تیار کرتی ہے۔ سائٹ پر تعمیر شروع کرنے سے پہلے سٹرکچرل انجینئر اور مقامی اتھارٹی (مثلاً ایل ڈی اے، سی ڈی اے، ڈی ایچ اے) سے منظوری لازمی ہے۔"
                    else
                        "BuildMyHome AI generates preliminary architectural planning concepts. Final structural designs, soil bearing calculations, RCC column/beam schedules, and setback approvals must be certified by a licensed structural engineer and municipal authorities before construction.",
                    fontSize = 11.sp,
                    color = Color(0xFF78350F),
                    lineHeight = 15.sp
                )
            }
        }
    }
}
