package com.whitecoats.clinicplus.utils

enum class ErrorCode(errorCode: Int) {
    FourEightyNine(489),
    FiveHundred(500),
    FourHundred(400),
    FourNotFour(404),
    FourNotOne(401);

    val error: Int = errorCode

    @JvmName("getError1")
    fun getError(): Int {
        return error;
    }
}
