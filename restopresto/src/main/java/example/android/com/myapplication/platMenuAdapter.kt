package example.android.com.myapplication

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import example.android.com.myapplication.entity.plat

/**
 * Created by start on 28/04/2018.
 */
class platMenuAdapter (_ctx: Activity, _listplats:List<plat>): BaseAdapter() {

    var ctx = _ctx
    val listplats = _listplats

    override fun getItem(p0: Int) = listplats.get(p0)
    override fun getItemId(p0: Int) = listplats.get(p0).hashCode().toLong()
    override fun getCount()= listplats.size
    override fun getView(position: Int, p0: View?, parent: ViewGroup?): View {
        var view = p0
        var viewHolder: ViewHolder
        if(view == null) {
            view = LayoutInflater.from(ctx).inflate(R.layout.plat_layout,parent,false)
            val nom = view?.findViewById<TextView>(R.id.nom_plat) as TextView
            val ingredients = view?.findViewById<TextView>(R.id.ingredients_plat) as TextView
            val prix = view?.findViewById<TextView>(R.id.prix) as TextView
            viewHolder= ViewHolder(nom, ingredients,prix)
            view.setTag(viewHolder)
        }
        else {
            viewHolder = view.getTag() as ViewHolder
        }

        viewHolder.nom.setText(listplats.get(position).nom)
        viewHolder.ingredients.setText(listplats.get(position).ingredients)
        viewHolder.prix.setText(" ${listplats.get(position).prix} DZD")
        return view
    }
    private data class ViewHolder(var nom: TextView, var ingredients: TextView, var prix: TextView)
}