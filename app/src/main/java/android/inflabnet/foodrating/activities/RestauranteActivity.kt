package android.inflabnet.foodrating.activities

import android.app.AlertDialog
import android.content.Intent
import android.inflabnet.foodrating.R
import android.inflabnet.foodrating.db.init.AppDatabase
import android.inflabnet.foodrating.db.init.AppDatabaseService
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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

        excluirBtn.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Tem certeza que gostaria excluir esse Restaurante?")
                .setCancelable(false)
                .setPositiveButton("Sim"){_, _ ->
                    GetDeleteRestaurante().execute(nomeRestaurante)

                }
                .setNegativeButton("Não") { _, _ ->
                    Toast.makeText(this,"Ok, cancelamento cancelado com sucesso! Rsrsrs",Toast.LENGTH_SHORT).show()
                }
                .setNeutralButton("Cancelar") {_, _ ->
                    Toast.makeText(this,"Operação cancelada",Toast.LENGTH_SHORT).show()
                }
            val alert = dialogBuilder.create()
            alert.setTitle("Excluir restaurante?")
            alert.show()
        }

    }
    inner class GetDeleteRestaurante:AsyncTask<String,Unit,Unit>(){
        override fun doInBackground(vararg params: String?) {
            val restauranteDel = appDatabase.restauranteDAO().buscaRestaurante(params[0]!!)
            appDatabase.refeicaoDAO().deleteRefeicoes(restauranteDel.id)
            appDatabase.restauranteDAO().deleteRestaurante(restauranteDel)

        }

        override fun onPostExecute(result: Unit?) {
            Toast.makeText(this@RestauranteActivity,"Restaurante Deletado!",Toast.LENGTH_SHORT).show()
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
