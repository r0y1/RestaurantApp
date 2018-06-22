package example.android.com.RestoPresto

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import example.android.com.RestoPresto.entities.Restaurant
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_restaurant.*
import org.jetbrains.anko.intentFor


class MainActivity : AppCompatActivity() {

    var detailImagesTab = arrayOf<Int>()
    var detailNomsTab = arrayOf<String>()
    var menujourbouton: Button? = null
    var commandebouton: Button? = null
    var reservebouton:Button? = null
    var infosbouton: Button? = null
    var menubouton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        menujourbouton =findViewById(R.id.menujour) as? Button
        commandebouton = findViewById(R.id.commande) as? Button
        reservebouton = findViewById(R.id.reserve) as? Button
        infosbouton = findViewById(R.id.infos) as? Button
        menubouton = findViewById(R.id.menus) as? Button
        // Instance of Uiil class............. rouya
        val util = Util()
        // View Model instance
        val restaurantModel = ViewModelProviders.of(this).get(RestaurantModel::class.java)
        // Création de l'adapter pour la liste
        restaurantModel.loadData(this)

        /* Si la liste des restaurants est vide */
        if (restaurantModel.restaurants==null) {
            restaurantModel.loadData(this)
        }
        else {
            // After the rotation of the screen, use restos of the ViewModel instance
            listrestos.adapter = RestaurantAdapter(this, restaurantModel.restaurants!!)
        }



        if (isTwoPane()  && restaurantModel.restaurant!=null) {

              //  restaurantModel.displayDatail (this,restaurantModel.restaurant!!)
            restaurantModel.loadDetail(this,restaurantModel.restaurant!!.id_restaurant)

            menujourbouton?.setOnClickListener({
                startActivity(intentFor<MenuJourActivity>("id_resto" to restaurantModel.restaurant!!.id_restaurant))
            })
            commandebouton?.setOnClickListener({
                startActivity(intentFor<CommanderActivity>("id_resto" to restaurantModel.restaurant!!.id_restaurant))
            })
            reservebouton?.setOnClickListener({
                startActivity(intentFor<ReserverActivity>("id_resto" to restaurantModel.restaurant!!.id_restaurant))
            })
            menubouton?.setOnClickListener({
                startActivity(intentFor<MenuDesMenusActivity>("id_resto" to restaurantModel.restaurant!!.id_restaurant))
            })
            infosbouton?.setOnClickListener({
                startActivity(intentFor<InfosActivity>("id_resto" to restaurantModel.restaurant!!.id_restaurant))
            }
            )}
            else
            {
                restaurantModel.loadDetail(this,0)

                System.out.println("contenu du intent est 0")


                menujourbouton?.setOnClickListener({
                    startActivity(intentFor<MenuJourActivity>("id_resto" to restaurantModel.restaurant!!.id_restaurant))
                })
                commandebouton?.setOnClickListener({
                    startActivity(intentFor<CommanderActivity>("id_resto" to restaurantModel.restaurant!!.id_restaurant))
                })
                reservebouton?.setOnClickListener({
                    startActivity(intentFor<ReserverActivity>("id_resto" to restaurantModel.restaurant!!.id_restaurant))
                })
                menubouton?.setOnClickListener({
                    startActivity(intentFor<MenuDesMenusActivity>("id_resto" to restaurantModel.restaurant!!.id_restaurant))
                })
                infosbouton?.setOnClickListener({
                    startActivity(intentFor<InfosActivity>("id_resto" to restaurantModel.restaurant!!.id_restaurant))
                })
            }



        // Listner pour les éléments de la liste


        listrestos.setOnItemClickListener { adapterView, view, i, l ->
            val resto = (adapterView.getItemAtPosition(i) as Restaurant)
            if (isTwoPane()) {
                // display detail data
                /*restaurantModel.restaurant = Restaurant(nom = detailNomsTab[i], lien = detailImagesTab[i].toString())
                restaurantModel.i = i*/

                restaurantModel.displayDatail(this,resto)

                menujourbouton?.setOnClickListener({
                    startActivity(intentFor<MenuJourActivity>("id_resto" to resto.id_restaurant))
                })
                commandebouton?.setOnClickListener({
                    startActivity(intentFor<CommanderActivity>("id_resto" to resto.id_restaurant))
                })
                reservebouton?.setOnClickListener({
                    startActivity(intentFor<ReserverActivity>("id_resto" to resto.id_restaurant))
                })
                menubouton?.setOnClickListener({
                    startActivity(intentFor<MenuDesMenusActivity>("id_resto" to resto.id_restaurant))
                })
                infosbouton?.setOnClickListener({
                    startActivity(intentFor<InfosActivity>("id_resto" to resto.id_restaurant))
                })
            }
            else {
                // send the position to the detail activity
                startActivity(intentFor<RestaurantActivity>("id_resto" to resto.id_restaurant))
            }
            }
        }

   /* fun loadData(): List<Restaurant> {
        detailImagesTab = arrayOf(R.drawable.ledey, R.drawable.lallamina, R.drawable.eldjenina, R.drawable.lapalmeraie, R.drawable.eldjazair)
        detailNomsTab=resources.getStringArray(R.array.restos)
        val imagesTab = arrayOf(R.drawable.ledey, R.drawable.lallamina, R.drawable.eldjenina, R.drawable.lapalmeraie, R.drawable.eldjazair)
        val nomsTab = resources.getStringArray(R.array.restos)
        val notesTab = resources.getStringArray(R.array.notes)

        val list = mutableListOf<Restaurant>()
        for (i in 0..imagesTab.size - 1) {
            list.add(Restaurant(nom = nomsTab[i], lien = imagesTab[i].toString(), note = notesTab[i]))

        }
        return list
    }*/
    fun isTwoPane() = findViewById<View>(R.id.fragment2) != null
}