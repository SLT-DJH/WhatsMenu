package com.jinhyun.whatsmenu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.firebase.firestore.FirebaseFirestore
import com.jinhyun.whatsmenu.databinding.ActivitySearchMenuListBinding
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

    lateinit var binding: ActivitySearchMenuListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchMenuListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        menucollection.addSnapshotListener { value, error ->
            if (error != null) {
                Log.d(TAG, "menuEventListner : failed $error")
                return@addSnapshotListener
            }

            menunames = ArrayList()

            for (doc in value!!) {
                doc.getString("name")?.let {
                    menunames.add(it)
                }
            }

            Log.d(TAG, "menuEventListener : result -> $menunames")

            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, menunames)
            binding.lvSearch.adapter = adapter

            binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextChange(newText: String?): Boolean {
                    adapter.filter.filter(newText)
                    return false
                }

                override fun onQueryTextSubmit(query: String?): Boolean {
                    binding.svSearch.clearFocus()
                    if (menunames.contains(query)) {
                        adapter.filter.filter(query)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            R.string.no_such_data,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return false
                }
            })

            binding.lvSearch.setOnItemClickListener { parent, view, position, id ->
                val element = parent.getItemAtPosition(position).toString()
                Log.d(TAG, "clicked! : $element")
                inputAlert(element)
            }
        }

        binding.ivBack.setOnClickListener {
            switch()
        }

        binding.btnAdd.setOnClickListener {
            showAlert()
        }

        binding.btnDelete.setOnClickListener {
            showDeleteAlert()
        }

    }

    override fun onBackPressed() {
        switch()
    }

    private fun switch() {
        val intent = Intent(this, LoadingActivity::class.java)
        intent.putExtra("from", "Check")
        startActivity(intent)
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
        finish()
    }

    private fun showAlert() {

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.add_menu_alert_popup, null)
        val name: EditText = view.findViewById(R.id.et_add_name)
        val password: EditText = view.findViewById(R.id.et_add_password)
        val confirmPassword: EditText = view.findViewById(R.id.et_confirm_password)
        val managerPassword: EditText = view.findViewById(R.id.et_add_manager_password)
        val confirmmgpw: EditText = view.findViewById(R.id.et_confirm_manager_password)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.add_menu_name)
            .setPositiveButton(R.string.confirm) { dialog, which ->
                if (name.text.toString().isNotBlank() && password.text.toString().isNotBlank() &&
                    managerPassword.text.toString().isNotBlank() && confirmPassword.text.toString()
                        .isNotBlank() &&
                    confirmmgpw.text.toString().isNotBlank()
                ) {
                    if (password.text.toString() == confirmPassword.text.toString() &&
                        managerPassword.text.toString() == confirmmgpw.text.toString()
                    ) {
                        val menunameRef = menucollection.document(name.text.toString())
                        menunameRef.get().addOnSuccessListener { document ->
                            if (document.data != null) {
                                Toast.makeText(this, R.string.already_data, Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                val newname = hashMapOf(
                                    "name" to name.text.toString(),
                                    "password" to password.text.toString(),
                                    "manager password" to managerPassword.text.toString()
                                )
                                menucollection.document(name.text.toString()).set(newname)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            R.string.menu_name_added,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }.addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        R.string.menu_name_add_fail,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, R.string.confirm_password_wrong, Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(this, R.string.please_input, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()

        alertDialog.setView(view)
        alertDialog.show()
    }

    private fun showDeleteAlert() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.delete_menu_alert_popup, null)
        val name: EditText = view.findViewById(R.id.et_del_name)
        val password: EditText = view.findViewById(R.id.et_del_password)
        val managerpassword: EditText = view.findViewById(R.id.et_del_manager_password)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.delete_menu_name)
            .setPositiveButton(R.string.confirm) { dialog, which ->
                if (name.text.toString().isNotBlank() && password.text.toString()
                        .isNotBlank() && managerpassword.text.toString().isNotBlank()
                ) {
                    val menunameRef = menucollection.document(name.text.toString())
                    menunameRef.get().addOnSuccessListener { document ->
                        if (document.data != null) {

                            val savedname = document.getString("name").toString()
                            val savedpassword = document.getString("password").toString()
                            val savedmanagerpassword =
                                document.getString("manager password").toString()

                            if (name.text.toString() == savedname && password.text.toString() == savedpassword
                                && managerpassword.text.toString() == savedmanagerpassword
                            ) {
                                menucollection.document(name.text.toString()).delete()
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            R.string.deleted_successfully,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }.addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        R.string.menu_name_delete_fail,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                Toast.makeText(
                                    this,
                                    R.string.menu_name_password_wrong,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            Toast.makeText(this, R.string.no_such_data, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, R.string.please_input, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()

        alertDialog.setView(view)
        alertDialog.show()
    }

    private fun inputAlert(item: String) {

        var pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.input_menu_alert_popup, null)
        val name: EditText = view.findViewById(R.id.et_request_input_name)
        val password: EditText = view.findViewById(R.id.et_password)

        name.setText(item)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.input_menu_list)
            .setPositiveButton(R.string.confirm) { dialog, which ->
                if (name.text.toString().isNotBlank() && password.text.toString().isNotBlank()) {
                    val menunameRef = menucollection.document(name.text.toString())
                    menunameRef.get().addOnSuccessListener { document ->
                        if (document.data != null) {
                            pref.edit().putString("name", name.text.toString()).apply()
                            getData()

                            val intent = Intent(this, MenuListActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(this, R.string.no_such_data, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, R.string.please_input, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()

        alertDialog.setView(view)
        alertDialog.show()
    }

    private fun getData() {
        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        if (pref.getString("name", "") != "") {
            val menuname = pref.getString("name", "").toString()

            //add data
            for (i in 0 until 15) {
                pluscalendar.set(yearnow, monthnow, daynow)
                pluscalendar.add(Calendar.DATE, i)

                val yearPlusChanging = pluscalendar.get(Calendar.YEAR)
                val monthPlusChanging = pluscalendar.get(Calendar.MONTH)
                val dayPlusChanging = pluscalendar.get(Calendar.DAY_OF_MONTH)

                val newPlusMonth = monthPlusChanging + 1

                val selectPlusDate = "$yearPlusChanging. $newPlusMonth. $dayPlusChanging."

                startGet(selectPlusDate, menuname)
            }

            //delete data
            for (i in -1 downTo -14) {
                minuscalendar.set(yearnow, monthnow, daynow)
                minuscalendar.add(Calendar.DATE, i)

                val yearMinusChanging = minuscalendar.get(Calendar.YEAR)
                val monthMinusChanging = minuscalendar.get(Calendar.MONTH)
                val dayMinusChanging = minuscalendar.get(Calendar.DAY_OF_MONTH)

                val newMinusMonth = monthMinusChanging + 1

                val selectMinusDate = "$yearMinusChanging. $newMinusMonth. $dayMinusChanging."

                startDelete(selectMinusDate)
            }
        }

    }

    private fun startGet(selectDate: String, menuName: String) {
        //breakfast data
        val breakfastRef = db.collection("Menu").document(menuName).collection(selectDate)
            .document("breakfast")
        breakfastRef.get().addOnSuccessListener { document ->
            val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

            if (document.data != null) {
                for (i in 1 until 7) {
                    pref.edit()
                        .putString("$selectDate breakfast $i", document.getString("$i").toString())
                        .apply()
                }

            } else {
                for (i in 1 until 7) {
                    pref.edit().putString("$selectDate breakfast $i", "").apply()
                }
            }
        }


        //lunch data
        val lunchRef = db.collection("Menu").document(menuName).collection(selectDate)
            .document("lunch")
        lunchRef.get().addOnSuccessListener { document ->
            val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

            if (document.data != null) {
                for (i in 1 until 7) {
                    pref.edit()
                        .putString("$selectDate lunch $i", document.getString("$i").toString())
                        .apply()
                }
            } else {
                for (i in 1 until 7) {
                    pref.edit().putString("$selectDate lunch $i", "").apply()
                }
            }
        }

        //dinner data
        val dinnerRef = db.collection("Menu").document(menuName).collection(selectDate)
            .document("dinner")
        dinnerRef.get().addOnSuccessListener { document ->
            val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

            if (document.data != null) {
                for (i in 1 until 7) {
                    pref.edit()
                        .putString("$selectDate dinner $i", document.getString("$i").toString())
                        .apply()
                }

            } else {
                for (i in 1 until 7) {
                    pref.edit().putString("$selectDate dinner $i", "").apply()
                }
            }
        }
    }

    private fun startDelete(selectDate: String) {
        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)
        //delete breakfast data
        if (pref.getString("$selectDate breakfast 1", "") != "") {
            for (i in 1 until 7) {
                pref.edit().remove("$selectDate breakfast $i").apply()
            }
        }
        //delete lunch data
        if (pref.getString("$selectDate lunch 1", "") != "") {
            for (i in 1 until 7) {
                pref.edit().remove("$selectDate lunch $i").apply()
            }
        }
        //delete dinner data
        if (pref.getString("$selectDate dinner 1", "") != "") {
            for (i in 1 until 7) {
                pref.edit().remove("$selectDate dinner $i").apply()
            }
        }

    }
}