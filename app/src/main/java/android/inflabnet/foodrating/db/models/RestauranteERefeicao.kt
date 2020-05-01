package android.inflabnet.foodrating.db.models

import android.inflabnet.foodrating.db.models.Refeicao
import android.inflabnet.foodrating.db.models.Restaurante
import androidx.room.Embedded
import androidx.room.Relation

data class RestauranteERefeicao (
    @Embedded val restaurante: Restaurante,
    @Relation(
        parentColumn = "id", //id na tabela orçamento
        entityColumn = "id_restaurante" //id de orçamento na tabela MesaOrc
    ) val refeicao: List<Refeicao>
)
