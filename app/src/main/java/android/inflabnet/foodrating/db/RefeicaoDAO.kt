package android.inflabnet.foodrating.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface RefeicaoDAO {

    @Insert
    fun guardar(refeicao: Refeicao?)

    @Query("Select count(*) from Refeicao")
    fun selectAll(): Int

    @Query("Select * from Refeicao where nome = :nome")
    fun buscaRefeicoes(nome: String): List<Refeicao>

    @Query("Select foto from Refeicao where nome = :nome")
    fun getUri(nome: String): String

}