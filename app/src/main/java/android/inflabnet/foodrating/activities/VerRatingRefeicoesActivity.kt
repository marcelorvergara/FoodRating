package android.inflabnet.foodrating.activities

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.inflabnet.foodrating.R
import android.inflabnet.foodrating.db.init.AppDatabase
import android.inflabnet.foodrating.db.init.AppDatabaseService
import android.inflabnet.foodrating.db.models.Refeicao
import android.inflabnet.foodrating.db.models.RestauranteERefeicao
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_ver_rating_refeicoes.*
import kotlinx.android.synthetic.main.activity_ver_rating_refeicoes.rootLayout
import java.sql.Ref

class VerRatingRefeicoesActivity : AppCompatActivity() {

    private lateinit var appDatabase : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_rating_refeicoes)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        val animDrawable = rootLayout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        appDatabase = AppDatabaseService.getInstance(this)

        val nomeRestaurante = intent.getStringExtra("nomeRestaurante")
        if(!nomeRestaurante.isNullOrBlank()){
            txtNomeRestauranteAvaliacao3.append(nomeRestaurante.toString())
        }

        //buscar refeições com transaction retorna List<RestauranteERefeicao>
        BuscaRefecoes().execute(nomeRestaurante.toString())

        btnVoltarRest.setOnClickListener {
            val intt = Intent(this@VerRatingRefeicoesActivity, RestauranteActivity::class.java)
            intt.putExtra("nomeRestaurante",nomeRestaurante)
            startActivity(intt)
        }
    }


    //uso do trasnsaction ok
    inner class BuscaRefecoes:AsyncTask<String,Unit,List<RestauranteERefeicao>>(){
        override fun doInBackground(vararg params: String?): List<RestauranteERefeicao> {
            val pkRestaurante = appDatabase.restauranteDAO().buscaPK(params[0]!!)

            val refeicaoRest = appDatabase.restauranteDAO().buscaGeral(pkRestaurante)
            return refeicaoRest
        }

        override fun onPostExecute(result: List<RestauranteERefeicao>?) {
            val toReturn: ArrayList<Refeicao> = ArrayList()
            result!!.forEach {
                it.refeicao.forEach {
                    toReturn.add(it)
                }
            }
            val context = applicationContext
            val linearLayoutManager = LinearLayoutManager(applicationContext)
            rvListaRefeicoes.layoutManager = linearLayoutManager
            rvListaRefeicoes.scrollToPosition(result.size - 1)
            rvListaRefeicoes.adapter = RefeicoesAdapter(context,toReturn) {
                val selectedItem= it
                val intt = Intent(this@VerRatingRefeicoesActivity, EditRatingRefeicoesActivity::class.java)
                intt.putExtra("nomeRefeicao",selectedItem)
                startActivity(intt)
            }

        }
    }
}
