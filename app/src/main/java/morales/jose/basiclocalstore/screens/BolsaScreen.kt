package morales.jose.basiclocalstore.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import morales.jose.basiclocalstore.data.PokemonEntity
import morales.jose.basiclocalstore.viewModel.PokemonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BolsaScreen(pokemonViewModel: PokemonViewModel) {
    // Observamos la lista filtrada desde el ViewModel
    val pokemons by pokemonViewModel.pokemonState.collectAsStateWithLifecycle()

    // Estado para el diálogo de eliminación
    var pokemonToDelete by remember { mutableStateOf<PokemonEntity?>(null) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color(0xFFEC6161))
                    .padding(16.dp)
                    .statusBarsPadding()
            ) {
                Text(
                    text = "MI POKÉBOLSA",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                // busqueda
                TextField(
                    value = pokemonViewModel.searchQuery,
                    onValueChange = { pokemonViewModel.searchQuery = it },
                    placeholder = { Text("Buscar por nombre o tipo...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
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
                .padding(16.dp)
        ) {
            // filtros
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = pokemonViewModel.typeFilter,
                    onValueChange = { pokemonViewModel.typeFilter = it },
                    label = { Text("Filtrar Tipo") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
                OutlinedTextField(
                    value = if (pokemonViewModel.minLevelFilter <= 1) "" else pokemonViewModel.minLevelFilter.toString(),
                    onValueChange = {
                        pokemonViewModel.minLevelFilter = it.toIntOrNull() ?: 1
                    },
                    label = { Text("Nivel Mín.") },
                    modifier = Modifier.weight(0.8f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // lista
            if (pokemons.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron Pokémones", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(pokemons) { pokemon ->
                        PokemonCardItem(
                            pokemon = pokemon,
                            onLevelUp = { pokemonViewModel.levelUp(pokemon) },
                            onDelete = { pokemonToDelete = pokemon }
                        )
                    }
                }
            }
        }
    }

    // dialogos de confirmacio
    pokemonToDelete?.let { pokemon ->
        AlertDialog(
            onDismissRequest = { pokemonToDelete = null },
            title = { Text("Liberar Pokémon") },
            text = { Text("¿Estás seguro de que quieres liberar a ${pokemon.name.uppercase()}? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        pokemonViewModel.releasePokemon(pokemon)
                        pokemonToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Liberar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { pokemonToDelete = null }) {
                    Text("Cancelar")
                }
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = pokemon.name.replaceFirstChar { it.uppercase() },
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingContent = {
                Column {
                    Text("Tipo: ${pokemon.type.lowercase()}", color = Color.DarkGray)
                    Text("Nivel: ${pokemon.level}", fontWeight = FontWeight.Medium, color = Color(0xFF3B4CCA))
                }
            },
            leadingContent = {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF2F2F2)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("#${pokemon.number}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            trailingContent = {
                Row {
                    IconButton(onClick = onLevelUp) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = "Subir Nivel",
                            tint = Color(0xFF4CAF50)
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar",
                            tint = Color.Red
                        )
                    }
                }
            }
        )
    }
}