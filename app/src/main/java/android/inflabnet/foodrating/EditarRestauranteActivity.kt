package android.inflabnet.foodrating

import android.content.Intent
import android.inflabnet.foodrating.db.AppDatabase
import android.inflabnet.foodrating.db.AppDatabaseService
import android.inflabnet.foodrating.db.Restaurante
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_editar_restaurante.*
import kotlinx.android.synthetic.main.activity_restaurante.*

class EditarRestauranteActivity : AppCompatActivity() {

    private lateinit var appDatabase : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_restaurante)

        appDatabase = AppDatabaseService.getInstance(this)

        val nomeRestaurante = intent.getStringExtra("nomeRestaurante")
        if(!nomeRestaurante.isNullOrBlank()){
            txtNomeEdtRestaurante.text = nomeRestaurante.toString()

            //BuscarAvaliacao().execute(nomeRestaurante.toString())
        }

        BuscarRestaurante().execute(nomeRestaurante.toString())

        alterarBtn.setOnClickListener {
            val endereco = edtEndereco1.text.toString()
            val tipo = edtTipoComida1.text.toString()
            val avaliacao = ratingRestaurante1.rating
            val restObj = Restaurante(txtNomeEdtRestaurante.text.toString(),endereco,tipo,avaliacao)
            UpdateRestaurante().execute(restObj)
        }

        voltarBtn.setOnClickListener {
            val intt = Intent(this, RestauranteActivity::class.java)
            val restauranteNome = txtNomeEdtRestaurante.text.toString()
            intt.putExtra("nomeRestaurante",restauranteNome)
            startActivity(intt)
        }
    }



    inner class UpdateRestaurante:AsyncTask<Restaurante,Unit,Unit>(){
        override fun doInBackground(vararg params: Restaurante?) {
            val pkRest = appDatabase.restauranteDAO().buscaPK(params[0]!!.nome)
            params[0]!!.id = pkRest
            appDatabase.restauranteDAO().update(params[0]!!)
        }

        override fun onPostExecute(result: Unit?) {
            Toast.makeText(applicationContext,"Atualizado com sucesso", Toast.LENGTH_SHORT).show()
        }
    }

    inner class BuscarRestaurante:AsyncTask<String,Unit,Restaurante>(){
        override fun doInBackground(vararg params: String?): Restaurante {
            var restauranteObj = appDatabase.restauranteDAO().buscaRestaurante(params[0]!!)
            return restauranteObj
        }

        override fun onPostExecute(result: Restaurante?) {
            edtEndereco1.setText(result!!.endereco)
            edtTipoComida1.setText(result!!.tipo)
            ratingRestaurante1.rating = result!!.avaliacao

        }
    }
}
