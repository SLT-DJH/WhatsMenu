package com.jinhyun.whatsmenu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_loading.*

class LoadingActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val menucollection = db.collection("Menu")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        tv_copyright.visibility = View.INVISIBLE
        tv_introduction.visibility = View.INVISIBLE
        btn_check_menu_name.visibility = View.INVISIBLE
        btn_input_menu_name.visibility = View.INVISIBLE

        val notanimation = AnimationUtils.loadAnimation(this, R.anim.not_loading_animation)

        iv_logo.startAnimation(notanimation)

        btn_check_menu_name.setOnClickListener{

        }

        btn_input_menu_name.setOnClickListener {
            showalert()

        }

        var pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        if(pref.getString("name","") == ""){
            startloading()
        }else{
            skiploading()
        }

    }

    private fun skiploading(){
        val handler = Handler().postDelayed({
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

    private fun listCodeInput(){

    }

    private fun getList(){

    }

    private fun showalert(){

        var pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.input_menu_alert_popup, null)
        var name : EditText = view.findViewById(R.id.et_request_input_name)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.input_menu_list)
            .setPositiveButton(R.string.confirm){dialog, which ->
                if(name.text.toString().isNotBlank()){
                    val menunameRef = menucollection.document(name.text.toString())
                    menunameRef.get().addOnSuccessListener { document ->
                        if(document.data != null){
                            var editor = pref.edit()
                            editor.putString("name", name.text.toString()).apply()

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