package com.bestteam.myfitroutine.Dialog

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bestteam.myfitroutine.Adapter.MealDialogResultAdapter
import com.bestteam.myfitroutine.Adapter.MealDialogSearchAdapter
import com.bestteam.myfitroutine.Contain
import com.bestteam.myfitroutine.Model.MealData
import com.bestteam.myfitroutine.Model.Meal_Adapter_Data
import com.bestteam.myfitroutine.Model.TotalNumData
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.View.MealFragment
import com.bestteam.myfitroutine.ViewModel.MealPlusViewModel
import com.bestteam.myfitroutine.databinding.MealDialogBinding
import com.bestteam.myfitroutine.retrofit.NetworkClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import kotlinx.coroutines.tasks.await
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MealPlusDialog() : DialogFragment(), MealDialogResultAdapter.ButtonClick {

    private lateinit var binding: MealDialogBinding
    private lateinit var viewModel: MealPlusViewModel
    private lateinit var searchAdapter: MealDialogSearchAdapter
    private lateinit var resultAdapter: MealDialogResultAdapter
    private lateinit var mealContext: Context
    private lateinit var gridLayoutManager: StaggeredGridLayoutManager

    private var searchData: ArrayList<Meal_Adapter_Data> = ArrayList()
    private var resultData: ArrayList<Meal_Adapter_Data> = ArrayList()

    private val db = Firebase.firestore
    private var auth: FirebaseAuth? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mealContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        binding = MealDialogBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[MealPlusViewModel::class.java]


        // 검색 기능
        searchSetupView()
        setupListeners()

        // clip 클릭시 firestore 데이터 가져오기
        chipClick()

        totalCalories()

        binding.btnDialogCancel.setOnClickListener {
            btnPlus()
            dismiss()

            val mealFragment = MealFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentFrame, mealFragment)
            transaction.commit()
        }

        binding.mealDialogPlus.setOnClickListener {

            btnPlus()
            dismiss()

            val mealFragment = MealFragment()
            val fragmentManager = requireActivity().supportFragmentManager
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentFrame, mealFragment)
            transaction.commit()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        isCancelable = false
    }

    override fun onResume() {
        super.onResume()

        //디바이스 크기 구하기
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
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

    private fun searchSetupView() {
        gridLayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.rvSearch.layoutManager = gridLayoutManager
        searchAdapter = MealDialogSearchAdapter(mealContext, searchData)
        binding.rvSearch.adapter = searchAdapter
        binding.rvSearch.itemAnimator = null

        searchAdapter.itemClick = object : MealDialogSearchAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {

                binding.rvSearch.isGone = true

                viewModel.searchPosition.value = position

                // mealType viewModel로 값 넘기기
                if (binding.mealDialogTypeBreakfast.isChecked) {
                    viewModel.mealDialogType.value = "아침"
                } else if (binding.mealDialogTypeLunch.isChecked) {
                    viewModel.mealDialogType.value = "점심"
                } else if (binding.mealDialogTypeDinner.isChecked) {
                    viewModel.mealDialogType.value = "저녁"
                } else {
                    viewModel.mealDialogType.value = "그외"
                }

                viewModel.mealSearchItemClick()

                searchAdapter.dataSet.clear()
                binding.mealEditText.setText("")

                resultSetupView()
                totalCalories()
            }
        }
    }

    private fun setupListeners() {
        binding.searchImage.setOnClickListener {
            val breakfast = binding.mealDialogTypeBreakfast
            val lunch = binding.mealDialogTypeLunch
            val dinner = binding.mealDialogTypeDinner
            val etc = binding.mealDialogTypeEtc

            val desc_kor = binding.mealEditText.text.toString()
            if (desc_kor.isNotEmpty()) {

                if (!breakfast.isChecked && !lunch.isChecked && !dinner.isChecked && !etc.isChecked) {
                    Toast.makeText(mealContext, "분류를 선택해 주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    fetchSearchResults(desc_kor)
                    binding.rvSearch.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(mealContext, "검색어를 입력해 주세요.", Toast.LENGTH_SHORT).show()
                binding.rvSearch.visibility = View.GONE
            }
        }
    }

    private fun fetchSearchResults(desc_kor: String) {

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
    }

    private fun resultSetupView() {
        resultData.clear()

//        gridLayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.rvResult.layoutManager = LinearLayoutManager(mealContext)
        resultAdapter = MealDialogResultAdapter(mealContext, this)
        binding.rvResult.adapter = resultAdapter


        // mealType viewModel로 값 넘기기
        if (binding.mealDialogTypeBreakfast.isChecked) {
            viewModel.mealDialogType.value = "아침"
        } else if (binding.mealDialogTypeLunch.isChecked) {
            viewModel.mealDialogType.value = "점심"
        } else if (binding.mealDialogTypeDinner.isChecked) {
            viewModel.mealDialogType.value = "저녁"
        } else {
            viewModel.mealDialogType.value = "그외"
        }

        viewModel.mealResultSetting()
        viewModel.resultData.observe(this) { resultData ->
            this.resultData.clear()
            this.resultData.addAll(resultData)
            Log.e("resultData", "resultData : ${this.resultData}")
            resultAdapter.resultDataSet = this.resultData
            resultAdapter.notifyDataSetChanged()
        }
    }

    private fun breakfastResult(){

        binding.rvResult.layoutManager = LinearLayoutManager(mealContext)
        resultAdapter = MealDialogResultAdapter(mealContext, this)
        binding.rvResult.adapter = resultAdapter

        resultData.clear()
        resultAdapter.resultDataSet.clear()

        viewModel.breakfastResultSetting()
        viewModel.resultData.observe(this) { resultData ->
            this.resultData.clear()
            this.resultData.addAll(resultData)
            Log.e("resultData", "resultData : ${this.resultData}")
            resultAdapter.resultDataSet = this.resultData
            resultAdapter.notifyDataSetChanged()
        }

    }
    private fun lunchResult(){

        binding.rvResult.layoutManager = LinearLayoutManager(mealContext)
        resultAdapter = MealDialogResultAdapter(mealContext, this)
        binding.rvResult.adapter = resultAdapter

        resultData.clear()
        resultAdapter.resultDataSet.clear()

        viewModel.lunchResultSetting()
        viewModel.resultData.observe(this) { resultData ->
            this.resultData.clear()
            this.resultData.addAll(resultData)
            Log.e("resultData", "resultData : ${this.resultData}")
            resultAdapter.resultDataSet = this.resultData
            resultAdapter.notifyDataSetChanged()
        }

    }
    private fun dinnerResult(){

        binding.rvResult.layoutManager = LinearLayoutManager(mealContext)
        resultAdapter = MealDialogResultAdapter(mealContext, this)
        binding.rvResult.adapter = resultAdapter

        viewModel.dinnerResultSetting()
        viewModel.resultData.observe(this) { resultData ->
            this.resultData.clear()
            this.resultData.addAll(resultData)
            Log.e("resultData", "resultData : ${this.resultData}")
            resultAdapter.resultDataSet = this.resultData
            resultAdapter.notifyDataSetChanged()
        }

    }
    private fun etcResult(){

        binding.rvResult.layoutManager = LinearLayoutManager(mealContext)
        resultAdapter = MealDialogResultAdapter(mealContext, this)
        binding.rvResult.adapter = resultAdapter

        resultData.clear()
        resultAdapter.resultDataSet.clear()

        viewModel.etcResultSetting()
        viewModel.resultData.observe(this) { resultData ->
            this.resultData.addAll(resultData)
            Log.e("resultData", "resultData : ${this.resultData}")
            resultAdapter.resultDataSet = this.resultData
            resultAdapter.notifyDataSetChanged()
        }

    }

    private fun chipClick() {

        binding.chipGroup.setOnCheckedChangeListener{ _, checkedId ->

            when(checkedId){
                R.id.meal_dialog_type_breakfast -> {
                    breakfastResult()
                    clickBreakfastChipCalorie()
                    resultData.clear()
                }
                R.id.meal_dialog_type_lunch -> {
                    lunchResult()
                    clickLunchChipCalorie()
                    resultData.clear()
                }
                R.id.meal_dialog_type_dinner -> {
                    dinnerResult()
                    clickDinnerChipCalorie()
                    resultData.clear()
                }
                R.id.meal_dialog_type_etc -> {
                    etcResult()
                    clickEtcChipCalorie()
                    resultData.clear()
                }
            }

        }
    }

    fun totalCalories() {

        // mealType viewModel로 값 넘기기
        if (binding.mealDialogTypeBreakfast.isChecked) {
            viewModel.mealDialogType.value = "아침"
        } else if (binding.mealDialogTypeLunch.isChecked) {
            viewModel.mealDialogType.value = "점심"
        } else if (binding.mealDialogTypeDinner.isChecked) {
            viewModel.mealDialogType.value = "저녁"
        } else {
            viewModel.mealDialogType.value = "그외"
        }

        viewModel.mealTotalCalorie()
    }

    private fun clickBreakfastChipCalorie() {
        val totalCal = binding.mealDialogTotalCalNum
        val totalCar = binding.mealDialogTotalCarbohydrateNum
        val totalPro = binding.mealDialogTotalProteinNum
        val totalFat = binding.mealDialogTotalFatNum

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealTypeTotalNum = db.collection("$uid").document(dateFormat).collection("totalNum")

        totalCal.text = "0"
        totalCar.text = "0"
        totalPro.text = "0"
        totalFat.text = "0"
        mealTypeTotalNum.document("아침 totalNum")
            .get()
            .addOnSuccessListener {
                if (it.contains("calorieSum") && it.contains("carbohydrateSum") && it.contains("proteinSum") && it.contains(
                        "fatSum"
                    )
                ) {
                    val mealTypeCal = it.getLong("calorieSum")
                    val mealTypeCar = it.getLong("carbohydrateSum")
                    val mealTypePro = it.getLong("proteinSum")
                    val mealTypeFat = it.getLong("fatSum")

                    totalCal.text = mealTypeCal.toString()
                    totalCar.text = mealTypeCar.toString()
                    totalPro.text = mealTypePro.toString()
                    totalFat.text = mealTypeFat.toString()
                }
            }
    }

    private fun clickLunchChipCalorie() {
        val totalCal = binding.mealDialogTotalCalNum
        val totalCar = binding.mealDialogTotalCarbohydrateNum
        val totalPro = binding.mealDialogTotalProteinNum
        val totalFat = binding.mealDialogTotalFatNum

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealTypeTotalNum = db.collection("$uid").document(dateFormat).collection("totalNum")

        totalCal.text = "0"
        totalCar.text = "0"
        totalPro.text = "0"
        totalFat.text = "0"

        mealTypeTotalNum.document("점심 totalNum")
            .get()
            .addOnSuccessListener {
                if (it.contains("calorieSum") && it.contains("carbohydrateSum") && it.contains("proteinSum") && it.contains(
                        "fatSum"
                    )
                ) {
                    val mealTypeCal = it.getLong("calorieSum")
                    val mealTypeCar = it.getLong("carbohydrateSum")
                    val mealTypePro = it.getLong("proteinSum")
                    val mealTypeFat = it.getLong("fatSum")

                    totalCal.text = mealTypeCal.toString()
                    totalCar.text = mealTypeCar.toString()
                    totalPro.text = mealTypePro.toString()
                    totalFat.text = mealTypeFat.toString()
                }
            }
    }
    private fun clickDinnerChipCalorie() {
        val totalCal = binding.mealDialogTotalCalNum
        val totalCar = binding.mealDialogTotalCarbohydrateNum
        val totalPro = binding.mealDialogTotalProteinNum
        val totalFat = binding.mealDialogTotalFatNum

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealTypeTotalNum = db.collection("$uid").document(dateFormat).collection("totalNum")

        totalCal.text = "0"
        totalCar.text = "0"
        totalPro.text = "0"
        totalFat.text = "0"
        mealTypeTotalNum.document("저녁 totalNum")
            .get()
            .addOnSuccessListener {
                if (it.contains("calorieSum") && it.contains("carbohydrateSum") && it.contains("proteinSum") && it.contains(
                        "fatSum"
                    )
                ) {
                    val mealTypeCal = it.getLong("calorieSum")
                    val mealTypeCar = it.getLong("carbohydrateSum")
                    val mealTypePro = it.getLong("proteinSum")
                    val mealTypeFat = it.getLong("fatSum")

                    totalCal.text = mealTypeCal.toString()
                    totalCar.text = mealTypeCar.toString()
                    totalPro.text = mealTypePro.toString()
                    totalFat.text = mealTypeFat.toString()
                }
            }
    }
    private fun clickEtcChipCalorie(){
        val totalCal = binding.mealDialogTotalCalNum
        val totalCar = binding.mealDialogTotalCarbohydrateNum
        val totalPro = binding.mealDialogTotalProteinNum
        val totalFat = binding.mealDialogTotalFatNum

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealTypeTotalNum = db.collection("$uid").document(dateFormat).collection("totalNum")

            totalCal.text = "0"
            totalCar.text = "0"
            totalPro.text = "0"
            totalFat.text = "0"
            mealTypeTotalNum.document("그외 totalNum")
                .get()
                .addOnSuccessListener {
                    if (it.contains("calorieSum") && it.contains("carbohydrateSum") && it.contains("proteinSum") && it.contains(
                            "fatSum"
                        )
                    ) {
                        val mealTypeCal = it.getLong("calorieSum")
                        val mealTypeCar = it.getLong("carbohydrateSum")
                        val mealTypePro = it.getLong("proteinSum")
                        val mealTypeFat = it.getLong("fatSum")

                        totalCal.text = mealTypeCal.toString()
                        totalCar.text = mealTypeCar.toString()
                        totalPro.text = mealTypePro.toString()
                        totalFat.text = mealTypeFat.toString()
                    }
                }
        }


    //삭제
    override fun resultMealDelete(position: Int) {

        resultAdapter = MealDialogResultAdapter(mealContext, this)

        viewModel.resultPosition.value = position

        // mealType viewModel로 값 넘기기
        if (binding.mealDialogTypeBreakfast.isChecked) {
            viewModel.mealDialogType.value = "아침"
        } else if (binding.mealDialogTypeLunch.isChecked) {
            viewModel.mealDialogType.value = "점심"
        } else if (binding.mealDialogTypeDinner.isChecked) {
            viewModel.mealDialogType.value = "저녁"
        } else {
            viewModel.mealDialogType.value = "그외"
        }

        viewModel.mealResultDelete(mealContext)

        viewModel.resultData.observe(this) {
            resultData.removeAt(position)
            resultAdapter.resultDataSet = resultData
            resultAdapter.notifyDataSetChanged()
        }

        totalCalories()
    }

    //+버튼 클릭
    override fun resultMealEditPlus(position: Int) {

        resultAdapter = MealDialogResultAdapter(mealContext, this)

        viewModel.resultPosition.value = position

        // mealType viewModel로 값 넘기기
        if (binding.mealDialogTypeBreakfast.isChecked) {
            viewModel.mealDialogType.value = "아침"
        } else if (binding.mealDialogTypeLunch.isChecked) {
            viewModel.mealDialogType.value = "점심"
        } else if (binding.mealDialogTypeDinner.isChecked) {
            viewModel.mealDialogType.value = "저녁"
        } else {
            viewModel.mealDialogType.value = "그외"
        }

        viewModel.mealResultPlus()
        viewModel.resultData.observe(this) { resultData ->
            this.resultData.clear()
            this.resultData.addAll(resultData)
            resultAdapter.resultDataSet = this.resultData
            resultAdapter.notifyDataSetChanged()

        }
        totalCalories()
    }

    //-버튼 클릭
    override fun resultMealEditMinus(position: Int) {

        resultAdapter = MealDialogResultAdapter(mealContext, this)

        viewModel.resultPosition.value = position

        // mealType viewModel로 값 넘기기
        if (binding.mealDialogTypeBreakfast.isChecked) {
            viewModel.mealDialogType.value = "아침"
        } else if (binding.mealDialogTypeLunch.isChecked) {
            viewModel.mealDialogType.value = "점심"
        } else if (binding.mealDialogTypeDinner.isChecked) {
            viewModel.mealDialogType.value = "저녁"
        } else {
            viewModel.mealDialogType.value = "그외"
        }

        viewModel.mealResultMinus()
        viewModel.resultData.observe(this) { resultData ->
            this.resultData.clear()
            this.resultData.addAll(resultData)
            resultAdapter.resultDataSet = this.resultData
            resultAdapter.notifyDataSetChanged()

        }
    }

    fun btnPlus() {
        viewModel.btnComplete(mealContext)
    }
}