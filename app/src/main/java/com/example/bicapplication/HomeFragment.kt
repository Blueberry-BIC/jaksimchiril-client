package com.example.bicapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bicapplication.Adapter.ChallListAdapter
import com.example.bicapplication.MainActivity.Companion.userId
import com.example.bicapplication.databinding.FragmentHomeBinding
import com.example.bicapplication.datamodel.ChallData
import com.example.bicapplication.manager.DataStoreModule
import com.example.bicapplication.retrofit.RetrofitInterface
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ChallListAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var dataStoreModule: DataStoreModule
    private lateinit var userName: String
    var challDataArray: ArrayList<ChallData> = ArrayList()    //192.168.175.133
    val retrofitInterface = RetrofitInterface.create(GlobalVari.getUrl())   //192.168.0.101 - 실제기기 연결시(본인 와파 ip 넣기)  //10.0.2.2 - 애뮬레이터 연결시

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Grid Layout
        layoutManager = GridLayoutManager(activity, 2)
        binding.homeRecyclerview.layoutManager = layoutManager

        dataStoreModule = DataStoreModule(requireContext())

        lifecycleScope.launch {
            userName = dataStoreModule.userNameData.first()
            Log.d("dataStore", "[homefrag] user_id: " + userName)
            binding.homeIdTextview.text = "안녕하세요, " + userName + " 님"
        }

        retrofitInterface.getActivatedChallInfo().enqueue(object : Callback<ArrayList<Any>> {
            override fun onResponse(
                call: Call<ArrayList<Any>>,
                response: Response<ArrayList<Any>>
            ) {
                if (response.isSuccessful) {
                    val challArray = JSONArray(response.body())
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
                    val todayDate = LocalDate.now().toString()
                    val today = dateFormat.parse(todayDate)

                    for (i in 0 until challArray.length()) {
                        var data = challArray.getJSONObject(i)

                        val startDate = data.getString("start_date").substring(0, 10)
                        val start = dateFormat.parse(startDate)
                        Log.d("homefrag", "data=${data}")

                        // 오늘 날짜 기준으로 시작 전인 챌린지 목록만 나타내기
                        if (today.before(start)) {
                            var challData = ChallData.getDefault()
                            challData.challId = data.getString("_id")
                            challData.challName = data.getString("chall_name")
                            challData.challDesc = data.getString("chall_desc")
                            challData.startdate = data.getString("start_date").substring(0, 10)
                            challData.enddate = data.getString("end_date").substring(0, 10)
                            challData.authMethod = data.getInt("auth_method")
                            challData.isPublic = data.getBoolean("is_public")
                            challData.category = data.getString("category")
                            challData.passwd = data.getInt("passwd")
                            challData.money = data.getInt("money")
                            challData.userNum = data.getInt("user_num")
                            challData.totalDays = data.getLong("total_days")
                            challData.isProgress = data.getInt("is_progress")

                            challDataArray.add(challData)
                        }
                    }

                    adapter = ChallListAdapter(challDataArray) {
                        Log.d("CHALL", "click event ${it}")
                        SelectedchallActivity.challData = it
                        val intent = Intent(activity, SelectedchallActivity::class.java)
                        startActivity(intent)
                    }
                    binding.homeRecyclerview.adapter = adapter

                }
                else {
                    Log.d("CHALL", "success getActivatedChall2 ${response.errorBody()}")
                }

            }

            override fun onFailure(call: Call<ArrayList<Any>>, t: Throwable) {
                Log.d("CHALL", "fail getActivatedChall ${t}")
            }
        })


        binding.addBtn.setOnClickListener {
            val intent = Intent(requireActivity(), NewchallActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }



    companion object {
        fun newInstance() : HomeFragment {
            return HomeFragment()
        }
    }
}