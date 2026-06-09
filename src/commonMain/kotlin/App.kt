import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import gameshop.generated.resources.*
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.Font
import kotlin.random.Random
import theme.DesignSystem

// --- MODELS ---
enum class Country(val label: String, val currency: String, val rate: Double) {
    USA("USA", "$", 1.0),
    EU("Europe", "€", 0.92),
    UK("UK", "£", 0.79),
    JP("Japan", "¥", 150.0)
}

data class Game(
    val id: String,
    val title: String,
    val basePrice: Double,
    val source: String = "Eneba",
    val description: String,
    val imageUrl: String,
    val color: Color,
    val rating: Double = 4.5 + Random.nextDouble(0.5),
    val sales: Int = Random.nextInt(1000, 50000)
)

data class PurchasedItem(
    val game: Game,
    val code: String,
    val date: String
)

// --- DATA ---
val mockGames = listOf(
    Game("1", "Cyberpunk 2077", 29.99, description = "Night City is waiting.", imageUrl = "https://images.eneba.com/v2/resizes/340x510/CYBERPUNK_2077_GOG.jpg", color = Color(0xFFFFE600)),
    Game("2", "Elden Ring", 49.99, description = "Become the Elden Lord.", imageUrl = "https://images.eneba.com/v2/resizes/340x510/ELDEN_RING_PC.jpg", color = Color(0xFFC19A6B)),
    Game("3", "The Witcher 3", 14.99, description = "Wild Hunt awaits.", imageUrl = "https://images.eneba.com/v2/resizes/340x510/THE_WITCHER_3_WILD_HUNT.jpg", color = Color(0xFF910000)),
    Game("4", "Red Dead Redemption 2", 39.99, description = "Outlaws for life.", imageUrl = "https://images.eneba.com/v2/resizes/340x510/RED_DEAD_REDEMPTION_2_PC.jpg", color = Color(0xFFB30000)),
    Game("5", "Hades II", 24.99, description = "Rogue-like god-like.", imageUrl = "https://images.eneba.com/v2/resizes/340x510/HADES_2.jpg", color = Color(0xFFFF4500)),
    Game("6", "Ghost of Tsushima", 34.99, description = "Honor in feudal Japan.", imageUrl = "https://images.eneba.com/v2/resizes/340x510/GHOST_OF_TSUSHIMA.jpg", color = Color(0xFF2E7D32)),
    Game("7", "Baldur's Gate 3", 59.99, description = "Gather your party.", imageUrl = "https://images.eneba.com/v2/resizes/340x510/BALDURS_GATE_3.jpg", color = Color(0xFFB08D57)),
    Game("8", "Stardew Valley", 14.99, description = "Build your farm.", imageUrl = "https://images.eneba.com/v2/resizes/340x510/STARDEW_VALLEY_PC.jpg", color = Color(0xFF4CAF50)),
    Game("9", "Terraria", 9.99, description = "Explore, build, fight.", imageUrl = "https://images.eneba.com/v2/resizes/340x510/TERRARIA_PC.jpg", color = Color(0xFF8D6E63)),
    Game("10", "Sekiro", 39.99, description = "Shadows die twice.", imageUrl = "https://images.eneba.com/v2/resizes/340x510/SEKIRO_SHADOWS_DIE_TWICE.jpg", color = Color(0xFFBF360C))
)

