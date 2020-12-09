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

        //하루 전으로 가기 버튼

        btn_mg_minus.setOnClickListener {
            changingcalendar.add(Calendar.DATE, -1)

            var yearchanging = changingcalendar.get(Calendar.YEAR)
            var monthchanging = changingcalendar.get(Calendar.MONTH)
            var daychanging = changingcalendar.get(Calendar.DAY_OF_MONTH)

            newmonth = monthchanging + 1

            selectdate = "$yearchanging. $newmonth. $daychanging."

            tv_mg_dateprint.text = selectdate

        }

        //하루 후로 가기 버튼

        btn_mg_plus.setOnClickListener {
            changingcalendar.add(Calendar.DATE, 1)

            var yearchanging = changingcalendar.get(Calendar.YEAR)
            var monthchanging = changingcalendar.get(Calendar.MONTH)
            var daychanging = changingcalendar.get(Calendar.DAY_OF_MONTH)

            newmonth = monthchanging + 1

            selectdate = "$yearchanging. $newmonth. $daychanging."

            tv_mg_dateprint.text = selectdate

        }

        //아침메뉴 저장 버튼
        breakfastSave.setOnClickListener {
            val breakfast = hashMapOf(
                "1" to breakfastInput1.text.toString(),
                "2" to breakfastInput2.text.toString(),
                "3" to breakfastInput3.text.toString(),
                "4" to breakfastInput4.text.toString(),
                "5" to breakfastInput5.text.toString(),
                "6" to breakfastInput6.text.toString()
            )

            menuCollection.document(menuname).collection(tv_mg_dateprint.text.toString())
                .document("breakfast").set(breakfast)
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
            val lunch = hashMapOf(
                "1" to lunchInput1.text.toString(),
                "2" to lunchInput2.text.toString(),
                "3" to lunchInput3.text.toString(),
                "4" to lunchInput4.text.toString(),
                "5" to lunchInput5.text.toString(),
                "6" to lunchInput6.text.toString()
            )

            menuCollection.document(menuname).collection(tv_mg_dateprint.text.toString())
                .document("lunch").set(lunch)
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
            val dinner = hashMapOf(
                "1" to dinnerInput1.text.toString(),
                "2" to dinnerInput2.text.toString(),
                "3" to dinnerInput3.text.toString(),
                "4" to dinnerInput4.text.toString(),
                "5" to dinnerInput5.text.toString(),
                "6" to dinnerInput6.text.toString()
            )

            menuCollection.document(menuname).collection(tv_mg_dateprint.text.toString())
                .document("dinner").set(dinner)
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

                return true

            }
            R.id.action_calendar -> {

                val daypick = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{
                        view, year, month, dayOfMonth ->

                    changingcalendar.set(year, month, dayOfMonth)

                    var newmonth = month + 1

                    var selectdate = "$yearnow. $newmonth. $dayOfMonth."

                    tv_mg_dateprint.text = selectdate


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