package morales.jose.basiclocalstore.data

class PokemonRepository( private val pokemonDao: PokemonDao){
    val allPokemon = pokemonDao.getAllPokemon()

    suspend fun add(pokemon : PokemonEntity){
        pokemonDao.add(pokemon)
    }
}