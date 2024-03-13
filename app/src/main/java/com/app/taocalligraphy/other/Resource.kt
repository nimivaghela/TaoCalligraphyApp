package com.app.taocalligraphy.other

data class Resource<out T>(
    val status: Status,
    val data: T?,
    val message:String?,
    val statusCode: String?=""
){
    companion object{

        fun <T> success(data:T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(statusCode:String, msg:String, data:T?): Resource<T> {
            return Resource(Status.ERROR, data, msg,statusCode)
        }

        fun <T> loading(data:T?): Resource<T> {
            return Resource(Status.LOADING,data, null)
        }

    }
}

/*
data class Resource<out T,F>(
    val status: Status,
    val data: T?,
    var exception: Throwable? = null,
    var errorData: F? = null
){
    companion object{

        fun <T,F> success(data:T?=null): Resource<T,F> {
            return Resource(Status.SUCCESS, data)
        }

        fun <T,F> error(data:T?=null, throwable: Throwable? = null, errorData: F? = null): Resource<T,F> {
            return Resource(Status.ERROR, data,exception = throwable, errorData = errorData)
        }

        fun <T,F> loading(data:T?=null): Resource<T,F> {
            return Resource(Status.LOADING, data)
        }

    }
}*/
