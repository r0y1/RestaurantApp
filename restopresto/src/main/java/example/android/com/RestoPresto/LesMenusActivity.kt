package example.android.com.RestoPresto


import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_les_menus.*
import android.support.design.widget.TabLayout
import example.android.com.RestoPresto.entities.User
import example.android.com.RestoPresto.singleton.RoomService
import org.jetbrains.anko.intentFor


class LesMenusActivity : AppCompatActivity(){

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    var  user: User?=null
    var id_resto:Int=0
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
         id_resto = intent.getIntExtra("id_resto",0)
        setContentView(R.layout.activity_les_menus)
        var pref : SharedPreferences? =this!!.getSharedPreferences("projetMobile", Context.MODE_PRIVATE)

        val mDb = RoomService.appDatabase.getUserDao()
       user  = mDb.getUsersByID(pref!!.getInt("id_user",0))
        toolbar.setTitle(intent.getStringExtra("nom"))

        setSupportActionBar(toolbar)
        // Action Up

        val ab = getSupportActionBar()
        ab?.setDisplayHomeAsUpEnabled(true)

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        if (user!!.preference =="vegetarien")  {
                tabs.getTabAt(0)!!.setText("VEGETARIEN")
                tabs.getTabAt(1)!!.setText("NORMAL")
                tabs.getTabAt(2)!!.setText("DIABETIQUE")
            }
        else if(user!!.preference =="normal") {
            tabs.getTabAt(0)!!.setText("NORMAL")
            tabs.getTabAt(1)!!.setText("VEGETARIEN")
            tabs.getTabAt(2)!!.setText("DIABETIQUE")
        }
        else{
            tabs.getTabAt(0)!!.setText("DIABETIQUE")
            tabs.getTabAt(1)!!.setText("NORMAL")
            tabs.getTabAt(2)!!.setText("VEGETARIEN")
        }
        container.adapter = mSectionsPagerAdapter
        // Loader les informations en local
        val menumodel = MenuModel()
        menumodel.remplirMenus()

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_les_menus, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }
        if (id == R.id.action_commande)
        {
            startActivity(intentFor<CommanderActivity>("id_resto" to id_resto))
        }


        return super.onOptionsItemSelected(item)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment {

            return when (user!!.preference)  {
           "vegetarien" -> {when (position) {
              0-> MenuVegetarienFragment()
               1-> MenuNormalFragment()
               else->MenuDiabetFragment()
           }
           }
           "normal" -> {when (position) {
                    0-> MenuNormalFragment()
                    1-> MenuVegetarienFragment()
               else->MenuDiabetFragment()

                }
                }
                //diabetique
           else -> {when (position) {
                    0-> MenuDiabetFragment()
                    1-> MenuNormalFragment()
               else->MenuVegetarienFragment()

                }
                }
            }
        }

        override fun getCount(): Int {
            // Show 2s total pages.
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {

            return when (user!!.preference)  {
                "vegetarien" -> {when (position) {
                    0-> "VEGETARIEN"
                    1-> "NORMAL"
                    else->"DIABETIQUE"
                }
                }
                "normal" -> {when (position) {
                    0-> "NORMAL"
                    1-> "VEGETARIEN"
                    else->"DIABETIQUE"
                }
                }
            //diabetique
                else -> {when (position) {
                    0->"DIABETIQUE"
                    1-> "NORMAL"
                    else-> "VEGETARIEN"
                }
                }
                }

    }}

}