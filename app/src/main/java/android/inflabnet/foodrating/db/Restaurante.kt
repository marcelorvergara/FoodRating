package android.inflabnet.foodrating.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Restaurante(var nome: String,
                  var endereco: String,
                  var tipo: String,
                  var avaliacao: Float,
                  @PrimaryKey(autoGenerate = true)
                  var id: Int? = null
)