package com.bestteam.myfitroutine.Dialog

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bestteam.myfitroutine.Adapter.MealDialogSearchAdapter
import com.bestteam.myfitroutine.Contain
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.Utils
import com.bestteam.myfitroutine.ViewModel.MealViewModel
import com.bestteam.myfitroutine.databinding.MealDialogBinding
import com.bestteam.myfitroutine.retrofit.NetworkClient.api

class MealPlusDialog : DialogFragment() {

    private lateinit var binding : MealDialogBinding
    private lateinit var viewModel : MealViewModel
    private lateinit var adapter : MealDialogSearchAdapter
    private lateinit var mealContext : Context
    private lateinit var gridLayoutManager: StaggeredGridLayoutManager

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mealContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = MealDialogBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MealViewModel::class.java)
//        viewModel.getMealData()

//        viewModel.searchResult.observe(this, Observer {
//
//            setupView()
//            setupListeners()
//
//        })

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
        adapter = MealDialogSearchAdapter(mealContext)
        binding.rvSearch.adapter = adapter
        binding.rvSearch.itemAnimator = null
    }

    private fun setupListeners(){
        binding.searchImage.setOnClickListener {
            val query = binding.mealEditText.text.toString()
            if(query.isNotEmpty()){
                Utils.saveRecentSearch(requireContext(), query)
                searchMealResult(query)
            }
            else{
                Toast.makeText(mealContext, "검색어를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchMealResult(query : String){
    }
}