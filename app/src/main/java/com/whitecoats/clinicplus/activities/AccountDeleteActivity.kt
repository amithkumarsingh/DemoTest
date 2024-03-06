package com.whitecoats.clinicplus.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import com.whitecoats.clinicplus.R
import com.whitecoats.clinicplus.databinding.ActivityAccountDeleteBinding
import com.whitecoats.clinicplus.models.APIError
import com.whitecoats.clinicplus.models.AccountDeleteRequest
import com.whitecoats.clinicplus.utils.AppUtilities
import com.whitecoats.clinicplus.utils.ErrorHandlerClass
import com.whitecoats.clinicplus.viewmodels.UserAccountDeleteViewModel


class AccountDeleteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAccountDeleteBinding

    private val accountDeleteViewModel: UserAccountDeleteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountDeleteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.reasonRadioGroup.clearCheck()
        binding.btnDelete.isEnabled = false
        binding.reasonOtherEt.isEnabled = false

        //setting up back button on toolbar
        setSupportActionBar(binding.accountDeleteTb)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val upArrow = AppUtilities.changeIconColor(
            resources.getDrawable(R.drawable.ic_arrow_back, theme), this, R.color.colorWhite
        )
        supportActionBar!!.setHomeAsUpIndicator(upArrow)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.btnDelete.setOnClickListener {
            val selectedId: Int = binding.reasonRadioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                /*Toast.makeText(this@AccountDeleteActivity,
                        findViewById<RadioButton>(selectedId).text,
                        Toast.LENGTH_SHORT)
                        .show()*/

                displayConfirmationPopup()
            }
        }

        binding.reasonRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
            //val radioButton = radioGroup.findViewById(i) as RadioButton
            when (i) {
                R.id.other_rb -> {
                    binding.reasonOtherEt.isEnabled = true
                }
                else -> {
                    binding.reasonOtherEt.isEnabled = false
                }
            }
            binding.btnDelete.isEnabled = true
        }

    }

    private fun displayConfirmationPopup() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
            .create()
        builder.setCancelable(false)
        val view = layoutInflater.inflate(R.layout.delete_account_popup, null)
        val deleteBtn = view.findViewById<Button>(R.id.delete_btn)
        val cancelBtn = view.findViewById<Button>(R.id.cancel_btn)
        var confirmLayout = view.findViewById<ConstraintLayout>(R.id.delete_confirm_cl)
        var deleteProgressLayout = view.findViewById<ConstraintLayout>(R.id.delete_progress_cl)
        builder.setView(view)
        cancelBtn.setOnClickListener {
            builder.dismiss()
        }
        deleteBtn.setOnClickListener {
            confirmLayout.visibility = View.GONE
            deleteProgressLayout.visibility = View.VISIBLE
            /*lifecycleScope.launch {
                delay(3000)
                var intent1 = Intent(this@AccountDeleteActivity, AccountDeleteAckActivity::class.java)
                startActivity(intent1)
                builder.dismiss()
            }*/

            var reason = ""
            var id = binding.reasonRadioGroup.checkedRadioButtonId
            reason = when (id) {
                R.id.other_rb -> binding.reasonOtherEt.text.toString()
                else -> findViewById<RadioButton>(id).text.toString()
            }
            accountDeleteViewModel.requestForUserAccountDelete(
                AccountDeleteRequest(
                    reason,
                    binding.improvementEt.text.toString()
                )
            )
            lifecycleScope.launchWhenStarted {

                accountDeleteViewModel.accountDeleteResponseLiveData.observe(
                    this@AccountDeleteActivity,
                    Observer {
                        builder.dismiss()
                        when (it) {
                            is UserAccountDeleteViewModel.AccountDeleteEvent.Success -> {
                                //Toast.makeText(this@AccountDeleteActivity,it.resultedText,Toast.LENGTH_SHORT).show()
                                navigateUserToDeleteAckScreen()
                            }
                            is UserAccountDeleteViewModel.AccountDeleteEvent.Failure -> {
                                val gson = GsonBuilder().create()
                                try {
                                    var apiError = gson.fromJson(
                                        it.errorText,
                                        APIError::class.java
                                    )
                                    if (apiError.status_code == 400) {
                                        navigateUserToDeleteAckScreen()
                                    } else {
                                        ErrorHandlerClass.errorHandler(
                                            this@AccountDeleteActivity,
                                            it.errorText
                                        )
                                        //  Toast.makeText(this@AccountDeleteActivity,apiError.message,Toast.LENGTH_SHORT).show()
                                    }

                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this@AccountDeleteActivity,
                                        "Something went wrong,please try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                            }
                            else -> Unit
                        }
                    })
            }
        }
        builder.setCanceledOnTouchOutside(false)
        builder.show()
    }

    private fun navigateUserToDeleteAckScreen() {
        var intent1 = Intent(this@AccountDeleteActivity, AccountDeleteAckActivity::class.java)
        intent1.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent1)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}