package example.android.com.RestoPresto

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import example.android.com.RestoPresto.entities.Restaurant
import example.android.com.RestoPresto.service.RetrofitService
import example.android.com.RestoPresto.singleton.RoomService
import kotlinx.android.synthetic.main.infos_resto.*
import org.jetbrains.anko.toast
import android.view.View
import com.firebase.jobdispatcher.*
import example.android.com.RestoPresto.R.id.nom
import example.android.com.RestoPresto.service.CommandeJobService
import example.android.com.RestoPresto.service.SynchroService
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.fragment_restaurant.*
import org.jetbrains.anko.act
import org.jetbrains.anko.browse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by start on 01/04/2018.
 */
class RestaurantModel: ViewModel() {

    var i:Int = 0
    var restaurant: Restaurant? = null
    var restaurants:List<Restaurant>? = null


    fun loadData(act: Activity) {
       // act.progressBar1.visibility = View.VISIBLE
        // Get restaurants from SQLite DB
        restaurants = RoomService.appDatabase.getRestaurantDao().getRestaurants()
        lancerJob(act)
        if (restaurants?.size == 0)
            // If the list of restaurants is empty, load from server and save them in SQLite DB
            getrestaurantsFromRemote(act)

        else {
            //act.progressBar1.visibility = View.GONE
            act.listrestos.adapter = RestaurantAdapter(act, restaurants!!)
        }
}

    private fun getrestaurantsFromRemote(act:Activity) {
        val call = RetrofitService.endpoint.getRestaurants()
        call.enqueue(object : Callback<List<Restaurant>> {
            override fun onResponse(call: Call<List<Restaurant>>?, response: Response<List<Restaurant>>?) {
                //act.progressBar1.visibility = View.GONE
                if (response?.isSuccessful!!) {
                    //act.toast("response successful 1")
                    restaurants = response?.body()
                   // act.progressBar1.visibility = View.GONE
                    act.listrestos.adapter= RestaurantAdapter(act, restaurants!!)
                    // save restaurants in SQLite DB
                    for (item in restaurants!!)
                    {
                    RoomService.appDatabase.getRestaurantDao().addRestaurants(item)
                    }
                } else {
                    //act.toast("Une erreur s'est produite1")
                }
            }

            override fun onFailure(call: Call<List<Restaurant>>?, t: Throwable?) {
               // act.progressBar1.visibility = View.GONE
                //act.toast("Une erreur s'est produite2")
            }


        })
    }

    fun loadDetail(act:Activity,idRestaurant:Int) {
        //act.progressBar2.visibility = View.VISIBLE
        // load Restaurant detail from SQLite DB
        this.restaurant = RoomService.appDatabase.getRestaurantDao().getRestaurantByID (idRestaurant)

        if(this.restaurant==null) {
            // if the Restaurant details don't exist, load the details from server and update SQLite DB

            loadDetailFromRemote(act,idRestaurant)
        }
        else {
            //act.progressBar2.visibility = View.GONE

            displayDetail(act, this.restaurant)
        }

    }

    private fun loadDetailFromRemote(act:Activity,idRestaurant:Int) {
        val call = RetrofitService.endpoint.getDetailRestaurant(idRestaurant)
        call.enqueue(object : Callback<Restaurant> {
            override fun onResponse(call: Call<Restaurant>?, response: Response<Restaurant>?) {
                //act.progressBar2.visibility = View.GONE
                if(response?.isSuccessful!!) {
                    var RestaurantDetail = response?.body()
                    displayDetail(act,RestaurantDetail!!)
                    // update the Restaurant in the SQLite DB to support offline mode
                    RoomService.appDatabase.getRestaurantDao().updateRestaurant(RestaurantDetail)
                    // update ViewModel
                    this@RestaurantModel.restaurant= RestaurantDetail

                }
                else {
                   // act.toast("Une erreur s'est produite3")

                }
            }

            override fun onFailure(call: Call<Restaurant>?, t: Throwable?) {
                //act.progressBar2.visibility = View.GONE
                //act.toast(t!!.message.toString())

            }
        })
    }

    fun displayDetail(act: Activity,Restaurant: Restaurant?) {
           //   Glide.with(act).load(baseUrl+ Restaurant!!.lien).into(act.imageView3)
        Glide.with(act).load(baseUrl+ Restaurant!!.lien).into(act.imagedetail)
        act.nomdetail.text = Restaurant!!.nom

    }
    fun displayInfos(act: Activity,resto: Restaurant?)
    {

        Glide.with(act).load(baseUrl+ resto!!.lien).into(act.imageView3)
        act.nom_resto.text = resto.nom
        act.adresse.text = "Adresse : "+resto.adresse
        act.numero_resto.text =resto.n_tel
        act.email_resto.text ="Email : "+resto.email
        act.note.text = "Note : "+resto.note
        act.description.text = resto.description
    }

    fun openFacebookPage(ctx: Context, facebookUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl))
        ctx.startActivity(intent)
    }

    /**
     * This function opens a web page
     */

    fun browseUrl(ctx: Context, url:String){
        ctx.browse(url)
    }

    fun lancerJob(act: Activity)
    {
        val dispatcher = FirebaseJobDispatcher(GooglePlayDriver(act))
        val args = Bundle()
        val myJob = dispatcher.newJobBuilder()
                .setService(SynchroService::class.java)
                .setRecurring(true) // Exécuter une fois
                .setTag("lll")
                .setExtras(args)
                .setLifetime(Lifetime.FOREVER) // durée de vie
                .setTrigger(Trigger.executionWindow(30, 40)) // temps de lancement
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR) // stratégie de relance
                .setConstraints(Constraint.ON_ANY_NETWORK).build() // mode WiFi uniquement
        System.out.print("je passe dans la programmation")
        dispatcher.mustSchedule(myJob)
    }

}
