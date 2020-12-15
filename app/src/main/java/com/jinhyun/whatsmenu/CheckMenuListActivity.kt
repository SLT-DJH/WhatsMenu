package com.jinhyun.whatsmenu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isInvisible
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_search_menu_list.*
import java.util.*
import kotlin.collections.ArrayList

class CheckMenuListActivity : AppCompatActivity() {

    //set today

    val calendar = Calendar.getInstance()
    val yearnow = calendar.get(Calendar.YEAR)
    val monthnow = calendar.get(Calendar.MONTH)
    val daynow = calendar.get(Calendar.DAY_OF_MONTH)
    var pluscalendar = calendar
    var minuscalendar = calendar

    val TAG = "CheckMenuListActivity"

    val db = FirebaseFirestore.getInstance()
    val menucollection = db.collection("Menu")
    var menunames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_menu_list)

        val search = findViewById<SearchView>(R.id.sv_search)
        val listview = findViewById<ListView>(R.id.lv_search)

        menucollection.addSnapshotListener { value, error ->
            if (error != null) {
                Log.d(TAG, "menuEventListner : failed $error")
                return@addSnapshotListener
            }

            menunames = ArrayList<String>()
            for (doc in value!!){
                doc.getString("name")?.let {
                    menunames.add(it)
                }
            }
            Log.d(TAG, "menuEventListner : result -> $menunames")

            if (menunames != null) {
                var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, menunames)
                listview.adapter = adapter

                search.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
                    override fun onQueryTextChange(newText: String?): Boolean {
                        adapter.filter.filter(newText)
                        return false
                    }

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        search.clearFocus()
                        if(menunames.contains(query)){
                            adapter.filter.filter(query)
                        }else{
                            Toast.makeText(applicationContext, R.string.no_such_data, Toast.LENGTH_SHORT).show()
                        }
                        return false
                    }
                })

            }
            
            listview.setOnItemClickListener { parent, view, position, id ->
                val element = parent.getItemAtPosition(position).toString()
                Log.d(TAG, "clicked! : $element")
                inputalert(element)
            }
        }

        iv_back.setOnClickListener {
            switch()
        }

        btn_add.setOnClickListener {
            showalert()
        }

        btn_delete.setOnClickListener {
            showdelealert()
        }

    }

    override fun onBackPressed() {
        switch()
    }

    fun switch(){
        val intent = Intent(this, LoadingActivity::class.java)
        intent.putExtra("from", "Check")
        startActivity(intent)
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
        finish()
    }

    private fun showalert(){

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.add_menu_alert_popup, null)
        var name : EditText = view.findViewById(R.id.et_add_name)
        var password : EditText = view.findViewById(R.id.et_add_password)
        var confirmpw : EditText = view.findViewById(R.id.et_confirm_password)
        var managerpassword : EditText = view.findViewById(R.id.et_add_manager_password)
        var confirmmgpw : EditText = view.findViewById(R.id.et_confirm_manager_password)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.add_menu_name)
            .setPositiveButton(R.string.confirm){dialog, which ->
                if(name.text.toString().isNotBlank() && password.text.toString().isNotBlank() &&
                    managerpassword.text.toString().isNotBlank() && confirmpw.text.toString().isNotBlank() &&
                        confirmmgpw.text.toString().isNotBlank()){
                    if (password.text.toString() == confirmpw.text.toString() &&
                        managerpassword.text.toString() == confirmmgpw.text.toString()){
                        val menunameRef = menucollection.document(name.text.toString())
                        menunameRef.get().addOnSuccessListener { document ->
                            if(document.data != null){
                                Toast.makeText(this, R.string.already_data, Toast.LENGTH_SHORT).show()
                            }else{
                                val newname = hashMapOf(
                                    "name" to name.text.toString(),
                                    "password" to password.text.toString(),
                                    "manager password" to managerpassword.text.toString()
                                )
                                menucollection.document(name.text.toString()).set(newname).addOnSuccessListener {
                                    Toast.makeText(this, R.string.menu_name_added, Toast.LENGTH_SHORT).show()
                                }.addOnFailureListener{
                                    Toast.makeText(this, R.string.menu_name_add_fail, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }else {
                        Toast.makeText(this, R.string.confirm_password_wrong, Toast.LENGTH_SHORT).show()
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

    private fun showdelealert(){
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.delete_menu_alert_popup, null)
        var name : EditText = view.findViewById(R.id.et_del_name)
        var password : EditText = view.findViewById(R.id.et_del_password)
        var managerpassword : EditText = view.findViewById(R.id.et_del_manager_password)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.delete_menu_name)
            .setPositiveButton(R.string.confirm){dialog, which ->
                if(name.text.toString().isNotBlank() && password.text.toString().isNotBlank() && managerpassword.text.toString().isNotBlank()){
                    val menunameRef = menucollection.document(name.text.toString())
                    menunameRef.get().addOnSuccessListener { document ->
                        if(document.data != null){

                            val savedname = document.getString("name").toString()
                            val savedpassword = document.getString("password").toString()
                            val savedmanagerpassword = document.getString("manager password").toString()

                            if(name.text.toString() == savedname && password.text.toString() == savedpassword
                                && managerpassword.text.toString() == savedmanagerpassword){
                                menucollection.document(name.text.toString()).delete().addOnSuccessListener {
                                    Toast.makeText(this, R.string.deleted_successfully, Toast.LENGTH_SHORT).show()
                                }.addOnFailureListener{
                                    Toast.makeText(this, R.string.menu_name_delete_fail, Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(this, R.string.menu_name_password_wrong, Toast.LENGTH_SHORT).show()
                            }

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

    private fun inputalert(item : String){

        var pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.input_menu_alert_popup, null)
        var name : EditText = view.findViewById(R.id.et_request_input_name)
        var password : EditText = view.findViewById(R.id.et_password)

        name.setText(item)

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
                minuscalendar.set(yearnow, monthnow, daynow)
                minuscalendar.add(Calendar.DATE, i)

                var yearminuschanging = minuscalendar.get(Calendar.YEAR)
                var monthminuschanging = minuscalendar.get(Calendar.MONTH)
                var dayminuschanging = minuscalendar.get(Calendar.DAY_OF_MONTH)

                var newminusmonth = monthminuschanging + 1

                var selectminusdate = "$yearminuschanging. $newminusmonth. $dayminuschanging."

                startdelete(selectminusdate, menuname)
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
}