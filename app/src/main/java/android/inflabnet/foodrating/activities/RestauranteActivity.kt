package android.inflabnet.foodrating.activities

import android.content.Intent
import android.inflabnet.foodrating.R
import android.inflabnet.foodrating.db.init.AppDatabase
import android.inflabnet.foodrating.db.init.AppDatabaseService
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_restaurante.*

class RestauranteActivity : AppCompatActivity() {

    private lateinit var appDatabase : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurante)

        appDatabase = AppDatabaseService.getInstance(this)
        rtBar.setIsIndicator(true)

        val nomeRestaurante = intent.getStringExtra("nomeRestaurante")
        if(!nomeRestaurante.isNullOrBlank()){
            txtXYZ.text = nomeRestaurante.toString()
            Log.i("Rating nome", nomeRestaurante.toString())
            BuscarAvaliacao().execute(nomeRestaurante.toString())
        }

        avaliarBtn.setOnClickListener {
            val intt = Intent(this, RatingRefeicoesActivity::class.java)
            val restauranteNome = txtXYZ.text.toString()
            intt.putExtra("nomeRestaurante",restauranteNome)
            startActivity(intt)
        }

        verBtn.setOnClickListener {
            val intt = Intent(this, VerRatingRefeicoesActivity::class.java)
            val restauranteNome = txtXYZ.text.toString()
            intt.putExtra("nomeRestaurante",restauranteNome)
            startActivity(intt)
        }

        editarRestaurantebtn.setOnClickListener {
            val intt = Intent(this, EditarRestauranteActivity::class.java)
            val restauranteNome = txtXYZ.text.toString()
            intt.putExtra("nomeRestaurante",restauranteNome)
            startActivity(intt)
        }

    }

    inner class BuscarAvaliacao:AsyncTask<String,Unit,Float>(){
        override fun doInBackground(vararg params: String?): Float {
            val avalEstrelas = appDatabase.restauranteDAO().avaliacao(params[0]!!)
            Log.i("Rating", avalEstrelas.toString())
            return avalEstrelas
        }

        override fun onPostExecute(result: Float?) {
            rtBar.rating = result!!.toFloat()
        }
    }
}
