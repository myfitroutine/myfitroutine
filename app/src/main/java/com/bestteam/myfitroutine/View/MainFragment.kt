package com.bestteam.myfitroutine.View

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bestteam.myfitroutine.Adapter.YoutubeAdapter
import com.bestteam.myfitroutine.Dialog.AvataDialog
import com.bestteam.myfitroutine.Dialog.TodayWeightDialog
import com.bestteam.myfitroutine.R
import com.bestteam.myfitroutine.Util.CustomToast
import com.bestteam.myfitroutine.ViewModel.MainViewModel
import com.bestteam.myfitroutine.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@Suppress("UNREACHABLE_CODE", "DEPRECATION")

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var weightViewModel: MainViewModel
    private lateinit var avataImageView: ImageView
    private lateinit var mContext: Context
    private lateinit var adapter: YoutubeAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = YoutubeAdapter(mContext)
        mContext = requireContext()

        weightViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        weightViewModel.setGoalWeight(requireActivity())

        avataImageView = view.findViewById(R.id.avata)

        val todayWeight = binding.txtTodayWeight
        val yesterdayWeight = binding.txtYesterWeight
        val todayDate = binding.txtToday
        val userName = binding.txtNickname

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.getUserName()
            weightViewModel.currentUserName.collect { name ->
                if (name != null) {
                    userName.text = name
                    CustomToast.createToast(requireContext(), "${name} 님의 운동을 응원합니다!")?.show()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.getCurrentDate()
            weightViewModel.currentDate.collect { currentDate ->
                todayDate.text = currentDate
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.getYesterdayWeight()
            weightViewModel.yesterdayWeight.collect { yesterday ->
                if (yesterday != null) {
                    yesterdayWeight.text = yesterday.toString() + "KG"
                    Log.d("nyh main", "onViewCreated:yesterday $yesterday")
                } else {
                    yesterdayWeight.text = "0"
                }
//                yesterdayWeight.text = (yesterday ?: "").toString()+"KG"
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.getTodayWeight()
            weightViewModel.todayWeight.collect { newWeight ->

                if (newWeight != null) {
                    todayWeight.text = newWeight.toString()
                } else {
                    todayWeight.text = "0"
                }
                Log.d("nyh main", "onViewCreated: $newWeight")
//                todayWeight.text = (newWeight ?: "").toString()+"KG"
            }
        }

//        val editTextToday = binding.btnTodayWeight
//        val editToday: String? = todayWeight.text.toString()
//
//        if (editToday.isNullOrEmpty() || editToday == "0" ) {
//            editTextToday.text = "입력하기"
//        } else {
//            editTextToday.text = "수정하기"
//        }

        todayWeight.setOnClickListener {
            val todayWeightDialog = TodayWeightDialog()
            todayWeightDialog.show(childFragmentManager, "TodayWeight")
        }
        binding.btnTodayWeight.setOnClickListener {
            val todayWeightDialog = TodayWeightDialog()
            todayWeightDialog.show(childFragmentManager, "TodayWeight")
        }

        binding.btnGraph.setOnClickListener {
            val graphFragment = GraphFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentFrame, graphFragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        binding.btnDiary.setOnClickListener {
            val diaryFragment = DiaryFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentFrame, diaryFragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        binding.btnMeal.setOnClickListener {
            val mealFragment = MealFragment()
            val transaction = fragmentManager?.beginTransaction()
            transaction?.replace(R.id.fragmentFrame, mealFragment)
            transaction?.addToBackStack(null)
            transaction?.commit()
        }
        binding.avata.setOnClickListener {
            val seletAvataDialog = AvataDialog()
            seletAvataDialog.show(childFragmentManager, "avata")
        }
        binding.btnEdit.setOnClickListener {
            val seletAvataDialog = AvataDialog()
            seletAvataDialog.show(childFragmentManager, "avata")
        }

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.selectedAvata.collect { avataName ->
                Log.d("nyh", "Collected Avata: $avataName")
                loadAndSetAvataImage()
            }
        }
        binding.rcvYoutube.layoutManager =
            LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false)
        binding.rcvYoutube.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.getVideo()
            weightViewModel.videoList.collect { videoList ->
                adapter.videoItems = videoList.toMutableList()
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onResume() {
        super.onResume()

//        weightViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        val todayWeight = binding.txtTodayWeight
        var weightGap = binding.txtChangeWeight
        var weightGapTxt = binding.txtChagneTxt
        val todayDate = binding.txtToday

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.getCurrentDate()
            weightViewModel.currentDate.collect { currentDate ->
                todayDate.text = currentDate
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.getTodayWeight()
            weightViewModel.todayWeight.collect { newWeight ->
                Log.d("nyh main", "onViewCreated: $newWeight")
                todayWeight.text = (newWeight ?: "0").toString()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.getWeightGap()
            weightViewModel.weightGap.collect { getWeightGap ->

                when {
                    getWeightGap != null && getWeightGap < 0 -> {
                        weightGapTxt.text = "감량"
                        weightGap.text = Math.abs(getWeightGap).toString()
                        weightGapTxt.setTextColor(Color.BLUE)
                        binding.linearLayout4.visibility = View.VISIBLE
                    }

                    getWeightGap != null && getWeightGap > 0 -> {
                        weightGapTxt.text = "증량"
                        weightGap.text = getWeightGap.toString()
                        weightGapTxt.setTextColor(Color.RED)
                        binding.linearLayout4.visibility = View.VISIBLE
                    }

                    else -> {
                        Log.d("nyh main", "Setting LinearLayout to GONE")
                        binding.linearLayout4.visibility = View.GONE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            weightViewModel.selectedAvata.collect { avataName ->
                Log.d("nyh", "Collected Avata: $avataName")
                loadAndSetAvataImage()
            }
        }
    }

    fun loadAndSetAvataImage() {
        val preferences = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val selectedAvata = preferences.getString("selectedAvata", "avata_1")

        val avataResourceId =
            resources.getIdentifier(selectedAvata, "drawable", requireActivity().packageName)
        avataImageView.setImageResource(avataResourceId)
    }
}