package com.jinhyun.whatsmenu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_search_menu_list.*

class CheckMenuListActivity : AppCompatActivity() {

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
        val view = inflater.inflate(R.layout.add_delete_menu_alert_popup, null)
        var name : EditText = view.findViewById(R.id.et_request_input_name)
        var password : EditText = view.findViewById(R.id.et_password)
        var managerpassword : EditText = view.findViewById(R.id.et_manager_password)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.add_menu_name)
            .setPositiveButton(R.string.confirm){dialog, which ->
                if(name.text.toString().isNotBlank() && password.text.toString().isNotBlank() && managerpassword.text.toString().isNotBlank()){
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
        val view = inflater.inflate(R.layout.add_delete_menu_alert_popup, null)
        var name : EditText = view.findViewById(R.id.et_request_input_name)
        var password : EditText = view.findViewById(R.id.et_password)
        var managerpassword : EditText = view.findViewById(R.id.et_manager_password)

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
}