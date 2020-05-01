package android.inflabnet.foodrating.db

import androidx.room.*

@Dao
interface RestauranteDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardar(restauranteAT: Restaurante?)

    @Query("Select nome from Restaurante")
    fun buscaNomes(): Array<String>

    @Query("Select count(*) from Restaurante")
    fun buscaQuantos(): Int

    @Query("Select id from Restaurante where nome = :nome")
    fun buscaPK(nome: String): Int

    @Transaction
    @Query("Select * from Restaurante  where nome = :nome")
    fun buscaRefeicoes(nome: String): List<RestauranteERefeicao>

    @Query("Select * from Refeicao where id_restaurante = :i")
    fun buscaRefeicoes2(i: Int): List<Refeicao>

    @Query("Select avaliacao from Restaurante where nome = :s")
    fun avaliacao(s: String): Float

    @Query("Select * from Restaurante where nome = :s")
    fun buscaRestaurante(s: String): Restaurante

    @Update
    fun update(s: Restaurante)

}