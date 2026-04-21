package morales.jose.basiclocalstore.viewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import morales.jose.basiclocalstore.data.PokemonEntity
import morales.jose.basiclocalstore.data.PokemonRepository

class PokemonViewModel(private val repository: PokemonRepository) : ViewModel() {

    var searchQuery by mutableStateOf("")
    var typeFilter by mutableStateOf("")
    var minLevelFilter by mutableStateOf(1)


    @OptIn(ExperimentalCoroutinesApi::class)
    val pokemonState: StateFlow<List<PokemonEntity>> =
        snapshotFlow { Triple(searchQuery, typeFilter, minLevelFilter) }
            .flatMapLatest { (search, type, level) ->
                repository.getFilteredPokemons(search, type, level)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun levelUp(pokemon: PokemonEntity) {
        if (pokemon.level < 100) {
            val exito = (1..100).random() > 30 // 70% probabilidad de éxito
            if (exito) {
                viewModelScope.launch {
                    repository.update(pokemon.copy(level = pokemon.level + 1))
                }
            }
        }
    }

    fun releasePokemon(pokemon: PokemonEntity) {
        viewModelScope.launch { repository.delete(pokemon) }
    }

    fun addPokemon(name: String, number: String, type: String, level: Int = 1) {
        viewModelScope.launch {
            repository.add(PokemonEntity(name = name, number = number, type = type, level = level))
        }
    }
    private val avaliblePokemon = listOf(
        PokemonEntity(name = "piplup", number = "393", type = "water"),
        PokemonEntity(name = "chikorita", number = "152", type = "planta"),
        PokemonEntity(name = "sprigatito", number = "906", type = "planta"),
        PokemonEntity(name = "lugia", number = "249", type = "psiquico")
    )

    var wildPokemon by mutableStateOf<PokemonEntity?>(null)
        private set
    var capturedPokemon by mutableStateOf(listOf<PokemonEntity>())
        private set
    var pokemonEscapo by mutableStateOf(false)
        private set

    fun searchPokemon() {
        wildPokemon = avaliblePokemon.random()
        pokemonEscapo = false
    }

    fun capturePokemon() {
        wildPokemon?.let {
            if ((1..100).random() > 50) {
                capturedPokemon = capturedPokemon + it
                pokemonEscapo = false
                wildPokemon = null
            } else {
                pokemonEscapo = true
                wildPokemon = null
            }
        }
    }

    fun releaseCapturedPokemon() {
        capturedPokemon = emptyList()
    }
}