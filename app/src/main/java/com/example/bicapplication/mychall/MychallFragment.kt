package com.example.bicapplication.mychall

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bicapplication.Adapter.ChallListAdapter
import com.example.bicapplication.SelectedchallActivity
import com.example.bicapplication.certify.CertifyStatusActivity
import com.example.bicapplication.databinding.FragmentHomeBinding
import com.example.bicapplication.databinding.FragmentMychallBinding
import com.example.bicapplication.datamodel.ChallData
import com.example.bicapplication.retrofit.RetrofitInterface
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MychallFragment : Fragment() {
    private lateinit var binding: FragmentMychallBinding
    private lateinit var adapter: ChallListAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    var challDataArray: ArrayList<ChallData> = ArrayList()
    val retrofitInterface = RetrofitInterface.create("http://10.0.2.2:8081/")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentMychallBinding.inflate(inflater, container, false)

        // Grid Layout
        layoutManager = GridLayoutManager(activity, 2)
        binding.mychallRecyclerview.layoutManager = layoutManager

        retrofitInterface.getActivatedChallInfo().enqueue(object : Callback<ArrayList<Any>> {
            override fun onResponse(
                call: Call<ArrayList<Any>>,
                response: Response<ArrayList<Any>>
            ) {
                if (response.isSuccessful) {
                    val challArray = JSONArray(response.body())

                    for (i in 0 until challArray.length()) {
                        var data = challArray.getJSONObject(i)

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
                        challData.userNum = data.getInt("user_num")
                        challData.totalDays = data.getLong("total_days")

                        challDataArray.add(challData)
                    }

                    // 챌린지 클릭 시 인증페이지로 이동
                    adapter = ChallListAdapter(challDataArray) {
                        SelectedchallActivity.challData = it
                        val intent = Intent(activity, CertifyStatusActivity::class.java)
                        startActivity(intent)
                    }
                    binding.mychallRecyclerview.adapter = adapter

                }
                else {
                    Log.d("CHALL", "success getActivatedChall2 ${response.errorBody()}")
                }

            }

            override fun onFailure(call: Call<ArrayList<Any>>, t: Throwable) {
                Log.d("CHALL", "fail getActivatedChall ${t}")
            }
        })


        /* 인증현황페이지 테스트 위한 작업
        binding.button.setOnClickListener {
            val intent = Intent(activity, CertifyStatusActivity::class.java)
            startActivity(intent)
        }
         */
        return binding.root
    }

    companion object {
        fun newInstance() : MychallFragment {
            return MychallFragment()
        }
    }
}