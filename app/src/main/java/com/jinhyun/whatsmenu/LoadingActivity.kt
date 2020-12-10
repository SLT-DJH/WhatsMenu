package com.jinhyun.whatsmenu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_search_menu_list.*
import kotlinx.android.synthetic.main.activity_loading.*
import java.util.*

class LoadingActivity : AppCompatActivity() {

    //set today

    val calendar = Calendar.getInstance()
    val yearnow = calendar.get(Calendar.YEAR)
    val monthnow = calendar.get(Calendar.MONTH)
    val daynow = calendar.get(Calendar.DAY_OF_MONTH)
    var pluscalendar = calendar
    var minuscalendar = calendar

    val TAG = "LoadingActivity"

    val db = FirebaseFirestore.getInstance()
    val menucollection = db.collection("Menu")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        tv_copyright.visibility = View.INVISIBLE
        tv_introduction.visibility = View.INVISIBLE
        btn_check_menu_name.visibility = View.INVISIBLE
        btn_input_menu_name.visibility = View.INVISIBLE

        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        if(intent.hasExtra("from")){
            Log.d(TAG, "from ${intent.getStringExtra("from")}")

            tv_copyright.visibility = View.VISIBLE
            tv_introduction.visibility = View.VISIBLE
            btn_check_menu_name.visibility = View.VISIBLE
            btn_input_menu_name.visibility = View.VISIBLE

        }else{
            getdata()

            val notanimation = AnimationUtils.loadAnimation(this, R.anim.not_loading_animation)

            iv_logo.startAnimation(notanimation)

            if(pref.getString("name","") == ""){
                startloading()
            }else{
                skiploading()
            }
        }

        btn_check_menu_name.setOnClickListener{
            pref.edit().putString("manager password", "").apply()
            val intent = Intent(this, CheckMenuListActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left)
            finish()
        }

        btn_input_menu_name.setOnClickListener {
            showalert()

        }

    }

    private fun getdata(){
        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        if (pref.getString("name", "") != ""){
            val menuname = pref.getString("name", "").toString()

            //add data
            for(i in 0 until 15){
                pluscalendar.set(yearnow, monthnow, daynow)
                pluscalendar.add(Calendar.DATE, i)

                var yearpluschanging = pluscalendar.get(Calendar.YEAR)
                var monthpluschanging = pluscalendar.get(Calendar.MONTH)
                var daypluschanging = pluscalendar.get(Calendar.DAY_OF_MONTH)

                var newplusmonth = monthpluschanging + 1

                var selectplusdate = "$yearpluschanging. $newplusmonth. $daypluschanging."

                startget(selectplusdate, menuname)
            }

            //delete data
            for(i in -1 downTo -14){
                pluscalendar.set(yearnow, monthnow, daynow)
                pluscalendar.add(Calendar.DATE, i)

                var yearpluschanging = pluscalendar.get(Calendar.YEAR)
                var monthpluschanging = pluscalendar.get(Calendar.MONTH)
                var daypluschanging = pluscalendar.get(Calendar.DAY_OF_MONTH)

                var newplusmonth = monthpluschanging + 1

                var selectplusdate = "$yearpluschanging. $newplusmonth. $daypluschanging."

                startdelete(selectplusdate, menuname)
            }
        }

    }

    private fun startget(selectdate : String, menuname : String){
        //breakfast data
        val breakfastRef = db.collection("Menu").document(menuname).collection(selectdate)
            .document("breakfast")
        breakfastRef.get().addOnSuccessListener { document ->
            val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

            if (document.data != null) {
                for(i in 1 until 7){
                    pref.edit().putString("$selectdate breakfast $i", document.getString("$i").toString()).apply()
                }

            } else {
                for(i in 1 until 7){
                    pref.edit().putString("$selectdate breakfast $i", "").apply()
                }
            }
        }


        //lunch data
        val lunchRef = db.collection("Menu").document(menuname).collection(selectdate)
            .document("lunch")
        lunchRef.get().addOnSuccessListener { document ->
            val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

            if (document.data != null) {
                for(i in 1 until 7){
                    pref.edit().putString("$selectdate lunch $i", document.getString("$i").toString()).apply()
                }
            } else {
                for(i in 1 until 7){
                    pref.edit().putString("$selectdate lunch $i", "").apply()
                }
            }
        }

        //dinner data
        val dinnerRef = db.collection("Menu").document(menuname).collection(selectdate)
            .document("dinner")
        dinnerRef.get().addOnSuccessListener { document ->
            val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

            if (document.data != null) {
                for(i in 1 until 7){
                    pref.edit().putString("$selectdate dinner $i", document.getString("$i").toString()).apply()
                }

            } else {
                for(i in 1 until 7){
                    pref.edit().putString("$selectdate dinner $i", "").apply()
                }
            }
        }
    }

    private fun startdelete(selectdate : String, menuname : String){
        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)
        //delete breakfast data
        if(pref.getString("$selectdate breakfast 1","") != ""){
            for(i in 1 until 7){
                pref.edit().remove("$selectdate breakfast $i").commit()
            }
        }
        //delete lunch data
        if(pref.getString("$selectdate lunch 1","") != ""){
            for(i in 1 until 7){
                pref.edit().remove("$selectdate lunch $i").commit()
            }
        }
        //delete dinner data
        if(pref.getString("$selectdate dinner 1","") != ""){
            for(i in 1 until 7){
                pref.edit().remove("$selectdate dinner $i").commit()
            }
        }

    }

    private fun skiploading(){
        Handler().postDelayed({
            val intent = Intent(this, MenuListActivity::class.java)
            startActivity(intent)

            finish()
        }, 2000)
    }

    private fun startloading(){
        val hanler = Handler().postDelayed({
            val upanimation = AnimationUtils.loadAnimation(this, R.anim.loading_animation)
            val alphaanimation = AnimationUtils.loadAnimation(this, R.anim.alpha_animation)

            iv_logo.startAnimation(upanimation)

            tv_copyright.startAnimation(alphaanimation)
            tv_introduction.startAnimation(alphaanimation)
            btn_check_menu_name.startAnimation(alphaanimation)
            btn_input_menu_name.startAnimation(alphaanimation)

            tv_copyright.visibility = View.VISIBLE
            tv_introduction.visibility = View.VISIBLE
            btn_check_menu_name.visibility = View.VISIBLE
            btn_input_menu_name.visibility = View.VISIBLE
        }, 2000)
    }

    private fun showalert(){

        var pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.input_menu_alert_popup, null)
        var name : EditText = view.findViewById(R.id.et_request_input_name)
        var password : EditText = view.findViewById(R.id.et_password)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.input_menu_list)
            .setPositiveButton(R.string.confirm){dialog, which ->
                if(name.text.toString().isNotBlank() && password.text.toString().isNotBlank()){
                    val menunameRef = menucollection.document(name.text.toString())
                    menunameRef.get().addOnSuccessListener { document ->
                        if(document.data != null){
                            pref.edit().putString("name", name.text.toString()).apply()
                            getdata()

                            val intent = Intent(this, MenuListActivity::class.java)
                            startActivity(intent)
                            finish()

                        }else{
                            Toast.makeText(this, R.string.no_such_data, Toast.LENGTH_SHORT).show()
                        }
                    }
                }else{
                    Toast.makeText(this, R.string.please_input, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()

        alertDialog.setView(view)
        alertDialog.show()
    }
}