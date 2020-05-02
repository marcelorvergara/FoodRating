package android.inflabnet.foodrating.db.dao

import android.inflabnet.foodrating.db.models.Refeicao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

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

    @Query("Delete from Refeicao where id_restaurante = :id")
    fun deleteRefeicoes(id: Int?)

    @Update
    fun update(refeicao: Refeicao)

}