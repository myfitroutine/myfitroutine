package com.bestteam.myfitroutine.Dialog

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bestteam.myfitroutine.Adapter.MealDialogSearchAdapter
import com.bestteam.myfitroutine.Contain
import com.bestteam.myfitroutine.Model.MealData
import com.bestteam.myfitroutine.Model.Meal_Adapter_Data
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.ViewModel.MealViewModel
import com.bestteam.myfitroutine.databinding.MealDialogBinding
import com.bestteam.myfitroutine.retrofit.NetworkClient.api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MealPlusDialog : DialogFragment() {

    private lateinit var binding : MealDialogBinding
    private lateinit var viewModel : MealViewModel
    private lateinit var searchAdapter : MealDialogSearchAdapter
    private lateinit var mealContext : Context
    private lateinit var gridLayoutManager: StaggeredGridLayoutManager
    private var searchData : ArrayList<Meal_Adapter_Data> = ArrayList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mealContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = MealDialogBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MealViewModel::class.java)

        setupView()
        setupListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false

        binding.btnDialogCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()

        //디바이스 크기 구하기
        val windowManager = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params : ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x

        //디바이스 크기의 %로 크기 조정
        params?.width = (deviceWidth * 0.95).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams

        //다이얼로그 모서리 둥글게 하기
        dialog?.window?.setBackgroundDrawableResource(R.drawable.meal_dialog_shape)


//        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

    }

    private fun setupView(){
        gridLayoutManager = StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)
        binding.rvSearch.layoutManager = gridLayoutManager
        searchAdapter = MealDialogSearchAdapter(mealContext)
        binding.rvSearch.adapter = searchAdapter
        binding.rvSearch.itemAnimator = null
    }

    private fun setupListeners(){
        binding.searchImage.setOnClickListener {
            val desc_kor = binding.mealEditText.text.toString()
            if (desc_kor.isNotEmpty()){

                fetchSearchResults(desc_kor)
            }
            else Toast.makeText(mealContext, "검색어를 입력해 주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchSearchResults(desc_kor:String){

        viewModel.fetchSearchResults(desc_kor)
        viewModel.searchData.observe(viewLifecycleOwner) { searchData ->
            this.searchData.clear()
            this.searchData.addAll(searchData)
            searchAdapter.dataSet = this.searchData
            searchAdapter.notifyDataSetChanged()
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            Log.e("fetchSearchResults", errorMessage)
            Toast.makeText(mealContext, "원하는 검색어가 없습니다.", Toast.LENGTH_SHORT).show()
        }

//        api.getMeal(desc_kor,1,100,"","", Contain.AUTH).enqueue(object : Callback<MealData>{
//            override fun onResponse(call: Call<MealData>, response: Response<MealData>) {
//                response.body()?.mealBody.let {
//                    Log.d("response","response : ${response.body()?.mealBody?.mealItems}")
//
//                    searchData.clear()
//                    if (response.isSuccessful){
//                        response.body()?.mealBody?.mealItems?.forEach {
//                            val title = it.DESC_KOR
//                            val calorie = it.NUTR_CONT1
//                            searchData.add(Meal_Adapter_Data(title,calorie))
//                        }
//                    }
//                    else {
//                        Log.e("fetchSearchResults", "Error: ${response.code()} - ${response.message()}")
//                    }
//                }
//                searchAdapter.dataSet = searchData
//                searchAdapter.notifyDataSetChanged()
//            }
//
//            override fun onFailure(call: Call<MealData>, t: Throwable) {
//                Log.e("fetchSearchResults", "Failure: ${t.message}")
//                Toast.makeText(mealContext, "원하는 검색어가 없습니다.", Toast.LENGTH_SHORT).show()
//            }
//
//        })

    }
}