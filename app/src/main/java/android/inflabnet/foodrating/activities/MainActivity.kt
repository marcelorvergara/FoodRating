package android.inflabnet.foodrating.activities

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.inflabnet.foodrating.R
import android.inflabnet.foodrating.db.init.AppDatabase
import android.inflabnet.foodrating.db.init.AppDatabaseService
import android.inflabnet.foodrating.db.models.Restaurante
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appDatabase : AppDatabase

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        val animDrawable = rootLayout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        //abre conexão com o banco
        appDatabase = AppDatabaseService.getInstance(this)

        alterarBtn.setOnClickListener {
            cadastroTela()
        }

        setupAutoCompleteRestaurantes()
        setupListRestaurantes().execute().get()

    }

    inner class setupListRestaurantes:AsyncTask<Unit,Unit,List<Restaurante>>(){
        override fun doInBackground(vararg params: Unit?):List<Restaurante> {
            val restaurantes =  appDatabase.restauranteDAO().buscaTudo()
            return restaurantes
        }

        override fun onPostExecute(result: List<Restaurante>?) {
            val linearLayoutManager = LinearLayoutManager(applicationContext)
            rcRecycleV.layoutManager = linearLayoutManager
            rcRecycleV.scrollToPosition(result!!.size)
            rcRecycleV.adapter = RestauranteAdapter(result) {
                val selectedItem= it
                val intt = Intent(this@MainActivity, RestauranteActivity::class.java)
                val restauranteNome = selectedItem.nome
                intt.putExtra("nomeRestaurante",restauranteNome)
                startActivity(intt)
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun setupAutoCompleteRestaurantes() {
        val nomes = PegarRestaurantes().execute().get()

        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,nomes)
        aCTVRestaurantes.setAdapter(adapter)
        aCTVRestaurantes.threshold = 1
        aCTVRestaurantes.text.toString()

        aCTVRestaurantes.onItemClickListener = AdapterView.OnItemClickListener {
            parent, view,position, id ->
            val selectedItem= parent.getItemAtPosition(position)
            val intt = Intent(this, RestauranteActivity::class.java)
            val restauranteNome = selectedItem.toString()
            intt.putExtra("nomeRestaurante",restauranteNome)
            startActivity(intt)
        }
        aCTVRestaurantes.setOnDismissListener {
            //Toast.makeText(applicationContext, "Sugestões fechadas", Toast.LENGTH_SHORT).show()
        }
        rootLayout.setOnClickListener {
            val text =aCTVRestaurantes.text
            Toast.makeText(applicationContext, "Aplicação teste", Toast.LENGTH_SHORT).show()
        }
        aCTVRestaurantes.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (b) {
                // Sugestões dropdown
                aCTVRestaurantes.showDropDown()
            }
        }
    }

    inner class PegarRestaurantes:AsyncTask<Unit,Unit,Array<String>>(){
        override fun doInBackground(vararg params: Unit?): Array<String> {
            val nomes = appDatabase.restauranteDAO().buscaNomes()
            return nomes
        }
    }

    private fun cadastroTela() {
        if (edtNovoRestaurante.text != null){
            val nomeNovo = edtNovoRestaurante.text.toString()
            val novoIntt = Intent(this, CadastrarActivity::class.java)
            novoIntt.putExtra("nomeRestaurante", nomeNovo)
            startActivity(novoIntt)
        }else{
            val novoIntt = Intent(this, CadastrarActivity::class.java)
            startActivity(novoIntt)
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onResume() {
        super.onResume()
        setupAutoCompleteRestaurantes()
    }
}
