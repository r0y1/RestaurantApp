package example.android.com.RestoPresto

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatDialogFragment
import example.android.com.RestoPresto.database.AppDatabase
import example.android.com.RestoPresto.entities.Ligne_commande
import example.android.com.RestoPresto.service.RetrofitService
import example.android.com.RestoPresto.singleton.RoomService
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ConfirmCmdDialogFragment : AppCompatDialogFragment() {
    var preferences: SharedPreferences? = null
    private var mDb: AppDatabase? = RoomService.appDatabase
    companion object {
        fun newInstance(id_cmd:Int): ConfirmCmdDialogFragment{
            val frag = ConfirmCmdDialogFragment()
            val args = Bundle()
            args.putInt("id_cmd", id_cmd)
            frag.setArguments(args)
            return frag
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity?.layoutInflater?.inflate(R.layout.fragment_confirm_cmd_dialog, null)
        var builder : AlertDialog.Builder = AlertDialog.Builder(activity)
        var nom = view?.findViewById<TextInputLayout>(R.id.textNom)
        val id_cmd = arguments!!.getInt("id_cmd",1)
        preferences = activity?.getSharedPreferences("projetMobile", Context.MODE_PRIVATE)
        nom!!.editText!!.setText(preferences!!.getString("nom",""))
        var adresse = view?.findViewById<TextInputLayout>(R.id.TextAdresse)
        adresse!!.editText!!.setText(preferences!!.getString("adresse",""))
        builder.setView(view).setTitle("Confirmation de la commande").setNegativeButton("Annuler", object : DialogInterface.OnClickListener {
            override  fun onClick(dialog: DialogInterface, id: Int) {

            }
        })
                .setPositiveButton("Confirmer", object : DialogInterface.OnClickListener {
                    override  fun onClick(dialog: DialogInterface, id: Int) {
                        if (nom!!.equals("") || adresse!!.equals(""))
                            activity?.toast("Veuillez remplir les champs")
                        else
                        {
                        validerCommande(id_cmd)
                        activity?.toast("Votre commande a bien été enregistrée")
                        activity?.finish()
                        }
                    }
                })
        return builder.create()
    }
    fun validerCommande(id_cmd:Int)
    {
        val commande = mDb!!.getCommandeDao().getCommandesById(id_cmd)
        commande.termine = 1
        mDb!!.getCommandeDao().updateCommande(commande)
        val ligneCmd = mDb!!.getLigneCommandeDao().getLigne_commandesByCommande(commande.id_cmd)
        val call2 = RetrofitService.endpoint.addCommande(commande)
        call2.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                //activity?.toast(t!!.message.toString())
            }
            override fun onResponse(call: Call<String>?, response: Response<String>?){
                if (response?.isSuccessful!!) {
                    System.out.println("je passe par contenir")
                    System.out.println(ligneCmd.get(0).toString())
                    for (lc in ligneCmd)
                    {

                        enregistrerLignesCommande(lc)
                    }
                } else {
                   // activity!!.toast(response.message())
                }
            }
        })
    }

    fun enregistrerLignesCommande(ligne_commande: Ligne_commande)
    {
        val call = RetrofitService.endpoint.addLigneCommande(ligne_commande)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable?) {
            }
            override fun onResponse(call: Call<String>?, response: Response<String>?){
                if (response?.isSuccessful!!) {
                    System.out.println("je passe par contenir")
                } else {
                }
            }
        })
    }

}// Required empty public constructor
