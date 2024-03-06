package com.whitecoats.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.whitecoats.clinicplus.MyClinicGlobalClass
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import com.zoho.livechat.android.ZohoLiveChat


class GetHelpTabFragment : Fragment() {
    private lateinit var whatAppCard: CardView
    private lateinit var emailCard: CardView
    private lateinit var liveChatCard: CardView
    private lateinit var appPreference: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val getHTabView = inflater.inflate(R.layout.tab_fragment_gethelp, container, false)
        whatAppCard = getHTabView.findViewById(R.id.getHelpWhatAppCard)
        emailCard = getHTabView.findViewById(R.id.getHelpEmailCard)
        liveChatCard = getHTabView.findViewById(R.id.getHelpLiveChatCard)
        appPreference =
            requireActivity().getSharedPreferences(ApiUrls.appSharedPref, Context.MODE_PRIVATE)
        val data = HashMap<String, Any>()
        MyClinicGlobalClass.logUserActionEvent(
            ApiUrls.doctorId, getString(R.string.HomeSupportScreen),
            data
        )
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
        liveChatCard.setOnClickListener {
            ZohoLiveChat.Chat.open()
        }

//        liveChatCard.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                if(AppConfigClass.homeGuideTab == 2) {
//                    showGuide(1);
//                    liveChatCard.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                }
//            }
//        });
        return getHTabView
    }
}