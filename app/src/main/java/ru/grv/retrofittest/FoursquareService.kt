package ru.grv.retrofittest


import retrofit2.http.GET

interface FoursquareService {
    @GET("/devfestapi/data.json")
    fun getVenues(): retrofit2.Call<InfoTopicDto>
}