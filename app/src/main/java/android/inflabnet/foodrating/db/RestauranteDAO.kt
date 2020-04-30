package android.inflabnet.foodrating.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RestauranteDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardar(restauranteAT: Restaurante?)

    @Query("Select nome from Restaurante")
    fun buscaNomes(): Array<String>

    @Query("Select count(*) from Restaurante")
    fun buscaQuantos(): Int

}