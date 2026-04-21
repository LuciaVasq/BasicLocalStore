package morales.jose.basiclocalstore.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import morales.jose.basiclocalstore.viewModel.PokemonViewModel
@Composable
fun CapturarScreen(pokemonViewModel: PokemonViewModel, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color(0xFFE0F2F1), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (pokemonViewModel.wildPokemon != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Face, modifier = Modifier.size(80.dp), contentDescription = null)
                    Text(
                        pokemonViewModel.wildPokemon?.name?.uppercase() ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            } else {
                Text("La hierba está tranquila...", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { pokemonViewModel.searchPokemon() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("BUSCAR EN LA HIERBA")
        }

        if (pokemonViewModel.wildPokemon != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { pokemonViewModel.capturePokemon() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCC0000))
            ) {
                Text("¡LANZAR POKÉBALL!")
            }
        }

        if (pokemonViewModel.pokemonEscapo) {
            Text("¡Oh no! Se ha escapado...", color = Color.Red, modifier = Modifier.padding(8.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón para guardar todo en la base de datos (CRUD: Create)
        if (pokemonViewModel.capturedPokemon.isNotEmpty()) {
            ElevatedButton(
                onClick = {
                    pokemonViewModel.capturedPokemon.forEach {
                        pokemonViewModel.addPokemon(it.name, it.number, it.type)
                    }
                    pokemonViewModel.releaseCapturedPokemon()
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("GUARDAR ${pokemonViewModel.capturedPokemon.size} EN LA BOLSA")
            }
        }
    }
}