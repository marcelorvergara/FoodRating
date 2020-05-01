package android.inflabnet.foodrating

import android.annotation.SuppressLint
import android.content.Intent
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

        alterarBtn.setOnClickListener {
            cadastroRestaurante()
        }


        voltarBtn.setOnClickListener {
            val novoIntt = Intent(this, MainActivity::class.java)
            startActivity(novoIntt)
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
        val endereco = edtEndereco1.text.toString()
        val tipo = edtTipoComida1.text.toString()
        val avaliacao = ratingRestaurante1.rating
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
            edtNome.setText("")
            edtEndereco1.setText("")
            edtTipoComida1.setText("")
            ratingRestaurante1.rating = 0.0f
        }
    }
}
