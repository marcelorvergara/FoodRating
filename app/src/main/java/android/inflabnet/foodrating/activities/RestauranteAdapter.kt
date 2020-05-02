package android.inflabnet.foodrating.activities

import android.inflabnet.foodrating.R
import android.inflabnet.foodrating.db.models.Restaurante
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_restaurante.view.*

class RestauranteAdapter (val restaurante: List<Restaurante>, private val itemClick: (Restaurante) -> Unit) :
RecyclerView.Adapter<RestauranteAdapter.ViewHolder>() {

    private var limit = 4


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_restaurante,parent,false)
        return ViewHolder(view,itemClick)
    }

    override fun getItemCount() = restaurante.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindForecast(restaurante[position])
    }

    class ViewHolder(view: View, val itemClick: (Restaurante) -> Unit) : RecyclerView.ViewHolder(view) {
        fun bindForecast(restaurante: Restaurante) {
            with(restaurante) {
                itemView.txtNomeRes.text = restaurante.nome
                itemView.txtTipo.text = restaurante.tipo
                itemView.setOnClickListener { itemClick(this) }
            }
        }
    }



}