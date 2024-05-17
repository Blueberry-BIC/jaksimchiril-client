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
import com.example.bicapplication.GlobalVari
import com.example.bicapplication.SelectedchallActivity
import com.example.bicapplication.certify.CertifyStatusActivity
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
    private var userid = "65e3b68de9562a3a91d247ca"
    var challDataArray: ArrayList<ChallData> = ArrayList()
    val retrofitInterface = RetrofitInterface.create(GlobalVari.getUrl()
    )  //127.0.0.1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=FragmentMychallBinding.inflate(inflater, container, false)

        // Grid Layout
        layoutManager = GridLayoutManager(activity, 2)
        binding.mychallRecyclerview.layoutManager = layoutManager

        retrofitInterface.getMyChall(userid).enqueue(object : Callback<ArrayList<Any>> {
            override fun onResponse(
                call: Call<ArrayList<Any>>,
                response: Response<ArrayList<Any>>
            ) {
                if (response.isSuccessful) {
                    val challArray = JSONArray(response.body())

                    for (i in 0 until challArray.length()) {
                        var data = challArray.getJSONObject(i)
                        /*var userlist = data.getJSONArray("user_list")
                        var users = mutableListOf<String>()
                        for (u in 0 until userlist.length()){
                            users.add(userlist[u].toString())
                        }
                        Log.d("mychall", "${users}")*/


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
                        //challData.userList = users

                        challDataArray.add(challData)
                    }

                    //챌린지 클릭시 해당 챌의 id값과 종료기간 인증act로 전달 및 이동
                    adapter = ChallListAdapter(challDataArray) {
                        //val intent = Intent(activity, CertifyStatusActivity::class.java)
                        //intent.putExtra("challId", it.challId)
                        //intent.putExtra("endDate", it.enddate)
                        SelectedchallActivity.challData = it
                        val intent = Intent(activity, SelectedchallActivity::class.java)
                        startActivity(intent)
                    }

                    binding.mychallRecyclerview.adapter = adapter

                }
                else {
                    Log.d("CHALL", "success getMyChall ${response.errorBody()}")
                }

            }

            override fun onFailure(call: Call<ArrayList<Any>>, t: Throwable) {
                Log.d("CHALL", "fail getMyChall ${t}")
            }
        })


        return binding.root
    }

    companion object {
        fun newInstance() : MychallFragment {
            return MychallFragment()
        }
    }
}