package com.coffenow.wave.ui.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.coffenow.wave.model.YTModel
import com.coffenow.wave.network.YTApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeViewModel : ViewModel() {

    private val _onlineData = MutableLiveData<YTModel?>()
    val onlineData = _onlineData
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading = _isLoading
    private val _isAllDataOnlineLoaded = MutableLiveData<Boolean>()
    val isAllDataOnlineLoaded = _isAllDataOnlineLoaded
    private val _message = MutableLiveData<String>()
    val message = _message
    var nextPageToken: String? = null
    var querySearch: String? = null

    fun getOnlineList(){
        _isLoading.value = true
        val client = YTApiConfig
            .getService()
            .getVideo(
                "snippet",
                querySearch,
                "video",
                "relevance",
                "12",
                nextPageToken)
        client.enqueue(object : Callback<YTModel>{
            override fun onResponse(call: Call<YTModel>, response: Response<YTModel>) {
                _isLoading.value = false
                if (response.isSuccessful){
                    val data = response.body()
                    if (data != null){
                        if (data.nextPageToken != null) { nextPageToken = data.nextPageToken }
                        else { _isAllDataOnlineLoaded.value = true }
                        if (data.items.isNotEmpty()){ _onlineData.value = data } }
                    else { _message.value = "No Music" }
                } else { _message.value = response.message() } }
            override fun onFailure(call: Call<YTModel>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "Failed: ", t)
                _message.value = t.message }
        })
    }

    companion object {
        private val TAG = HomeViewModel::class.java.simpleName
    }

}

