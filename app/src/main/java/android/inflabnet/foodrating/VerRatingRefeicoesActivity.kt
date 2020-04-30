package android.inflabnet.foodrating

import android.graphics.BitmapFactory
import android.inflabnet.foodrating.db.AppDatabase
import android.inflabnet.foodrating.db.AppDatabaseService
import android.inflabnet.foodrating.db.Refeicao
import android.inflabnet.foodrating.db.RestauranteERefeicao
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_ver_rating_refeicoes.*
import kotlinx.android.synthetic.main.item_refeicao.view.*

class VerRatingRefeicoesActivity : AppCompatActivity() {

    private lateinit var appDatabase : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_rating_refeicoes)

        appDatabase = AppDatabaseService.getInstance(this)

        val nomeRestaurante = intent.getStringExtra("nomeRestaurante")
        if(!nomeRestaurante.isNullOrBlank()){
            txtNomeRestauranteAvaliacao3.append(nomeRestaurante.toString())
        }
        //buscar a PK de acordo com o nome
        val nomeRestauranteParaPK = txtNomeRestauranteAvaliacao3.text.toString().replace("Restaurante:","")
        val pkRestauranteConsultaRefeicoes = BuscaPK().execute(nomeRestauranteParaPK).get()

        //buscar refeicoes-avaiacoes com pk, pois transaction não funciona
        InitRefeicoes().execute(pkRestauranteConsultaRefeicoes).get()


        //val tot = InitRefeicoes().execute(pkRestauranteParaConsulta).get()

    }
    inner class GetUri:AsyncTask<String,Unit,String>(){
        override fun doInBackground(vararg params: String?): String {
            val uri = appDatabase.refeicaoDAO().getUri(params[0]!!)
            return uri
        }
    }

    inner class BuscaPK:AsyncTask<String,Unit,Int>(){
        override fun doInBackground(vararg params: String?): Int {
            val pk = appDatabase.restauranteDAO().buscaPK(params[0]!!)
            return pk
        }
    }

    inner class InitRefeicoes:AsyncTask<Int,Unit,List<Refeicao>>(){
        override fun doInBackground(vararg params: Int?): List<Refeicao> {
            val listaRefeicoes = appDatabase.restauranteDAO().buscaRefeicoes2(params[0]!!)

            return listaRefeicoes
        }

        override fun onPostExecute(result: List<Refeicao>?) {
            val linearLayoutManager = LinearLayoutManager(applicationContext)
            rvListaRefeicoes.layoutManager = linearLayoutManager
            rvListaRefeicoes.scrollToPosition(result!!.size - 1)
            var con = applicationContext
            rvListaRefeicoes.adapter = RefeicoesAdapter(this@VerRatingRefeicoesActivity,result){
                Toast.makeText(applicationContext,"Avaliação",Toast.LENGTH_SHORT).show()
            }
        }

    }


}
