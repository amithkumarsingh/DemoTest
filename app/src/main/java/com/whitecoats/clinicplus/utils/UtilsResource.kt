package com.whitecoats.clinicplus.utils


class UtilsResource<T> (
     val status: Status, val data: T?,
     val message: String?
) {
    enum class Status {
        SUCCESS, ERROR
    }

    companion object {
        fun <T> success(data: T): UtilsResource<T> {
            return UtilsResource(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String?): UtilsResource<T> {
            return UtilsResource(Status.ERROR, null, msg)
        }
    }
}
