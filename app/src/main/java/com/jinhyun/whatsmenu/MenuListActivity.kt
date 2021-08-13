package com.jinhyun.whatsmenu

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.jinhyun.whatsmenu.databinding.ActivityMenuListBinding
import com.jinhyun.whatsmenu.databinding.CustomActionbarBinding
import java.util.*
import kotlin.collections.ArrayList

class MenuListActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    val menucollection = db.collection("Menu")

    val calendar = Calendar.getInstance()

    val TAG = "MenuListActivity"

    //현재 날짜 구하기

    val yearnow = calendar.get(Calendar.YEAR)
    val monthnow = calendar.get(Calendar.MONTH)
    val daynow = calendar.get(Calendar.DAY_OF_MONTH)
    val datenow = calendar.get(Calendar.DAY_OF_WEEK)

    var changingCalendar = calendar

    lateinit var binding: ActivityMenuListBinding
    lateinit var toolbarBinding: CustomActionbarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuListBinding.inflate(layoutInflater)
        toolbarBinding = CustomActionbarBinding.bind(binding.root)
        setContentView(R.layout.activity_menu_list)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)
        val menuname = pref.getString("name", "").toString()

        val menutitle = "$menuname " + getString(R.string.menu)

        toolbarBinding.toolbarTitle.text = menutitle


        changingCalendar.set(yearnow, monthnow, daynow)

        var newmonth = monthnow + 1

        var selectdate = "$yearnow. $newmonth. $daynow."

        binding.tvDateprint.text = selectdate
        binding.tvDateprint.text = getdate(datenow)

        insertData(selectdate)


        binding.btnMinus.setOnClickListener {
            changingCalendar.add(Calendar.DATE, -1)

            var yearchanging = changingCalendar.get(Calendar.YEAR)
            var monthchanging = changingCalendar.get(Calendar.MONTH)
            var daychanging = changingCalendar.get(Calendar.DAY_OF_MONTH)
            var datechanging = changingCalendar.get(Calendar.DAY_OF_WEEK)

            newmonth = monthchanging + 1

            selectdate = "$yearchanging. $newmonth. $daychanging."

            binding.tvDateprint.text = selectdate
            binding.tvTextdate.text = getdate(datechanging)

            insertData(selectdate)

        }

        binding.btnPlus.setOnClickListener {
            changingCalendar.add(Calendar.DATE, 1)

            var yearchanging = changingCalendar.get(Calendar.YEAR)
            var monthchanging = changingCalendar.get(Calendar.MONTH)
            var daychanging = changingCalendar.get(Calendar.DAY_OF_MONTH)
            var datechanging = changingCalendar.get(Calendar.DAY_OF_WEEK)

            newmonth = monthchanging + 1

            selectdate = "$yearchanging. $newmonth. $daychanging."

            binding.tvDateprint.text = selectdate
            binding.tvTextdate.text = getdate(datechanging)

            insertData(selectdate)

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LoadingActivity::class.java)
        intent.putExtra("from", "Menu")
        startActivity(intent)
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_action_bar, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_today -> {
                var newmonth = monthnow + 1

                changingCalendar.set(yearnow, monthnow, daynow)

                var selectdate = "$yearnow. $newmonth. $daynow."

                binding.tvDateprint.text = selectdate
                binding.tvTextdate.text = getdate(datenow)

                insertData(selectdate)


                return true

            }
            R.id.action_calendar -> {

                val daypick = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{
                    view, year, month, dayOfMonth ->

                    changingCalendar.set(year, month, dayOfMonth)

                    var newmonth = month + 1

                    var selectdate = "$year. $newmonth. $dayOfMonth."

                    binding.tvDateprint.text = selectdate
                    binding.tvTextdate.text = getdate(changingCalendar.get(Calendar.DAY_OF_WEEK))

                    insertData(selectdate)


                }, yearnow, monthnow, daynow)

                daypick.show()

                return true

            }
            R.id.action_menu -> {
                return true
            }
            R.id.action_change -> {
                val intent = Intent(this, LoadingActivity::class.java)
                intent.putExtra("from", "Menu")
                startActivity(intent)
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
                finish()
                return true
            }
            R.id.action_manager -> {
                val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val view = inflater.inflate(R.layout.manager_alert_popup, null)
                val managerpassword : EditText = view.findViewById(R.id.et_manager_password)

                val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)
                val menuname = pref.getString("name", "").toString()
                val managerPassword = pref.getString("$menuname manager password", "").toString()

                managerpassword.setText(managerPassword)

                val alertDialog = AlertDialog.Builder(this)
                    .setTitle(R.string.manager_sign_in)
                    .setPositiveButton(R.string.confirm){dialog, which ->
                        if(managerpassword.text.toString().isNotBlank()){
                            val menunameRef = menucollection.document(menuname)
                            menunameRef.get().addOnSuccessListener { document ->
                                if(document.data != null){

                                    val savedmanagerpassword = document.getString("manager password").toString()

                                    if(managerpassword.text.toString() == savedmanagerpassword){
                                        pref.edit().putString("$menuname manager password", managerpassword.text.toString()).apply()

                                        val intent = Intent(this, ManagerActivity::class.java)
                                        startActivity(intent)
                                    }else{
                                        Toast.makeText(this, R.string.manager_password_wrong, Toast.LENGTH_SHORT).show()
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
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun getdate(number : Int) =
        when(number){
            1 -> getString(R.string.sunday)
            2 -> getString(R.string.monday)
            3 -> getString(R.string.tuesday)
            4 -> getString(R.string.wednesday)
            5 -> getString(R.string.thursday)
            6 -> getString(R.string.friday)
            7 -> getString(R.string.saturday)
            else -> "error"
        }

    private fun insertData(date : String) {
        val menulist = ArrayList<MainData>()
        Log.d(TAG, "insertData menulist length " + menulist.size.toString())

        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)
        val menuname = pref.getString("name", "").toString()

        //아침 -> 점심 -> 저녁 -> 출력

        insertBreakfast(menulist, date, menuname)
    }

    private fun insertBreakfast(menulist : ArrayList<MainData>, date : String, menuname : String){
        Log.d(TAG, "start insertBreakfast")
        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        if(pref.getString("$date breakfast 1", "") == ""){
            val breakfastRef = db.collection("Menu").document(menuname).collection(date)
                .document("breakfast")
            breakfastRef.get().addOnSuccessListener { document ->
                if (document.data != null) {

                    var meal1 = document.getString("1").toString()
                    var meal2 = document.getString("2").toString()
                    var meal3 = document.getString("3").toString()
                    var meal4 = document.getString("4").toString()
                    var meal5 = document.getString("5").toString()
                    var meal6 = document.getString("6").toString()

                    if (meal1 == "null") {
                        meal1 = ""
                    }
                    if (meal2 =="null") {
                        meal2 = ""
                    }
                    if (meal3 == "null") {
                        meal3 = ""
                    }
                    if (meal4 == "null") {
                        meal4 = ""
                    }
                    if (meal5 == "null") {
                        meal5 = ""
                    }
                    if (meal6 == "null") {
                        meal6 = ""
                    }

                    menulist += MainData(getString(R.string.breakfast), meal1, meal2, meal3, meal4, meal5, meal6)
                    insertLunch(menulist, date, menuname)
                } else {
                    menulist += MainData(getString(R.string.breakfast), "", "", "",
                        "", "", "")
                    insertLunch(menulist, date, menuname)
                }
            }
        }else{
            menulist += MainData(getString(R.string.breakfast),
                pref.getString("$date breakfast 1", "").toString(),
                pref.getString("$date breakfast 2", "").toString(),
                pref.getString("$date breakfast 3", "").toString(),
                pref.getString("$date breakfast 4", "").toString(),
                pref.getString("$date breakfast 5", "").toString(),
                pref.getString("$date breakfast 6", "").toString()
                )
            insertLunch(menulist, date, menuname)
        }

    }

    private fun insertLunch(menulist : ArrayList<MainData>, date : String, menuname : String){
        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        if(pref.getString("$date lunch 1", "") == ""){
            val lunchRef = db.collection("Menu").document(menuname).collection(date)
                .document("lunch")
            lunchRef.get().addOnSuccessListener { document ->
                if (document.data != null) {
                    var meal1 = document.getString("1").toString()
                    var meal2 = document.getString("2").toString()
                    var meal3 = document.getString("3").toString()
                    var meal4 = document.getString("4").toString()
                    var meal5 = document.getString("5").toString()
                    var meal6 = document.getString("6").toString()

                    if (meal1 == "null") {
                        meal1 = ""
                    }
                    if (meal2 == "null") {
                        meal2 = ""
                    }
                    if (meal3 == "null") {
                        meal3 = ""
                    }
                    if (meal4 == "null") {
                        meal4 = ""
                    }
                    if (meal5 == "null") {
                        meal5 = ""
                    }
                    if (meal6 == "null") {
                        meal6 = ""
                    }
                    menulist += MainData(getString(R.string.lunch),  meal1, meal2, meal3, meal4, meal5, meal6)
                    insertDinner(menulist, date, menuname)
                } else {
                    menulist += MainData(getString(R.string.lunch), "", "", "",
                        "", "", "")
                    insertDinner(menulist, date, menuname)
                }
            }
        }else{
            menulist += MainData(getString(R.string.lunch),
                pref.getString("$date lunch 1", "").toString(),
                pref.getString("$date lunch 2", "").toString(),
                pref.getString("$date lunch 3", "").toString(),
                pref.getString("$date lunch 4", "").toString(),
                pref.getString("$date lunch 5", "").toString(),
                pref.getString("$date lunch 6", "").toString()
            )
            insertDinner(menulist, date, menuname)
        }
    }

    private fun insertDinner(menulist : ArrayList<MainData>, date : String, menuname : String) {
        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        if(pref.getString("$date dinner 1", "") == ""){
            val dinnerRef = db.collection("Menu").document(menuname).collection(date)
                .document("dinner")
            dinnerRef.get().addOnSuccessListener { document ->
                if (document.data != null) {
                    var meal1 = document.getString("1").toString()
                    var meal2 = document.getString("2").toString()
                    var meal3 = document.getString("3").toString()
                    var meal4 = document.getString("4").toString()
                    var meal5 = document.getString("5").toString()
                    var meal6 = document.getString("6").toString()

                    if (meal1 == "null") {
                        meal1 = ""
                    }
                    if (meal2 == "null") {
                        meal2 = ""
                    }
                    if (meal3 == "null") {
                        meal3 = ""
                    }
                    if (meal4 == "null") {
                        meal4 = ""
                    }
                    if (meal5 == "null") {
                        meal5 = ""
                    }
                    if (meal6 == "null") {
                        meal6 = ""
                    }

                    menulist += MainData(getString(R.string.dinner), meal1, meal2, meal3, meal4, meal5, meal6)

                    binding.rvMenu.adapter = ItemAdapter(menulist)
                    binding.rvMenu.layoutManager = LinearLayoutManager(this)
                    binding.rvMenu.setHasFixedSize(true)
                } else {
                    menulist += MainData(
                        getString(R.string.dinner), "", "", "",
                        "", "", ""
                    )

                    binding.rvMenu.adapter = ItemAdapter(menulist)
                    binding.rvMenu.layoutManager = LinearLayoutManager(this)
                    binding.rvMenu.setHasFixedSize(true)
                }
            }
        }else{
            menulist += MainData(getString(R.string.dinner),
                pref.getString("$date dinner 1", "").toString(),
                pref.getString("$date dinner 2", "").toString(),
                pref.getString("$date dinner 3", "").toString(),
                pref.getString("$date dinner 4", "").toString(),
                pref.getString("$date dinner 5", "").toString(),
                pref.getString("$date dinner 6", "").toString()
            )
            Log.d(TAG, "print what? : $menuname")
            binding.rvMenu.adapter = ItemAdapter(menulist)
            binding.rvMenu.layoutManager = LinearLayoutManager(this)
            binding.rvMenu.setHasFixedSize(true)
        }
    }
}