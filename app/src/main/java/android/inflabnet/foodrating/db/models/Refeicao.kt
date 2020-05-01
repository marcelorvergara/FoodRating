package android.inflabnet.foodrating.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Refeicao (
    var nome: String,
    var ingredientes: String,
    var avaliacao: String,
    var data: String,
    var foto: String,
    var nota: Int,
    var id_restaurante: Int,
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
)