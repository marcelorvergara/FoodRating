package android.inflabnet.foodrating.db

import android.content.Context
import androidx.room.Room

//classe singleton
class AppDatabaseService {
    companion object{
        var instance : AppDatabase? = null
        private const val database_name = "appDatabase2.sql"
        fun getInstance(context: Context): AppDatabase {
            if(instance ==null){
                instance = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    database_name
                ).build()
                //).fallbackToDestructiveMigration().build()
            }
            return instance as AppDatabase
        }
    }
}