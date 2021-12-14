package com.example.practicafinalcompose.retrofit

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

const val base_url = "http://iesayala.ddns.net/DavidQuesada/"

interface PeliculasInterface {
    @GET("selectDB.php/")
    fun peliculasInfo(): Call<PeliculasInfo>
}

object PeliculasInstance {
    val peliculasInterface: PeliculasInterface

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(base_url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        peliculasInterface = retrofit.create(PeliculasInterface::class.java)
    }


}