package morales.jose.basiclocalstore.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao{
    @Query("SELECT * FROM pokemon_table ORDER BY number ASC")
    fun getAllPokemon() : Flow<List<PokemonEntity>>

    @Query("""
        SELECT * FROM pokemon_table 
        WHERE (name LIKE :searchQuery OR type LIKE :searchQuery)
        AND (:filterType = '' OR type = :filterType)
        AND level >= :minLevel
        ORDER BY number ASC
    """)
    fun getFilteredPokemons(
        searchQuery: String,
        filterType: String,
        minLevel: Int
    ): Flow<List<PokemonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(pokemon: PokemonEntity)

    @Update
    suspend fun update(pokemon: PokemonEntity)

    @Delete
    suspend fun delete(pokemon: PokemonEntity)
}