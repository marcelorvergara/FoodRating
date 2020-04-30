package android.inflabnet.foodrating

import android.annotation.SuppressLint
import android.inflabnet.foodrating.db.AppDatabase
import android.inflabnet.foodrating.db.AppDatabaseService
import android.inflabnet.foodrating.db.Restaurante
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_cadastrar.*

class CadastrarActivity : AppCompatActivity() {

    private lateinit var appDatabase : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar)

        appDatabase = AppDatabaseService.getInstance(this)

        val nomeRestaurante = intent.getStringExtra("nomeRestaurante")
        if(!nomeRestaurante.isNullOrBlank()){
            edtNome.setText(nomeRestaurante.toString())
        }

        proximoBtn.setOnClickListener {
            cadastroRestaurante()
        }

        verificarBtn.setOnClickListener {
            showToast()
        }
    }

    private fun showToast() {
        val nomes = PegarRestaurantes().execute().get()
            Toast.makeText(this,nomes.toString(),Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("StaticFieldLeak")
    inner class PegarRestaurantes:AsyncTask<Unit,Unit,Int>(){
        override fun doInBackground(vararg params: Unit?): Int {
            val restaurantesCadastrados = appDatabase.restauranteDAO().buscaQuantos()
            return restaurantesCadastrados
        }
    }

    private fun cadastroRestaurante() {
        val nome = edtNome.text.toString()
        val endereco = edtEndereco.text.toString()
        val tipo = edtTipoComida.text.toString()
        val avaliacao = ratingRestaurante.numStars.toFloat()
        val objRestaurante = Restaurante(nome,endereco,tipo,avaliacao)

        CadastroNoBanco().execute(objRestaurante)
    }

    inner class CadastroNoBanco: AsyncTask<Restaurante,Unit,Unit>(){
        override fun doInBackground(vararg params: Restaurante?) {
            val restauranteAT = params[0]
            appDatabase.restauranteDAO().guardar(restauranteAT)
        }

        override fun onPostExecute(result: Unit?) {
            Toast.makeText(applicationContext,"Cadstro com sucesso!",Toast.LENGTH_SHORT).show()
        }
    }
}
