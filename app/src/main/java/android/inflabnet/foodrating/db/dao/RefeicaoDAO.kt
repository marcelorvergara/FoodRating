package android.inflabnet.foodrating.db.dao

import android.inflabnet.foodrating.db.models.Refeicao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RefeicaoDAO {

    @Insert
    fun guardar(refeicao: Refeicao?)

    @Query("Select * from Refeicao")
    fun selectAll(): List<Refeicao>

    @Query("Select * from Refeicao where id = :id_restaurante")
    fun buscaRefeicoes(id_restaurante: Int): List<Refeicao>

    @Query("Select foto from Refeicao where nome = :nome")
    fun getUri(nome: String): String

}