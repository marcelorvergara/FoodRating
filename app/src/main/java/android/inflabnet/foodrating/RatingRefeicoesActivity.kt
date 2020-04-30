package android.inflabnet.foodrating

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.inflabnet.foodrating.db.AppDatabase
import android.inflabnet.foodrating.db.AppDatabaseService
import android.inflabnet.foodrating.db.Refeicao
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_rating_refeicoes.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class RatingRefeicoesActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {

    private var photoFile: File? = null
    private val CAPTURE_IMAGE_REQUEST = 1
    private lateinit var mCurrentPhotoPath: String
    private val IMAGE_DIRECTORY_NAME = "RATINGFOOD"

    private lateinit var appDatabase : AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rating_refeicoes)

        appDatabase = AppDatabaseService.getInstance(this)

        val nomeRestaurante = intent.getStringExtra("nomeRestaurante")
        if(!nomeRestaurante.isNullOrBlank()){
            txtNomeRestauranteAvaliacao.append(nomeRestaurante.toString())
        }

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
            val dpDialog = DatePickerDialog(this,DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                //mostrar no edt view
                val newMonth = month.toInt() + 1
                val dataStr = "$dayOfMonth/$newMonth/$year"
                edtDataAvaliacao.setText(dataStr)
            }, ano,mes,dia)
            dpDialog.show()
        }

        //nota
        seekBar.progress = 5
        this.seekBar!!.setOnSeekBarChangeListener(this)

        //guardar no banco
        guardarBtn.setOnClickListener {
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
                    fotoPath = "@drawable/ic_add_a_photo_black_24dp"
                }
                //pegar PK do restaurante
                val pkRestauranteParaInsert = PegarPKRestaurante().execute(txtNomeRestauranteAvaliacao.text.toString().replace("Restaurante:","")).get()

                //guardar a avaliação no db
                val refeicaoObj = Refeicao(nomePrato,ingredientesPrato,avaliacaoPrato,dataAvaliacaoPrato,fotoPath,notaPrato,pkRestauranteParaInsert)
                GuardarAvaliacaoRefeicao().execute(refeicaoObj)
                val totRefeicao = VerificarAvaliacoes().execute().get()
                displayMessage(this, totRefeicao.toString())
            }
        }
    }
    inner class VerificarAvaliacoes:AsyncTask<Unit,Unit,Int>(){
        override fun doInBackground(vararg params: Unit?):Int {
            val tot = appDatabase.refeicaoDAO().selectAll()
            return tot
        }
    }

    inner class GuardarAvaliacaoRefeicao: AsyncTask<Refeicao,Unit,Unit>(){
        override fun doInBackground(vararg params: Refeicao?) {
            appDatabase.refeicaoDAO().guardar(params[0])
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            displayMessage(applicationContext,"Avaliação da Refeição Guardada com Sucesso")
        }
    }

    inner class PegarPKRestaurante:AsyncTask<String,Unit,Int>(){
        override fun doInBackground(vararg params: String?): Int {
            val pkrestaurante = appDatabase.restauranteDAO().buscaPK(params[0]!!)
            return pkrestaurante
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
                    Log.i("Mayank", photoFile!!.getAbsolutePath())

                    // Continue only if the File was successfully created
                    if (photoFile != null) {


                        var photoURI = FileProvider.getUriForFile(this,
                            "android.inflabnet.foodrating.fileprovider",
                            photoFile!!
                        )

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST)

                    }
                } catch (ex: Exception) {
                    // Error occurred while creating the File
                    displayMessage(baseContext,"Capture Image Bug: "  + ex.message.toString())
                }


            } else {
                displayMessage(baseContext, "Nullll")
            }
        }


    }

    /* Capture Image function for 4.4.4 and lower. Not tested for Android Version 3 and 2 */
    private fun captureImage2() {
        try {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = createImageFile4()
            if (photoFile != null) {
                displayMessage(baseContext, photoFile!!.getAbsolutePath())
                Log.i("caminhov4", photoFile!!.getAbsolutePath())
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
                displayMessage(baseContext, "Unable to create directory.")
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
