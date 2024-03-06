package com.whitecoats.clinicplus

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.simbo.simboalphahelper.SimboAlphaHelper
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

class MainActivityRepository {

    private lateinit var simbo_wss1_key: String
    private lateinit var simbo_authKey: String
    private lateinit var simbo_devId: String
    var helper: SimboAlphaHelper? = null
    private var authKey = "rmcC2H08tMojPqalkGm6SKUfxVXDIWeOQWBcLDkuYxWgM4Y0Ne2EV37HW095/Oqo"
   // private var authKey = AppConfigClass.simboAuthKey;
   private val wss1_url = "wss://test.alpha.phit.in/asr"
   //private val wss1_url = AppConfigClass.simboWss1_url
    private val wss2_url = "wss://test.alpha.phit.in/asr/drapp"
   private var devId = "demo-api-2021-02-05-0001"
   //private var devId = AppConfigClass.simboDevId


    private val splDevMapping = mapOf<String,String>("pediatrics" to "demo-api-0010-valmo-pedia", "dermatology" to "demo-api-0011-valmo-derma", "neurology" to "demo-api-0012-valmo-neuro")
    private val devToken = mapOf<String,String>("demo-api-0010-valmo-pedia" to "e3b0c44698fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b858", "demo-api-0011-valmo-derma" to "f0a339f3e6453b7a3cf7e8aab2e7a8808f1ef10846dfa842c436e2e4ecefe259", "demo-api-0012-valmo-neuro" to "212873a1eb43b4e1f28016c4adb12293dfa22060fdfb6fa77a6758827efb9ac9")

    var currentText:String = ""
    var convertedText = ""
    var symtoms = ""
    var diagnosis = ""
    var med = ""
    var followup = ""
    var investigation = ""
    var vitals = ""
    var miscNotes = ""
    var audioLevel=0
    var mutableString:MutableLiveData<String> = MutableLiveData()
    var diagnosisMutable:MutableLiveData<String> = MutableLiveData()
    var symptomsMutable:MutableLiveData<String> = MutableLiveData()
    var medMutable:MutableLiveData<String> = MutableLiveData()
    var followupMutable:MutableLiveData<String> = MutableLiveData()
    var investigationMutable:MutableLiveData<String> = MutableLiveData()
    var vitalsMutable:MutableLiveData<String> = MutableLiveData()
    var miscNotesMutable:MutableLiveData<String> = MutableLiveData()
    var logsMutable:MutableLiveData<String> = MutableLiveData()
    var audioMutable:MutableLiveData<Int> = MutableLiveData()
    var newText:String = ""

    internal fun startAudioRecording(simboDevID: String,simboAuthKey : String,simboWss1_key: String) {
       // devId = splDevMapping.get(speciality).toString()
      //  authKey = devToken.get(devId).toString()
        simbo_devId=simboDevID
        simbo_authKey=simboAuthKey
        simbo_wss1_key=simboWss1_key
        if (helper == null) {
            setupHelper()
        }
            currentText = ""
            convertedText = ""
            diagnosis = ""
            investigation = ""
            vitals = ""
            miscNotes = ""
            med = ""
            medMutable.postValue(med)
            miscNotesMutable.postValue(miscNotes)
            vitalsMutable.postValue(vitals)
            investigationMutable.postValue(investigation)
            diagnosisMutable.postValue(diagnosis)
//            mutableString.postValue(currentText)
            logsMutable.postValue(currentText)

        helper?.connectWSSandAutenticate()
//        currentText = "Hello"
        mutableString.postValue(convertedText)
//        mutableString.value = convertedText
    }

