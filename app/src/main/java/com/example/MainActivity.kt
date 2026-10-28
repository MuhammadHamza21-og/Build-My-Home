package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.cost.ConstructionCostCalculator
import com.example.ui.components.AppTopBar
import com.example.ui.screens.*
import com.example.ui.theme.ArchitecturalCyan
import com.example.ui.theme.ArchitecturalNavy
import com.example.ui.theme.BuildMyHomeTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.HousePlanningViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: HousePlanningViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BuildMyHomeTheme {
                MainAppContent(viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: HousePlanningViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val isUrdu by viewModel.isUrdu.collectAsStateWithLifecycle()
    val plotInfo by viewModel.plotInfo.collectAsStateWithLifecycle()
    val requirements by viewModel.requirements.collectAsStateWithLifecycle()
    val designAlternatives by viewModel.designAlternatives.collectAsStateWithLifecycle()
    val selectedDesign by viewModel.selectedDesign.collectAsStateWithLifecycle()
    val selectedFloorLevel by viewModel.selectedFloorLevel.collectAsStateWithLifecycle()
    val selectedRoom by viewModel.selectedRoom.collectAsStateWithLifecycle()
    val costEstimate by viewModel.costEstimate.collectAsStateWithLifecycle()
    val savedProjects by viewModel.savedProjects.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()

    // Android Hardware / Gesture Back Button Handling
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            AppTopBar(
                currentScreen = currentScreen,
                isUrdu = isUrdu,
                onBack = { viewModel.navigateBack() },
                onToggleLanguage = { viewModel.toggleLanguage() },
                onNavigate = { viewModel.navigateTo(it) }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = ArchitecturalNavy,
                contentColor = Color.White
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.DASHBOARD,
                    onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text(if (isUrdu) "ہوم" else "Home", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ArchitecturalNavy,
                        selectedTextColor = ArchitecturalCyan,
                        indicatorColor = ArchitecturalCyan,
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.FLOOR_PLAN_2D,
                    onClick = { viewModel.navigateTo(AppScreen.FLOOR_PLAN_2D) },
                    icon = { Icon(Icons.Default.ViewQuilt, contentDescription = "2D Plan") },
                    label = { Text(if (isUrdu) "۲ ڈی نقشہ" else "2D Plan", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ArchitecturalNavy,
                        selectedTextColor = ArchitecturalCyan,
                        indicatorColor = ArchitecturalCyan,
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.VISUALIZER_3D,
                    onClick = { viewModel.navigateTo(AppScreen.VISUALIZER_3D) },
                    icon = { Icon(Icons.Default.ViewInAr, contentDescription = "3D View") },
                    label = { Text(if (isUrdu) "۳ ڈی ماڈل" else "3D Model", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ArchitecturalNavy,
                        selectedTextColor = ArchitecturalCyan,
                        indicatorColor = ArchitecturalCyan,
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.COST_ESTIMATOR,
                    onClick = { viewModel.navigateTo(AppScreen.COST_ESTIMATOR) },
                    icon = { Icon(Icons.Default.Paid, contentDescription = "Cost") },
                    label = { Text(if (isUrdu) "لاگت" else "Cost", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ArchitecturalNavy,
                        selectedTextColor = ArchitecturalCyan,
                        indicatorColor = ArchitecturalCyan,
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.PLOT_CALCULATOR,
                    onClick = { viewModel.navigateTo(AppScreen.PLOT_CALCULATOR) },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = "Calc") },
                    label = { Text(if (isUrdu) "کیلکولیٹر" else "Calc", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = ArchitecturalNavy,
                        selectedTextColor = ArchitecturalCyan,
                        indicatorColor = ArchitecturalCyan,
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> DashboardScreen(
                    savedProjects = savedProjects,
                    isUrdu = isUrdu,
                    onNavigate = { viewModel.navigateTo(it) },
                    onLoadSample = { viewModel.loadSampleProject(it) },
                    onLoadProject = { viewModel.loadProject(it) },
                    onDeleteProject = { viewModel.deleteProject(it) }
                )

                AppScreen.PLOT_INPUT -> PlotInputScreen(
                    plotInfo = plotInfo,
                    isUrdu = isUrdu,
                    onPlotInfoChanged = { viewModel.updatePlotInfo(it) },
                    onProceed = { viewModel.navigateTo(AppScreen.REQUIREMENTS) }
                )

                AppScreen.REQUIREMENTS -> RequirementsScreen(
                    requirements = requirements,
                    isUrdu = isUrdu,
                    isGenerating = isGenerating,
                    onRequirementsChanged = { viewModel.updateRequirements(it) },
                    onGenerate = {
                        viewModel.generatePlans()
                        viewModel.navigateTo(AppScreen.DESIGN_ALTERNATIVES)
                    }
                )

                AppScreen.DESIGN_ALTERNATIVES -> DesignAlternativesScreen(
                    alternatives = designAlternatives,
                    selectedDesign = selectedDesign,
                    isUrdu = isUrdu,
                    onSelectDesign = { viewModel.selectDesignAlternative(it) },
                    onNavigate = { viewModel.navigateTo(it) }
                )

                AppScreen.FLOOR_PLAN_2D -> {
                    val activeDesign = selectedDesign ?: designAlternatives.firstOrNull()
                    if (activeDesign != null) {
                        FloorPlanEditorScreen(
                            floorPlanDesign = activeDesign,
                            plotInfo = plotInfo,
                            selectedFloorLevel = selectedFloorLevel,
                            selectedRoom = selectedRoom,
                            isUrdu = isUrdu,
                            onSelectFloorLevel = { viewModel.selectFloorLevel(it) },
                            onSelectRoom = { viewModel.selectRoom(it) },
                            onSelectAlternative = { viewModel.selectDesignAlternative(it) },
                            onSaveProject = { viewModel.saveCurrentProject(it) },
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }
                }

                AppScreen.VISUALIZER_3D -> {
                    val activeDesign = selectedDesign ?: designAlternatives.firstOrNull()
                    if (activeDesign != null) {
                        Visualizer3DScreen(
                            floorPlanDesign = activeDesign,
                            plotInfo = plotInfo,
                            requirements = requirements,
                            isUrdu = isUrdu,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }
                }

                AppScreen.COST_ESTIMATOR -> CostEstimatorScreen(
                    currentEstimate = costEstimate,
                    isUrdu = isUrdu,
                    onRecalculate = { city, quality ->
                        val updated = requirements.copy(constructionQuality = quality)
                        viewModel.updateRequirements(updated)
                    }
                )

                AppScreen.PLOT_CALCULATOR -> PlotCalculatorScreen(
                    currentStandard = plotInfo.marlaStandard,
                    isUrdu = isUrdu,
                    onStandardChanged = { newStd ->
                        viewModel.updatePlotInfo(plotInfo.copy(marlaStandard = newStd))
                    }
                )

                AppScreen.COMPARE_DESIGNS -> ProjectCompareScreen(
                    alternatives = designAlternatives,
                    selectedDesign = selectedDesign,
                    isUrdu = isUrdu,
                    onSelectAlternative = { viewModel.selectDesignAlternative(it) },
                    onNavigate = { viewModel.navigateTo(it) }
                )

                AppScreen.SAVED_PROJECTS -> DashboardScreen(
                    savedProjects = savedProjects,
                    isUrdu = isUrdu,
                    onNavigate = { viewModel.navigateTo(it) },
                    onLoadSample = { viewModel.loadSampleProject(it) },
                    onLoadProject = { viewModel.loadProject(it) },
                    onDeleteProject = { viewModel.deleteProject(it) }
                )
            }
        }
    }
}
