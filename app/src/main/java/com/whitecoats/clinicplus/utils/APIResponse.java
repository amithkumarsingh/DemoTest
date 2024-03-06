package com.whitecoats.clinicplus.utils;

public class APIResponse<T> {

    public T responseClassObj;
    public String error;


    public APIResponse(T obj){
        this.responseClassObj=obj;
        this.error=null;
    }

    public APIResponse(String _error){
        this.responseClassObj=null;
        this.error=_error;
    }

    public T getResponseClassObj() {
        return responseClassObj;
    }

    public String getError() {
        return error;
    }
}
