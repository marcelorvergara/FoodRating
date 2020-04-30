package android.inflabnet.foodrating

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_restaurante.*

class RestauranteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurante)

        val nomeRestaurante = intent.getStringExtra("nomeRestaurante")
        if(!nomeRestaurante.isNullOrBlank()){
            txtXYZ.setText(nomeRestaurante.toString())
        }

        avaliarBtn.setOnClickListener {
            val novoIntt = Intent(this, RatingRefeicoesActivity::class.java)
            startActivity(novoIntt)
        }

    }
}
