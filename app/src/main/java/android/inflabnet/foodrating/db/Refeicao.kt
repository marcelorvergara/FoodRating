package android.inflabnet.foodrating.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Refeicao (
    var nome: String,
    var resumo: String,
    var avaliacao: String,
    var data: String,
    var foto: String,
    var id_restaurante: Int,
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
)