package morales.jose.basiclocalstore.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import morales.jose.basiclocalstore.viewModel.PokemonViewModel
import morales.jose.basiclocalstore.viewModel.WildPokemon

@Composable
fun CapturarScreen(pokemonViewModel: PokemonViewModel, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Header ────────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(listOf(Color(0xFF2E7D32), Color(0xFF66BB6A)))
                )
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Text(
                "ÁREA SALVAJE",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Wild-encounter arena ──────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8F5E9)
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AnimatedContent(
                        targetState = pokemonViewModel.wildPokemon,
                        transitionSpec = {
                            fadeIn() + slideInVertically { it / 2 } togetherWith
                                    fadeOut() + slideOutVertically { -it / 2 }
                        },
                        label = "wild_pokemon_anim"
                    ) { wild ->
                        if (wild != null) {
                            WildPokemonDisplay(wild)
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🌿", fontSize = 56.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    if (pokemonViewModel.pokemonEscapo)
                                        "¡Se escapó! La hierba vuelve a la calma..."
                                    else
                                        "La hierba se mueve suavemente...",
                                    color = Color(0xFF388E3C),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Escape message
            AnimatedVisibility(visible = pokemonViewModel.pokemonEscapo) {
                Text(
                    "¡Oh no! ¡Se ha escapado!",
                    color = Color(0xFFD32F2F),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Action buttons ────────────────────────────────────────────────
            Button(
                onClick = { pokemonViewModel.searchPokemon() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
            ) {
                Text(
                    "🌿  BUSCAR EN LA HIERBA",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            AnimatedVisibility(visible = pokemonViewModel.wildPokemon != null) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { pokemonViewModel.capturePokemon() },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC0000))
                    ) {
                        Text(
                            "⚫  ¡LANZAR POKÉBALL!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Captured list ─────────────────────────────────────────────────
            AnimatedVisibility(visible = pokemonViewModel.capturedPokemon.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF388E3C),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "Capturados esta sesión",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF388E3C)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.heightIn(max = 160.dp)
                    ) {
                        items(pokemonViewModel.capturedPokemon) { p ->
                            CapturedMiniCard(p)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            pokemonViewModel.capturedPokemon.forEach {
                                pokemonViewModel.addPokemon(it.name, it.number, it.type)
                            }
                            pokemonViewModel.releaseCapturedPokemon()
                            onBack()
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B4CCA))
                    ) {
                        Text(
                            "💼  GUARDAR ${pokemonViewModel.capturedPokemon.size} EN LA BOLSA",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WildPokemonDisplay(wild: WildPokemon) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(3.dp, Color(0xFF388E3C), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🐾", fontSize = 36.sp)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            wild.name.uppercase(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1B5E20)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF388E3C).copy(alpha = 0.15f)
        ) {
            Text(
                wild.type,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF388E3C),
                fontWeight = FontWeight.SemiBold
            )
        }
        Text(
            "#${wild.number}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun CapturedMiniCard(wild: WildPokemon) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("✅", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                wild.name.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                wild.type,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                "#${wild.number}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.LightGray
            )
        }
    }
}