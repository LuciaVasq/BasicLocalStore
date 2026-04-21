package morales.jose.basiclocalstore.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import morales.jose.basiclocalstore.data.PokemonEntity
import morales.jose.basiclocalstore.data.PokemonRepository

class PokemonViewModel(private val repository: PokemonRepository): ViewModel() {

    private val avaliblePokemon = listOf(
        PokemonEntity(name = "piplup", number="393", type="water"),
        PokemonEntity(name = "chikorita", number="152", type="planta"),
        PokemonEntity(name = "sprigatito", number="906", type="planta"),
        PokemonEntity(name = "lugia", number="249", type="psiquico")
    )
    var wildPokemon by mutableStateOf<PokemonEntity?>(null)
        private set
    var capturedPokemon by mutableStateOf(listOf<PokemonEntity>())
        private set

    var pokemonEscapo by mutableStateOf(false )
    private set

    fun searchPokemon(){
        wildPokemon= avaliblePokemon.random()
    }

    fun releaseCapturedPokemon(){
        capturedPokemon= emptyList()
    }

    fun capturePokemon(){
        wildPokemon?.let{
            val success = (1..100).random()
            if(success > 50){
                capturedPokemon= capturedPokemon+ it
                pokemonEscapo = false
                wildPokemon= null
            }else
            {
                pokemonEscapo= true
                wildPokemon= null
            }
        }
    }

    val pokemonState : StateFlow<List<PokemonEntity>> = repository.allPokemon
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    fun addPokemon (name :String, number: String, type: String, level: Int =1){
        viewModelScope.launch {
            repository.add(
                PokemonEntity(
                    name= name,
                    number = number,
                    type = type,
                    level = level
                )

            )
        }
    }
}