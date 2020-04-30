package android.inflabnet.foodrating.db

import androidx.room.Embedded
import androidx.room.Relation

class RestauranteERefeicao (
    @Embedded val restaurante: Restaurante,
    @Relation(
        parentColumn = "id", //id na tabela orçamento
        entityColumn = "id_restaurante" //id de orçamento na tabela MesaOrc
    ) val refeicao: Refeicao
)