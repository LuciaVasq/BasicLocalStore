package morales.jose.basiclocalstore.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import morales.jose.basiclocalstore.data.PokemonEntity
import morales.jose.basiclocalstore.viewModel.PokemonViewModel

// ── Type → colour mapping ─────────────────────────────────────────────────────
private fun typeColor(type: String): Color = when (type.lowercase()) {
    "fuego"     -> Color(0xFFFF6B35)
    "agua"      -> Color(0xFF4A90D9)
    "planta"    -> Color(0xFF56C16A)
    "eléctrico" -> Color(0xFFFFD93D)
    "psíquico"  -> Color(0xFFE040FB)
    "fantasma"  -> Color(0xFF7C4DFF)
    "roca"      -> Color(0xFFAFA882)
    "lucha"     -> Color(0xFFC0392B)
    "dragón"    -> Color(0xFF5C6BC0)
    "normal"    -> Color(0xFFBDBDBD)
    else        -> Color(0xFF78909C)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BolsaScreen(pokemonViewModel: PokemonViewModel) {
    val pokemons by pokemonViewModel.pokemonState.collectAsStateWithLifecycle()
    var pokemonToDelete by remember { mutableStateOf<PokemonEntity?>(null) }

    // Snackbar for level-up feedback
    val snackbarHostState = remember { SnackbarHostState() }
    val levelUpMessage = pokemonViewModel.levelUpMessage
    LaunchedEffect(levelUpMessage) {
        if (levelUpMessage != null) {
            snackbarHostState.showSnackbar(levelUpMessage)
            pokemonViewModel.clearLevelUpMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Column(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFCC3333), Color(0xFFEC6161))
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "MI POKÉBOLSA",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "${pokemons.size} Pokémon capturado${if (pokemons.size != 1) "s" else ""}",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(12.dp))
                // ── Search bar ────────────────────────────────────────────
                TextField(
                    value = pokemonViewModel.searchQuery,
                    onValueChange = { pokemonViewModel.searchQuery = it },
                    placeholder = { Text("Buscar por nombre o tipo...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF8F8F8))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // ── Filters ───────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = pokemonViewModel.typeFilter,
                        onValueChange = { pokemonViewModel.typeFilter = it },
                        label = { Text("Tipo", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = if (pokemonViewModel.minLevelFilter <= 1) ""
                        else pokemonViewModel.minLevelFilter.toString(),
                        onValueChange = {
                            pokemonViewModel.minLevelFilter = it.toIntOrNull() ?: 1
                        },
                        label = { Text("Niv. Mín.", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.weight(0.75f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── List ──────────────────────────────────────────────────────
            AnimatedVisibility(visible = pokemons.isEmpty(), enter = fadeIn(), exit = fadeOut()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎒", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "La bolsa está vacía",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Text(
                            "¡Ve a capturar nuevos Pokémon!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                }
            }

            AnimatedVisibility(visible = pokemons.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(pokemons, key = { it.id }) { pokemon ->
                        PokemonCardItem(
                            pokemon = pokemon,
                            onLevelUp = { pokemonViewModel.levelUp(pokemon) },
                            onDelete  = { pokemonToDelete = pokemon }
                        )
                    }
                }
            }
        }
    }

    // ── Delete confirmation dialog ────────────────────────────────────────────
    pokemonToDelete?.let { pokemon ->
        AlertDialog(
            onDismissRequest = { pokemonToDelete = null },
            icon = { Text("🔴", fontSize = 32.sp) },
            title = {
                Text(
                    "¿Liberar a ${pokemon.name.replaceFirstChar { it.uppercase() }}?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("Esta acción no se puede deshacer. El Pokémon regresará a la naturaleza.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        pokemonViewModel.releasePokemon(pokemon)
                        pokemonToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) { Text("Liberar", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                OutlinedButton(onClick = { pokemonToDelete = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun PokemonCardItem(
    pokemon: PokemonEntity,
    onLevelUp: () -> Unit,
    onDelete: () -> Unit
) {
    val color = typeColor(pokemon.type)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Coloured top bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(color)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Number badge
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "#${pokemon.number}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pokemon.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    // Type badge
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = color.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = pokemon.type,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    // Level bar
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Nv. ${pokemon.level}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF3B4CCA),
                            modifier = Modifier.width(44.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        LinearProgressIndicator(
                            progress = { pokemon.level / 100f },
                            modifier = Modifier
                                .weight(1f)
                                .height(5.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = color,
                            trackColor = color.copy(alpha = 0.15f)
                        )
                    }
                }
                // Action buttons
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = onLevelUp) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Subir Nivel",
                            tint = Color(0xFF43A047),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Liberar",
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}