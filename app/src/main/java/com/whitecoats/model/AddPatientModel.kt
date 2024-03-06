package com.whitecoats.model

class AddPatientModel {
    var interfaceName: String? = null
    var interfaceId = 0
    var isAutoGenrateGeneralId: String? = null
    var isAutoRegistered: String? = null
    override fun toString(): String {
        return interfaceName!!
    }
}