package morales.jose.basiclocalstore.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import morales.jose.basiclocalstore.viewModel.PokemonViewModel

@Composable
fun CapturarScreen(pokemonViewModel: PokemonViewModel, onBack:()-> Unit){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {pokemonViewModel.searchPokemon()}
        ) {
            Text("Buscar en la hierva")
        }
        Spacer(modifier = Modifier.height(16.dp))
        pokemonViewModel.wildPokemon?.let {pokemon ->
            Text("aparecio un ${pokemon.name}" )
        }
        Button(onClick = {pokemonViewModel.capturePokemon()}) {
            Text("capturar")
        }
    }
    if(pokemonViewModel.pokemonEscapo){
        Text("el pokemon se escapo", color = Color.Red)
    }
    Spacer(modifier = Modifier.height(28.dp))

    Text("POkemons capturados: ${pokemonViewModel.capturedPokemon.size}")

    LazyColumn() {
        items(pokemonViewModel.capturedPokemon){
            pokemon ->
            Text("${pokemon.name} - ${pokemon.type}")
        }
    }
    Spacer(modifier = Modifier.height(28.dp))
    ElevatedButton(
        onClick = {
          for( pokemon in pokemonViewModel.capturedPokemon){
              pokemonViewModel.addPokemon(pokemon.name,pokemon.number,pokemon.type)
          }
            onBack()
        }
    ) {
        Text("Mandar a la bolsa")
    }
    TextButton(onClick = {pokemonViewModel.releaseCapturedPokemon()}) {
        Text("Liberar pokemons")
    }
}