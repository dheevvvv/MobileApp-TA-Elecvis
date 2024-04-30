package com.dheevvvv.taelecvis.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DownloadManager
import android.content.*
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.red
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.dheevvvv.taelecvis.R
import com.dheevvvv.taelecvis.databinding.FragmentReportBinding
import com.dheevvvv.taelecvis.model.power_usage.Data
import com.dheevvvv.taelecvis.model.power_usage.ReportData
import com.dheevvvv.taelecvis.viewmodel.HomeViewModel
import com.dheevvvv.taelecvis.viewmodel.ReportViewModel
import com.dheevvvv.taelecvis.viewmodel.UserViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import com.itextpdf.text.pdf.draw.LineSeparator
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@AndroidEntryPoint
class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding
    private var selectedStartDate:String = ""
    private var selectedEndDate:String = ""

    private val userViewModel: UserViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val reportViewModel: ReportViewModel by activityViewModels()
    private val REQUEST_CODE_PERMISSION = 101
    private var email:String = ""
    private var username:String = ""
    private var userId:Int = 0

    private var totalAllKwh:Float = 0f
    private var totalAllPeakConsump:Float = 0f
    private var totalAllAverageIntensity:Float = 0f
    private var totalAllAverageVoltage:Float = 0f
    private var totalaLLRupiah:Float = 0f

    private var averageAllTotalKwh:Float = 0f
    private var averageAllTotalPeakEnergy:Float = 0f
    private var averageAllTotalAverageIntensity:Float = 0f
    private var averageAllTotalAverageVoltage:Float = 0f
    private var averageAllTotalRupiah:Float = 0f

    private var totalPengeluaranKwhLastWeek:Float = 0f
    private var totalPengeluaranRupiahLastWeek:Float = 0f
    private var selectedStartDateLastWeek:String = ""
    private var dataLastWeek:List<Data> = emptyList()

    private var datas:List<Data> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomNavigation.setOnNavigationItemSelectedListener { item->
            when(item.itemId) {
                R.id.home -> {
                    findNavController().navigate(R.id.action_reportFragment_to_homeFragment)
                    true
                }

                R.id.energy -> {
                    findNavController().navigate(R.id.action_reportFragment_to_consumptionTransactionFragment)
                    true
                }

                R.id.notification -> {
                    findNavController().navigate(R.id.action_reportFragment_to_notificationFragment)
                    true
                }

                R.id.account -> {
                    findNavController().navigate(R.id.action_reportFragment_to_profileFragment)
                    true
                }

                else -> {
                    false
                }
            }
        }

        binding.btnPilihTanggal.setOnClickListener {
            showDateRangePicker()
        }

        reportViewModel.selectedStartDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer { startDate->
            if (startDate!=null){
                selectedStartDate  = startDate
                reportViewModel.selectedEndDate.observe(viewLifecycleOwner, androidx.lifecycle.Observer { endDate->
                    selectedEndDate = endDate
                    userViewModel.getUserId()
                    userViewModel.userId.observe(viewLifecycleOwner, androidx.lifecycle.Observer {id->
                        val user = id
                        userId = id
                        homeViewModel.callApiGetPowerUsage(user, selectedStartDate, selectedEndDate)
                        homeViewModel.powerUsageData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { data->
                            datas = data
                            bulanIni()
                        })
                    })
//                    selisih(pengeluaranKwhBulanIni, pengeluaranKwhBulanLalu, pengeluaranRpBulanIni, pengeluaranRpBulanLalu)
                })
            } else{
                Toast.makeText(context, "Pilih Tanggal terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        })


        binding.cvBulanIni.setOnClickListener {
            bulanIni()
            binding.cvBulanIni.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_green))
            binding.cvBulanLalu.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white2))
        }

        binding.cvBulanLalu.setOnClickListener {
            callDataLastWeek()
            selisih(totalAllKwh, totalPengeluaranKwhLastWeek, totalaLLRupiah, totalPengeluaranRupiahLastWeek)
            binding.cvBulanIni.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white2))
            binding.cvBulanLalu.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blue_green))
        }

        checkStoragePermissions()


        binding.btnUnduhPdf.setOnClickListener {
            generatePdf()
        }

    }

    private fun checkStoragePermissions() {
        val writePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            // Izin belum diberikan, minta izin kepada pengguna
            requestStoragePermission()
        } else {
            // Izin diberikan, lanjutkan dengan menampilkan notifikasi
            Toast.makeText(requireContext(), "Izin storage telah diberikan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_CODE_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Izin telah diberikan untuk menyimpan file di penyimpanan eksternal.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Izin diperlukan untuk menyimpan file di penyimpanan eksternal.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("SimpleDateFormat", "DiscouragedApi")
    private fun generatePdf() {

        val fileName = "ElectricityUsageReport.pdf"
        val filePath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
        val document = Document(PageSize.A4)
        val outputStream = FileOutputStream(filePath)
        val writer = PdfWriter.getInstance(document, outputStream)
        // Set document margins
        document.setMargins(20f, 20f, 50f, 50f)
        document.open()

        //Logo
        val resourceId = resources.getIdentifier("logo", "drawable", requireContext().packageName)
        val logoBitmap = BitmapFactory.decodeResource(resources, resourceId)
        val byteArrayOutputStream = ByteArrayOutputStream()
        logoBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val logoImage = Image.getInstance(byteArray)

//        val logoImage = Image.getInstance(logoPath)
        logoImage.alignment = Image.ALIGN_LEFT
        val scaledWidth = 100f
        val scaledHeight = 100f
        logoImage.scaleAbsolute(scaledWidth, scaledHeight)
        document.add(logoImage)


        val title = Paragraph("Laporan Penggunaan Konsumsi Listrik")
        title.alignment = Paragraph.ALIGN_CENTER
        title.spacingAfter = 20f
        document.add(title)

        // Add horizontal line
        val line = Paragraph(Chunk("------------------------------------------------------------------------------------------------------------------------"))
        line.alignment = Element.ALIGN_CENTER
        line.spacingAfter = 20f
        document.add(line)

        // Informasi penerima dan tanggal laporan

        userViewModel.getEmail()
        userViewModel.getUsername()
        userViewModel.email.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            email = it
        })
        userViewModel.username.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            username = it
        })

        // Add recipient information
        val recipientInfo = Paragraph("To: $username\nDate: ${Date().toString()}")
        recipientInfo.alignment = Paragraph.ALIGN_RIGHT
        recipientInfo.spacingAfter = 20f
        document.add(recipientInfo)

        //periode
        val periodeDate = Paragraph("Periode Konsumsi: $selectedStartDate - $selectedEndDate")
        periodeDate.alignment = Paragraph.ALIGN_LEFT
        periodeDate.spacingAfter = 20f
        document.add(periodeDate)

        //Table data transaksi

        // Add transaction details table
        val table = PdfPTable(6)
        table.totalWidth = 400f
        table.setWidths(floatArrayOf(3f, 3f, 3f, 3f, 3f, 4f))

        // Table headers
        table.addCell(PdfPCell(Paragraph("Tanggal Transaksi")))
        table.addCell(PdfPCell(Paragraph("Total Pengeluaran kWh")))
        table.addCell(PdfPCell(Paragraph("Puncak Konsumsi Tertinggi")))
        table.addCell(PdfPCell(Paragraph("Rata-Rata Intensitas")))
        table.addCell(PdfPCell(Paragraph("Rata-rata Voltase")))
        table.addCell(PdfPCell(Paragraph("Total Pengeluaran Rupiah")))

        val groupedByDate = datas.groupBy { it.date }
        val jumlahHari = groupedByDate.keys.size
        for ((date, dataList) in groupedByDate) {
            val tanggal = date
            val totalKwh = dataList.sumByDouble { it.globalActivePower }
            val totalIntensity = dataList.sumByDouble { it.globalIntensity }
            val totalAverageVoltage = dataList.sumByDouble { it.voltage }
            val totalAmount = dataList.sumByDouble { it.globalActivePower * 1500 }

            val peakEnergy = dataList.maxByOrNull { it.globalActivePower }?.globalActivePower ?: 0.0

            val averageKwh = totalKwh / dataList.size
            val averageIntensity = totalIntensity / dataList.size
            val averageAverageVoltage = totalAverageVoltage / dataList.size
            val averageAmount = totalAmount / dataList.size

            totalAllKwh += totalKwh.toFloat()
            totalAllPeakConsump += peakEnergy.toFloat()
            totalAllAverageIntensity += averageIntensity.toFloat()
            totalAllAverageVoltage += averageAverageVoltage.toFloat()
            totalaLLRupiah += totalAmount.toFloat()

            averageAllTotalKwh = totalAllKwh / jumlahHari
            averageAllTotalPeakEnergy = totalAllPeakConsump / jumlahHari
            averageAllTotalAverageIntensity = totalAllAverageIntensity / jumlahHari
            averageAllTotalAverageVoltage = totalAllAverageVoltage / jumlahHari
            averageAllTotalRupiah = totalaLLRupiah / jumlahHari


            val totalKwhFormatted = formatDecimal(totalKwh.toFloat())
            val peakEnergyFormatted = formatDecimal(peakEnergy.toFloat())
            val averageIntensityFormatted = formatDecimal(averageIntensity.toFloat())
            val averageAverageVoltageFormatted = formatDecimal(averageAverageVoltage.toFloat())
            val totalAmountFormatted = formatToIDR(totalAmount.toFloat())

            table.addCell(tanggal)
            table.addCell(totalKwhFormatted)
            table.addCell(peakEnergyFormatted)
            table.addCell(averageIntensityFormatted)
            table.addCell(averageAverageVoltageFormatted)
            table.addCell(totalAmountFormatted)

        }

        document.add(table)

        document.add(line)

        val tableTotal = PdfPTable(6)
        tableTotal.totalWidth = 400f
        tableTotal.setWidths(floatArrayOf(3f, 3f, 3f, 3f, 3f, 4f))

        // Table headers
        tableTotal.addCell(PdfPCell(Paragraph("Total")))
        tableTotal.addCell(PdfPCell(Paragraph("${formatDecimal(totalAllKwh)} kWh")))
        tableTotal.addCell(PdfPCell(Paragraph("${formatDecimal(totalAllPeakConsump)} kWh")))
        tableTotal.addCell(PdfPCell(Paragraph("${formatDecimal(totalAllAverageIntensity)} A")))
        tableTotal.addCell(PdfPCell(Paragraph("${formatDecimal(totalAllAverageVoltage)} V")))
        tableTotal.addCell(PdfPCell(Paragraph(formatToIDR(totalaLLRupiah))))
        document.add(tableTotal)

        val tableAverage = PdfPTable(6)
        tableAverage.totalWidth = 400f
        tableAverage.setWidths(floatArrayOf(3f, 3f, 3f, 3f, 3f, 4f))

        // Table headers
        tableAverage.addCell(PdfPCell(Paragraph("Rata-rata")))
        tableAverage.addCell(PdfPCell(Paragraph("${formatDecimal(averageAllTotalKwh)} kWh")))
        tableAverage.addCell(PdfPCell(Paragraph("${formatDecimal(averageAllTotalPeakEnergy)} kWh")))
        tableAverage.addCell(PdfPCell(Paragraph("${formatDecimal(averageAllTotalAverageIntensity)} A")))
        tableAverage.addCell(PdfPCell(Paragraph("${formatDecimal(averageAllTotalAverageVoltage)} V")))
        tableAverage.addCell(PdfPCell(Paragraph(formatToIDR(averageAllTotalRupiah))))
        document.add(tableAverage)


        // Footer: Halaman dan timestamp
        val pageAndTimestamp = "Halaman ${writer.pageNumber} | Created at ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date())}"
        val pageAndTimestampPhrase = Phrase(pageAndTimestamp)
        ColumnText.showTextAligned(writer.directContent, Element.ALIGN_CENTER, pageAndTimestampPhrase, PageSize.A4.width - 72,
            36F, 0f)

        document.close()
        writer.close()
        outputStream.close()

        // Setelah menutup dokumen PDF, buat URI untuk file PDF
        val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.provider", filePath)

        // Buat Intent untuk membuka PDF
        val openFileIntent = Intent(Intent.ACTION_VIEW)
        openFileIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        openFileIntent.setDataAndType(uri, "application/pdf")

        // Coba membuka PDF
        try {
            startActivity(openFileIntent)
        } catch (e: ActivityNotFoundException) {
            // Handle jika tidak ada aplikasi PDF viewer yang tersedia
            Toast.makeText(requireContext(), "Tidak ada aplikasi PDF viewer yang tersedia", Toast.LENGTH_SHORT).show()
        }

    }

    fun formatDecimal(value: Float): String {
        val df = DecimalFormat("#.##")
        df.roundingMode = java.math.RoundingMode.DOWN
        return df.format(value)
    }

    // Fungsi untuk mengubah nilai ke format mata uang Rupiah (IDR)
    fun formatToIDR(value: Float): String {
        val currencyFormat = DecimalFormat.getCurrencyInstance() as DecimalFormat
        val formatSymbols = DecimalFormatSymbols()
        formatSymbols.currencySymbol = "Rp "
        currencyFormat.decimalFormatSymbols = formatSymbols
        return currencyFormat.format(value)
    }

    @SuppressLint("SetTextI18n")
    private fun showDateRangePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT,
            DatePickerDialog.OnDateSetListener { view: DatePicker, selectedYear: Int, monthOfYear: Int, dayOfMonth: Int ->
                val startDateCalendar = Calendar.getInstance()
                startDateCalendar.set(selectedYear, monthOfYear, dayOfMonth)

                val endDateCalendar = startDateCalendar.clone() as Calendar
                endDateCalendar.add(Calendar.DAY_OF_MONTH, 7)

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val startDate = dateFormat.format(startDateCalendar.time)
                val endDate = dateFormat.format(endDateCalendar.time)

                val startDateLastWeekCalendar = startDateCalendar.clone() as Calendar
                startDateLastWeekCalendar.add(Calendar.DAY_OF_MONTH, -7)
                selectedStartDateLastWeek = dateFormat.format(startDateLastWeekCalendar.time)

                binding.tvSelectedDate.text = "$startDate - $endDate"
                // Simpan tanggal terpilih ke ViewModel
                reportViewModel.saveSelectedStartDate(startDate)
                reportViewModel.saveSelectedEndDate(endDate)
            },
            year,
            month,
            day
        )

        // Set mode ke tahun dan bulan
        datePickerDialog.datePicker.init(year, month, 1, null)

        datePickerDialog.show()
    }


