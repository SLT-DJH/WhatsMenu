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
import com.jinhyun.whatsmenu.databinding.ActivityManagerBinding
import com.jinhyun.whatsmenu.databinding.CustomActionbarBinding
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

    lateinit var binding: ActivityManagerBinding
    lateinit var toolbarBinding: CustomActionbarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerBinding.inflate(layoutInflater)
        toolbarBinding = CustomActionbarBinding.bind(binding.root)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)
        val menuname = pref.getString("name", "").toString()

        val menutitle = "$menuname " + getString(R.string.menu)

        toolbarBinding.toolbarTitle.text = menutitle

        Log.d(TAG, "get string : $menuname")

        //현재 날짜 구하기

        changingcalendar.set(yearnow, monthnow, daynow)

        var newmonth = monthnow + 1

        var selectdate = "$yearnow. $newmonth. $daynow."

        binding.tvMgDateprint.text = selectdate
        binding.tvMgTextdate.text = getDate(datenow)

        //하루 전으로 가기 버튼

        binding.btnMgMinus.setOnClickListener {
            changingcalendar.add(Calendar.DATE, -1)

            val monthchanging = changingcalendar.get(Calendar.MONTH)
            val datechanging = changingcalendar.get(Calendar.DAY_OF_WEEK)

            newmonth = monthchanging + 1

            selectdate = "${changingcalendar.get(Calendar.YEAR)}. " +
                    "${changingcalendar.get(Calendar.MONTH) + 1}. " +
                    "${changingcalendar.get(Calendar.DAY_OF_MONTH)}."

            binding.tvMgDateprint.text = selectdate
            binding.tvMgTextdate.text = getDate(datechanging)

        }

        //하루 후로 가기 버튼

        binding.btnMgPlus.setOnClickListener {
            changingcalendar.add(Calendar.DATE, 1)

            var monthchanging = changingcalendar.get(Calendar.MONTH)
            var datechanging = changingcalendar.get(Calendar.DAY_OF_WEEK)

            newmonth = monthchanging + 1

            selectdate = "${changingcalendar.get(Calendar.YEAR)}. " +
                    "$newmonth. ${changingcalendar.get(Calendar.DAY_OF_MONTH)}."

            binding.tvMgDateprint.text = selectdate
            binding.tvMgTextdate.text = getDate(datechanging)

        }

        //아침메뉴 저장 버튼
        binding.breakfastSave.setOnClickListener {
            val tempdate = binding.tvMgDateprint.text.toString()

            val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

            val breakfastData = hashMapOf<String, String>()

            if (binding.breakfastInput1.text.isNotBlank()){
                pref.edit().putString("$tempdate breakfast 1", binding.breakfastInput1.text.toString()).apply()
                breakfastData["1"] =  binding.breakfastInput1.text.toString()
            }
            if (binding.breakfastInput2.text.isNotBlank()){
                pref.edit().putString("$tempdate breakfast 2", binding.breakfastInput2.text.toString()).apply()
                breakfastData["2"] =  binding.breakfastInput2.text.toString()
            }
            if (binding.breakfastInput3.text.isNotBlank()){
                pref.edit().putString("$tempdate breakfast 3", binding.breakfastInput3.text.toString()).apply()
                breakfastData["3"] =  binding.breakfastInput3.text.toString()
            }
            if (binding.breakfastInput4.text.isNotBlank()){
                pref.edit().putString("$tempdate breakfast 4", binding.breakfastInput4.text.toString()).apply()
                breakfastData["4"] =  binding.breakfastInput4.text.toString()
            }
            if (binding.breakfastInput5.text.isNotBlank()){
                pref.edit().putString("$tempdate breakfast 5", binding.breakfastInput5.text.toString()).apply()
                breakfastData["5"] =  binding.breakfastInput5.text.toString()
            }
            if (binding.breakfastInput6.text.isNotBlank()){
                pref.edit().putString("$tempdate breakfast 6", binding.breakfastInput6.text.toString()).apply()
                breakfastData["6"] =  binding.breakfastInput6.text.toString()
            }

            menuCollection.document(menuname).collection(binding.tvMgDateprint.text.toString())
                .document("breakfast").set(breakfastData, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
                    binding.breakfastInput1.text.clear()
                    binding.breakfastInput2.text.clear()
                    binding.breakfastInput3.text.clear()
                    binding.breakfastInput4.text.clear()
                    binding.breakfastInput5.text.clear()
                    binding.breakfastInput6.text.clear()

                }
                .addOnFailureListener{ Toast.makeText(this, getString(R.string.save_fail), Toast.LENGTH_SHORT).show()}

        }

        //점심메뉴 저장 버튼
        binding.lunchSave.setOnClickListener {
            val tempdate = binding.tvMgDateprint.text.toString()

            val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

            val lunchData = hashMapOf<String, String>()

            if (binding.lunchInput1.text.isNotBlank()){
                pref.edit().putString("$tempdate lunch 1", binding.lunchInput1.text.toString()).apply()
                lunchData["1"] =  binding.lunchInput1.text.toString()
            }
            if (binding.lunchInput2.text.isNotBlank()){
                pref.edit().putString("$tempdate lunch 2", binding.lunchInput2.text.toString()).apply()
                lunchData["2"] =  binding.lunchInput2.text.toString()
            }
            if (binding.lunchInput3.text.isNotBlank()){
                pref.edit().putString("$tempdate lunch 3", binding.lunchInput3.text.toString()).apply()
                lunchData["3"] =  binding.lunchInput3.text.toString()
            }
            if (binding.lunchInput4.text.isNotBlank()){
                pref.edit().putString("$tempdate lunch 4", binding.lunchInput4.text.toString()).apply()
                lunchData["4"] =  binding.lunchInput4.text.toString()
            }
            if (binding.lunchInput5.text.isNotBlank()){
                pref.edit().putString("$tempdate lunch 5", binding.lunchInput5.text.toString()).apply()
                lunchData["5"] =  binding.lunchInput5.text.toString()
            }
            if (binding.lunchInput6.text.isNotBlank()){
                pref.edit().putString("$tempdate lunch 6", binding.lunchInput6.text.toString()).apply()
                lunchData["6"] =  binding.lunchInput6.text.toString()
            }

            menuCollection.document(menuname).collection(binding.tvMgDateprint.text.toString())
                .document("lunch").set(lunchData, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
                    binding.lunchInput1.text.clear()
                    binding.lunchInput2.text.clear()
                    binding.lunchInput3.text.clear()
                    binding.lunchInput4.text.clear()
                    binding.lunchInput5.text.clear()
                    binding.lunchInput6.text.clear()

                }
                .addOnFailureListener{ Toast.makeText(this, getString(R.string.save_fail), Toast.LENGTH_SHORT).show()}

        }

        //저녁메뉴 저장 버튼
        binding.dinnerSave.setOnClickListener{
            val tempdate = binding.tvMgDateprint.text.toString()

            val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

            val dinnerData = hashMapOf<String, String>()

            if (binding.dinnerInput1.text.isNotBlank()){
                pref.edit().putString("$tempdate dinner 1", binding.dinnerInput1.text.toString()).apply()
                dinnerData["1"] =  binding.dinnerInput1.text.toString()
            }
            if (binding.dinnerInput2.text.isNotBlank()){
                pref.edit().putString("$tempdate dinner 2", binding.dinnerInput2.text.toString()).apply()
                dinnerData["2"] =  binding.dinnerInput2.text.toString()
            }
            if (binding.dinnerInput3.text.isNotBlank()){
                pref.edit().putString("$tempdate dinner 3", binding.dinnerInput3.text.toString()).apply()
                dinnerData["3"] =  binding.dinnerInput3.text.toString()
            }
            if (binding.dinnerInput4.text.isNotBlank()){
                pref.edit().putString("$tempdate dinner 4", binding.dinnerInput4.text.toString()).apply()
                dinnerData["4"] =  binding.dinnerInput4.text.toString()
            }
            if (binding.dinnerInput5.text.isNotBlank()){
                pref.edit().putString("$tempdate dinner 5", binding.dinnerInput5.text.toString()).apply()
                dinnerData["5"] =  binding.dinnerInput5.text.toString()
            }
            if (binding.dinnerInput6.text.isNotBlank()){
                pref.edit().putString("$tempdate dinner 6", binding.dinnerInput6.text.toString()).apply()
                dinnerData["6"] =  binding.dinnerInput6.text.toString()
            }

            menuCollection.document(menuname).collection(binding.tvMgDateprint.text.toString())
                .document("dinner").set(dinnerData, SetOptions.merge())
                .addOnSuccessListener {
                    Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
                    binding.dinnerInput1.text.clear()
                    binding.dinnerInput2.text.clear()
                    binding.dinnerInput3.text.clear()
                    binding.dinnerInput4.text.clear()
                    binding.dinnerInput5.text.clear()
                    binding.dinnerInput6.text.clear()

                }
                .addOnFailureListener{ Toast.makeText(this, getString(R.string.save_fail), Toast.LENGTH_SHORT).show()}

        }

    }

    private fun getDate(number : Int) =
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

                binding.tvMgDateprint.text = selectdate
                binding.tvMgTextdate.text = getDate(datenow)

                return true

            }
            R.id.action_calendar -> {

                val daypick = DatePickerDialog(this, DatePickerDialog.OnDateSetListener{
                        view, year, month, dayOfMonth ->

                    changingcalendar.set(year, month, dayOfMonth)

                    var newmonth = month + 1

                    var selectdate = "$year. $newmonth. $dayOfMonth."

                    binding.tvMgDateprint.text = selectdate
                    binding.tvMgTextdate.text = getDate(changingcalendar.get(Calendar.DAY_OF_WEEK))


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