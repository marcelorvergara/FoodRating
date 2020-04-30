package android.inflabnet.foodrating

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_rating_refeicoes.*

class RatingRefeicoesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating_refeicoes)

        foodView.setOnClickListener {
            var i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(i,200)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 200){
            var bmp = data?.extras?.get("data") as Bitmap

            foodView.setImageBitmap(bmp)

        }
    }
}
