package com.whitecoats.clinicplus

import android.content.Intent
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.zoho.livechat.android.ZohoLiveChat
import com.zoho.salesiqembed.ZohoSalesIQ

class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)
        val backArrow = ContextCompat.getDrawable(this, R.drawable.ic_arrow_back)
        backArrow?.setColorFilter(ContextCompat.getColor(this,R.color.colorWhite), PorterDuff.Mode.SRC_ATOP)
        val toolbar = findViewById<Toolbar>(R.id.commListToolbar)
        toolbar.navigationIcon = backArrow // your drawable
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        val whatAppCard = findViewById<CardView>(R.id.getHelpWhatAppCard)
        val emailCard = findViewById<CardView>(R.id.getHelpEmailCard)
        val supportCard = findViewById<CardView>(R.id.getHelpLiveChatCard)
        supportCard.setOnClickListener { ZohoLiveChat.Chat.open() }
        whatAppCard.setOnClickListener {
            val url = "https://api.whatsapp.com/send?phone=+91 8080200999"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        emailCard.setOnClickListener {
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "practicesupport@whitecoats.com", null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject")
            startActivity(Intent.createChooser(emailIntent, "Send email..."))
        }
    }
}