package com.whitecoats.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.PorterDuff
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import co.mobiwise.materialintro.shape.Focus
import co.mobiwise.materialintro.shape.FocusGravity
import co.mobiwise.materialintro.shape.ShapeType
import co.mobiwise.materialintro.view.MaterialIntroView
import com.whitecoats.clinicplus.ActivityMoreClickListener
import com.whitecoats.clinicplus.CommunicationDetailsActivity
import com.whitecoats.clinicplus.CommunicationListActivity
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.apis.ApiUrls
import com.whitecoats.model.CommunicationListModel

class CommunicationListAdapter(
    private val communicationDetailsModelList: List<CommunicationListModel>,
    mContext: Context,
    articlesTypeValue: Int,
    activityMoreListner: ActivityMoreClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val activityMoreListner: ActivityMoreClickListener
    private var mContext: Context?
    private val appPreference: SharedPreferences
    private val articlesTypeValue: Int

    init {
        this.mContext = mContext
        this.activityMoreListner = activityMoreListner
        appPreference = mContext.getSharedPreferences(ApiUrls.appSharedPref, 0)
        this.articlesTypeValue = articlesTypeValue
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        if (i == TYPE_ITEM) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_communication, viewGroup, false)
            mContext = viewGroup.context
            return MyViewHolder(view)
        } else if (i == TYPE_HEADER) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_row_communication, viewGroup, false)
            mContext = viewGroup.context
            return MyViewHolder(view)
        } else if (i == TYPE_FOOTER) {
            val view = LayoutInflater.from(viewGroup.context).inflate(
                R.layout.activity_path_orderview_footer,
                viewGroup, false
            )
            return FooterViewHolder(view)
        }
        throw RuntimeException("there is no type that matches the type $i + make sure your using types correctly")
    }

    override fun onBindViewHolder(
        myViewHolder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") i: Int
    ) {
        myViewHolder.itemView.tag = communicationDetailsModelList[i]
        if (myViewHolder is MyViewHolder) {
            val itemViewHolder = myViewHolder
            val communicationDetailsListModel = communicationDetailsModelList[i]
            itemViewHolder.title.text = communicationDetailsListModel.title
            itemViewHolder.date.text = communicationDetailsListModel.date
            itemViewHolder.category.text = communicationDetailsListModel.category
            itemViewHolder.deleteIcon.setColorFilter(
                ContextCompat.getColor(mContext!!,R.color.colorDanger),
                PorterDuff.Mode.SRC_IN
            )
            itemViewHolder.communicationCardViewLayout.setOnClickListener { view: View? ->
                val intent = Intent(mContext, CommunicationDetailsActivity::class.java)
                intent.putExtra("articlesId", communicationDetailsListModel.articlesId)
                intent.putExtra("itemPosition", i)
                intent.putExtra("articlesValues", communicationDetailsListModel.articlesType)
                intent.putExtra("title", communicationDetailsListModel.title)
                intent.putExtra("description", communicationDetailsListModel.desc)
                intent.putExtra("content", communicationDetailsListModel.content_value)
                intent.putExtra("category", communicationDetailsListModel.category)
                intent.putExtra("date", communicationDetailsListModel.date)
                intent.putExtra("content_path", communicationDetailsListModel.contentPath)
                mContext!!.startActivity(intent)
            }
            itemViewHolder.commListPlayVideoLayout.setOnClickListener { view: View? ->
                val url = communicationDetailsListModel.contentPath
                val i1 = Intent(Intent.ACTION_VIEW)
                i1.data = Uri.parse(url)
                mContext!!.startActivity(i1)
            }
            itemViewHolder.commListDeleteLayout.setOnClickListener { view: View? ->
                activityMoreListner.onItemClick(
                    view,
                    communicationDetailsListModel.articlesId,
                    i,
                    ""
                )
            }
            if (articlesTypeValue == 3) {
                itemViewHolder.commListPdfLayout.visibility = View.VISIBLE
                itemViewHolder.commListDeleteLayout.viewTreeObserver.addOnGlobalLayoutListener(
                    object : OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            showGuide(
                                1,
                                i,
                                itemViewHolder,
                                "Tap to delete the Pdfs you no longer need"
                            )
                            itemViewHolder.commListDeleteLayout.viewTreeObserver.removeOnGlobalLayoutListener(
                                this
                            )
                        }
                    })
            } else {
                itemViewHolder.commListPdfLayout.visibility = View.GONE
                itemViewHolder.commListDeleteLayout.viewTreeObserver.addOnGlobalLayoutListener(
                    object : OnGlobalLayoutListener {
                        override fun onGlobalLayout() {
                            showGuide(
                                1,
                                i,
                                itemViewHolder,
                                "Tap to delete the article you no longer need"
                            )
                            itemViewHolder.commListDeleteLayout.viewTreeObserver.removeOnGlobalLayoutListener(
                                this
                            )
                        }
                    })
            }
            itemViewHolder.pdfText.setOnClickListener { view: View? ->
                activityMoreListner.onItemClick(
                    view,
                    i,
                    -1,
                    "pdf"
                )
            }
        } else if (myViewHolder is FooterViewHolder) {
            footerHolder = myViewHolder
            if (CommunicationListActivity.isMoreData && communicationDetailsModelList.size >= 10) {
                myViewHolder.footerText.visibility = View.VISIBLE
            } else {
                myViewHolder.footerText.visibility = View.GONE
            }
            footerHolder!!.footerText.setOnClickListener { view: View? ->
                activityMoreListner.onItemClick(
                    view,
                    -1,
                    -1,
                    "LOADMORE"
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return communicationDetailsModelList.size
    }

    override fun getItemViewType(position: Int): Int {
        if (isPositionHeader(position)) {
            return TYPE_HEADER
        } else if (isPositionFooter(position)) {
            return TYPE_FOOTER
        }
        return TYPE_ITEM
    }

    private fun isPositionHeader(position: Int): Boolean {
        return position == 0
    }

    private fun isPositionFooter(position: Int): Boolean {
        return if (position >= communicationDetailsModelList.size - 1 && communicationDetailsModelList.size >= 10) { // && data.size() >= 10 && appointmentsActivity.isMoreData
            true
        } else {
            false
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView
        var date: TextView
        var category: TextView
        var pdfText: TextView
        val deleteIcon: ImageView
        val communicationCardViewLayout: CardView
        val commListPlayVideoLayout: RelativeLayout
        val commListDeleteLayout: RelativeLayout
        val commListPdfLayout: RelativeLayout

        init {
            title = itemView.findViewById(R.id.commListHeader)
            date = itemView.findViewById(R.id.commListDate)
            category = itemView.findViewById(R.id.commListCategoryData)
            deleteIcon = itemView.findViewById(R.id.commListDeleteIcon)
            communicationCardViewLayout = itemView.findViewById(R.id.communicationCardViewLayout)
            commListPlayVideoLayout = itemView.findViewById(R.id.commListPlayVideoLayout)
            commListDeleteLayout = itemView.findViewById(R.id.commListDeleteLayout)
            commListPdfLayout = itemView.findViewById(R.id.commListPdfLayout)
            pdfText = itemView.findViewById(R.id.pdfText)
        }
    }

    inner class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var footerText: TextView
        var bottomSpace: View

        init {
            footerText = view.findViewById(R.id.footer_text)
            bottomSpace = view.findViewById(R.id.bottomSpace)
        }
    }

    inner class HeaderViewHolder(var headerView: View) : RecyclerView.ViewHolder(
        headerView
    )

    private fun showGuide(section: Int, position: Int, myViewHolder: MyViewHolder, text: String) {
        if (position == 0) {
            when (section) {
                1 -> if (!appPreference.getBoolean("Article", false)) {
//                        new GuideView.Builder(mContext)
//                                .setTitle("Deleting Article")
//                                .setContentText("Tap to delete the article you no longer need")
//                                .setTargetView(myViewHolder.commListDeleteLayout)
//                                .setDismissType(DismissType.outside)
//                                .setGuideListener(new GuideListener() {
//                                    @Override
//                                    public void onDismiss(View view) {
//                                        showGuide(2, position, myViewHolder);
//                                        SharedPreferences.Editor editor = appPreference.edit();
//                                        editor.putBoolean("Article", true);
//                                        editor.commit();
//                                    }
//                                })
//                                .build()
//                                .show();
                    MaterialIntroView.Builder(mContext as Activity?)
                        .enableDotAnimation(true)
                        .enableIcon(false)
                        .dismissOnTouch(true)
                        .setFocusGravity(FocusGravity.CENTER)
                        .setFocusType(Focus.NORMAL)
                        .setDelayMillis(50)
                        .enableFadeAnimation(true)
                        .setInfoText(text)
                        .setShape(ShapeType.RECTANGLE)
                        .setTarget(myViewHolder.commListDeleteLayout)
                        .setUsageId("intro_commArticle") //THIS SHOULD BE UNIQUE ID
                        .setListener {
                            if (articlesTypeValue == 3) {
                                showGuide(
                                    2,
                                    position,
                                    myViewHolder,
                                    "Tap to add a new pdfs and share it with your patients"
                                )
                                val editor = appPreference.edit()
                                editor.putBoolean("Article", true)
                                editor.apply()
                            } else {
                                showGuide(
                                    2,
                                    position,
                                    myViewHolder,
                                    "Tap to add a new article and share it with your patients"
                                )
                                val editor = appPreference.edit()
                                editor.putBoolean("Article", true)
                                editor.apply()
                            }
                        }
                        .show()
                }
                2 -> {
                    //sending broadcast
                    val intent = Intent(CommunicationListActivity.CUSTOM_BROADCAST_ACTION)
                    intent.putExtra("Activity", "CommunicationList")
                    intent.putExtra("text", text)
                    /*set the package name for broadcast and changes the custom_broadcast_action string value to normal string*/if (mContext != null) {
                        intent.setPackage(mContext!!.packageName)
                        mContext!!.sendBroadcast(intent)
                    }
                }
            }
        }
    }

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
        private const val TYPE_FOOTER = 2
        @SuppressLint("StaticFieldLeak")
        var footerHolder: FooterViewHolder? = null
    }
}