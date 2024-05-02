package com.dheevvvv.taelecvis.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentRecomendationBinding
import com.dheevvvv.taelecvis.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class RecomendationFragment : Fragment() {
    private lateinit var binding: FragmentRecomendationBinding
    private val homeViewModel: HomeViewModel by activityViewModels()
    private var dataId:Int = 0
    private var totalConsumTren:Float = 0f
    private var averageConsumpTren:Float = 0f
    private var maxPuncak:Float = 0f
    private var averagePuncak:Float = 0f
    private var averageIntensitas: Float = 0f
    private var averageVoltage:Float = 0f
    private var totalSubmeter:Float = 0f
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

        dataId = arguments?.getInt("dataChartId")!!
        getData()

        calculateRecommendation()

    }

    @SuppressLint("SetTextI18n")
    private fun calculateRecommendation() {
        when (dataId) {
            1 -> {
                val actualValue = averageConsumpTren
                val threshold = THRESHOLD_VALUE_TREN_KONSUMSI_HARIAN * totalConsumTren
                binding.tv80.setText("> 15% dari Total Konsumsi Listrik")
                binding.tv40.setText("< 15% dari Total Konsumsi Listrik")
                binding.tvNetral.setText("Hemat")
                binding.tvOver.setText("Boros")
                if (actualValue > threshold) {
                    // Jika nilai puncak melebihi ambang batas, berikan rekomendasi sesuai
                    binding.tvRecommendation.text = getString(R.string.rekom_tren_warning)
                    val selisih = actualValue - threshold
                    val persentase = selisih / threshold * 100
                    binding.progressText.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.progressText.setText("$persentase %")
                    binding.tvHematBoros.setText("Boros")
                    binding.circularProgressBar.trackColor = ContextCompat.getColor(requireContext(),R.color.red)
                    binding.tvHematBoros.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.circularProgressBar.progress = persentase.toInt()
                } else {
                    // Jika nilai puncak belum melebihi ambang batas, berikan rekomendasi lain
                    binding.tvRecommendation.text = getString(R.string.rekom_tren_aman)
                    val selisih = actualValue - threshold
                    val persentase = selisih / threshold * 100
                    binding.progressText.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    binding.progressText.setText("$persentase %")
                    binding.tvHematBoros.setText("Hemat")
                    binding.circularProgressBar.trackColor = ContextCompat.getColor(requireContext(),R.color.green)
                    binding.tvHematBoros.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    binding.circularProgressBar.progress = persentase.toInt()
                }
            }
            2 -> {
                binding.tv80.setText("> 90% dari Rata-rata")
                binding.tv40.setText("< 90% dari Rata-rata")
                binding.tvNetral.setText("Aman")
                binding.tvOver.setText("Warning")
                val actualValue = maxPuncak
                val threshold = THRESHOLD_VALUE_PUNCAK_LISTRIK * averagePuncak
                if (actualValue > THRESHOLD_VALUE_PUNCAK_LISTRIK) {
                    // Jika nilai puncak melebihi ambang batas, berikan rekomendasi sesuai
                    binding.tvRecommendation.text = getString(R.string.rekom_puncak_warning)
                    val selisih = actualValue - threshold
                    val persentase = selisih / threshold * 100
                    binding.progressText.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.progressText.setText("$persentase %")
                    binding.tvHematBoros.setText("Warning")
                    binding.circularProgressBar.trackColor = ContextCompat.getColor(requireContext(),R.color.red)
                    binding.tvHematBoros.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.circularProgressBar.progress = persentase.toInt()
                } else {
                    // Jika nilai puncak belum melebihi ambang batas, berikan rekomendasi lain
                    binding.tvRecommendation.text = getString(R.string.rekom_puncak_aman)
                    val selisih = actualValue - threshold
                    val persentase = selisih / threshold * 100
                    binding.progressText.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    binding.progressText.setText("$persentase %")
                    binding.tvHematBoros.setText("Aman")
                    binding.circularProgressBar.trackColor = ContextCompat.getColor(requireContext(),R.color.green)
                    binding.tvHematBoros.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    binding.circularProgressBar.progress = persentase.toInt()
                }
            }
            3 -> {
                binding.tv80.setText("> 220 volt")
                binding.tv40.setText("< 220 volt")
                binding.tvNetral.setText("Stabil")
                binding.tvOver.setText("Warning")
                val actualValue = averageVoltage
                if (actualValue > THRESHOLD_VALUE_VOLTASE_TEGANGAN) {
                    // Jika nilai puncak melebihi ambang batas, berikan rekomendasi sesuai
                    binding.tvRecommendation.text = getString(R.string.rekom_voltase_warning)
                    val selisih = actualValue - THRESHOLD_VALUE_VOLTASE_TEGANGAN
                    val persentase = selisih / THRESHOLD_VALUE_VOLTASE_TEGANGAN * 100
                    binding.progressText.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.progressText.setText("$persentase %")
                    binding.tvHematBoros.setText("Warning")
                    binding.circularProgressBar.trackColor = ContextCompat.getColor(requireContext(),R.color.red)
                    binding.tvHematBoros.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.circularProgressBar.progress = persentase.toInt()
                } else {
                    // Jika nilai puncak belum melebihi ambang batas, berikan rekomendasi lain
                    binding.tvRecommendation.text = getString(R.string.rekom_voltase_aman)
                    val selisih = actualValue - THRESHOLD_VALUE_VOLTASE_TEGANGAN
                    val persentase = selisih / THRESHOLD_VALUE_VOLTASE_TEGANGAN * 100
                    binding.progressText.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    binding.progressText.setText("$persentase %")
                    binding.tvHematBoros.setText("Stabil")
                    binding.circularProgressBar.trackColor = ContextCompat.getColor(requireContext(),R.color.green)
                    binding.tvHematBoros.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    binding.circularProgressBar.progress = persentase.toInt()
                }
            }
            4 -> {
                binding.tv80.setText("> 15 ampere")
                binding.tv40.setText("< 15 ampere")
                binding.tvNetral.setText("Aman")
                binding.tvOver.setText("Warning")
                val actualValue = averageIntensitas
                if (actualValue > THRESHOLD_VALUE_INTENSITAS_LISTRIK) {
                    // Jika nilai puncak melebihi ambang batas, berikan rekomendasi sesuai
                    binding.tvRecommendation.text = getString(R.string.rekom_intensitas_warning)
                    val selisih = actualValue - THRESHOLD_VALUE_INTENSITAS_LISTRIK
                    val persentase = selisih / THRESHOLD_VALUE_INTENSITAS_LISTRIK * 100
                    binding.progressText.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.progressText.setText("$persentase %")
                    binding.tvHematBoros.setText("Warning")
                    binding.circularProgressBar.trackColor = ContextCompat.getColor(requireContext(),R.color.red)
                    binding.tvHematBoros.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.circularProgressBar.progress = persentase.toInt()
                } else {
                    // Jika nilai puncak belum melebihi ambang batas, berikan rekomendasi lain
                    binding.tvRecommendation.text = getString(R.string.rekom_intensitas_aman)
                    val selisih = actualValue - THRESHOLD_VALUE_INTENSITAS_LISTRIK
                    val persentase = selisih / THRESHOLD_VALUE_INTENSITAS_LISTRIK * 100
                    binding.progressText.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    binding.progressText.setText("$persentase %")
                    binding.tvHematBoros.setText("Aman")
                    binding.circularProgressBar.trackColor = ContextCompat.getColor(requireContext(),R.color.green)
                    binding.tvHematBoros.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    binding.circularProgressBar.progress = persentase.toInt()
                }
            }
            5 -> {
                binding.tv80.setText("> 400kWh perBulan atau 13.33kWh perHari")
                binding.tv40.setText("< 400kWh perBulan atau 13.33kWh perHari")
                binding.tvNetral.setText("Normal")
                binding.tvOver.setText("Boros")
                val actualValue = totalSubmeter
                if (actualValue > THRESHOLD_VALUE_SUBMETER) {
                    // Jika nilai puncak melebihi ambang batas, berikan rekomendasi sesuai
                    binding.tvRecommendation.text = getString(R.string.rekom_submeter_warning)
                    val selisih = actualValue - THRESHOLD_VALUE_SUBMETER
                    val persentase = selisih / THRESHOLD_VALUE_SUBMETER * 100
                    binding.progressText.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.progressText.setText("$persentase %")
                    binding.tvHematBoros.setText("Boros")
                    binding.circularProgressBar.trackColor = ContextCompat.getColor(requireContext(),R.color.red)
                    binding.tvHematBoros.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                    binding.circularProgressBar.progress = persentase.toInt()
                } else {
                    // Jika nilai puncak belum melebihi ambang batas, berikan rekomendasi lain
                    binding.tvRecommendation.text = getString(R.string.rekom_submeter_aman)
                    val selisih = actualValue - THRESHOLD_VALUE_SUBMETER
                    val persentase = selisih / THRESHOLD_VALUE_SUBMETER * 100
                    binding.progressText.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    binding.progressText.setText("$persentase %")
                    binding.tvHematBoros.setText("Normal")
                    binding.circularProgressBar.trackColor = ContextCompat.getColor(requireContext(),R.color.green)
                    binding.tvHematBoros.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    binding.circularProgressBar.progress = persentase.toInt()
                }
            } else -> binding.tvRecommendation.text = getString(R.string.desc_null)
        }
    }

    private fun getData(){
        homeViewModel.totalConsumptionEnergyTren.observe(viewLifecycleOwner, Observer {
            totalConsumTren = it
        })
        homeViewModel.averageValueTren.observe(viewLifecycleOwner, Observer {
            averageConsumpTren = it
        })
        homeViewModel.averageValuePuncak.observe(viewLifecycleOwner, Observer {
            averagePuncak = it
        })
        homeViewModel.averageValueIntensitas.observe(viewLifecycleOwner, Observer {
            averageIntensitas = it
        })
        homeViewModel.averageValueVoltage.observe(viewLifecycleOwner, Observer {
            averageVoltage = it
        })
        homeViewModel.maxValuePuncak.observe(viewLifecycleOwner, Observer {
            maxPuncak = it
        })
        homeViewModel.totalValueSubmeter.observe(viewLifecycleOwner, Observer {
            totalSubmeter = it
        })
    }

    companion object {
        private const val THRESHOLD_VALUE_TREN_KONSUMSI_HARIAN = 0.15
        private const val THRESHOLD_VALUE_PUNCAK_LISTRIK = 0.9
        private const val THRESHOLD_VALUE_INTENSITAS_LISTRIK = 15
        private const val THRESHOLD_VALUE_VOLTASE_TEGANGAN = 220
        private const val THRESHOLD_VALUE_SUBMETER = 400
        fun newInstance(dataId:Int): RecomendationFragment {
            val fragment = RecomendationFragment()
            val args = Bundle()
            args.putInt("dataChartId", dataId)
            fragment.arguments = args
            return fragment
        }
    }

}