// --- APP ENTRY ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf("Shop") }
    var selectedGame by remember { mutableStateOf<Game?>(null) }
    val cartItems = remember { mutableStateListOf<Game>() }
    val purchasedItems = remember { mutableStateListOf<PurchasedItem>() }
    var selectedCountry by remember { mutableStateOf(Country.USA) }
    var searchQuery by remember { mutableStateOf("") }
    
    var isCheckingOut by remember { mutableStateOf(false) }
    var isProcessingPayment by remember { mutableStateOf(false) }
    var navExpanded by remember { mutableStateOf(true) }

    val googleSansFlex = FontFamily(Font(Res.font.google_sans_flex))

    val darkScheme = darkColorScheme(
        primary = DesignSystem.Primary,
        primaryContainer = DesignSystem.PrimaryContainer,
        onPrimaryContainer = DesignSystem.OnPrimaryContainer,
        background = DesignSystem.Background,
        surface = DesignSystem.Surface,
        surfaceVariant = DesignSystem.SurfaceVariant,
        onSurface = DesignSystem.OnBackground
    )

    val customTypography = Typography(
        displayLarge = TextStyle(fontFamily = googleSansFlex, fontWeight = FontWeight.Black, fontSize = 57.sp),
        displaySmall = TextStyle(fontFamily = googleSansFlex, fontWeight = FontWeight.Black, fontSize = 36.sp),
        headlineLarge = TextStyle(fontFamily = googleSansFlex, fontWeight = FontWeight.Black, fontSize = 32.sp),
        headlineMedium = TextStyle(fontFamily = googleSansFlex, fontWeight = FontWeight.Bold, fontSize = 28.sp),
        titleLarge = TextStyle(fontFamily = googleSansFlex, fontWeight = FontWeight.Bold, fontSize = 22.sp),
        bodyLarge = TextStyle(fontFamily = googleSansFlex, fontWeight = FontWeight.Normal, fontSize = 16.sp),
        labelLarge = TextStyle(fontFamily = googleSansFlex, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    )

    MaterialTheme(
        colorScheme = darkScheme,
        typography = customTypography
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Row(modifier = Modifier.fillMaxSize()) {
                AppNavigationRail(selectedTab, navExpanded, { navExpanded = !navExpanded }) { 
                    selectedTab = it; selectedGame = null; isCheckingOut = false 
                }
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                    Spacer(Modifier.height(24.dp))
                    SearchBar(searchQuery) { searchQuery = it }
                    AnimatedContent(
                        targetState = when {
                            isProcessingPayment -> "Processing"
                            isCheckingOut -> "Checkout"
                            selectedGame != null -> selectedGame
                            else -> selectedTab
                        },
                        modifier = Modifier.weight(1f),
                        transitionSpec = {
                            fadeIn(animationSpec = spring(stiffness = Spring.StiffnessLow)) + 
                            scaleIn(initialScale = 0.95f) togetherWith fadeOut()
                        }
                    ) { state ->
                        when (state) {
                            "Processing" -> ProcessingScreen()
                            "Checkout" -> CheckoutScreen(cartItems, selectedCountry, { isCheckingOut = false }) {
                                scope.launch {
                                    isProcessingPayment = true
                                    delay(2500)
                                    purchasedItems.addAll(cartItems.map { PurchasedItem(it, generateSecureCode(), "03/06/2026") })
                                    cartItems.clear()
                                    isProcessingPayment = false
                                    isCheckingOut = false
                                    selectedTab = "Purchased"
                                }
                            }
                            is Game -> GameDetailsScreen(state, selectedCountry) { cartItems.add(state); selectedGame = null; selectedTab = "Cart" }
                            "Shop" -> ShopScreen(selectedCountry, searchQuery, onGameClick = { selectedGame = it })
                            "Cart" -> CartScreen(cartItems, selectedCountry) { isCheckingOut = true }
                            "Purchased" -> PurchasedScreen(purchasedItems)
                            "Dashboard" -> DashboardScreen(purchasedItems)
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENTS ---
@Composable
fun AppNavigationRail(selectedTab: String, expanded: Boolean, onToggle: () -> Unit, onSelect: (String) -> Unit) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.width(if (expanded) 240.dp else 80.dp),
        header = {
            IconButton(onClick = onToggle, modifier = Modifier.padding(vertical = 12.dp)) { 
                Icon(Icons.Default.Menu, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) 
            }
        }
    ) {
        val navItems = listOf(
            Triple("Shop", Icons.Outlined.Storefront, Icons.Filled.Storefront),
            Triple("Cart", Icons.Outlined.ShoppingBag, Icons.Filled.ShoppingBag),
            Triple("Purchased", Icons.Outlined.AssignmentTurnedIn, Icons.Filled.AssignmentTurnedIn),
            Triple("Dashboard", Icons.Outlined.BarChart, Icons.Filled.BarChart)
        )
        navItems.forEach { (label, outlined, filled) ->
            val isSelected = selectedTab == label
            NavigationRailItem(
                selected = isSelected,
                onClick = { onSelect(label) },
                icon = { 
                    Icon(if (isSelected) filled else outlined, null) 
                },
                label = if (expanded) { { Text(label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) } } else null,
                alwaysShowLabel = expanded,
                colors = NavigationRailItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val width by animateDpAsState(if (expanded) 600.dp else 300.dp, DesignSystem.ExpressiveSpring)
    Surface(
        modifier = Modifier.width(width).height(64.dp).padding(vertical = 4.dp).clickable { expanded = !expanded },
        shape = DesignSystem.PillShape,
        color = DesignSystem.SurfaceVariant
    ) {
        Row(modifier = Modifier.padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Search, null)
            Spacer(Modifier.width(16.dp))
            BasicTextField(query, onQueryChange, textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface))
        }
    }
}

@Composable
fun ShopScreen(country: Country, searchQuery: String, onGameClick: (Game) -> Unit) {
    val filtered = if (searchQuery.isEmpty()) mockGames else mockGames.filter { it.title.contains(searchQuery, ignoreCase = true) }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(220.dp),
        contentPadding = PaddingValues(32.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Card(
                modifier = Modifier.fillMaxWidth().height(240.dp),
                shape = DesignSystem.LargeShape,
                colors = CardDefaults.cardColors(containerColor = DesignSystem.PrimaryContainer)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.weight(1f).padding(32.dp).align(Alignment.CenterVertically)) {
                        Text("BEST DEALS FROM", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f))
                        Text("ENEBA!", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = {}, shape = DesignSystem.PillShape) {
                            Text("EXPLORE NOW")
                        }
                    }
                    Image(
                        painter = org.jetbrains.compose.resources.painterResource(Res.drawable.deals),
                        null,
                        modifier = Modifier.fillMaxHeight().width(400.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) { 
            Text("Most trending", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 16.dp)) 
        }
        items(filtered) { GameGridItem(it, country, onGameClick) }
    }
}

@Composable
fun GameGridItem(game: Game, country: Country, onClick: (Game) -> Unit) {
    Card(
        onClick = { onClick(game) },
        modifier = Modifier.fillMaxWidth().height(320.dp),
        shape = DesignSystem.NormalShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.3f))
    ) {
        Column {
            KamelImage(
                asyncPainterResource(game.imageUrl),
                null,
                modifier = Modifier.fillMaxWidth().height(200.dp).clip(DesignSystem.NormalShape),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(16.dp)) {
                Text(game.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(game.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${country.currency}${String.format("%.2f", game.basePrice * country.rate)}",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = DesignSystem.Primary
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.AddShoppingCart, null, tint = DesignSystem.Primary)
                }
            }
        }
    }
}

@Composable
fun CartScreen(items: MutableList<Game>, country: Country, onCheckout: () -> Unit) {
    var showEnebaPrompt by remember { mutableStateOf(false) }
    val total = items.sumOf { it.basePrice * country.rate }
    
    Column(modifier = Modifier.fillMaxSize().padding(32.dp)) {
        Text("Your Cart", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(24.dp))
        
        if (items.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items) { game ->
                    CartItemRow(game, country) { items.remove(game) }
                }
            }
            
            HorizontalDivider(Modifier.padding(vertical = 16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text("${country.currency}${String.format("%.2f", total)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = DesignSystem.Primary)
            }
            
            Spacer(Modifier.height(24.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(onClick = { items.clear() }, modifier = Modifier.weight(1f), shape = DesignSystem.PillShape) {
                    Text("CLEAR CART")
                }
                Button(onClick = { showEnebaPrompt = true }, modifier = Modifier.weight(1f), shape = DesignSystem.PillShape) {
                    Text("BUY FROM ENEBA")
                }
            }
        }
    }
    
    if (showEnebaPrompt) {
        AlertDialog(
            onDismissRequest = { showEnebaPrompt = false },
            title = { Text("Redirecting to Eneba") },
            text = { Text("You are about to be redirected to Eneba. All games in your cart will be opened in separate tabs for you to complete the purchase.") },
            confirmButton = {
                Button(onClick = {
                    items.forEach { game ->
                        val url = "https://www.eneba.com/search?text=${game.title.replace(" ", "%20")}"
                        try {
                            java.awt.Desktop.getDesktop().browse(java.net.URI(url))
                        } catch (e: Exception) { e.printStackTrace() }
                    }
                    items.clear()
                    showEnebaPrompt = false
                }) {
                    Text("PROCEED")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEnebaPrompt = false }) { Text("CANCEL") }
            }
        )
    }
}

@Composable
fun CartItemRow(game: Game, country: Country, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(0.2f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            KamelImage(asyncPainterResource(game.imageUrl), null, modifier = Modifier.size(60.dp).clip(DesignSystem.NormalShape), contentScale = ContentScale.Crop)
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(game.title, fontWeight = FontWeight.Bold)
                Text("${country.currency}${String.format("%.2f", game.basePrice * country.rate)}", color = DesignSystem.Primary)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun CheckoutScreen(items: List<Game>, country: Country, onBack: () -> Unit, onPay: () -> Unit) { /* ... */ }

@Composable
fun ProcessingScreen() { /* ... */ }

@Composable
fun PurchasedScreen(items: List<PurchasedItem>) { 
    Column(modifier = Modifier.padding(32.dp)) {
        Text("Purchased Items", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
    }
}

@Composable
fun DashboardScreen(items: List<PurchasedItem>) {
    Column(modifier = Modifier.padding(32.dp)) {
        Text("Market Dashboard", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
    }
}

@Composable
fun GameDetailsScreen(game: Game, country: Country, onAddToCart: () -> Unit) { /* ... */ }

fun generateSecureCode(): String = "XXXXX-XXXXX-XXXXX"
