package com.dheevvvv.taelecvis.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentElectricInfoBinding
import com.dheevvvv.taelecvis.viewmodel.HomeViewModel


class ElectricInfoFragment : Fragment() {
    private lateinit var binding : FragmentElectricInfoBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var dataId:Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentElectricInfoBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataId = arguments?.getInt("idChartData")!!

    }

    companion object {
        fun newInstance(dataId:Int): ElectricInfoFragment {
            val fragment = ElectricInfoFragment()
            val args = Bundle()
            args.putInt("idChartData", dataId)
            fragment.arguments = args
            return fragment
        }
    }


}