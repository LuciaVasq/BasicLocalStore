package morales.jose.basiclocalstore.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import morales.jose.basiclocalstore.data.PokemonEntity
import morales.jose.basiclocalstore.data.PokemonRepository
import kotlin.random.Random

data class WildPokemon(val name: String, val number: String, val type: String)

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonViewModel(private val repository: PokemonRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _typeFilter  = MutableStateFlow("")
    private val _minLevel    = MutableStateFlow(1)

    var searchQuery: String
        get() = _searchQuery.value
        set(v) { _searchQuery.value = v }

    var typeFilter: String
        get() = _typeFilter.value
        set(v) { _typeFilter.value = v }

    var minLevelFilter: Int
        get() = _minLevel.value
        set(v) { _minLevel.value = if (v < 1) 1 else v }

    val pokemonState: StateFlow<List<PokemonEntity>> =
        combine(_searchQuery, _typeFilter, _minLevel) { search, type, minLvl ->
            Triple(search.trim(), type.trim(), minLvl)
        }
            .flatMapLatest { (search, type, minLvl) ->
                repository.getFilteredPokemons(search, type, minLvl)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    var levelUpMessage by mutableStateOf<String?>(null)
        private set

    fun clearLevelUpMessage() { levelUpMessage = null }
    var wildPokemon     by mutableStateOf<WildPokemon?>(null);           private set
    var pokemonEscapo   by mutableStateOf(false);                        private set
    var capturedPokemon by mutableStateOf<List<WildPokemon>>(emptyList()); private set

    fun addPokemon(name: String, number: String, type: String) {
        viewModelScope.launch {
            repository.add(PokemonEntity(name = name, number = number, type = type, level = 1))
        }
    }

    fun levelUp(pokemon: PokemonEntity) {
        if (pokemon.level >= 100) {
            levelUpMessage =
                "${pokemon.name.replaceFirstChar { it.uppercase() }} ya está en el nivel máximo (100). ¡Es una leyenda!"
            return
        }
        val success = Random.nextFloat() < 0.70f
        if (success) {
            val newLevel = pokemon.level + 1
            viewModelScope.launch { repository.update(pokemon.copy(level = newLevel)) }
            levelUpMessage =
                "¡${pokemon.name.replaceFirstChar { it.uppercase() }} subió al nivel $newLevel! ✨"
        } else {
            levelUpMessage = "El entrenamiento falló esta vez... ¡Sigue intentándolo!"
        }
    }

    fun releasePokemon(pokemon: PokemonEntity) {
        viewModelScope.launch { repository.delete(pokemon) }
    }

    private val wildPool = listOf(
        WildPokemon("bulbasaur",  "001", "Planta"),
        WildPokemon("charmander", "004", "Fuego"),
        WildPokemon("squirtle",   "007", "Agua"),
        WildPokemon("pikachu",    "025", "Eléctrico"),
        WildPokemon("mewtwo",     "150", "Psíquico"),
        WildPokemon("gengar",     "094", "Fantasma"),
        WildPokemon("snorlax",    "143", "Normal"),
        WildPokemon("machamp",    "068", "Lucha"),
        WildPokemon("geodude",    "074", "Roca"),
        WildPokemon("haunter",    "093", "Fantasma"),
        WildPokemon("eevee",      "133", "Normal"),
        WildPokemon("lapras",     "131", "Agua"),
        WildPokemon("dragonite",  "149", "Dragón"),
        WildPokemon("jolteon",    "135", "Eléctrico"),
        WildPokemon("arcanine",   "059", "Fuego"),
    )

    fun searchPokemon() {
        pokemonEscapo = false
        wildPokemon = wildPool.random()
    }

    fun capturePokemon() {
        val pokemon = wildPokemon ?: return
        val caught = Random.nextFloat() < 0.65f
        if (caught) {
            capturedPokemon = capturedPokemon + pokemon
            wildPokemon = null
            pokemonEscapo = false
        } else {
            pokemonEscapo = true
            wildPokemon = null
        }
    }

    fun releaseCapturedPokemon() { capturedPokemon = emptyList() }
}