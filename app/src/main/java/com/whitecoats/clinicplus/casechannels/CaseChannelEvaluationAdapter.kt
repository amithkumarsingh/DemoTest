package com.whitecoats.clinicplus.casechannels

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.AppUtilities
import com.whitecoats.clinicplus.R
import com.whitecoats.model.PatientRecordsModel

class CaseChannelEvaluationAdapter(
    activity: Activity,
    patientRecordsModels: List<PatientRecordsModel>?,
    caseDoctorOrganisationClickListener: CaseDoctorOrganisationClickListener
) : RecyclerView.Adapter<CaseChannelEvaluationAdapter.ViewHolder>() {
    private val activity: Activity
    private val patientRecordsModels: List<PatientRecordsModel>?
    private val appUtilities: AppUtilities = AppUtilities()
    private val caseDoctorOrganisationClickListener: CaseDoctorOrganisationClickListener

    init {
        this.activity = activity
        this.patientRecordsModels = patientRecordsModels
        this.caseDoctorOrganisationClickListener = caseDoctorOrganisationClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_case_channel_record_hand_written, viewGroup, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHolder: ViewHolder, @SuppressLint("RecyclerView") i: Int) {
        val recordsModel = patientRecordsModels!![i]
        if (recordsModel.type == 1) { // 1 for symptoms
            viewHolder.caseChannelSymptomsCardView.visibility = View.VISIBLE
            viewHolder.caseChannelInvestgationCardView.visibility = View.GONE
            viewHolder.caseChannelDiagnosisCardView.visibility = View.GONE
            // viewHolder.caseChannelRecordEvalObservationLayout.setVisibility(View.GONE);
            viewHolder.caseChannelObservationCardView.visibility = View.GONE
            //
            viewHolder.caseChannelRecordSymptomsName.text = recordsModel.symptomName
            viewHolder.caseChannelRecordSymptomsDoctorName.text = recordsModel.createdUser
            viewHolder.caeChannelRecordSymtomsDescription.text = recordsModel.symptom_description
            viewHolder.caseChannelRecordSymtomsStatus.text = recordsModel.symptomStatus
            val temp = appUtilities.changeDateFormat(
                "yyyy-MM-dd mm:HH:ss",
                "dd MMM, yy",
                recordsModel.symptomFirstSeen
            )
            viewHolder.caseChannelRecordSymptomsDate.text = temp
        } else if (recordsModel.type == 2) { //2 for observation
            viewHolder.caseChannelDiagnosisCardView.visibility = View.GONE
            viewHolder.caseChannelInvestgationCardView.visibility = View.GONE
            viewHolder.caseChannelSymptomsCardView.visibility = View.GONE
            // viewHolder.caseChannelRecordEvalObservationLayout.setVisibility(View.GONE);
            viewHolder.caseChannelObservationCardView.visibility = View.VISIBLE
            viewHolder.caseChannelRecordObservationDoctorName.text =
                recordsModel.obsCatName + " \nby " + recordsModel.createdUser
            appUtilities.changeDateFormat(
                "MM/dd/yyyy mm:HH a",
                "dd MMM, yy mm:HH a",
                recordsModel.created_At
            )
            viewHolder.caseChannelRecordObservationDate.text = recordsModel.created_At
            try {
                val patientRecordsModel = patientRecordsModels[i]
                viewHolder.caseChannelRecordPrimaryKeyName.text =
                    patientRecordsModel.primaryKey + ": "
                if (patientRecordsModel.primaryData != null) {
                    viewHolder.caseChannelRecordPrimaryName.text = patientRecordsModel.primaryData
                } else {
                    viewHolder.caseChannelRecordPrimaryName.text = "-"
                }
                viewHolder.caseChannelRecordSecondKeyName.text = patientRecordsModel.secKey + ": "
                if (patientRecordsModel.secData != null) {
                    viewHolder.caseChannelRecordSecondName.text = patientRecordsModel.secData
                } else {
                    viewHolder.caseChannelRecordSecondName.text = "-"
                }
                if (patientRecordsModel.ternaryKey != null) {
                    viewHolder.caseChannelRecordTernaryKeyName.text =
                        patientRecordsModel.ternaryKey + ": "
                }
                viewHolder.caseChannelRecordTernaryName.text = patientRecordsModel.ternaryData
                viewHolder.caseChannelRecordPrimaryName.setTextColor(ContextCompat.getColor(activity,R.color.colorGreyText))
                if (patientRecordsModel.primaryKey.contains("File") &&
                    patientRecordsModel.primaryData.equals("yes", ignoreCase = true)
                ) {
                    viewHolder.caseChannelRecordPrimaryName.text =
                        activity.resources.getString(R.string.view)
                    viewHolder.caseChannelRecordPrimaryName.setTextColor(
                        ContextCompat.getColor(activity,R.color.colorAccent)
                    )
                } else if (patientRecordsModel.primaryKey.contains("Attachment") &&
                    patientRecordsModel.primaryData.equals("yes", ignoreCase = true)
                ) {
                    viewHolder.caseChannelRecordPrimaryName.text =
                        activity.resources.getString(R.string.view)
                    viewHolder.caseChannelRecordPrimaryName.setTextColor(
                        ContextCompat.getColor(activity,R.color.colorAccent
                        )
                    )
                }
                viewHolder.caseChannelRecordSecondName.setTextColor(ContextCompat.getColor(activity,R.color.colorGreyText))
                if (((patientRecordsModel.secKey != null) && patientRecordsModel.secKey.contains("File") &&
                            patientRecordsModel.secData.equals("yes", ignoreCase = true))
                ) {
                    viewHolder.caseChannelRecordSecondName.text =
                        activity.resources.getString(R.string.view)
                    viewHolder.caseChannelRecordSecondName.setTextColor(
                        ContextCompat.getColor(activity,R.color.colorAccent
                        )
                    )
                } else if (((patientRecordsModel.secKey != null) && patientRecordsModel.secKey.contains(
                        "Attachment"
                    ) &&
                            patientRecordsModel.secData.equals("yes", ignoreCase = true))
                ) {
                    viewHolder.caseChannelRecordSecondName.text =
                        activity.resources.getString(R.string.view)
                    viewHolder.caseChannelRecordSecondName.setTextColor(
                        ContextCompat.getColor(activity,R.color.colorAccent
                        )
                    )
                }
                viewHolder.caseChannelRecordTernaryName.setTextColor(ContextCompat.getColor(activity,R.color.colorGreyText))
                if (((patientRecordsModel.ternaryKey != null) && patientRecordsModel.ternaryKey.contains(
                        "File"
                    ) &&
                            patientRecordsModel.ternaryData.equals("yes", ignoreCase = true))
                ) {
                    viewHolder.caseChannelRecordTernaryName.text =
                        activity.resources.getString(R.string.view)
                    viewHolder.caseChannelRecordTernaryName.setTextColor(
                        ContextCompat.getColor(activity,R.color.colorAccent
                        )
                    )
                } else if (((patientRecordsModel.ternaryKey != null) && patientRecordsModel.ternaryKey.contains(
                        "Attachment"
                    ) &&
                            patientRecordsModel.ternaryData.equals("yes", ignoreCase = true))
                ) {
                    viewHolder.caseChannelRecordTernaryName.text =
                        activity.resources.getString(R.string.view)
                    viewHolder.caseChannelRecordTernaryName.setTextColor(
                        ContextCompat.getColor(activity,R.color.colorAccent
                        )
                    )
                }
                viewHolder.caseChannelRecordPrimaryName.setOnClickListener { view ->
                    if (viewHolder.caseChannelRecordPrimaryName.text.toString()
                            .equals(activity.resources.getString(R.string.view), ignoreCase = true)
                    ) {
                        caseDoctorOrganisationClickListener.onItemClick(
                            view,
                            0,
                            patientRecordsModel.fileUrl.trim { it <= ' ' },
                            "FILE_URL"
                        )
                    }
                }
                viewHolder.caseChannelRecordSecondName.setOnClickListener(object :
                    View.OnClickListener {
                    override fun onClick(view: View) {
                        if (viewHolder.caseChannelRecordSecondName.text.toString().equals(
                                activity.resources.getString(R.string.view),
                                ignoreCase = true
                            )
                        ) {
                            caseDoctorOrganisationClickListener.onItemClick(
                                view,
                                0,
                                patientRecordsModel.fileUrl.trim { it <= ' ' },
                                "FILE_URL"
                            )
                        }
                    }
                })
                viewHolder.caseChannelRecordTernaryName.setOnClickListener(object :
                    View.OnClickListener {
                    override fun onClick(view: View) {
                        if (viewHolder.caseChannelRecordTernaryName.text.toString().equals(
                                activity.resources.getString(R.string.view),
                                ignoreCase = true
                            )
                        ) {
                            caseDoctorOrganisationClickListener.onItemClick(
                                view,
                                0,
                                patientRecordsModel.fileUrl.trim { it <= ' ' },
                                "FILE_URL"
                            )
                        }
                    }
                })
                viewHolder.caseChannelRecordViewAll.setOnClickListener(object :
                    View.OnClickListener {
                    override fun onClick(v: View) {
                        caseChannelRecordClickFlag = 1
                        val intent =
                            Intent(activity, CaseChannelRecordsMoreInfoActivity::class.java)
                        intent.putExtra("CatId", patientRecordsModel.catId)
                        intent.putExtra("RecordId", patientRecordsModel.recordId.toString() + "")
                        intent.putExtra("RecordDetail", patientRecordsModel.catRecordData)
                        intent.putExtra("RecordField", patientRecordsModel.fieldDictionary)
                        intent.putExtra("CatName", patientRecordsModel.catName)
                        intent.putExtra(
                            "RecordImageUrl",
                            patientRecordsModel.fileUrl.trim { it <= ' ' })
                        activity.startActivity(intent)
                    }
                })
                viewHolder.caeChannelRecordObservationLayout.setOnClickListener(object :
                    View.OnClickListener {
                    override fun onClick(v: View) {
                        caseChannelRecordClickFlag = 1
                        val intent =
                            Intent(activity, CaseChannelRecordsMoreInfoActivity::class.java)
                        intent.putExtra("CatId", patientRecordsModel.catId)
                        intent.putExtra("RecordId", patientRecordsModel.recordId.toString() + "")
                        intent.putExtra("RecordDetail", patientRecordsModel.catRecordData)
                        intent.putExtra("RecordField", patientRecordsModel.fieldDictionary)
                        intent.putExtra("CatName", patientRecordsModel.catName)
                        intent.putExtra(
                            "RecordImageUrl",
                            patientRecordsModel.fileUrl.trim { it <= ' ' })
                        activity.startActivity(intent)
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (recordsModel.type == 3) { //3 for investigation
            viewHolder.caseChannelDiagnosisCardView.visibility = View.GONE
            viewHolder.caseChannelInvestgationCardView.visibility = View.VISIBLE
            viewHolder.caseChannelSymptomsCardView.visibility = View.GONE
            viewHolder.caseChannelObservationCardView.visibility = View.GONE
            viewHolder.caseChannelRecordInvestgationName.text = recordsModel.investName
            viewHolder.caeChannelRecordInvestgationParameter.text = recordsModel.investParam
            viewHolder.caseChannelRecordInvestgationValue.text = recordsModel.investValue
            viewHolder.caeChannelRecordInvestgationNote.text = recordsModel.investNote
            viewHolder.caseChannelRecordInvestgationDoctorName.text = recordsModel.createdUser
            val temp = appUtilities.changeDateFormat(
                "yyyy-MM-dd mm:HH:ss",
                "dd MMM, yy mm:HH",
                recordsModel.created_At
            )
            viewHolder.caseChannelRecordInvestgationDate.text = temp
            if (recordsModel.fileUrl.equals(
                    "",
                    ignoreCase = true
                ) || recordsModel.fileUrl.equals("null", ignoreCase = true)
            ) {
                viewHolder.caseChannelRecordInvestgationView.text = "Not available"
            } else {
                viewHolder.caseChannelRecordInvestgationView.text = "View"
            }
            viewHolder.caseChannelRecordInvestgationView.setOnClickListener(object :
                View.OnClickListener {
                override fun onClick(view: View) {
                    if (viewHolder.caseChannelRecordInvestgationView.text.toString()
                            .equals("Not available", ignoreCase = true)
                    ) {
                        Toast.makeText(activity, "No attachment available", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        caseDoctorOrganisationClickListener.onItemClick(
                            view,
                            i,
                            "",
                            recordsModel.fileUrl
                        )
                    }
                }
            })
        } else if (recordsModel.type == 4) { //4 for diagnosis
            viewHolder.caseChannelDiagnosisCardView.visibility = View.VISIBLE
            viewHolder.caseChannelSymptomsCardView.visibility = View.GONE
            viewHolder.caseChannelInvestgationCardView.visibility = View.GONE
            viewHolder.caseChannelObservationCardView.visibility = View.GONE
            viewHolder.caseChannelRecordDiagnosisnName.text = recordsModel.diagName
            var temp = appUtilities.changeDateFormat(
                "yyyy-MM-dd mm:HH:ss",
                "dd MMM, yy",
                recordsModel.diagPoisted
            )
            val postedDate = "Posited On: <b>$temp</b>"
            viewHolder.caeChannelRecordDiagnosisPositedOn.text = Html.fromHtml(postedDate)
            viewHolder.caseChannelRecordDiagnosisStatus.text = recordsModel.diagStatus
            temp = appUtilities.changeDateFormat(
                "yyyy-MM-dd mm:HH:ss",
                "dd MMM, yy",
                recordsModel.diagConfirmed
            )
            val confirmDate = "<b>$temp</b>"
            viewHolder.caseChannelRecordDiagnosisConfirmed_RuledOutOn.text =
                Html.fromHtml(confirmDate)
            val temp1 = appUtilities.changeDateFormat(
                "yyyy-MM-dd mm:HH:ss",
                "dd MMM, yy mm:HH",
                recordsModel.created_At
            )
            viewHolder.caseChannelRecordDiagnosisDate.text = temp1
            viewHolder.caseChannelRecordDiagnosisDoctorName.text = recordsModel.createdUser
        } else if (recordsModel.type == 5) { //5 for treatment plan
            viewHolder.caseChannelDiagnosisCardView.visibility = View.GONE
            viewHolder.caseChannelInvestgationCardView.visibility = View.GONE
            viewHolder.caseChannelSymptomsCardView.visibility = View.GONE
            viewHolder.caseChannelObservationCardView.visibility = View.VISIBLE
            viewHolder.caseChannelRecordObservationDoctorName.text =
                recordsModel.obsCatName + " \nby " + recordsModel.createdUser
            appUtilities.changeDateFormat(
                "MM/dd/yyyy mm:HH a",
                "dd MMM, yy mm:HH a",
                recordsModel.created_At
            )
            viewHolder.caseChannelRecordObservationDate.text = recordsModel.created_At
            try {
                val patientRecordsModel = patientRecordsModels[i]
                viewHolder.caseChannelRecordPrimaryKeyName.text =
                    patientRecordsModel.primaryKey + ": "
                if (patientRecordsModel.primaryData != null) {
                    viewHolder.caseChannelRecordPrimaryName.text = patientRecordsModel.primaryData
                } else {
                    viewHolder.caseChannelRecordPrimaryName.text = "-"
                }
                viewHolder.caseChannelRecordSecondKeyName.text = patientRecordsModel.secKey + ": "
                if (patientRecordsModel.secData != null) {
                    viewHolder.caseChannelRecordSecondName.text = patientRecordsModel.secData
                } else {
                    viewHolder.caseChannelRecordSecondName.text = "-"
                }
                if (patientRecordsModel.ternaryKey != null) {
                    viewHolder.caseChannelRecordTernaryKeyName.text =
                        patientRecordsModel.ternaryKey + ": "
                }
                viewHolder.caseChannelRecordTernaryName.text = patientRecordsModel.ternaryData
                viewHolder.caseChannelRecordPrimaryName.setTextColor(ContextCompat.getColor(activity,R.color.colorGreyText))
                if (patientRecordsModel.primaryKey.contains("File") &&
                    patientRecordsModel.primaryData.equals("yes", ignoreCase = true)
                ) {
                    viewHolder.caseChannelRecordPrimaryName.text =
                        activity.resources.getString(R.string.view)
                    viewHolder.caseChannelRecordPrimaryName.setTextColor(
                        ContextCompat.getColor(activity,R.color.colorAccent
                        )
                    )
                } else if (patientRecordsModel.primaryKey.contains("Attachment") &&
                    patientRecordsModel.primaryData.equals("yes", ignoreCase = true)
                ) {
                    viewHolder.caseChannelRecordPrimaryName.text =
                        activity.resources.getString(R.string.view)
                    viewHolder.caseChannelRecordPrimaryName.setTextColor(
                        ContextCompat.getColor(activity,R.color.colorAccent
                        )
                    )
                }
                viewHolder.caseChannelRecordSecondName.setTextColor(ContextCompat.getColor(activity,R.color.colorGreyText))
                if (((patientRecordsModel.secKey != null) && patientRecordsModel.secKey.contains("File") &&
                            patientRecordsModel.secData.equals("yes", ignoreCase = true))
                ) {
                    viewHolder.caseChannelRecordSecondName.text =
                        activity.resources.getString(R.string.view)
                    viewHolder.caseChannelRecordSecondName.setTextColor(
                        ContextCompat.getColor(activity,R.color.colorAccent
                        )
                    )
                } else if (((patientRecordsModel.secKey != null) && patientRecordsModel.secKey.contains(
                        "Attachment"
                    ) &&
                            patientRecordsModel.secData.equals("yes", ignoreCase = true))
                ) {
                    viewHolder.caseChannelRecordSecondName.text =
                        activity.resources.getString(R.string.view)
                    viewHolder.caseChannelRecordSecondName.setTextColor(
                        ContextCompat.getColor(activity,R.color.colorAccent
                        )
                    )
                }
                viewHolder.caseChannelRecordTernaryName.setTextColor(ContextCompat.getColor(activity,R.color.colorGreyText))
                if (((patientRecordsModel.ternaryKey != null) && patientRecordsModel.ternaryKey.contains(
                        "File"
                    ) &&
                            patientRecordsModel.ternaryData.equals("yes", ignoreCase = true))
                ) {
                    viewHolder.caseChannelRecordTernaryName.text =
                        activity.resources.getString(R.string.view)
                    viewHolder.caseChannelRecordTernaryName.setTextColor(
                        ContextCompat.getColor(activity,R.color.colorAccent
                        )
                    )
                } else if (((patientRecordsModel.ternaryKey != null) && patientRecordsModel.ternaryKey.contains(
                        "Attachment"
                    ) &&
                            patientRecordsModel.ternaryData.equals("yes", ignoreCase = true))
                ) {
                    viewHolder.caseChannelRecordTernaryName.text =
                        activity.resources.getString(R.string.view)
                    viewHolder.caseChannelRecordTernaryName.setTextColor(
                        ContextCompat.getColor(activity,R.color.colorAccent
                        )
                    )
                }
                viewHolder.caseChannelRecordPrimaryName.setOnClickListener { view ->
                    if (viewHolder.caseChannelRecordPrimaryName.text.toString().equals(
                            activity.resources.getString(R.string.view),
                            ignoreCase = true
                        )
                    ) {
                        caseDoctorOrganisationClickListener.onItemClick(
                            view,
                            0,
                            patientRecordsModel.fileUrl.trim { it <= ' ' },
                            "FILE_URL"
                        )
                    }
                }
                viewHolder.caseChannelRecordSecondName.setOnClickListener { view ->
                    if (viewHolder.caseChannelRecordSecondName.text.toString().equals(
                            activity.resources.getString(R.string.view),
                            ignoreCase = true
                        )
                    ) {
                        caseDoctorOrganisationClickListener.onItemClick(
                            view,
                            0,
                            patientRecordsModel.fileUrl.trim { it <= ' ' },
                            "FILE_URL"
                        )
                    }
                }
                viewHolder.caseChannelRecordTernaryName.setOnClickListener { view ->
                    if (viewHolder.caseChannelRecordTernaryName.text.toString().equals(
                            activity.resources.getString(R.string.view),
                            ignoreCase = true
                        )
                    ) {
                        caseDoctorOrganisationClickListener.onItemClick(
                            view,
                            0,
                            patientRecordsModel.fileUrl.trim { it <= ' ' },
                            "FILE_URL"
                        )
                    }
                }
                viewHolder.caseChannelRecordViewAll.setOnClickListener(object :
                    View.OnClickListener {
                    override fun onClick(v: View) {
                        caseChannelRecordClickFlag = 1
                        val intent =
                            Intent(activity, CaseChannelRecordsMoreInfoActivity::class.java)
                        intent.putExtra("CatId", patientRecordsModel.catId)
                        intent.putExtra("RecordId", patientRecordsModel.recordId.toString() + "")
                        intent.putExtra("RecordDetail", patientRecordsModel.catRecordData)
                        intent.putExtra("RecordField", patientRecordsModel.fieldDictionary)
                        intent.putExtra("CatName", patientRecordsModel.catName)
                        intent.putExtra(
                            "RecordImageUrl",
                            patientRecordsModel.fileUrl.trim { it <= ' ' })
                        activity.startActivity(intent)
                    }
                })
                viewHolder.caeChannelRecordObservationLayout.setOnClickListener(object :
                    View.OnClickListener {
                    override fun onClick(v: View) {
                        caseChannelRecordClickFlag = 1
                        val intent =
                            Intent(activity, CaseChannelRecordsMoreInfoActivity::class.java)
                        intent.putExtra("CatId", patientRecordsModel.catId)
                        intent.putExtra("RecordId", patientRecordsModel.recordId.toString() + "")
                        intent.putExtra("RecordDetail", patientRecordsModel.catRecordData)
                        intent.putExtra("RecordField", patientRecordsModel.fieldDictionary)
                        intent.putExtra("CatName", patientRecordsModel.catName)
                        intent.putExtra(
                            "RecordImageUrl",
                            patientRecordsModel.fileUrl.trim { it <= ' ' })
                        activity.startActivity(intent)
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            viewHolder.caseChannelDiagnosisCardView.visibility = View.GONE
            viewHolder.caseChannelSymptomsCardView.visibility = View.GONE
            viewHolder.caseChannelInvestgationCardView.visibility = View.GONE
            viewHolder.caseChannelRecordObservationText!!.text =
                recordsModel.obsCatName + " by " + recordsModel.createdUser
        }
    }

    override fun getItemCount(): Int {
        return patientRecordsModels?.size ?: 0
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //Symtoms
        var caseChannelRecordSymptomsDoctorName: TextView
        var caseChannelRecordSymptomsDate: TextView
        var caseChannelRecordSymptomsName: TextView
        var caeChannelRecordSymtomsDescription: TextView
        var caseChannelRecordSymtomsStatus: TextView
        var caseChannelSymptomsCardView: CardView

        //Investigation
        var caseChannelRecordInvestgationDoctorName: TextView
        var caseChannelRecordInvestgationDate: TextView
        var caseChannelRecordInvestgationName: TextView
        var caeChannelRecordInvestgationParameter: TextView
        var caseChannelRecordInvestgationValue: TextView
        var caeChannelRecordInvestgationNote: TextView
        var caseChannelRecordInvestgationView: TextView
        var caseChannelInvestgationCardView: CardView

        //Diagnosis
        var caseChannelRecordDiagnosisDoctorName: TextView
        var caseChannelRecordDiagnosisDate: TextView
        var caseChannelRecordDiagnosisnName: TextView
        var caeChannelRecordDiagnosisPositedOn: TextView
        var caseChannelRecordDiagnosisConfirmed_RuledOutOn: TextView
        var caseChannelRecordDiagnosisStatus: TextView
        var caseChannelDiagnosisCardView: CardView

        var caseChannelRecordObservationText: TextView? = null
        var caseChannelRecordObservationDoctorName: TextView
        var caseChannelRecordObservationDate: TextView
        var caseChannelRecordPrimaryKeyName: TextView
        var caseChannelRecordPrimaryName: TextView
        var caseChannelRecordSecondKeyName: TextView
        var caseChannelRecordSecondName: TextView
        var caseChannelRecordTernaryKeyName: TextView
        var caseChannelRecordTernaryName: TextView
        var caseChannelRecordViewAll: TextView
        var caseChannelObservationCardView: CardView
        var caeChannelRecordObservationLayout: RelativeLayout

        init {

            //Symtoms
            caseChannelSymptomsCardView = itemView.findViewById(R.id.caseChannelSymptomsCardView)
            caseChannelRecordSymptomsDoctorName =
                itemView.findViewById(R.id.caseChannelRecordSymptomsDoctorName)
            caseChannelRecordSymptomsDate =
                itemView.findViewById(R.id.caseChannelRecordSymptomsDate)
            caseChannelRecordSymptomsName =
                itemView.findViewById(R.id.caseChannelRecordSymptomsName)
            caeChannelRecordSymtomsDescription =
                itemView.findViewById(R.id.caeChannelRecordSymtomsDescription)
            caseChannelRecordSymtomsStatus =
                itemView.findViewById(R.id.caseChannelRecordSymtomsStatus)

            //Investigation
            caseChannelInvestgationCardView =
                itemView.findViewById(R.id.caseChannelInvestgationCardView)
            caseChannelRecordInvestgationDoctorName =
                itemView.findViewById(R.id.caseChannelRecordInvestgationDoctorName)
            caseChannelRecordInvestgationDate =
                itemView.findViewById(R.id.caseChannelRecordInvestgationDate)
            caseChannelRecordInvestgationName =
                itemView.findViewById(R.id.caseChannelRecordInvestgationName)
            caeChannelRecordInvestgationParameter =
                itemView.findViewById(R.id.caeChannelRecordInvestgationParameter)
            caseChannelRecordInvestgationValue =
                itemView.findViewById(R.id.caseChannelRecordInvestgationValue)
            caeChannelRecordInvestgationNote =
                itemView.findViewById(R.id.caeChannelRecordInvestgationNote)
            caseChannelRecordInvestgationView =
                itemView.findViewById(R.id.caseChannelRecordInvestgationView)

            //Diagnosis
            caseChannelDiagnosisCardView = itemView.findViewById(R.id.caseChannelDiagnosisCardView)
            caseChannelRecordDiagnosisDoctorName =
                itemView.findViewById(R.id.caseChannelRecordDiagnosisDoctorName)
            caseChannelRecordDiagnosisDate =
                itemView.findViewById(R.id.caseChannelRecordDiagnosisDate)
            caseChannelRecordDiagnosisnName =
                itemView.findViewById(R.id.caseChannelRecordDiagnosisnName)
            caeChannelRecordDiagnosisPositedOn =
                itemView.findViewById(R.id.caeChannelRecordDiagnosisPositedOn)
            caseChannelRecordDiagnosisConfirmed_RuledOutOn =
                itemView.findViewById(R.id.caseChannelRecordDiagnosisConfirmed_RuledOutOn)
            caseChannelRecordDiagnosisStatus =
                itemView.findViewById(R.id.caseChannelRecordDiagnosisStatus)

            //Observation
            caseChannelObservationCardView =
                itemView.findViewById(R.id.caseChannelObservationCardView)
            caseChannelRecordObservationDoctorName =
                itemView.findViewById(R.id.caseChannelRecordObservationDoctorName)
            caseChannelRecordObservationDate =
                itemView.findViewById(R.id.caseChannelRecordObservationDate)
            caeChannelRecordObservationLayout =
                itemView.findViewById(R.id.caeChannelRecordObservationLayout)
            caseChannelRecordPrimaryKeyName =
                itemView.findViewById(R.id.caseChannelRecordPrimaryKeyName)
            caseChannelRecordPrimaryName = itemView.findViewById(R.id.caseChannelRecordPrimaryName)
            caseChannelRecordSecondKeyName =
                itemView.findViewById(R.id.caseChannelRecordSecondKeyName)
            caseChannelRecordSecondName = itemView.findViewById(R.id.caseChannelRecordSecondName)
            caseChannelRecordTernaryKeyName =
                itemView.findViewById(R.id.caseChannelRecordTernaryKeyName)
            caseChannelRecordTernaryName = itemView.findViewById(R.id.caseChannelRecordTernaryName)
            caseChannelRecordViewAll = itemView.findViewById(R.id.caseChannelRecordViewAll)
        }
    }

    companion object {
        @JvmField
        var caseChannelRecordClickFlag = 0
    }
}