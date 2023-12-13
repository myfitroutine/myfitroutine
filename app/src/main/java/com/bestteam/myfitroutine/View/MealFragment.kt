package com.bestteam.myfitroutine.View

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bestteam.myfitroutine.Dialog.MealPlusDialog
import com.bestteam.myfitroutine.databinding.FragmentMealBinding

class MealFragment : Fragment() {

    private lateinit var binding : FragmentMealBinding
    private lateinit var mealContext : Context


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mealContext = context
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = FragmentMealBinding.inflate(inflater, container, false)

        binding.plusMeal.setOnClickListener {
            val dialog = MealPlusDialog()
            dialog.show(childFragmentManager,"MealPlusDialog")
        }

        return binding.root
    }


}