package com.cleytongoncalves.ofertala.features

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.cleytongoncalves.ofertala.R
import com.cleytongoncalves.ofertala.features.base.BaseActivity
import kotlinx.android.synthetic.main.activity_new_auction.*

class NewAuctionActivity : BaseActivity() {
    companion object {
        fun getStartIntent(context: Context): Intent {
            return Intent(context, NewAuctionActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(detail_toolbar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun layoutId() = R.layout.activity_new_auction

}