//    @SuppressLint("SetTextI18n")
//    private fun showDateRangePicker() {
//        val builder = MaterialDatePicker.Builder.dateRangePicker()
//
//        val constraintsBuilder = CalendarConstraints.Builder()
//        val calendar = Calendar.getInstance()
//        val minDate = calendar.timeInMillis
//        calendar.add(Calendar.YEAR, -21)
//        val maxDate = calendar.timeInMillis
//        constraintsBuilder.setStart(minDate)
//        constraintsBuilder.setEnd(maxDate)
//        builder.setCalendarConstraints(constraintsBuilder.build())
//
//        val picker = builder.build()
//
//        picker.addOnPositiveButtonClickListener { selection ->
//            val startDate = Date(selection.first ?: 0)
//            val endDate = Date(selection.second ?: 0)
//
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//            val startDateString = dateFormat.format(startDate)
//            val endDateString = dateFormat.format(endDate)
//
//
//            // Simpan startDate dan endDate terpilih
//            reportViewModel.saveSelectedStartDate(startDateString)
//            reportViewModel.saveSelectedEndDate(endDateString)
//
//            binding.tvSelectedDate.setText("$startDateString - $endDateString")
//        }
//
//        picker.show(childFragmentManager, picker.toString())
//    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun bulanIni(){
        binding.tvPengeluaranKwh.setText("$totalAllKwh kWh")
        binding.tvPengeluaranRupiah.setText(formatToIDR(totalaLLRupiah))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun callDataLastWeek(){
        homeViewModel.callApiGetPowerUsage(userId, selectedStartDateLastWeek, selectedStartDate)
        homeViewModel.powerUsageData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            dataLastWeek = it
            bulanlalu()
        })
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun bulanlalu(){
        val groupedByDate = dataLastWeek.groupBy { it.date }
        for ((_, dataList) in groupedByDate) {
            val totalKwh = dataList.sumByDouble { it.globalActivePower }
            val totalAmount = dataList.sumByDouble { it.globalActivePower * 1500 }

            totalPengeluaranKwhLastWeek += totalKwh.toFloat()
            totalPengeluaranRupiahLastWeek += totalAmount.toFloat()

            binding.tvPengeluaranKwh.setText("$totalPengeluaranKwhLastWeek kWh")
            binding.tvPengeluaranRupiah.setText(formatToIDR(totalPengeluaranRupiahLastWeek))

        }
    }

    private fun selisih(pengeluaranKwhBulanIni: Float, pengeluaranKwhBulanLalu: Float, rpBulanIni: Float, rpBulanLalu: Float) {
        // Menghitung selisih KWh antara bulan ini dan bulan lalu
        val selisihKwh = pengeluaranKwhBulanIni - pengeluaranKwhBulanLalu

        // Menentukan status (boros / hemat) berdasarkan selisih KWh
        val status = when {
            selisihKwh > 0 -> "hemat"
            selisihKwh < 0 -> "boros"
            else -> "tidak berubah"
        }

        // Menghitung selisih Rp antara bulan ini dan bulan lalu
        val selisihRp = rpBulanIni - rpBulanLalu

        // Menghitung persentase selisih KWh
        val percentageSelisihKwh = if (pengeluaranKwhBulanLalu != 0f) {
            (selisihKwh / pengeluaranKwhBulanLalu) * 100
        } else {
            0f
        }

        // Menetapkan nilai pada elemen UI dengan menggunakan binding, pastikan binding telah diinisialisasi sebelumnya
        binding.tvSelisihKwh.text = formatDecimal(selisihKwh)
        binding.tvSelisihRp.text = formatToIDR(selisihRp)

        // Menetapkan teks dan warna track ProgressBar berdasarkan status
        binding.tvHematBoros.text = status.capitalize() // Mengubah status menjadi huruf kapital
        binding.circularProgressBar.trackColor = ContextCompat.getColor(requireContext(), if (status == "boros") R.color.red else R.color.green)

        // Menetapkan teks persentase selisih KWh
        binding.progressText.text = String.format("%.2f %%", percentageSelisihKwh)
        binding.progressText.setTextColor(ContextCompat.getColor(requireContext(), if (status == "boros") R.color.red else R.color.green))
    }


}