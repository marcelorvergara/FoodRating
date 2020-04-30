package android.inflabnet.foodrating.db

import android.inflabnet.foodrating.db.Refeicao
import android.inflabnet.foodrating.db.RefeicaoDAO
import android.inflabnet.foodrating.db.Restaurante
import android.inflabnet.foodrating.db.RestauranteDAO
import androidx.room.Database
import androidx.room.RoomDatabase

//anotação com relação de entidades(tabelas) que compõe a base
@Database(
    entities = arrayOf(
        Restaurante::class,
        Refeicao::class
    ),
    //para notificar mudanças da base de dados do dispositivo
    version = 2
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun restauranteDAO(): RestauranteDAO
    abstract fun refeicaoDAO(): RefeicaoDAO

}