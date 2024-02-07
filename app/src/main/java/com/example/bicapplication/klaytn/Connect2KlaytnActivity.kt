package com.example.bicapplication.klaytn

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat.startActivity
import com.example.bicapplication.databinding.ActivityConnect2KlaytnBinding
import com.example.bicapplication.retrofit.RetrofitInterface
import retrofit2.*
import retrofit2.http.Url
import java.net.URISyntaxException
import java.net.URLEncoder

class Connect2KlaytnActivity : AppCompatActivity() {
    private lateinit var binding:ActivityConnect2KlaytnBinding

    val retrofitInterface = RetrofitInterface.create("https://api.kaikas.io/api/v1/k/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConnect2KlaytnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.webviewConnect2klaytn.settings.apply {
            setSupportMultipleWindows(true)
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            loadWithOverviewMode = true
            useWideViewPort = true
            setSupportZoom(false)
            domStorageEnabled = true
        }
        connect2kaikas()

    }

    private fun connect2kaikas() {
        val reqAuth = AuthData("auth", AuthData.Bapp("BIC"), AuthData.CallBack("http://bic/main", "http://bic/main"))
        var request_key = ""

        //prepare - request - result
        //retrofit 객체 만들어서 method 사용
        retrofitInterface.requestAuth(reqAuth).enqueue(object : Callback<PrepareRespData> {
            override fun onResponse(call: Call<PrepareRespData>, response: Response<PrepareRespData>) {
                //통신 성공했을 때
                if(response.isSuccessful){
                    request_key = response.body()?.request_key.toString()
                    Log.d("AUTH", "success auth ${response} and ${request_key}")
                    request(request_key)
                }
                else{
                    Log.d("AUTH", "success auth, but ${response.errorBody()}")
                }
            }
            //통신 실패했을 때
            override fun onFailure(call: Call<PrepareRespData>, t: Throwable) {
                Log.d("AUTH", "fail with ${t}")
            }

        })
    }

    //deeplink to kaikas app
    private fun request(reqkey:String) {
        try {
            val url = "https://app.kaikas.io/a/${reqkey}"
            initViews()
            binding.webviewConnect2klaytn.loadUrl(url)
            Log.d("REQUEST", "success to url")
        } catch (e:Exception){
            Log.d("REQUEST", "fail to request. ${e}")
        }
    }

    private fun resultAuth(reqkey: String, iface: RetrofitInterface) {
        var walletaddr = ""
        //retrofit 객체 만들어서 method 사용
        iface.requestResult(reqkey).enqueue(object:Callback<AuthResultData>{
            override fun onResponse(call: Call<AuthResultData>, response: Response<AuthResultData>) {
                if (response.isSuccessful){
                    walletaddr = response.body()?.result?.klaytn_address.toString()
                    Log.d("RESULT", "success to take wallet addr: ${walletaddr}")
                }
            }

            override fun onFailure(call: Call<AuthResultData>, t: Throwable) {
                Log.d("RESULT", "fail to take wallet address")
            }
        })
    }

    private fun initViews(){
        binding.webviewConnect2klaytn.webChromeClient = object : WebChromeClient(){
            override fun onCloseWindow(window: WebView?) {
                Log.d("CLOSEVIEW", "close webview")
            }
        }
        binding.webviewConnect2klaytn.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView, url:String): Boolean {
                url?.let {
                    if (!URLUtil.isNetworkUrl(url)&& !URLUtil.isJavaScriptUrl(url)){
                        // 딥링크로 URI 객체 만들기
                        val uri = try {
                            Uri.parse(url)
                        } catch (e:Exception){
                            Log.d("MAKE URI", "EXCEPTION ${e}")
                            return false
                        }
                        Log.d("MAKE URI", "make ${uri}")
                        return when (uri.scheme){
                            "intent" -> {
                                Log.d("INTENT", "move to intent")
                                startSchemeIntent(it)
                            }
                            else -> {
                                return try{
                                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                                    Log.d("INTENT", "move to activity")
                                    true
                                } catch (e:java.lang.Exception){
                                    false
                                }
                            }
                        }
                    } else {
                        return false
                    }
                } ?: return false
            }
        }
    }

    private fun startSchemeIntent(url:String):Boolean {
        val schemeIntent: Intent = try {
            Intent.parseUri(url, Intent.URI_INTENT_SCHEME) // Intent scheme parsing
        } catch (e:URISyntaxException){
            return false
        }
        try {
            Log.d("SCHEME", "move to app")
            startActivity(schemeIntent) // 앱으로 이동
            return true
        } catch (e:ActivityNotFoundException){ // 앱이 설치안되어 있는 경우
            Log.d("SCHEME", "move to market")
            val packageName = schemeIntent.`package`

            if (!packageName.isNullOrBlank()){
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$packageName") // 스토어로 이동
                    )
                )
                return true
            }
        }
        return false
    }

}