    private  fun setupHelper() {
//        currentText = "Hey"
        mutableString.postValue(convertedText)
//        mutableString.value = currentText
        helper = SimboAlphaHelper(mapOf("devId" to simbo_devId, "authKey" to simbo_authKey,"wssUrl1" to simbo_wss1_key));
        helper?.onWSSMessage ( fun(data:String){
            try {
                val response = JSONObject(data)
                if (response.optString("auth").isNullOrEmpty() == false && response.optString("auth") == "success") {
                   // helper?.sendStates("resume","checkout","listening");
                    helper?.unMuteAudio();
                }
            }catch (e:Exception) {
                e.printStackTrace()
            }
            Log.d("Repository","current message ${data}")
//            currentText += data
            currentText = data
            parseData(data)
            mutableString.postValue(convertedText)
            logsMutable.postValue(currentText)
//            mutableString.value = currentText
        } )
        helper?.onWSSError ( fun(data:String) {
            Log.d("Repository","WSSERrror is ${data}")
         //   print("WSSERrror is ${data}")//commented by dileep
            helper = null
//            currentText += data
//            mutableString.postValue(currentText)
//            mutableString.value = currentText
        } )
        helper?.onError ( fun (data:String) {
            Log.d("Repository","Error is ${data}")
            print("Error is ${data}")
        } )
        helper?.onAlertMsg  (fun (data:String) {
            Log.d("Repository","AlertMessage is ${data}")
            print("AlertMessage is ${data}")
        })

        helper?.onVolumeLevelChange (fun (data: Int) {
            audioLevel=data;
            audioMutable.postValue(audioLevel);
            Log.d("Repository","volume level is ${data}")
        })

    }

    private fun parseData(data:String) {
        try {
            val jsonObject = JSONObject(data)
            if (jsonObject.optJSONObject("stt") != null) {
                val stt = jsonObject.getJSONObject("stt")
                if ( stt.optBoolean("final") == true ) {
//                    convertedText += stt.optString("txt") ?: ""
//                    convertedText += "\n"
                    convertedText = stt.optString("txt") ?: ""
                }
            }else if (jsonObject.optJSONArray("prs") != null) {
               // parseIntentValues(jsonObject.getJSONArray("prs"))
            }
        }catch (e:JSONException) {
            e.printStackTrace()
        }
    }

/*
    private fun parseIntentValues(intents:JSONArray) {
        for (index in 0..intents.length()) {
            val intent = intents.getJSONObject(index)
            val intentValue = intent.optString("intent") ?: ""
            if (intent.optJSONArray("data") != null) {
                val dataValues = intent.getJSONArray("data")
                for(dataIndex in 0..dataValues.length()) {
                    val datavalue = dataValues.getJSONObject(dataIndex)
                    for (dataKey:String in datavalue.keys()) {
                        if (intentValue == "diagnosis") {
                            diagnosis += (dataKey+" : "+ datavalue.getString(dataKey) + "\n")
                            diagnosisMutable.postValue(diagnosis)
                        }else if(intentValue == "med") {
                            med += (dataKey+" : "+ datavalue.getString(dataKey) + "\n")
                            medMutable.postValue(med)
                        }else if(intentValue == "followup") {
                            //Need to check scenario of appending intent also with Ali
                            med += (dataKey+" : "+ datavalue.getString(dataKey) + "\n")
                            medMutable.postValue(med)
                        } else if(intentValue == "ls" || intentValue == "ls_done") {
                            investigation += (dataKey+" : "+ datavalue.getString(dataKey) + "\n")
                            investigationMutable.postValue(investigation)
                        } else if(intentValue == "symp") {
                            symtoms += (dataKey+" : "+ datavalue.getString(dataKey) + "\n")
                            symptomsMutable.postValue(symtoms)
                        } else if(intentValue == "vitals") {
                            vitals += (dataKey+" : "+ datavalue.getString(dataKey) + "\n")
                            vitalsMutable.postValue(vitals)
                        } else if(intentValue == "misc_notes") {
                            miscNotes += (dataKey+" : "+ datavalue.getString(dataKey) + "\n")
                            miscNotesMutable.postValue(miscNotes)
                        }
                    }
                }

            }


        }
    }
*/

    internal  fun stopAudioRecording() {
      helper?.disConnectHelper()
     //   helper?.muteAudio();
//        currentText = "Recording stopped"
//        mutableString.value = currentText
    }
}