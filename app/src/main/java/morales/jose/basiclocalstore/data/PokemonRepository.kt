package morales.jose.basiclocalstore.data

class PokemonRepository(private val pokemonDao: PokemonDao) {
    fun getFilteredPokemons(search: String, type: String, minLevel: Int) =
        pokemonDao.getFilteredPokemons("%$search%", type, minLevel)

    suspend fun update(pokemon: PokemonEntity) = pokemonDao.update(pokemon)
    suspend fun delete(pokemon: PokemonEntity) = pokemonDao.delete(pokemon)
    suspend fun add(pokemon: PokemonEntity) = pokemonDao.add(pokemon)
}