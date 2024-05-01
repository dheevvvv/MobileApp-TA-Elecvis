package com.dheevvvv.taelecvis.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentRecomendationBinding
import com.dheevvvv.taelecvis.viewmodel.HomeViewModel


class RecomendationFragment : Fragment() {
    private lateinit var binding: FragmentRecomendationBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var dataId:Int = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRecomendationBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

    companion object {
        fun newInstance(dataId:Int): RecomendationFragment {
            val fragment = RecomendationFragment()
            val args = Bundle()
            args.putInt("dataChartId", dataId)
            fragment.arguments = args
            return fragment
        }
    }

}