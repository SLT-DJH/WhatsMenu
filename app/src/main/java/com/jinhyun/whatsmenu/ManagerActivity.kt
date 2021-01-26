package com.jinhyun.whatsmenu

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_manager.*
import kotlinx.android.synthetic.main.custom_actionbar.*
import java.util.*

class ManagerActivity : AppCompatActivity() {
    val TAG = "ManagerActivitiy"

    val calendar = Calendar.getInstance()

    val db = FirebaseFirestore.getInstance()
    val menuCollection = db.collection("Menu")

    val yearnow = calendar.get(Calendar.YEAR)
    val monthnow = calendar.get(Calendar.MONTH)
    val daynow = calendar.get(Calendar.DAY_OF_MONTH)
    val datenow = calendar.get(Calendar.DAY_OF_WEEK)

    var changingcalendar = calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)
        val menuname = pref.getString("name", "").toString()

        val menutitle = "$menuname " + getString(R.string.menu)

        toolbar_title.text = menutitle

        Log.d(TAG, "get string : $menuname")

        //현재 날짜 구하기

        changingcalendar.set(yearnow, monthnow, daynow)

        var newmonth = monthnow + 1

        var selectdate = "$yearnow. $newmonth. $daynow."

        tv_mg_dateprint.text = selectdate
        tv_mg_textdate.text = getdate(datenow)

        //하루 전으로 가기 버튼

        btn_mg_minus.setOnClickListener {
            changingcalendar.add(Calendar.DATE, -1)

            var yearchanging = changingcalendar.get(Calendar.YEAR)
            var monthchanging = changingcalendar.get(Calendar.MONTH)
            var daychanging = changingcalendar.get(Calendar.DAY_OF_MONTH)
            var datechanging = changingcalendar.get(Calendar.DAY_OF_WEEK)

            newmonth = monthchanging + 1

            selectdate = "$yearchanging. $newmonth. $daychanging."

            tv_mg_dateprint.text = selectdate
            tv_mg_textdate.text = getdate(datechanging)

        }

        //하루 후로 가기 버튼

        btn_mg_plus.setOnClickListener {
            changingcalendar.add(Calendar.DATE, 1)

            var yearchanging = changingcalendar.get(Calendar.YEAR)
            var monthchanging = changingcalendar.get(Calendar.MONTH)
            var daychanging = changingcalendar.get(Calendar.DAY_OF_MONTH)
            var datechanging = changingcalendar.get(Calendar.DAY_OF_WEEK)

            newmonth = monthchanging + 1

            selectdate = "$yearchanging. $newmonth. $daychanging."

            tv_mg_dateprint.text = selectdate
            tv_mg_textdate.text = getdate(datechanging)

        }

        //아침메뉴 저장 버튼
        breakfastSave.setOnClickListener {
            val tempdate = tv_mg_dateprint.text.toString()

            val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

            val breakfastData = hashMapOf<String, String>()

            if (breakfastInput1.text.isNotBlank()){
                pref.edit().putString("$tempdate breakfast 1", breakfastInput1.text.toString()).apply()
                breakfastData["1"] =  breakfastInput1.text.toString()
            }
            if (breakfastInput2.text.isNotBlank()){
                pref.edit().putString("$tempdate breakfast 2", breakfastInput2.text.toString()).apply()
                breakfastData["2"] =  breakfastInput2.text.toString()
            }
            if (breakfastInput3.text.isNotBlank()){
                pref.edit().putString("$tempdate breakfast 3", breakfastInput3.text.toString()).apply()
                breakfastData["3"] =  breakfastInput3.text.toString()
            }
            if (breakfastInput4.text.isNotBlank()){
                pref.edit().putString("$tempdate breakfast 4", breakfastInput4.text.toString()).apply()
                breakfastData["4"] =  breakfastInput4.text.toString()
            }
            if (breakfastInput5.text.isNotBlank()){
                pref.edit().putString("$tempdate breakfast 5", breakfastInput5.text.toString()).apply()
                breakfastData["5"] =  breakfastInput5.text.toString()
            }
            if (breakfastInput6.text.isNotBlank()){
                pref.edit().putString("$tempdate breakfast 6", breakfastInput6.text.toString()).apply()
                breakfastData["6"] =  breakfastInput6.text.toString()
            }

            menuCollection.document(menuname).collection(tv_mg_dateprint.text.toString())
                .document("breakfast").set(breakfastData, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
                    breakfastInput1.text.clear()
                    breakfastInput2.text.clear()
                    breakfastInput3.text.clear()
                    breakfastInput4.text.clear()
                    breakfastInput5.text.clear()
                    breakfastInput6.text.clear()

                }
                .addOnFailureListener{ Toast.makeText(this, getString(R.string.save_fail), Toast.LENGTH_SHORT).show()}

        }

        //점심메뉴 저장 버튼
        lunchSave.setOnClickListener {
            val tempdate = tv_mg_dateprint.text.toString()

            val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

            val lunchData = hashMapOf<String, String>()

            if (lunchInput1.text.isNotBlank()){
                pref.edit().putString("$tempdate lunch 1", lunchInput1.text.toString()).apply()
                lunchData["1"] =  lunchInput1.text.toString()
            }
            if (lunchInput2.text.isNotBlank()){
                pref.edit().putString("$tempdate lunch 2", lunchInput2.text.toString()).apply()
                lunchData["2"] =  lunchInput2.text.toString()
            }
            if (lunchInput3.text.isNotBlank()){
                pref.edit().putString("$tempdate lunch 3", lunchInput3.text.toString()).apply()
                lunchData["3"] =  lunchInput3.text.toString()
            }
            if (lunchInput4.text.isNotBlank()){
                pref.edit().putString("$tempdate lunch 4", lunchInput4.text.toString()).apply()
                lunchData["4"] =  lunchInput4.text.toString()
            }
            if (lunchInput5.text.isNotBlank()){
                pref.edit().putString("$tempdate lunch 5", lunchInput5.text.toString()).apply()
                lunchData["5"] =  lunchInput5.text.toString()
            }
            if (lunchInput6.text.isNotBlank()){
                pref.edit().putString("$tempdate lunch 6", lunchInput6.text.toString()).apply()
                lunchData["6"] =  lunchInput6.text.toString()
            }

            menuCollection.document(menuname).collection(tv_mg_dateprint.text.toString())
                .document("lunch").set(lunchData, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
                    lunchInput1.text.clear()
                    lunchInput2.text.clear()
                    lunchInput3.text.clear()
                    lunchInput4.text.clear()
                    lunchInput5.text.clear()
                    lunchInput6.text.clear()

                }
                .addOnFailureListener{ Toast.makeText(this, getString(R.string.save_fail), Toast.LENGTH_SHORT).show()}

        }

        //저녁메뉴 저장 버튼
        dinnerSave.setOnClickListener{
            val tempdate = tv_mg_dateprint.text.toString()

            val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

            val dinnerData = hashMapOf<String, String>()

            if (dinnerInput1.text.isNotBlank()){
                pref.edit().putString("$tempdate dinner 1", dinnerInput1.text.toString()).apply()
                dinnerData["1"] =  dinnerInput1.text.toString()
            }
            if (dinnerInput2.text.isNotBlank()){
                pref.edit().putString("$tempdate dinner 2", dinnerInput2.text.toString()).apply()
                dinnerData["2"] =  dinnerInput2.text.toString()
            }
            if (dinnerInput3.text.isNotBlank()){
                pref.edit().putString("$tempdate dinner 3", dinnerInput3.text.toString()).apply()
                dinnerData["3"] =  dinnerInput3.text.toString()
            }
            if (dinnerInput4.text.isNotBlank()){
                pref.edit().putString("$tempdate dinner 4", dinnerInput4.text.toString()).apply()
                dinnerData["4"] =  dinnerInput4.text.toString()
            }
            if (dinnerInput5.text.isNotBlank()){
                pref.edit().putString("$tempdate dinner 5", dinnerInput5.text.toString()).apply()
                dinnerData["5"] =  dinnerInput5.text.toString()
            }
            if (dinnerInput6.text.isNotBlank()){
                pref.edit().putString("$tempdate dinner 6", dinnerInput6.text.toString()).apply()
                dinnerData["6"] =  dinnerInput6.text.toString()
            }

            menuCollection.document(menuname).collection(tv_mg_dateprint.text.toString())
                .document("dinner").set(dinnerData, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
                    dinnerInput1.text.clear()
                    dinnerInput2.text.clear()
                    dinnerInput3.text.clear()
                    dinnerInput4.text.clear()
                    dinnerInput5.text.clear()
                    dinnerInput6.text.clear()

                }
                .addOnFailureListener{ Toast.makeText(this, getString(R.string.save_fail), Toast.LENGTH_SHORT).show()}

        }

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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MenuListActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater = getMenuInflater()
        menuInflater.inflate(R.menu.menu_manager_action_bar, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_today -> {
                var newmonth = monthnow + 1

                changingcalendar.set(yearnow, monthnow, daynow)

                var selectdate = "$yearnow. $newmonth. $daynow."

                tv_mg_dateprint.text = selectdate
                tv_mg_textdate.text = getdate(datenow)

                return true

            }
            R.id.action_calendar -> {

                val daypick = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{
                        view, year, month, dayOfMonth ->

                    changingcalendar.set(year, month, dayOfMonth)

                    var newmonth = month + 1

                    var selectdate = "$year. $newmonth. $dayOfMonth."

                    tv_mg_dateprint.text = selectdate
                    tv_mg_textdate.text = getdate(changingcalendar.get(Calendar.DAY_OF_WEEK))


                }, yearnow, monthnow, daynow)

                daypick.show()

                return true

            }
            R.id.action_menu -> {
                return true
            }
            R.id.action_change -> {
                val intent = Intent(this, LoadingActivity::class.java)
                intent.putExtra("from", "Manager")
                startActivity(intent)
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right)
                finish()
                return true
            }
            R.id.action_user -> {
                val intent = Intent(this, MenuListActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}