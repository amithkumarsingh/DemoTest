package com.whitecoats.clinicplus.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.interfaces.PaymentTypeClick
import com.whitecoats.clinicplus.models.UpiIdDetails
import java.net.URI


class AddUPIIdAndQRAdapter(
    private var btnSaveUPIIDs: Button, private var swPaymentModeUPI: SwitchCompat,
    private var tvSwitchText: TextView,
    private var upiIdDetailsList: MutableList<UpiIdDetails>,
    private var paymentItemClick: PaymentTypeClick
) : RecyclerView.Adapter<AddUPIIdAndQRAdapter.MyViewHolder>() {
    private lateinit var context: Context
    val list = arrayListOf(0, 0, 0)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.add_upi_id_qr_layouts, parent, false)
        context = parent.context
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return upiIdDetailsList.size
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {

        if (upiIdDetailsList[position].upi_scan_image_url != "") {
            val uri = URI(upiIdDetailsList[position].upi_scan_image_url)
            val segments: List<String> = uri.path.split("/")
            val idStr = segments[segments.size - 1]
            holder.uploadQR!!.visibility = View.GONE
            holder.llParentAddedQR!!.visibility = View.VISIBLE
            holder.tvAddedQRName!!.text = idStr
        } else {
            holder.uploadQR!!.visibility = View.VISIBLE
            holder.llParentAddedQR!!.visibility = View.GONE
            holder.tvAddedQRName!!.text = ""
        }
        if (upiIdDetailsList[position].upi_id != "") {
            holder.etEnterUPI!!.setText(upiIdDetailsList[position].upi_id)
        } else {
            holder.etEnterUPI!!.setText("")
        }

        holder.uploadQR!!.setOnClickListener {
            try {
                val activity = context as Activity
                val view: View = activity.window.currentFocus!!
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(view.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            holder.etEnterUPI!!.clearFocus()
            paymentItemClick.onItemClick(
                position,
                "UploadQACode",
                holder.uploadQR!!,
                holder.llParentAddedQR!!
            )
        }
        holder.imgEditQRName!!.setOnClickListener {
            try {
                val activity = context as Activity
                val view: View = activity.window.currentFocus!!
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(view.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            holder.etEnterUPI!!.clearFocus()
            paymentItemClick.onItemClick(
                position,
                "UploadQACode",
                holder.uploadQR!!,
                holder.llParentAddedQR!!
            )
        }
        holder.imgDeleteQRName!!.setOnClickListener {
            try {
                val activity = context as Activity
                val view: View = activity.window.currentFocus!!
                val imm =
                    context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(view.windowToken, 0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            holder.etEnterUPI!!.clearFocus()
            /* holder.uploadQR!!.visibility = View.VISIBLE
             holder.llParentAddedQR!!.visibility = View.GONE*/
            paymentItemClick.onItemClick(
                position,
                "DeleteQACode",
                holder.uploadQR!!,
                holder.llParentAddedQR!!
            )
        }
        holder.llCopyUPI!!.setOnClickListener {
            if (!holder.etEnterUPI!!.text.toString().equals("", ignoreCase = true)) {
                val clipboard: ClipboardManager =
                    context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip =
                    ClipData.newPlainText("label", holder.etEnterUPI!!.text.toString().trim())
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
            }


            /* val clipboard = ContextCompat.getSystemService(
                 context,
                 ClipboardManager::class.java
             )
             val clip =
                 ClipData.newPlainText("label", holder.etEnterUPI!!.text.toString().trim())
             clipboard?.setPrimaryClip(clip)*/


            // Toast.makeText(context, "UPI ID copied successfully", Toast.LENGTH_SHORT).show()
        }

        holder.etEnterUPI!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val enterUPIId: String = holder.etEnterUPI!!.text.toString()
                if (position == 0) {
                    if (enterUPIId.isNotEmpty()) {
                        upiIdDetailsList[0].upi_id = enterUPIId;

//                        list.removeAt(0)
//                        list.add(0, 1)
//                        if (upiIdDetailsList[1].upi_id.length > 0) {
//                            list.removeAt(1)
//                            list.add(1, 1)
//                        }
//                        if (upiIdDetailsList[2].upi_id.length > 0) {
//                            list.removeAt(2)
//                            list.add(2, 1)
//                        }
                    } else {

                        upiIdDetailsList[0].upi_id = enterUPIId;
//                        list.removeAt(0)
//                        list.add(0, 0)
//
//                        if (upiIdDetailsList[1].upi_id.length > 0) {
//                            list.removeAt(1)
//                            list.add(1, 1)
//                        }
//                        if (upiIdDetailsList[2].upi_id.length > 0) {
//                            list.removeAt(2)
//                            list.add(2, 1)
//                        }


                    }
                } else if (position == 1) {

                    if (enterUPIId.isNotEmpty()) {
                        upiIdDetailsList[1].upi_id = enterUPIId;

//                        list.removeAt(1)
//                        list.add(1, 1)
//
//                        if (upiIdDetailsList[0].upi_id.length > 0) {
//                            list.removeAt(0)
//                            list.add(0, 1)
//                        }
//                        if (upiIdDetailsList[2].upi_id.length > 0) {
//                            list.removeAt(2)
//                            list.add(2, 1)
//                        }

                    } else {
                    upiIdDetailsList[1].upi_id = enterUPIId;

//                    list.removeAt(1)
//                        list.add(1, 0)
//
//                        if (upiIdDetailsList[0].upi_id.length > 0) {
//                            list.removeAt(0)
//                            list.add(0, 1)
//                        }
//                        if (upiIdDetailsList[2].upi_id.length > 0) {
//                            list.removeAt(2)
//                            list.add(2, 1)
//                        }

                    }
                } else {
                    if (enterUPIId.isNotEmpty()) {
                        upiIdDetailsList[2].upi_id = enterUPIId;

//                        list.removeAt(2)
//                        list.add(2, 1)
//
//                        if (upiIdDetailsList[0].upi_id.length > 0) {
//                            list.removeAt(0)
//                            list.add(0, 1)
//                        }
//                        if (upiIdDetailsList[1].upi_id.length > 0) {
//                            list.removeAt(1)
//                            list.add(1, 1)
//                        }
                    } else {
                        upiIdDetailsList[2].upi_id = enterUPIId;

//                        list.removeAt(2)
//                        list.add(2, 0)
//
//                        if (upiIdDetailsList[0].upi_id.length > 0) {
//                            list.removeAt(0)
//                            list.add(0, 1)
//                        }
//                        if (upiIdDetailsList[1].upi_id.length > 0) {
//                            list.removeAt(1)
//                            list.add(1, 1)
//                        }
                    }
                }

                if (enterUPIId.isNotEmpty()) {
                    btnSaveUPIIDs.isEnabled = true
                    btnSaveUPIIDs.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorAccent
                        )
                    )
                    btnSaveUPIIDs.setTextColor(ContextCompat.getColor(context, R.color.white))
                    swPaymentModeUPI.isEnabled = true
                    tvSwitchText.alpha = 1f
                    tvSwitchText.isClickable = true
                } else {
                    btnSaveUPIIDs.isEnabled = false
                    btnSaveUPIIDs.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.light_blue
                        )
                    )
                    btnSaveUPIIDs.setTextColor(ContextCompat.getColor(context, R.color.white))
                    swPaymentModeUPI.isEnabled = false
                    tvSwitchText.alpha = 0.5f
                    tvSwitchText.isClickable = false
                }
                if (enterUPIId == "") {
                    for (i in 0 until upiIdDetailsList.size) {
                        if (upiIdDetailsList[i].upi_scan_image_url != "") {
                            btnSaveUPIIDs.isEnabled = true
                            btnSaveUPIIDs.setBackgroundColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.colorAccent
                                )
                            )
                            btnSaveUPIIDs.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.white
                                )
                            )
                            swPaymentModeUPI.isEnabled = true
                            tvSwitchText.alpha = 1f
                            tvSwitchText.isClickable = true
                            break
                        }
                    }
                }

                // added by dileep singh 11-09-2023


                if (upiIdDetailsList[0].upi_id.length > 0 || upiIdDetailsList[1].upi_id.length > 0 || upiIdDetailsList[2].upi_id.length > 0) {
                    btnSaveUPIIDs.isEnabled = true
                    btnSaveUPIIDs.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorAccent
                        )
                    )
                    btnSaveUPIIDs.setTextColor(ContextCompat.getColor(context, R.color.white))
                    swPaymentModeUPI.isEnabled = true
                    tvSwitchText.alpha = 1f
                    tvSwitchText.isClickable = true
                } else {
                    btnSaveUPIIDs.isEnabled = false
                    btnSaveUPIIDs.setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            R.color.light_blue
                        )
                    )
                    btnSaveUPIIDs.setTextColor(ContextCompat.getColor(context, R.color.white))
                    swPaymentModeUPI.isEnabled = false
                    tvSwitchText.alpha = 0.5f
                    tvSwitchText.isClickable = false
                }


            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var constUPIParent: ConstraintLayout? = null
        var llUPI: LinearLayout? = null
        var etEnterUPI: EditText? = null
        var llCopyUPI: LinearLayout? = null
        var imgCopyUPI: ImageView? = null
        var tvCopyUPI: TextView? = null
        var tvOr: TextView? = null
        var uploadQR: LinearLayout? = null
        var imgUploadQR: ImageView? = null
        var tvUploadQR: TextView? = null
        var llParentAddedQR: LinearLayout? = null
        var llAddedQRName: LinearLayout? = null
        var tvAddedQRName: TextView? = null
        var llUploadDeleteQR: LinearLayout? = null
        var imgEditQRName: ImageView? = null
        var imgDeleteQRName: ImageView? = null
        var viewUPISep: View? = null

        init {
            constUPIParent = itemView.findViewById(R.id.const_UPI_parent)
            llUPI = itemView.findViewById(R.id.ll_UPI)
            etEnterUPI = itemView.findViewById(R.id.et_enter_UPI)
            llCopyUPI = itemView.findViewById(R.id.ll_copy_UPI)
            imgCopyUPI = itemView.findViewById(R.id.img_copy_UPI)
            tvCopyUPI = itemView.findViewById(R.id.tv_copy_UPI)
            tvOr = itemView.findViewById(R.id.tv_Or)
            uploadQR = itemView.findViewById(R.id.upload_QR)
            imgUploadQR = itemView.findViewById(R.id.img_upload_QR)
            tvUploadQR = itemView.findViewById(R.id.tv_upload_QR)
            llParentAddedQR = itemView.findViewById(R.id.ll_parent_added_QR)
            llAddedQRName = itemView.findViewById(R.id.ll_added_QRName)
            tvAddedQRName = itemView.findViewById(R.id.tv_added_QRName)
            llUploadDeleteQR = itemView.findViewById(R.id.ll_Upload_Delete_QR)
            imgEditQRName = itemView.findViewById(R.id.img_edit_QR_Name)
            imgDeleteQRName = itemView.findViewById(R.id.img_delete_QR_Name)
            viewUPISep = itemView.findViewById(R.id.view_UPI)
        }

    }
}