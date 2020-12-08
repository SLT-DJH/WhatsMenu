package com.jinhyun.whatsmenu

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_menu_list.*
import kotlinx.android.synthetic.main.custom_actionbar.*
import java.util.*

class MenuListActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()

    val calendar = Calendar.getInstance()

    val TAG = "MenuListActivity"

    //현재 날짜 구하기

    val yearnow = calendar.get(Calendar.YEAR)
    val monthnow = calendar.get(Calendar.MONTH)
    val daynow = calendar.get(Calendar.DAY_OF_MONTH)

    var changingcalendar = calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_list)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val menuname = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)
            .getString("name", "").toString()

        val menutitle = "$menuname " + getString(R.string.menu)

        toolbar_title.text = menutitle

        Log.d(TAG, "get string : $menuname")


        changingcalendar.set(yearnow, monthnow, daynow)

        var newmonth = monthnow + 1

        var selectdate = "$yearnow. $newmonth. $daynow."

        tv_dateprint.text = selectdate

        insertData(selectdate)


        btn_minus.setOnClickListener {
            changingcalendar.add(Calendar.DATE, -1)

            var yearchanging = changingcalendar.get(Calendar.YEAR)
            var monthchanging = changingcalendar.get(Calendar.MONTH)
            var daychanging = changingcalendar.get(Calendar.DAY_OF_MONTH)

            newmonth = monthchanging + 1

            selectdate = "$yearchanging. $newmonth. $daychanging."

            tv_dateprint.text = selectdate

            insertData(selectdate)

        }

        btn_plus.setOnClickListener {
            changingcalendar.add(Calendar.DATE, 1)

            var yearchanging = changingcalendar.get(Calendar.YEAR)
            var monthchanging = changingcalendar.get(Calendar.MONTH)
            var daychanging = changingcalendar.get(Calendar.DAY_OF_MONTH)

            newmonth = monthchanging + 1

            selectdate = "$yearchanging. $newmonth. $daychanging."

            tv_dateprint.text = selectdate

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

                insertData(selectdate)


                return true

            }
            R.id.action_calendar -> {

                val daypick = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{
                    view, year, month, dayOfMonth ->

                    changingcalendar.set(year, month, dayOfMonth)

                    var newmonth = month + 1

                    var selectdate = "$yearnow. $newmonth. $dayOfMonth."

                    tv_dateprint.text = selectdate

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
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun insertData(date : String) {
        val menuname = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)
            .getString("name", "").toString()
        val menulist = ArrayList<MainData>()

        //아침 -> 점심 -> 저녁 -> 출력

        insertBreakfast(menulist, date, menuname)
    }

    private fun insertBreakfast(menulist : ArrayList<MainData>, date : String, menuname : String){

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
                menulist += MainData("아침", meal1, meal2, meal3, meal4, meal5, meal6)
                insertLunch(menulist, date, menuname)
            } else {
                menulist += MainData("아침", "", "", "",
                    "", "", "")
                insertLunch(menulist, date, menuname)
            }
        }

    }

    private fun insertLunch(menulist : ArrayList<MainData>, date : String, menuname : String){

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
                menulist += MainData("점심",  meal1, meal2, meal3, meal4, meal5, meal6)
                insertDinner(menulist, date, menuname)
            } else {
                menulist += MainData("점심", "", "", "",
                    "", "", "")
                insertDinner(menulist, date, menuname)
            }
        }
    }

    private fun insertDinner(menulist : ArrayList<MainData>, date : String, menuname : String) {
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
                menulist += MainData("저녁", meal1, meal2, meal3, meal4, meal5, meal6)

                rv_menu.adapter = ItemAdapter(menulist)
                rv_menu.layoutManager = LinearLayoutManager(this)
                rv_menu.setHasFixedSize(true)
            } else {
                menulist += MainData(
                    "저녁", "", "", "",
                    "", "", ""
                )

                rv_menu.adapter = ItemAdapter(menulist)
                rv_menu.layoutManager = LinearLayoutManager(this)
                rv_menu.setHasFixedSize(true)
            }
        }
    }
}