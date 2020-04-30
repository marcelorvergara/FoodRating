package android.inflabnet.foodrating

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_rating_refeicoes.*
import kotlinx.android.synthetic.main.activity_restaurante.*

class RestauranteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurante)

        val nomeRestaurante = intent.getStringExtra("nomeRestaurante")
        if(!nomeRestaurante.isNullOrBlank()){
            txtXYZ.text = nomeRestaurante.toString()
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
            val novoIntt = Intent(this, CadastrarActivity::class.java)
            startActivity(novoIntt)
        }

    }
}
