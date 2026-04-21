package morales.jose.basiclocalstore.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import morales.jose.basiclocalstore.viewModel.PokemonViewModel

@Composable
fun BolsaScreen(pokemonViewModel: PokemonViewModel){
    val pokemons by pokemonViewModel.pokemonState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(5.dp, 12.dp)
        ) {
            items(pokemons){ pokemon ->
                Card (
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ){
                    ListItem( headlineContent = { Text(pokemon.name) },
                        supportingContent = {Text(pokemon.type)})
                }

            }
        }
    }}