package com.whitecoats.clinicplus.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.models.EMRFamilyModel

class EMRFamilyRecyclerAdapter(
    private val familyModelList: List<EMRFamilyModel>,
    private val familyInterface: FamilyInterface
) : RecyclerView.Adapter<EMRFamilyRecyclerAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View
        itemView = when (viewType) {
            Normal -> LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_emr_family, parent, false)
            Footer -> LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_emr_family_footer, parent, false)
            else -> LayoutInflater.from(parent.context)
                .inflate(R.layout.list_row_emr_family, parent, false)
        }
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        when (viewType) {
            Normal -> {
                val familyModel = familyModelList[position]
                holder.famName?.text = familyModel.relative_name
                holder.famRelation?.text = familyModel.relation.relation_name
                holder.famAge?.text =
                    String.format("Age: %s %s", familyModel.relative_age_str, familyModel.age_type)
                holder.famCard?.setOnClickListener { view: View? ->
                    familyInterface.onCardClick(
                        familyModel
                    )
                }
            }
            Footer -> {}
        }
    }

    override fun getItemCount(): Int {
        return familyModelList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == familyModelList.size) Footer else Normal
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var famName: TextView? = null
        var famRelation: TextView? = null
        var famAge: TextView? = null
        var famCard: CardView? = null

        init {
            famName = itemView.findViewById(R.id.familyName)
            famRelation = itemView.findViewById(R.id.familyRelation)
            famAge = itemView.findViewById(R.id.familyAge)
            famCard = itemView.findViewById(R.id.emrFamCard)
        }
    }

    interface FamilyInterface {
        fun onCardClick(familyModel: EMRFamilyModel?)
    }

    companion object {
        const val Header = 1
        const val Normal = 2
        const val Footer = 3
    }
}