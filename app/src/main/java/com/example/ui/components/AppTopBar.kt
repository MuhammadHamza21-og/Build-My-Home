package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ArchitecturalBlue
import com.example.ui.theme.ArchitecturalCyan
import com.example.ui.theme.ArchitecturalNavy
import com.example.ui.viewmodel.AppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    currentScreen: AppScreen,
    isUrdu: Boolean,
    onBack: () -> Unit,
    onToggleLanguage: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Architecture,
                        contentDescription = "App Logo",
                        tint = ArchitecturalCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUrdu) "بلڈ مائی ہوم AI" else "BuildMyHome AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = getScreenSubtitle(currentScreen, isUrdu),
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        },
        navigationIcon = {
            if (currentScreen != AppScreen.DASHBOARD) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            // Language Toggle (EN / اردو)
            TextButton(
                onClick = onToggleLanguage,
                colors = ButtonDefaults.textButtonColors(contentColor = ArchitecturalCyan)
            ) {
                Icon(
                    Icons.Default.Language,
                    contentDescription = "Language",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isUrdu) "English" else "اردو",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (currentScreen != AppScreen.DASHBOARD) {
                IconButton(onClick = { onNavigate(AppScreen.DASHBOARD) }) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home Dashboard",
                        tint = Color.White
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ArchitecturalNavy,
            titleContentColor = Color.White
        )
    )
}

private fun getScreenSubtitle(screen: AppScreen, isUrdu: Boolean): String {
    return when (screen) {
        AppScreen.DASHBOARD -> if (isUrdu) "معماری منصوبہ بندی ڈیش بورڈ" else "Architectural Design Studio"
        AppScreen.PLOT_INPUT -> if (isUrdu) "مرحلہ ۱: پلاٹ کا رقبہ و پیمائش" else "Step 1: Plot Dimensions & Boundaries"
        AppScreen.REQUIREMENTS -> if (isUrdu) "مرحلہ ۲: کمروں کی ضروریات و ترجیحات" else "Step 2: Rooms & Construction Scope"
        AppScreen.DESIGN_ALTERNATIVES -> if (isUrdu) "مرحلہ ۳: ۳ مختلف متبادل نقشے" else "Step 3: 3 Architectural Alternatives"
        AppScreen.FLOOR_PLAN_2D -> if (isUrdu) "۲ ڈی فلور پلان بلیو پرنٹ" else "2D Dimensioned Floor Plan"
        AppScreen.VISUALIZER_3D -> if (isUrdu) "۳ ڈی ہاؤس ماڈل و مناظر" else "Interactive 3D House Visualizer"
        AppScreen.COST_ESTIMATOR -> if (isUrdu) "تعمیراتی لاگت کا تخمینہ" else "Construction Cost & Materials"
        AppScreen.PLOT_CALCULATOR -> if (isUrdu) "پلاٹ رقبہ کیلکولیٹر" else "Pakistani Plot Area Calculator"
        AppScreen.COMPARE_DESIGNS -> if (isUrdu) "نقشوں کا تقابل" else "Design Alternatives Comparison"
        AppScreen.SAVED_PROJECTS -> if (isUrdu) "محفوظ شدہ منصوبے" else "My Saved Projects"
    }
}
