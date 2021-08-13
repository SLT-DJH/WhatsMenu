package com.jinhyun.whatsmenu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.firestore.FirebaseFirestore
import com.jinhyun.whatsmenu.databinding.ActivityLoadingBinding
import java.util.*

class LoadingActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_CODE_UPDATE = 0
    }

    //set today
    val calendar = Calendar.getInstance()
    val yearnow = calendar.get(Calendar.YEAR)
    val monthnow = calendar.get(Calendar.MONTH)
    val daynow = calendar.get(Calendar.DAY_OF_MONTH)
    var pluscalendar = calendar
    var minuscalendar = calendar

    val TAG = "LoadingActivity"

    val db = FirebaseFirestore.getInstance()
    val menucollection = db.collection("Menu")

    lateinit var appUpdateManager: AppUpdateManager
    lateinit var binding: ActivityLoadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvCopyright.visibility = View.INVISIBLE
        binding.tvIntroduction.visibility = View.INVISIBLE
        binding.btnCheckMenuName.visibility = View.INVISIBLE
        binding.btnInputMenuName.visibility = View.INVISIBLE
        binding.tvAsk.visibility = View.INVISIBLE

        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        if (intent.hasExtra("from")) {
            Log.d(TAG, "from ${intent.getStringExtra("from")}")

            binding.tvCopyright.visibility = View.VISIBLE
            binding.tvIntroduction.visibility = View.VISIBLE
            binding.btnCheckMenuName.visibility = View.VISIBLE
            binding.btnInputMenuName.visibility = View.VISIBLE
            binding.tvAsk.visibility = View.VISIBLE

        } else {
            appUpdateManager = AppUpdateManagerFactory.create(this)

            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                Log.d(TAG, "appUpdateInfo : $appUpdateInfo")

                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        REQUEST_CODE_UPDATE
                    )
                }
            }

            getdata()

            val noAnimation = AnimationUtils.loadAnimation(this, R.anim.not_loading_animation)

            binding.ivLogo.startAnimation(noAnimation)

            if (pref.getString("name", "") == "") {
                startLoading()
            } else {
                skipLoading()
            }
        }

        binding.btnCheckMenuName.setOnClickListener {
            pref.edit().putString("manager password", "").apply()
            val intent = Intent(this, CheckMenuListActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left)
            finish()
        }

        binding.btnInputMenuName.setOnClickListener {
            showAlert()
        }

        binding.tvAsk.setOnClickListener {
            val email = Intent(
                Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", "cmjh951330@gmail.com", null)
            )
                .putExtra(Intent.EXTRA_SUBJECT, "[QnA Quest] ")
                .putExtra(Intent.EXTRA_TEXT, "")
            startActivity(Intent.createChooser(email, ""))
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_UPDATE) {
            if (resultCode != Activity.RESULT_OK) {
                Toast.makeText(this, "업데이트 취소", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    private fun getdata() {
        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        if (pref.getString("name", "") != "") {
            val menuname = pref.getString("name", "").toString()

            //add data
            for (i in 0 until 15) {
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
            for (i in -1 downTo -14) {
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

    private fun startget(selectdate: String, menuname: String) {
        //breakfast data
        val breakfastRef = db.collection("Menu").document(menuname).collection(selectdate)
            .document("breakfast")
        breakfastRef.get().addOnSuccessListener { document ->
            val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

            if (document.data != null) {
                for (i in 1 until 7) {
                    if (document.getString("$i") != null) {
                        pref.edit().putString(
                            "$selectdate breakfast $i",
                            document.getString("$i").toString()
                        ).apply()
                    } else {
                        pref.edit().putString("$selectdate breakfast $i", "").apply()
                    }
                }

            } else {
                for (i in 1 until 7) {
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
                for (i in 1 until 7) {
                    if (document.getString("$i") != null) {
                        pref.edit()
                            .putString("$selectdate lunch $i", document.getString("$i").toString())
                            .apply()
                    } else {
                        pref.edit().putString("$selectdate lunch $i", "").apply()
                    }
                }
            } else {
                for (i in 1 until 7) {
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
                for (i in 1 until 7) {
                    if (document.getString("$i") != null) {
                        pref.edit()
                            .putString("$selectdate dinner $i", document.getString("$i").toString())
                            .apply()
                    } else {
                        pref.edit().putString("$selectdate dinner $i", "").apply()
                    }
                }

            } else {
                for (i in 1 until 7) {
                    pref.edit().putString("$selectdate dinner $i", "").apply()
                }
            }
        }
    }

    private fun startdelete(selectdate: String, menuname: String) {
        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)
        //delete breakfast data
        if (pref.getString("$selectdate breakfast 1", "") != "") {
            for (i in 1 until 7) {
                pref.edit().remove("$selectdate breakfast $i").apply()
            }
        }
        //delete lunch data
        if (pref.getString("$selectdate lunch 1", "") != "") {
            for (i in 1 until 7) {
                pref.edit().remove("$selectdate lunch $i").apply()
            }
        }
        //delete dinner data
        if (pref.getString("$selectdate dinner 1", "") != "") {
            for (i in 1 until 7) {
                pref.edit().remove("$selectdate dinner $i").apply()
            }
        }

    }

    private fun skipLoading() {
        Handler(mainLooper).postDelayed({
            val intent = Intent(applicationContext, MenuListActivity::class.java)
            startActivity(intent)

            finish()
        }, 2000)
    }

    private fun startLoading() {
        Handler(mainLooper).postDelayed({
            val upAnimation = AnimationUtils.loadAnimation(this, R.anim.loading_animation)
            val alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.alpha_animation)

            binding.ivLogo.startAnimation(upAnimation)

            binding.tvCopyright.startAnimation(alphaAnimation)
            binding.tvIntroduction.startAnimation(alphaAnimation)
            binding.btnCheckMenuName.startAnimation(alphaAnimation)
            binding.btnInputMenuName.startAnimation(alphaAnimation)

            binding.tvCopyright.visibility = View.VISIBLE
            binding.tvIntroduction.visibility = View.VISIBLE
            binding.btnCheckMenuName.visibility = View.VISIBLE
            binding.btnInputMenuName.visibility = View.VISIBLE
            binding.tvAsk.visibility = View.VISIBLE
        }, 2000)
    }

    private fun showAlert() {

        val pref = this.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.input_menu_alert_popup, null)
        val name: EditText = view.findViewById(R.id.et_request_input_name)
        val password: EditText = view.findViewById(R.id.et_password)

        val menuId = pref.getString("name", "").toString()
        val menuPassword = pref.getString("password", "").toString()

        name.setText(menuId)
        password.setText(menuPassword)

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.input_menu_list)
            .setPositiveButton(R.string.confirm) { dialog, which ->
                if (name.text.toString().isNotBlank() && password.text.toString().isNotBlank()) {
                    val menunameRef = menucollection.document(name.text.toString())
                    menunameRef.get().addOnSuccessListener { document ->
                        if (document.data != null) {
                            pref.edit().putString("name", name.text.toString()).apply()
                            pref.edit().putString("password", password.text.toString()).apply()
                            getdata()

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

    override fun onResume() {
        super.onResume()

//        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
//            if (appUpdateInfo.updateAvailability() ==
//                UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
//            ) {
//                appUpdateManager.startUpdateFlowForResult(
//                    appUpdateInfo,
//                    AppUpdateType.IMMEDIATE,
//                    this,
//                    REQUEST_CODE_UPDATE
//                )
//            }
//        }
    }
}