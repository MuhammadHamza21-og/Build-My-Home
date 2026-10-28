package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.cost.ConstructionCostCalculator
import com.example.data.db.AppDatabase
import com.example.data.db.ProjectEntity
import com.example.data.generator.ArchitecturalValidator
import com.example.data.generator.FloorPlanGeneratorEngine
import com.example.data.generator.ValidationResult
import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    DASHBOARD,
    PLOT_INPUT,
    REQUIREMENTS,
    DESIGN_ALTERNATIVES,
    FLOOR_PLAN_2D,
    VISUALIZER_3D,
    COST_ESTIMATOR,
    PLOT_CALCULATOR,
    COMPARE_DESIGNS,
    SAVED_PROJECTS
}

class HousePlanningViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val projectDao = db.projectDao()

    val savedProjects: StateFlow<List<ProjectEntity>> = projectDao.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _isUrdu = MutableStateFlow(false)
    val isUrdu: StateFlow<Boolean> = _isUrdu.asStateFlow()

    private val _plotInfo = MutableStateFlow(
        PlotInfo(
            projectName = "DHA Phase 6 Villa",
            city = "Lahore",
            statedAreaValue = 10f,
            statedAreaUnit = PlotAreaUnit.MARLA,
            marlaStandard = MarlaStandard.STANDARD_225,
            plotShape = PlotShape.RECTANGLE,
            roadFacing = RoadFacingSide.FRONT,
            boundaries = BoundaryMeasurements(front = 35f, rear = 35f, left = 65f, right = 65f)
        )
    )
    val plotInfo: StateFlow<PlotInfo> = _plotInfo.asStateFlow()

    private val _requirements = MutableStateFlow(
        HouseRequirements(
            buildingType = BuildingType.DOUBLE_STOREY,
            rooms = RoomRequirements(bedrooms = 4, attachedBaths = 4, carParkingSlots = 2),
            architecturalStyle = ArchitecturalStyle.MODERN_PAKISTANI,
            constructionQuality = ConstructionQuality.STANDARD
        )
    )
    val requirements: StateFlow<HouseRequirements> = _requirements.asStateFlow()

    private val _designAlternatives = MutableStateFlow<List<FloorPlanDesign>>(emptyList())
    val designAlternatives: StateFlow<List<FloorPlanDesign>> = _designAlternatives.asStateFlow()

    private val _selectedDesign = MutableStateFlow<FloorPlanDesign?>(null)
    val selectedDesign: StateFlow<FloorPlanDesign?> = _selectedDesign.asStateFlow()

    private val _selectedFloorLevel = MutableStateFlow(0) // 0 = Ground Floor
    val selectedFloorLevel: StateFlow<Int> = _selectedFloorLevel.asStateFlow()

    private val _selectedRoom = MutableStateFlow<RoomGeometry?>(null)
    val selectedRoom: StateFlow<RoomGeometry?> = _selectedRoom.asStateFlow()

    private val _costEstimate = MutableStateFlow<ConstructionCostEstimate?>(null)
    val costEstimate: StateFlow<ConstructionCostEstimate?> = _costEstimate.asStateFlow()

    private val _validationResult = MutableStateFlow<ValidationResult?>(null)
    val validationResult: StateFlow<ValidationResult?> = _validationResult.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _screenHistory = mutableListOf<AppScreen>()

    init {
        generatePlans()
    }

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            _screenHistory.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (_screenHistory.isNotEmpty()) {
            _currentScreen.value = _screenHistory.removeAt(_screenHistory.lastIndex)
            return true
        }
        if (_currentScreen.value != AppScreen.DASHBOARD) {
            _currentScreen.value = AppScreen.DASHBOARD
            return true
        }
        return false
    }

    fun toggleLanguage() {
        _isUrdu.value = !_isUrdu.value
    }

    fun updatePlotInfo(newPlot: PlotInfo) {
        _plotInfo.value = newPlot
        generatePlans()
    }

    fun updateRequirements(newReq: HouseRequirements) {
        _requirements.value = newReq
        generatePlans()
    }

    fun selectDesignAlternative(alt: AlternativeType) {
        val design = _designAlternatives.value.find { it.alternative == alt }
        if (design != null) {
            _selectedDesign.value = design
            updateValidationAndCost(design)
        }
    }

    fun selectFloorLevel(level: Int) {
        _selectedFloorLevel.value = level
        _selectedRoom.value = null
    }

    fun selectRoom(room: RoomGeometry?) {
        _selectedRoom.value = room
    }

    fun generatePlans() {
        viewModelScope.launch {
            _isGenerating.value = true
            val plot = _plotInfo.value
            val req = _requirements.value

            val alternatives = FloorPlanGeneratorEngine.generateDesignAlternatives(plot, req)
            _designAlternatives.value = alternatives

            val currentSelectedAlt = _selectedDesign.value?.alternative ?: AlternativeType.DESIGN_B
            val current = alternatives.find { it.alternative == currentSelectedAlt } ?: alternatives.firstOrNull()

            _selectedDesign.value = current
            if (current != null) {
                updateValidationAndCost(current)
            }
            _isGenerating.value = false
        }
    }

    private fun updateValidationAndCost(design: FloorPlanDesign) {
        val groundPlan = design.getFloor(0)
        if (groundPlan != null) {
            _validationResult.value = ArchitecturalValidator.validateFloorPlan(
                _plotInfo.value.boundaries,
                groundPlan
            )
        }
        _costEstimate.value = ConstructionCostCalculator.calculateCost(
            totalCoveredAreaSqFt = design.totalCoveredAreaSqFt,
            city = _plotInfo.value.city,
            quality = _requirements.value.constructionQuality
        )
    }

    fun saveCurrentProject(customName: String? = null) {
        viewModelScope.launch {
            val plot = _plotInfo.value
            val req = _requirements.value
            val selectedAlt = _selectedDesign.value?.alternative?.code ?: "B"

            val project = ProjectEntity(
                id = UUID.randomUUID().toString(),
                name = customName ?: plot.projectName,
                city = plot.city,
                statedArea = plot.statedAreaValue,
                areaUnit = plot.statedAreaUnit.name,
                marlaStandard = plot.marlaStandard.name,
                plotShape = plot.plotShape.name,
                frontWidth = plot.boundaries.front,
                rearWidth = plot.boundaries.rear,
                leftLength = plot.boundaries.left,
                rightLength = plot.boundaries.right,
                buildingType = req.buildingType.name,
                bedrooms = req.rooms.bedrooms,
                baths = req.rooms.attachedBaths,
                kitchens = req.rooms.kitchens,
                selectedDesignAlt = selectedAlt,
                architecturalStyle = req.architecturalStyle.name,
                constructionQuality = req.constructionQuality.name,
                createdAt = System.currentTimeMillis(),
                modifiedAt = System.currentTimeMillis()
            )
            projectDao.insertProject(project)
        }
    }

    fun loadProject(project: ProjectEntity) {
        val areaUnit = try { PlotAreaUnit.valueOf(project.areaUnit) } catch (_: Exception) { PlotAreaUnit.MARLA }
        val marlaStd = try { MarlaStandard.valueOf(project.marlaStandard) } catch (_: Exception) { MarlaStandard.STANDARD_225 }
        val plotShape = try { PlotShape.valueOf(project.plotShape) } catch (_: Exception) { PlotShape.RECTANGLE }
        val bldgType = try { BuildingType.valueOf(project.buildingType) } catch (_: Exception) { BuildingType.DOUBLE_STOREY }
        val archStyle = try { ArchitecturalStyle.valueOf(project.architecturalStyle) } catch (_: Exception) { ArchitecturalStyle.MODERN_PAKISTANI }
        val quality = try { ConstructionQuality.valueOf(project.constructionQuality) } catch (_: Exception) { ConstructionQuality.STANDARD }

        _plotInfo.value = PlotInfo(
            projectName = project.name,
            city = project.city,
            statedAreaValue = project.statedArea,
            statedAreaUnit = areaUnit,
            marlaStandard = marlaStd,
            plotShape = plotShape,
            boundaries = BoundaryMeasurements(
                front = project.frontWidth,
                rear = project.rearWidth,
                left = project.leftLength,
                right = project.rightLength
            )
        )

        _requirements.value = HouseRequirements(
            buildingType = bldgType,
            rooms = RoomRequirements(bedrooms = project.bedrooms, attachedBaths = project.baths, kitchens = project.kitchens),
            architecturalStyle = archStyle,
            constructionQuality = quality
        )

        generatePlans()
        val altType = when (project.selectedDesignAlt) {
            "A" -> AlternativeType.DESIGN_A
            "C" -> AlternativeType.DESIGN_C
            else -> AlternativeType.DESIGN_B
        }
        selectDesignAlternative(altType)
        navigateTo(AppScreen.FLOOR_PLAN_2D)
    }

    fun deleteProject(id: String) {
        viewModelScope.launch {
            projectDao.deleteProjectById(id)
        }
    }

    fun loadSampleProject(type: String) {
        when (type) {
            "5_MARLA" -> {
                _plotInfo.value = PlotInfo(
                    projectName = "5 Marla Smart Family Home",
                    city = "Lahore",
                    statedAreaValue = 5f,
                    statedAreaUnit = PlotAreaUnit.MARLA,
                    marlaStandard = MarlaStandard.STANDARD_225,
                    boundaries = BoundaryMeasurements(front = 25f, rear = 25f, left = 45f, right = 45f)
                )
                _requirements.value = HouseRequirements(
                    buildingType = BuildingType.DOUBLE_STOREY,
                    rooms = RoomRequirements(bedrooms = 3, attachedBaths = 3, carParkingSlots = 1),
                    architecturalStyle = ArchitecturalStyle.MODERN_PAKISTANI,
                    constructionQuality = ConstructionQuality.STANDARD
                )
            }
            "10_MARLA" -> {
                _plotInfo.value = PlotInfo(
                    projectName = "10 Marla Luxury Executive Villa",
                    city = "Islamabad",
                    statedAreaValue = 10f,
                    statedAreaUnit = PlotAreaUnit.MARLA,
                    marlaStandard = MarlaStandard.STANDARD_225,
                    boundaries = BoundaryMeasurements(front = 35f, rear = 35f, left = 65f, right = 65f)
                )
                _requirements.value = HouseRequirements(
                    buildingType = BuildingType.DOUBLE_STOREY,
                    rooms = RoomRequirements(bedrooms = 4, attachedBaths = 4, carParkingSlots = 2),
                    architecturalStyle = ArchitecturalStyle.CONTEMPORARY_LUXURY,
                    constructionQuality = ConstructionQuality.PREMIUM_EXECUTIVE
                )
            }
            "1_KANAL" -> {
                _plotInfo.value = PlotInfo(
                    projectName = "1 Kanal Architectural Estate",
                    city = "Lahore",
                    statedAreaValue = 1f,
                    statedAreaUnit = PlotAreaUnit.KANAL,
                    marlaStandard = MarlaStandard.STANDARD_225,
                    boundaries = BoundaryMeasurements(front = 50f, rear = 50f, left = 90f, right = 90f)
                )
                _requirements.value = HouseRequirements(
                    buildingType = BuildingType.DOUBLE_STOREY,
                    rooms = RoomRequirements(bedrooms = 5, attachedBaths = 5, carParkingSlots = 3),
                    architecturalStyle = ArchitecturalStyle.MODERN_PAKISTANI,
                    constructionQuality = ConstructionQuality.PREMIUM_EXECUTIVE
                )
            }
        }
        generatePlans()
        selectDesignAlternative(AlternativeType.DESIGN_B)
        navigateTo(AppScreen.FLOOR_PLAN_2D)
    }
}
