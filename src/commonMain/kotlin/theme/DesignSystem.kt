package theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.ui.unit.Dp

object DesignSystem {
    // --- Red-Themed Dark Palette (No OS Interaction) ---
    val Background = Color(0xFF141212)
    val OnBackground = Color(0xFFF2DEDD)
    val Surface = Color(0xFF1A1515)
    val SurfaceVariant = Color(0xFF2D2B2B)
    val Primary = Color(0xFFE57373) // Soft Red Accent
    val PrimaryContainer = Color(0xFF5D1010) // Dark Red Container
    val OnPrimaryContainer = Color(0xFFFFDAD6)
    val Outline = Color(0xFF857373)

    // --- Shapes ---
    val SmallShape = RoundedCornerShape(12.dp)
    val NormalShape = RoundedCornerShape(16.dp)
    val LargeShape = RoundedCornerShape(24.dp)
    val PillShape = RoundedCornerShape(50.dp)
    
    // --- Motion ---
    val ExpressiveSpring = spring<Dp>(
        stiffness = 300f,
        dampingRatio = 0.6f
    )
    val FastSpring = spring<Dp>(
        stiffness = 500f,
        dampingRatio = 0.8f
    )
}
