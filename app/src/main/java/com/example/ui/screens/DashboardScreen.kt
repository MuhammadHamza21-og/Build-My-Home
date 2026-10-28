package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.db.ProjectEntity
import com.example.ui.components.SafetyDisclaimerBanner
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    savedProjects: List<ProjectEntity>,
    isUrdu: Boolean,
    onNavigate: (AppScreen) -> Unit,
    onLoadSample: (String) -> Unit,
    onLoadProject: (ProjectEntity) -> Unit,
    onDeleteProject: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceLight),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Banner with architectural concept asset
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hero_house),
                    contentDescription = "Architectural House Concept",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Gradient scrim overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC0B192C), Color(0xFF0B192C))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Surface(
                        color = ArchitecturalEmerald,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isUrdu) "اے آئی ہاؤس پلاننگ اسٹوڈیو" else "AI HOUSE PLANNING ENGINE",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isUrdu) "اپنے خوابوں کا گھر ڈیزائن کریں" else "Design Your Dream Home",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (isUrdu)
                            "پلاٹ کی پیمائش درج کریں اور فوری ۲ ڈی و ۳ ڈی نقشے حاصل کریں"
                        else
                            "Real dimensions, Pakistani Marla standards, 2D floor plans & interactive 3D models",
                        fontSize = 12.sp,
                        color = Color(0xFFCBD5E1),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // 2. Primary Action Button: Start New House Design
        item {
            PaddingBox {
                Button(
                    onClick = { onNavigate(AppScreen.PLOT_INPUT) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("start_design_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = ArchitecturalNavy),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        Icons.Default.AddHome,
                        contentDescription = "Start Design",
                        tint = ArchitecturalCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isUrdu) "نیا گھر ڈیزائن کریں (مرحلہ وار)" else "Start New House Design",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }

        // 3. Quick Action Feature Grid
        item {
            PaddingBox {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = if (isUrdu) "رقبہ کیلکولیٹر" else "Plot Calculator",
                        subtitle = if (isUrdu) "مرلہ، کنال، گز، فٹ" else "Marla, Kanal, Sq Ft",
                        icon = Icons.Default.Calculate,
                        iconTint = ArchitecturalCyan,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppScreen.PLOT_CALCULATOR) }
                    )
                    QuickActionCard(
                        title = if (isUrdu) "تعمیراتی لاگت" else "Cost Estimator",
                        subtitle = if (isUrdu) "گرے اسٹرکچر و فنشنگ" else "Grey & Finishing Rates",
                        icon = Icons.Default.Paid,
                        iconTint = ArchitecturalGold,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppScreen.COST_ESTIMATOR) }
                    )
                }
            }
        }

        item {
            PaddingBox {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionCard(
                        title = if (isUrdu) "۲ ڈی فلور پلان" else "2D Floor Plan",
                        subtitle = if (isUrdu) "پیمائش و کمروں کے سائز" else "Dimensioned Blueprint",
                        icon = Icons.Default.ViewQuilt,
                        iconTint = ArchitecturalEmerald,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppScreen.FLOOR_PLAN_2D) }
                    )
                    QuickActionCard(
                        title = if (isUrdu) "۳ ڈی ویژولائزر" else "3D Visualizer",
                        subtitle = if (isUrdu) "۳۶۰ ڈگری ماڈل و چھت" else "360° Orbit & Cutaway",
                        icon = Icons.Default.ViewInAr,
                        iconTint = Color(0xFFA29BFE),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AppScreen.VISUALIZER_3D) }
                    )
                }
            }
        }

        // 4. One-Click Popular Plot Templates
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                PaddingBox {
                    Text(
                        text = if (isUrdu) "پاکستان کے مقبول ترین پلاٹ سائز (ایک کلک سے کھولیں)" else "Popular House Templates (1-Click Load)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        PlotTemplateCard(
                            title = "5 Marla (25' × 45')",
                            subtitle = if (isUrdu) "لاہور، راولپنڈی، اسلام آباد" else "Smart Family Duplex (3 Bed, Porch)",
                            tag = "5 Marla",
                            color = Color(0xFF1E3E62),
                            onClick = { onLoadSample("5_MARLA") }
                        )
                    }
                    item {
                        PlotTemplateCard(
                            title = "10 Marla (35' × 65')",
                            subtitle = if (isUrdu) "ڈی ایچ اے، بحریہ، گلبرگ" else "Executive Luxury Villa (4 Bed, 2 Car)",
                            tag = "10 Marla",
                            color = Color(0xFF0F172A),
                            onClick = { onLoadSample("10_MARLA") }
                        )
                    }
                    item {
                        PlotTemplateCard(
                            title = "1 Kanal (50' × 90')",
                            subtitle = if (isUrdu) "شاہانہ رہائش گاہ" else "Contemporary Mansion (5 Bed, Lawn)",
                            tag = "1 Kanal",
                            color = Color(0xFF065F46),
                            onClick = { onLoadSample("1_KANAL") }
                        )
                    }
                }
            }
        }

        // 5. My Saved Projects Section
        item {
            PaddingBox {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUrdu) "میرے محفوظ شدہ نقشے" else "My Saved Projects",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = ArchitecturalNavy
                    )
                    Text(
                        text = "${savedProjects.size} ${if (isUrdu) "پروجیکٹس" else "Saved"}",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        if (savedProjects.isEmpty()) {
            item {
                PaddingBox {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = SurfaceContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.FolderOpen,
                                contentDescription = "No Saved Projects",
                                tint = TextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isUrdu) "ابھی تک کوئی پروجیکٹ محفوظ نہیں ہوا" else "No saved projects yet",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                            Text(
                                text = if (isUrdu)
                                    "نیا نقشہ تیار کر کے اسے محفوظ کریں تاکہ بعد میں دیکھ سکیں"
                                else
                                    "Create a new house design above to save and reopen anytime",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        } else {
            items(savedProjects) { project ->
                PaddingBox {
                    SavedProjectItem(
                        project = project,
                        isUrdu = isUrdu,
                        onOpen = { onLoadProject(project) },
                        onDelete = { onDeleteProject(project.id) }
                    )
                }
            }
        }

        // 6. Architectural Safety Disclaimer
        item {
            PaddingBox {
                SafetyDisclaimerBanner(isUrdu = isUrdu)
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(iconTint.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun PlotTemplateCard(
    title: String,
    subtitle: String,
    tag: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Surface(
                color = Color(0x33FFFFFF),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = tag,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, fontSize = 11.sp, color = Color(0xFFCBD5E1), lineHeight = 15.sp)
        }
    }
}

@Composable
private fun SavedProjectItem(
    project: ProjectEntity,
    isUrdu: Boolean,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onOpen() },
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFE0F2FE), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.HomeWork,
                    contentDescription = "Project",
                    tint = ArchitecturalBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${project.statedArea} ${project.areaUnit} • ${project.city} • ${project.frontWidth.toInt()}'×${project.leftLength.toInt()}'",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
                Text(
                    text = "${project.bedrooms} Bed, ${project.baths} Bath • Design ${project.selectedDesignAlt}",
                    fontSize = 10.sp,
                    color = ArchitecturalEmerald,
                    fontWeight = FontWeight.Medium
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.DeleteOutline,
                    contentDescription = "Delete Project",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun PaddingBox(content: @Composable () -> Unit) {
    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
        content()
    }
}
