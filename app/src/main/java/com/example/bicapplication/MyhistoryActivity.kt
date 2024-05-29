package com.example.bicapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.bicapplication.adapter.MyhistoryAdapter
import com.example.bicapplication.databinding.ActivityMyhistoryBinding
import com.example.bicapplication.datamodel.ChallData
import com.example.bicapplication.manager.DataStoreModule
import com.example.bicapplication.retrofit.RetrofitInterface
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyhistoryActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMyhistoryBinding
    private lateinit var dataStoreModule: DataStoreModule
    private lateinit var userId: String
    private lateinit var adapter: MyhistoryAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    var completedChallArray: ArrayList<ChallData> = ArrayList()
    val retrofitInterface = RetrofitInterface.create(GlobalVari.getUrl())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyhistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataStoreModule = DataStoreModule(applicationContext)

        lifecycleScope.launch {
            userId = dataStoreModule.userIdData.first()
            Log.d("dataStore", "[Myhistory] user_id: " + userId)
        }

        layoutManager = LinearLayoutManager(this)
        binding.myhistoryRecyclerview.layoutManager = layoutManager

        retrofitInterface.getCompletedChall(userId).enqueue(object: Callback<ArrayList<Any>> {
            override fun onResponse(
                call: Call<ArrayList<Any>>,
                response: Response<ArrayList<Any>>
            ) {
                if (response.isSuccessful) {
                    Log.d("myhistory", "${response.body()}")
                    val challArray = JSONArray(response.body())

                    for (i in 0 until challArray.length()) {
                        var data = challArray.getJSONObject(i)
                        Log.d("myhistory", "data=${data}")

                        var completed = ChallData.getDefault()
                        completed.challId = data.getString("_id")
                        completed.challName = data.getString("chall_name")
                        completed.startdate = data.getString("start_date").substring(0, 10)
                        completed.authMethod = data.getInt("auth_method")
                        completed.category = data.getString("category")
                        completed.userNum = data.getInt("user_num")
                        completed.totalDays = data.getLong("total_days")

                        completedChallArray.add(completed)
                    }
                    adapter = MyhistoryAdapter(completedChallArray) {
                        val intent = Intent(this@MyhistoryActivity, ShowPrizeStatusActivity::class.java)
                        startActivity(intent)
                    }

                    binding.myhistoryRecyclerview.adapter = adapter
                }
                else {
                    Log.d("Myhistory", "success getCompletedChall ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<Any>>, t: Throwable) {
                Log.d("Myhistory", "fail getCompletedChall ${t}")
            }
        })
    }

}