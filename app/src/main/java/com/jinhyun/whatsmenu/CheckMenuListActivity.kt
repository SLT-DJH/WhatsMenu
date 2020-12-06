package com.jinhyun.whatsmenu

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_search_menu_list.*

class CheckMenuListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_menu_list)

        iv_back.setOnClickListener {
            switch()
        }

        btn_add.setOnClickListener {

        }

        btn_delete.setOnClickListener {

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
}