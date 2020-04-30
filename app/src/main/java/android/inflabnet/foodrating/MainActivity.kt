package android.inflabnet.foodrating

import android.content.Intent
import android.inflabnet.foodrating.db.AppDatabase
import android.inflabnet.foodrating.db.AppDatabaseService
import android.inflabnet.foodrating.db.Restaurante
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var appDatabase : AppDatabase

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //abre conexão com o banco
        appDatabase = AppDatabaseService.getInstance(this)

        proximoBtn.setOnClickListener {
            cadastroTela()
        }

        setupAutoCompleteRestaurantes()
    }


    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun setupAutoCompleteRestaurantes() {
        val nomes = PegarRestaurantes().execute().get()

        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,nomes)
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
