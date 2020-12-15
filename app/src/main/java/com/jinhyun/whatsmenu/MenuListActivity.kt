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
import kotlinx.android.synthetic.main.activity_menu_list.*
import kotlinx.android.synthetic.main.custom_actionbar.*
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

    var changingcalendar = calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_list)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)
        val menuname = pref.getString("name", "").toString()

        val menutitle = "$menuname " + getString(R.string.menu)

        toolbar_title.text = menutitle


        changingcalendar.set(yearnow, monthnow, daynow)

        var newmonth = monthnow + 1

        var selectdate = "$yearnow. $newmonth. $daynow."

        tv_dateprint.text = selectdate
        tv_textdate.text = getdate(datenow)

        insertData(selectdate)


        btn_minus.setOnClickListener {
            changingcalendar.add(Calendar.DATE, -1)

            var yearchanging = changingcalendar.get(Calendar.YEAR)
            var monthchanging = changingcalendar.get(Calendar.MONTH)
            var daychanging = changingcalendar.get(Calendar.DAY_OF_MONTH)
            var datechanging = changingcalendar.get(Calendar.DAY_OF_WEEK)

            newmonth = monthchanging + 1

            selectdate = "$yearchanging. $newmonth. $daychanging."

            tv_dateprint.text = selectdate
            tv_textdate.text = getdate(datechanging)

            insertData(selectdate)

        }

        btn_plus.setOnClickListener {
            changingcalendar.add(Calendar.DATE, 1)

            var yearchanging = changingcalendar.get(Calendar.YEAR)
            var monthchanging = changingcalendar.get(Calendar.MONTH)
            var daychanging = changingcalendar.get(Calendar.DAY_OF_MONTH)
            var datechanging = changingcalendar.get(Calendar.DAY_OF_WEEK)

            newmonth = monthchanging + 1

            selectdate = "$yearchanging. $newmonth. $daychanging."

            tv_dateprint.text = selectdate
            tv_textdate.text = getdate(datechanging)

            insertData(selectdate)

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.menu_action_bar, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_today -> {
                var newmonth = monthnow + 1

                changingcalendar.set(yearnow, monthnow, daynow)

                var selectdate = "$yearnow. $newmonth. $daynow."

                tv_dateprint.text = selectdate
                tv_textdate.text = getdate(datenow)

                insertData(selectdate)


                return true

            }
            R.id.action_calendar -> {

                val daypick = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{
                    view, year, month, dayOfMonth ->

                    changingcalendar.set(year, month, dayOfMonth)

                    var newmonth = month + 1

                    var selectdate = "$year. $newmonth. $dayOfMonth."

                    tv_dateprint.text = selectdate
                    tv_textdate.text = getdate(changingcalendar.get(Calendar.DAY_OF_WEEK))

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
                var managerpassword : EditText = view.findViewById(R.id.et_manager_password)

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
                                        finish()
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
                    val meal1 = document.getString("1").toString()
                    val meal2 = document.getString("2").toString()
                    val meal3 = document.getString("3").toString()
                    val meal4 = document.getString("4").toString()
                    val meal5 = document.getString("5").toString()
                    val meal6 = document.getString("6").toString()
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
                    val meal1 = document.getString("1").toString()
                    val meal2 = document.getString("2").toString()
                    val meal3 = document.getString("3").toString()
                    val meal4 = document.getString("4").toString()
                    val meal5 = document.getString("5").toString()
                    val meal6 = document.getString("6").toString()
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
                    val meal1 = document.getString("1").toString()
                    val meal2 = document.getString("2").toString()
                    val meal3 = document.getString("3").toString()
                    val meal4 = document.getString("4").toString()
                    val meal5 = document.getString("5").toString()
                    val meal6 = document.getString("6").toString()
                    menulist += MainData(getString(R.string.dinner), meal1, meal2, meal3, meal4, meal5, meal6)

                    rv_menu.adapter = ItemAdapter(menulist)
                    rv_menu.layoutManager = LinearLayoutManager(this)
                    rv_menu.setHasFixedSize(true)
                } else {
                    menulist += MainData(
                        getString(R.string.dinner), "", "", "",
                        "", "", ""
                    )

                    rv_menu.adapter = ItemAdapter(menulist)
                    rv_menu.layoutManager = LinearLayoutManager(this)
                    rv_menu.setHasFixedSize(true)
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
            rv_menu.adapter = ItemAdapter(menulist)
            rv_menu.layoutManager = LinearLayoutManager(this)
            rv_menu.setHasFixedSize(true)
        }
    }
}