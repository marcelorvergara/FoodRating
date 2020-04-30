package android.inflabnet.foodrating

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.inflabnet.foodrating.db.Refeicao
import android.inflabnet.foodrating.db.RestauranteERefeicao
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewConfiguration.get
import android.view.ViewGroup
import androidx.appcompat.widget.ResourceManagerInternal.get
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_refeicao.view.*
import java.io.File
import java.security.AccessController.getContext

class RefeicoesAdapter(val context: Context,val refeicao: List<Refeicao>, private val itemClick: (Refeicao) -> Unit) :
    RecyclerView.Adapter<RefeicoesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_refeicao,parent,false)
        return ViewHolder(context,view, itemClick)
    }

    override fun getItemCount() = refeicao.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindForecast(refeicao[position])
    }

    class ViewHolder(val context: Context,view: View, val itemClick: (Refeicao) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bindForecast(refeicao: Refeicao) {
            with(refeicao) {
                val firstChar = refeicao.foto.substring(0,1)
                Log.i("Refeccc",firstChar)
                if(firstChar.equals("@")){
                    itemView.imgView.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.ic_block_black_24dp))
                }else {
                    itemView.imgView.setImageBitmap(BitmapFactory.decodeFile(refeicao.foto))
                }
                itemView.txtNome.text = refeicao.nome
                itemView.txtNota.append(refeicao.nota.toString())
                itemView.setOnClickListener { itemClick(this) }
            }
        }
    }

}