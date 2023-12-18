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
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bestteam.myfitroutine.Adapter.MealDialogResultAdapter
import com.bestteam.myfitroutine.Adapter.MealDialogSearchAdapter
import com.bestteam.myfitroutine.Contain
import com.bestteam.myfitroutine.Model.MealData
import com.bestteam.myfitroutine.Model.Meal_Adapter_Data
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.ViewModel.MealPlusViewModel
import com.bestteam.myfitroutine.databinding.MealDialogBinding
import com.bestteam.myfitroutine.retrofit.NetworkClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
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

    private lateinit var binding : MealDialogBinding
    private lateinit var viewModel : MealPlusViewModel
    private lateinit var searchAdapter : MealDialogSearchAdapter
    private lateinit var resultAdapter : MealDialogResultAdapter
    private lateinit var mealContext : Context
    private lateinit var gridLayoutManager: StaggeredGridLayoutManager

    private var searchData : ArrayList<Meal_Adapter_Data> = ArrayList()
    private var resultData : ArrayList<Meal_Adapter_Data> = ArrayList()

    private val db = Firebase.firestore
    private var auth : FirebaseAuth? = null

    private val networkInterface = NetworkClient.api

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mealContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = MealDialogBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MealPlusViewModel::class.java)


        // 검색 기능
        searchSetupView()
        setupListeners()

        // clip 클릭시 firestore 데이터 가져오기
        chipClick()

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

    private fun searchSetupView(){
        gridLayoutManager = StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)
        binding.rvSearch.layoutManager = gridLayoutManager
        searchAdapter = MealDialogSearchAdapter(mealContext,searchData)
        binding.rvSearch.adapter = searchAdapter
        binding.rvSearch.itemAnimator = null

        searchAdapter.itemClick = object : MealDialogSearchAdapter.ItemClick{
            override fun onClick(view: View, position: Int) {

                binding.rvSearch.isGone = true

                auth = Firebase.auth

                val searchData = searchData[position]

                val currentDate = Calendar.getInstance().time
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

                val title = searchData.title
                val calorie = searchData.calorie
                val carbohydrate = searchData.resultCarbohydrate
                val protein = searchData.resultProtein
                val fat = searchData.resultFat
                val uid = auth?.currentUser?.uid
                val resultCountNum = searchData.resultCountNum

                val mealTypeText = if(binding.mealDialogTypeBreakfast.isChecked){
                    "아침"
                } else if (binding.mealDialogTypeLunch.isChecked){
                    "점심"
                } else if (binding.mealDialogTypeDinner.isChecked){
                    "저녁"
                } else "그외"

                val mealResult = hashMapOf(
                    "title" to title,
                    "calorie" to calorie,
                    "resultCarbohydrate" to carbohydrate,
                    "resultProtein" to protein,
                    "resultFat" to fat,
                    "uid" to uid,
                    "resultCountNum" to resultCountNum
                )

                db.collection("$uid").document(dateFormat)
                    .collection("$mealTypeText").document(title)
                    .set(mealResult)
                    .addOnFailureListener {
                        Log.d("mealResult", "DocumentSnapshot successfully written!")
                    }
                    .addOnFailureListener { e ->
                        Log.w("mealResult", "Error writing document", e)
                    }
                searchAdapter.dataSet.clear()
                binding.mealEditText.setText("")
                resultSetupView()
            }
        }
    }

    private fun setupListeners(){
        binding.searchImage.setOnClickListener {
            val breakfast = binding.mealDialogTypeBreakfast
            val lunch = binding.mealDialogTypeLunch
            val dinner = binding.mealDialogTypeDinner
            val etc = binding.mealDialogTypeEtc

            val desc_kor = binding.mealEditText.text.toString()
            if (desc_kor.isNotEmpty()){

                if (!breakfast.isChecked && !lunch.isChecked && !dinner.isChecked && !etc.isChecked){
                    Toast.makeText(mealContext,"분류를 선택해 주세요.",Toast.LENGTH_SHORT).show()
                }
                else {
                    fetchSearchResults(desc_kor)
                    binding.rvSearch.visibility = View.VISIBLE
                }
            }
            else {
                Toast.makeText(mealContext, "검색어를 입력해 주세요.", Toast.LENGTH_SHORT).show()
                binding.rvSearch.visibility = View.GONE
            }
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
    }

    private fun resultSetupView(){
        gridLayoutManager = StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL)
        binding.rvResult.layoutManager = gridLayoutManager
        resultAdapter = MealDialogResultAdapter(mealContext, this)
        binding.rvResult.adapter = resultAdapter
        binding.rvResult.itemAnimator = null

        resultData.clear()

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealTypeText = if(binding.mealDialogTypeBreakfast.isChecked){
            "아침"
        } else if (binding.mealDialogTypeLunch.isChecked){
            "점심"
        } else if (binding.mealDialogTypeDinner.isChecked){
            "저녁"
        } else "그외"

        db.collection("$uid").document("$dateFormat")
            .collection("$mealTypeText")
            .get()
            .addOnSuccessListener { result ->
                val gson = GsonBuilder().create()
                for(document in result){
                    val value = gson.toJson(document.data)
                    val resulFormat = gson.fromJson(value, Meal_Adapter_Data::class.java)
                    resultData.add(resulFormat)
                    Log.d("resultSetup","resultData : $resultData")
                    Log.d("resultSetup","document ID : ${document.id}, Data: $value")
                    Log.d("resultSetup", "Size of resultData: ${resultData.size}")
                }
                resultAdapter.resultDataSet = resultData
                resultAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Log.d("resultSetupView","resultData : $resultData")
            }
    }

    fun chipClick(){

        val breakfast = binding.mealDialogTypeBreakfast
        val lunch = binding.mealDialogTypeLunch
        val dinner = binding.mealDialogTypeDinner
        val etc = binding.mealDialogTypeEtc

        breakfast.setOnClickListener { resultSetupView() }
        lunch.setOnClickListener { resultSetupView() }
        dinner.setOnClickListener { resultSetupView() }
        etc.setOnClickListener { resultSetupView() }
    }

    override fun resultMealDelete(position: Int) {

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealTypeText = if(binding.mealDialogTypeBreakfast.isChecked){
            "아침"
        } else if (binding.mealDialogTypeLunch.isChecked){
            "점심"
        } else if (binding.mealDialogTypeDinner.isChecked){
            "저녁"
        } else "그외"

        val mealResult = resultData[position]
        val mealTitle = mealResult.title

        db.collection("$uid").document(dateFormat)
            .collection("$mealTypeText").document(mealTitle)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(mealContext, "삭제되었습니다.",Toast.LENGTH_SHORT).show()
                Log.d("mealDelete","mealTitle $mealTitle")
            }
            .addOnFailureListener {  e ->
                Log.w(
                    "mealDelete", "Error deleting document", e
                )
            }
        resultData.removeAt(position)
        resultAdapter.notifyDataSetChanged()
    }

    //+버튼 클릭
    override fun resultMealEditPlus(position: Int) {

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealTypeText = if(binding.mealDialogTypeBreakfast.isChecked){
            "아침"
        } else if (binding.mealDialogTypeLunch.isChecked){
            "점심"
        } else if (binding.mealDialogTypeDinner.isChecked){
            "저녁"
        } else "그외"

        val mealResult = resultData[position]
        val mealTitle = mealResult.title

        networkInterface.getMeal(mealTitle,1, 1, "", "", Contain.AUTH).enqueue(object :Callback<MealData>{
            override fun onResponse(call: Call<MealData>, response: Response<MealData>) {
                if (response.isSuccessful){
                    response.body()?.mealBody?.mealItems?.forEach {
                        val calorie = it.NUTR_CONT1
                        val carbohydrate = it.NUTR_CONT2
                        val protein = it.NUTR_CONT3
                        val fat = it.NUTR_CONT4

                        db.collection("$uid").document(dateFormat)
                            .collection("$mealTypeText").document(mealTitle)
                            .update(
                                "calorie", FieldValue.increment(calorie.toLong()),
                                "resultCarbohydrate", FieldValue.increment(carbohydrate.toLong()),
                                "resultProtein", FieldValue.increment(protein.toLong()),
                                "resultFat", FieldValue.increment(fat.toLong()),
                                "resultCountNum",FieldValue.increment(1)
                            )
                    }
                    resultAdapter.resultDataSet = resultData
                    resultAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<MealData>, t: Throwable) {
                Log.e("resultMealEditPlus","resultMealEditPlus error")
            }
        })
    }

    //-버튼 클릭
    override fun resultMealEditMinus(position: Int) {

        auth = Firebase.auth
        val uid = auth?.currentUser?.uid

        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate)

        val mealTypeText = if(binding.mealDialogTypeBreakfast.isChecked){
            "아침"
        } else if (binding.mealDialogTypeLunch.isChecked){
            "점심"
        } else if (binding.mealDialogTypeDinner.isChecked){
            "저녁"
        } else "그외"

        val mealResult = resultData[position]
        val mealTitle = mealResult.title

        networkInterface.getMeal(mealTitle,1, 1, "", "", Contain.AUTH).enqueue(object :Callback<MealData>{
            override fun onResponse(call: Call<MealData>, response: Response<MealData>) {
                if (response.isSuccessful){
                    response.body()?.mealBody?.mealItems?.forEach {
                        val calorie = it.NUTR_CONT1
                        val carbohydrate = it.NUTR_CONT2
                        val protein = it.NUTR_CONT3
                        val fat = it.NUTR_CONT4

                        db.collection("$uid").document(dateFormat)
                            .collection("$mealTypeText").document(mealTitle)
                            .update(
                                "calorie", FieldValue.increment(-calorie.toLong()),
                                "resultCarbohydrate", FieldValue.increment(-carbohydrate.toLong()),
                                "resultProtein", FieldValue.increment(-protein.toLong()),
                                "resultFat", FieldValue.increment(-fat.toLong()),
                                "resultCountNum",FieldValue.increment(-1)
                            )
                    }
                }
            }

            override fun onFailure(call: Call<MealData>, t: Throwable) {
                Log.e("resultMealEditPlus","resultMealEditPlus error")
            }

        })
        resultAdapter.resultDataSet = resultData
        resultAdapter.notifyDataSetChanged()
    }
}