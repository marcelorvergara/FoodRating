package android.inflabnet.foodrating.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.inflabnet.foodrating.R
import android.inflabnet.foodrating.db.init.AppDatabase
import android.inflabnet.foodrating.db.init.AppDatabaseService
import android.inflabnet.foodrating.db.models.Refeicao
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_edit_rating_refeicoes.*
import kotlinx.android.synthetic.main.activity_edit_rating_refeicoes.edtAvaliacao
import kotlinx.android.synthetic.main.activity_edit_rating_refeicoes.edtDataAvaliacao
import kotlinx.android.synthetic.main.activity_edit_rating_refeicoes.edtIngredientes
import kotlinx.android.synthetic.main.activity_edit_rating_refeicoes.edtNomePrato
import kotlinx.android.synthetic.main.activity_edit_rating_refeicoes.foodView
import kotlinx.android.synthetic.main.activity_edit_rating_refeicoes.seekBar
import kotlinx.android.synthetic.main.activity_edit_rating_refeicoes.txtNomeRestauranteAvaliacao
import kotlinx.android.synthetic.main.activity_edit_rating_refeicoes.txtNota
import kotlinx.android.synthetic.main.activity_rating_refeicoes.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditRatingRefeicoesActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private lateinit var appDatabase : AppDatabase
    //foto
    private var photoFile: File? = null
    private val CAPTURE_IMAGE_REQUEST = 1
    private lateinit var mCurrentPhotoPath: String
    private val IMAGE_DIRECTORY_NAME = "RATINGFOOD"
    //nome restaurante
    lateinit var nomeRestauranteRefeicao : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_rating_refeicoes)

        appDatabase = AppDatabaseService.getInstance(this)

        //se veio do RecycleView com refeicoes
        val refeicaoRv: Refeicao? = intent.getSerializableExtra("nomeRefeicao") as Refeicao
        if (refeicaoRv != null) {
            if(!refeicaoRv.nome.isBlank()){
                nomeRestauranteRefeicao = GetNomeRestauranteFromId().execute(refeicaoRv.id_restaurante).get()
                //colocando o nome
                txtNomeRestauranteAvaliacao.append("\n${nomeRestauranteRefeicao}")
                //colocando a foto
                val firstChar = refeicaoRv.foto.substring(0,1)
                if(firstChar.equals("@")){
                    foodView.setImageDrawable(
                        ContextCompat.getDrawable(
                            this,
                            R.drawable.ic_block_black_24dp
                        ))
                }else {
                    foodView.setImageBitmap(BitmapFactory.decodeFile(refeicaoRv.foto))
                }
                //colocando o nome do prato
                edtNomePrato.setText(refeicaoRv.nome)
                //colocando os ingredientes
                edtIngredientes.setText(refeicaoRv.ingredientes)
                //colocando a avaliacao
                edtAvaliacao.setText(refeicaoRv.avaliacao)
                //colocando a fata
                edtDataAvaliacao.setText(refeicaoRv.data)
                //coloca a nota
                txtNota.text = refeicaoRv.nota.toString()
                seekBar.progress = refeicaoRv.nota

                txtId.text = refeicaoRv.id.toString()
                txtIdRestaurante.text = refeicaoRv.id_restaurante.toString()

            }
        }

        this.seekBar!!.setOnSeekBarChangeListener(this)

        foodView.setOnClickListener{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                captureImage()
            } else {
                captureImage2()
            }
        }
        //calendário
        val c = Calendar.getInstance()
        val ano = c.get(Calendar.YEAR)
        val mes = c.get(Calendar.MONTH)
        val dia = c.get(Calendar.DAY_OF_MONTH)

        edtDataAvaliacao.setOnFocusChangeListener { _, _ ->
            val dpDialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                //mostrar no edt view
                val newMonth = month.toInt() + 1
                val dataStr = "$dayOfMonth/$newMonth/$year"
                edtDataAvaliacao.setText(dataStr)
            }, ano,mes,dia)
            dpDialog.show()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(edtDataAvaliacao.windowToken, 0)
        }

        voltarrrrBtn.setOnClickListener{
            val intt = Intent(this@EditRatingRefeicoesActivity, VerRatingRefeicoesActivity::class.java)
            intt.putExtra("nomeRestaurante",nomeRestauranteRefeicao)
            startActivity(intt)

        }

        //guardar no banco
        update2Btn.setOnClickListener {
            if (edtNomePrato.text.isNullOrBlank() || edtIngredientes.text.isNullOrBlank()|| edtAvaliacao.text.isNullOrBlank() || edtDataAvaliacao.text.isNullOrBlank()
                || txtNota.text == "nota"){
                displayMessage(this,"Favor preencher todos os campos")
            }else{
                val nomePrato = edtNomePrato.text.toString()
                val ingredientesPrato = edtIngredientes.text.toString()
                val avaliacaoPrato = edtAvaliacao.text.toString()
                val dataAvaliacaoPrato = edtDataAvaliacao.text.toString()
                val notaPrato = txtNota.text.toString().toInt()
                var fotoPath:String?=null
                //checar se o user tirou uma foto
                if(this::mCurrentPhotoPath.isInitialized){
                    fotoPath = mCurrentPhotoPath
                }else{
                    fotoPath = refeicaoRv!!.foto
                }
                //pegar PK do restaurante
                val pkRestauranteParaInsert = txtIdRestaurante.text.toString().toInt()
                //guardar a avaliação no db
                val refeicaoObj =
                    Refeicao(
                        nomePrato,
                        ingredientesPrato,
                        avaliacaoPrato,
                        dataAvaliacaoPrato,
                        fotoPath,
                        notaPrato,
                        pkRestauranteParaInsert,
                        txtId.text.toString().toInt()
                    )
                UpdateAvaliacaoRefeicao().execute(refeicaoObj)
            }
        }

    }

    inner class GetNomeRestauranteFromId: AsyncTask<Int, Unit, String>(){
        override fun doInBackground(vararg params: Int?): String {
            val nome = appDatabase.restauranteDAO().buscaNome(params[0]!!)
            return nome
        }
    }

    inner class UpdateAvaliacaoRefeicao:AsyncTask<Refeicao,Unit,String>(){
        override fun doInBackground(vararg params: Refeicao?):String {
            appDatabase.refeicaoDAO().update(params[0]!!)
            val nomeRest = appDatabase.restauranteDAO().buscaNome(params[0]!!.id_restaurante)
            return nomeRest
        }
        override fun onPostExecute(result: String?) {
            Toast.makeText(this@EditRatingRefeicoesActivity,"Avaliação atualizada com sucesso!",Toast.LENGTH_SHORT).show()
            val intt = Intent(this@EditRatingRefeicoesActivity, VerRatingRefeicoesActivity::class.java)
            intt.putExtra("nomeRestaurante",result)
            startActivity(intt)
        }
    }

    private fun captureImage() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                // Create the File where the photo should go
                try {

                    photoFile = createImageFile()
                    displayMessage(baseContext, photoFile!!.getAbsolutePath())

                    // Continue only if the File was successfully created
                    if (photoFile != null) {


                        val photoURI = FileProvider.getUriForFile(this,
                            "android.inflabnet.foodrating.fileprovider",
                            photoFile!!
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)

                    }
                } catch (ex: Exception) {
                    // Error occurred while creating the File
                    displayMessage(baseContext,"Problema com a captura da imagem: "  + ex.message.toString())
                }


            } else {
                displayMessage(baseContext, "Nullll")
            }
        }


    }

    /* versões antigas */
    private fun captureImage2() {
        try {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = createImageFile4()
            if (photoFile != null) {
                displayMessage(baseContext, photoFile!!.getAbsolutePath())

                val photoURI = Uri.fromFile(photoFile)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST)
            }
        } catch (e: Exception) {
            displayMessage(baseContext, "Camera não disponível." + e.toString())
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefixo */
            ".jpg", /* sufixo */
            storageDir      /* diretório */
        )

        // caminho do arquivo

        mCurrentPhotoPath = image.absolutePath
        Log.i("caminho",mCurrentPhotoPath.toString())
        return image
    }
    private fun createImageFile4(): File? {
        // External sdcard location
        val mediaStorageDir = File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            IMAGE_DIRECTORY_NAME)
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                displayMessage(baseContext, "Não foi possível criar o diretório.")
                return null
            }
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss",
            Locale.getDefault()).format(Date())

        return File(mediaStorageDir.path + File.separator
                + "IMG_" + timeStamp + ".jpg")

    }

    private fun displayMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAPTURE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val myBitmap = BitmapFactory.decodeFile(photoFile!!.getAbsolutePath())
            foodView.setImageBitmap(myBitmap)
        } else {
            displayMessage(baseContext, "Requisição cancelada. Alguma coisa deu errada.")
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                captureImage()
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        txtNota.text = progress.toString()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        txtNota.text = "Nota para refeição"
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

}
