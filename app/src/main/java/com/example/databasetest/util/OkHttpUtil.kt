package com.example.databasetest.util

import android.util.Log
import okhttp3.*
import java.io.IOException

class OkHttpUtil private constructor() {
    private val builder: OkHttpClient.Builder = OkHttpClient.Builder()
    private val requestBuilder: Request.Builder
    private val okHttpClient: OkHttpClient
    fun httpGet(url: String?, callback: ICallback) {
        val request = requestBuilder.url(url).build()
        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.invoke("数据错误")
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                callback.invoke(response.body().string())
            }
        })
    }

    /**
     * 接口用于回调数据
     */
    interface ICallback {
        operator fun invoke(string: String?)
    }

    /**
     * 请求拦截器
     */
    internal class RequestLoggerInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            Log.e(this.javaClass.simpleName, "url    =  : " + request.url())
            Log.e(this.javaClass.simpleName, "method =  : " + request.method())
            Log.e(this.javaClass.simpleName, "headers=  : " + request.headers())
            Log.e(this.javaClass.simpleName, "body   =  : " + request.body())
            return chain.proceed(request)
        }
    }

    /**
     * 响应拦截器
     */
    internal class ResponseLoggerInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val response = chain.proceed(chain.request())
            Log.e(this.javaClass.simpleName, "code    =  : " + response.code())
            Log.e(this.javaClass.simpleName, "message =  : " + response.message())
            Log.e(this.javaClass.simpleName, "protocol=  : " + response.protocol())
            return if (response.body() != null && response.body().contentType() != null) {
                val mediaType = response.body().contentType()
                val string = response.body().string()
                Log.e(this.javaClass.simpleName, "mediaType=  :  $mediaType")
                Log.e(this.javaClass.simpleName, "string   =  : $string")
                val responseBody = ResponseBody.create(mediaType, string)
                response.newBuilder().body(responseBody).build()
            } else {
                response
            }
        }
    }

    companion object {
        var okHttpUtil: OkHttpUtil? = null
        val instance: OkHttpUtil?
            get() {
                if (null == okHttpUtil) {
                    synchronized(OkHttpUtil::class.java) {
                        if (null == okHttpUtil) {
                            okHttpUtil = OkHttpUtil()
                        }
                    }
                }
                return okHttpUtil
            }
    }

    init {
        okHttpClient = builder.addInterceptor(RequestLoggerInterceptor())
            .addInterceptor(ResponseLoggerInterceptor())
            .build()
        requestBuilder = Request.Builder() //省的每次都new  request操作,直接builder出来,随后需要什么往里加,build出来即可
    }
}