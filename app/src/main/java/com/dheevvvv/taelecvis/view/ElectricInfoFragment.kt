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
import dagger.hilt.android.AndroidEntryPoint


class ElectricInfoFragment : Fragment() {
    private lateinit var binding : FragmentElectricInfoBinding
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

        dataId = arguments?.getInt("dataChartId")!!

        val description = when (dataId) {
            1 -> R.string.desc_trenkosnsumsi
            2 -> R.string.desc_puncaklistrik
            3 -> R.string.desc_voltase
            4 -> R.string.desc_Intensitas
            5 -> R.string.desc_submeter
            else -> R.string.desc_null
        }
        binding.tvDesc.text = getString(description)

    }

    companion object {
        fun newInstance(dataId:Int): ElectricInfoFragment {
            val fragment = ElectricInfoFragment()
            val args = Bundle()
            args.putInt("dataChartId", dataId)
            fragment.arguments = args
            return fragment
        }
    }


}