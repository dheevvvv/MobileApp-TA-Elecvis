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
import com.itextpdf.text.pdf.ColumnText
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

@AndroidEntryPoint
class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding
    private var selectedStartDate:String = ""
    private var selectedEndDate:String = ""
    private var pengeluaranKwhBulanIni: Float = 0F
    private var pengeluaranRpBulanIni: Float = 0F
    private var pengeluaranKwhBulanLalu: Float = 0F
    private var pengeluaranRpBulanLalu: Float = 0F
    private val userViewModel: UserViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val reportViewModel: ReportViewModel by activityViewModels()
    private val REQUEST_CODE_PERMISSION = 101
    private var email:String = ""
    private var username:String = ""
    private var totalGlobalActivePowerPerDay:kotlin.collections.Map<String, Double> = emptyMap()
    private var averageGlobalIntensityPerDay:kotlin.collections.Map<String, Double> = emptyMap()
    private var averageVoltagePerDay:kotlin.collections.Map<String, Double> = emptyMap()
    private var highestEnergyConsumptionPerDay:kotlin.collections.Map<String, Data?> = emptyMap()
    private var totalRp:kotlin.collections.Map<String, Double> = emptyMap()
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
//                    bulanIni()
//                    bulanlalu()
//                    selisih(pengeluaranKwhBulanIni, pengeluaranKwhBulanLalu, pengeluaranRpBulanIni, pengeluaranRpBulanLalu)
                })
            } else{
                Toast.makeText(context, "Pilih Tanggal terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        })



        binding.cvBulanIni.setOnClickListener {
            binding.tvPengeluaranKwh.setText("$pengeluaranKwhBulanIni kWh")
            binding.tvPengeluaranRupiah.setText("Rp. $pengeluaranRpBulanIni")
        }

        binding.cvBulanLalu.setOnClickListener {
            binding.tvPengeluaranKwh.setText("$pengeluaranKwhBulanLalu kWh")
            binding.tvPengeluaranRupiah.setText("Rp. $pengeluaranRpBulanLalu")
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
        val document = Document()
        val outputStream = FileOutputStream(filePath)
        val writer = PdfWriter.getInstance(document, outputStream)
        document.open()

        //Logo
        val logoPath = "logo.png"
        val resourceId = resources.getIdentifier("logo", "drawable", requireContext().packageName)
        val logoBitmap = BitmapFactory.decodeResource(resources, resourceId)
        val byteArrayOutputStream = ByteArrayOutputStream()
        logoBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val logoImage = Image.getInstance(byteArray)

//        val logoImage = Image.getInstance(logoPath)
        logoImage.alignment = Image.ALIGN_LEFT
        document.add(logoImage)

        // Judul
        val title = "Laporan Penggunaan Konsumsi Listrik"
        val titleEnglish = "Electricity Usage Report"
        val titlePhrase = Phrase(title)
        val titleEnglishPhrase = Phrase(titleEnglish)
        ColumnText.showTextAligned(writer.directContent, Element.ALIGN_CENTER, titlePhrase, PageSize.A4.width / 2, PageSize.A4.height - 36, 0f)
        ColumnText.showTextAligned(writer.directContent, Element.ALIGN_CENTER, titleEnglishPhrase, PageSize.A4.width / 2, PageSize.A4.height - 56, 0f)

        // Garis horizontal
        val horizontalLine = PdfPTable(1)
        horizontalLine.totalWidth = PageSize.A4.width - 72
        horizontalLine.spacingBefore = 20f
        horizontalLine.addCell(PdfPCell().apply { border = 0 })
        document.add(horizontalLine)


        // Informasi penerima dan tanggal laporan

        userViewModel.getEmail()
        userViewModel.getUsername()
        userViewModel.email.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            email = it
        })
        userViewModel.username.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            username = it
        })

        val receiverInfoTable = PdfPTable(2)
        receiverInfoTable.totalWidth = PageSize.A4.width - 72
        receiverInfoTable.addCell(PdfPCell(Phrase("To")).apply { border = 0 })
        receiverInfoTable.addCell(PdfPCell(Phrase("Period Consumption Date")).apply { border = 0 })
        receiverInfoTable.addCell(PdfPCell(Phrase("Username :\n $username")).apply { border = 0 })
        receiverInfoTable.addCell(PdfPCell(Phrase("$selectedStartDate - $selectedEndDate")).apply { border = 0 })
        receiverInfoTable.addCell(PdfPCell(Phrase(email)).apply { border = 0 })
//        receiverInfoTable.addCell(PdfPCell(Phrase(SimpleDateFormat("dd/MM/yyyy").format(Date()))).apply { border = 0 })
        document.add(receiverInfoTable)

        //Table data transaksi
        val transactionDataTable = PdfPTable(6)
        transactionDataTable.totalWidth = PageSize.A4.width - 72

        val columnHeaders = listOf("Tanggal Transaksi", "Pengeluaran kWh", "Puncak Konsumsi Tertinggi", "Puncak Intensitas", "Rata-rata Voltase", "Pengeluaran Rupiah")
        val englishColumnHeaders = listOf("Transaction Date", "Usage (kWh)", "Peak Consumption", "Peak Intensity", "Average Voltage", "Expenditure (Rp)")
        for (i in columnHeaders.indices) {
            transactionDataTable.addCell(PdfPCell(Phrase(columnHeaders[i])).apply { border = 0 })
            transactionDataTable.addCell(PdfPCell(Phrase(englishColumnHeaders[i])).apply { border = 0 })
        }

        // transactionDataTable.addCell(...)
        userViewModel.getUserId()
        userViewModel.userId.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val userId = it
            homeViewModel.callApiGetPowerUsage(userId, selectedStartDate, selectedEndDate)
            homeViewModel.powerUsageData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { data->

                totalGlobalActivePowerPerDay = calculateTotalGlobalActivePowerPerDayInRange(data, selectedStartDate, selectedEndDate)

                highestEnergyConsumptionPerDay = findHighestEnergyConsumptionPerDayInRange(data, selectedStartDate, selectedEndDate)

                averageGlobalIntensityPerDay = calculateAverageGlobalIntensityPerDayInRange(data, selectedStartDate, selectedEndDate)

                averageVoltagePerDay = calculateAverageVoltagePerDayInRange(data, selectedStartDate, selectedEndDate)

                totalRp = calculatePengeluaranPowerPerDayInRange(data, selectedStartDate, selectedEndDate)


                // 5. Gabungan hasil perhitungan untuk membentuk data transaksi
                val transactionData = mutableListOf<ReportData>()
                totalGlobalActivePowerPerDay.keys.forEach { date ->
                    val usage = totalGlobalActivePowerPerDay[date] ?: 0.0
                    val peakConsumption = highestEnergyConsumptionPerDay[date]?.globalActivePower ?: 0.0
                    val averageIntensity = averageGlobalIntensityPerDay[date] ?: 0.0
                    val averageVoltase = averageVoltagePerDay[date] ?: 0.0
                    val expenditure = totalRp[date] ?: 0.0
                    transactionData.add(ReportData(date, usage, peakConsumption, averageIntensity, averageVoltase, expenditure))
                }
                addTransactionDataToPdfTable(transactionDataTable, transactionData)
            })
        })

        document.add(transactionDataTable)

        // Table total pengeluaran
        val totalGlobalActivePower = totalGlobalActivePowerPerDay.values.sum()
        val expenditureTotal = totalRp.values.sum()

        // 4. Hitung rata-rata atribut lainnya dari semua hari dalam rentang waktu tertentu
        val averageGlobalIntensity = averageGlobalIntensityPerDay.values.average()
        val averageVoltage = averageVoltagePerDay.values.average()


        val totalExpenditureTable = PdfPTable(5)
        totalExpenditureTable.totalWidth = PageSize.A4.width - 72
        // menambahkan kolom dan judul bahasa Inggris
        val totalColumnHeaders = listOf("Total Pengeluaran kWh", "Rata-rata Intensitas Listrik", "Rata-rata Voltase", "Total Pengeluaran Rupiah")
        val totalEnglishColumnHeaders = listOf("Total Usage (kWh)", "Average Intensity", "Average Voltage", "Total Expenditure (Rp)")
        for (i in totalColumnHeaders.indices) {
            totalExpenditureTable.addCell(PdfPCell(Phrase(totalColumnHeaders[i])).apply { border = 0 })
            totalExpenditureTable.addCell(PdfPCell(Phrase(totalEnglishColumnHeaders[i])).apply { border = 0 })
        }
        // totalExpenditureTable.addCell(...)
        totalExpenditureTable.addCell(PdfPCell(Phrase("$totalGlobalActivePower")).apply { border = 1 })
        totalExpenditureTable.addCell(PdfPCell(Phrase("$averageGlobalIntensity")).apply { border = 1 })
        totalExpenditureTable.addCell(PdfPCell(Phrase("$averageVoltage")).apply { border = 1 })
        totalExpenditureTable.addCell(PdfPCell(Phrase("$expenditureTotal")).apply { border = 1 })
        document.add(totalExpenditureTable)

        // Footer: Halaman dan timestamp
        val pageAndTimestamp = "Halaman ${writer.pageNumber} | ${SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(Date())}"
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

    @SuppressLint("SetTextI18n")
    private fun showDateRangePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT,
            DatePickerDialog.OnDateSetListener { view: DatePicker, selectedYear: Int, monthOfYear: Int, dayOfMonth: Int ->
                val startDateCalendar = Calendar.getInstance()
                startDateCalendar.set(selectedYear, monthOfYear, 1) // Set tanggal awal ke 1

                val endDateCalendar = startDateCalendar.clone() as Calendar
                endDateCalendar.add(Calendar.DAY_OF_WEEK, 7) // Tambah satu bulan untuk mendapatkan tanggal akhir

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val startDate = dateFormat.format(startDateCalendar.time)
                val endDate = dateFormat.format(endDateCalendar.time)

                binding.tvSelectedDate.text = "$startDate - $endDate"
                // Simpan tanggal terpilih ke ViewModel
                reportViewModel.saveSelectedStartDate(startDate)
                reportViewModel.saveSelectedEndDate(endDate)
            },
            year,
            month,
            1 // Tanggal selalu diatur ke 1
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
    private fun calculatePengeluaranConsumption(dataList: List<Data>, startDate: String, endDate: String): Float {

        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)

        var consumptionTotal = 0f

        // Iterasi setiap hari dalam rentang tanggal
        var currentDate = start
        while (!currentDate.isAfter(end)) {
            // Iterasi setiap data dan akumulasikan konsumsi harian
            for (data in dataList) {
                val dataDate = LocalDate.parse(data.date)
                if (dataDate == currentDate) {
                    consumptionTotal += data.globalActivePower.toFloat()
                }
            }
        }
        val consumptionPrice = consumptionTotal * 1500
        binding.tvPengeluaranKwh.setText("$consumptionTotal kWh")
        binding.tvPengeluaranRupiah.setText("Rp $consumptionPrice")

        return consumptionTotal
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bulanIni(){
        userViewModel.getUserId()
        userViewModel.userId.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val userId = it
            homeViewModel.callApiGetPowerUsage(userId, selectedStartDate, selectedEndDate)
            homeViewModel.powerUsageData.observe(viewLifecycleOwner, androidx.lifecycle.Observer { data->
                if (data!=null){
                    calculatePengeluaranConsumption(data, selectedStartDate, selectedEndDate)
                    val pengeluaran = calculatePengeluaranConsumption(data, selectedStartDate, selectedEndDate)
                    pengeluaranKwhBulanIni = pengeluaran
                    val pengeluaranRupiahBulanIni = pengeluaranKwhBulanIni * 1500
                    pengeluaranRpBulanIni = pengeluaranRupiahBulanIni
                }
            })
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun bulanlalu(){
        userViewModel.getUserId()
        userViewModel.userId.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            val userId = it
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()

            // Convert selectedStartDate to Date object
            val startDate = dateFormat.parse(selectedStartDate)

            // Set the calendar to the startDate
            calendar.time = startDate!!

            // Set the calendar to the start of the current month
            calendar.set(Calendar.DAY_OF_MONTH, 1)

            calendar.add(Calendar.DAY_OF_MONTH, -1)

            val endDateForLastMonth = dateFormat.format(calendar.time)

            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 30)
            val startDateForLastMonth = dateFormat.format(calendar.time)

            homeViewModel.callApiGetPowerUsage(userId, startDateForLastMonth, endDateForLastMonth)
            homeViewModel.powerUsageData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {data->
                if (data!=null){
                    calculatePengeluaranConsumption(data, startDateForLastMonth, endDateForLastMonth)
                    val pengeluaran = calculatePengeluaranConsumption(data, startDateForLastMonth, endDateForLastMonth)
                    pengeluaranKwhBulanLalu = pengeluaran
                    val pengeluaranRupiahBulanLalu = pengeluaranKwhBulanLalu * 1500
                    pengeluaranRpBulanLalu = pengeluaranRupiahBulanLalu
                }
            })

        })
    }

    @SuppressLint("SetTextI18n")
    private fun selisih(pengeluaranKwhBulanIni:Float, pengeluaranKwhBulanLalu:Float, rpBulanIni:Float, rpBulanLalu:Float){
        val selisihKwh = pengeluaranKwhBulanIni - pengeluaranKwhBulanLalu
        val status = if (selisihKwh > 0 ) {
            "boros"
        } else if (selisihKwh < 0 ) {
            "hemat"
        } else {
            "unknown"
        }

        val selisihRp = rpBulanIni - rpBulanLalu

        val percentageSelisihKwh = (selisihKwh / pengeluaranKwhBulanLalu) * 100

        binding.tvSelisihKwh.setText("$selisihKwh kWh")
        binding.tvSelisihRp.setText("Rp. $selisihRp")

        if (status=="boros"){
            binding.tvHematBoros.setText("Boros")
            binding.circularProgressBar.trackColor = ContextCompat.getColor(requireContext(), R.color.red)
        } else if (status=="hemat"){
            binding.tvHematBoros.setText("Hemat")
            binding.circularProgressBar.trackColor = ContextCompat.getColor(requireContext(), R.color.green)
        }

        binding.progressText.setText("$percentageSelisihKwh %")

    }

    // Fungsi untuk menghitung per hari dalam rentang waktu tertentu
    fun calculateTotalGlobalActivePowerPerDayInRange(data: List<Data>, startDate: String, endDate: String): Map<String, Double> {
        return data.filter { it.date in startDate..endDate }
            .groupBy { it.date }
            .mapValues { (_, values) -> values.sumOf { it.globalActivePower } }
    }

    fun findHighestEnergyConsumptionPerDayInRange(data: List<Data>, startDate: String, endDate: String): Map<String, Data?> {
        return data.filter { it.date in startDate..endDate }
            .groupBy { it.date }
            .mapValues { (_, values) -> values.maxByOrNull { it.globalActivePower } }
    }

    fun calculateAverageGlobalIntensityPerDayInRange(data: List<Data>, startDate: String, endDate: String): Map<String, Double> {
        return data.filter { it.date in startDate..endDate }
            .groupBy { it.date }
            .mapValues { (_, values) -> values.map { it.globalIntensity }.average() }
    }

    fun calculateAverageVoltagePerDayInRange(data: List<Data>, startDate: String, endDate: String): Map<String, Double> {
        return data.filter { it.date in startDate..endDate }
            .groupBy { it.date }
            .mapValues { (_, values) -> values.map { it.voltage }.average() }
    }

    fun calculatePengeluaranPowerPerDayInRange(data: List<Data>, startDate: String, endDate: String): Map<String, Double> {
        return data.filter { it.date in startDate..endDate }
            .groupBy { it.date }
            .mapValues { (_, values) -> values.sumOf { it.globalActivePower * 1500} }
    }


    // Fungsi untuk menambahkan data transaksi ke dalam tabel PDF
    fun addTransactionDataToPdfTable(table: PdfPTable, data: List<ReportData>) {
        data.forEach { transaction ->
            table.addCell(PdfPCell(Phrase(transaction.transactionDate)).apply { border = 0 }) // Tanggal transaksi
            table.addCell(PdfPCell(Phrase(transaction.usage.toString())).apply { border = 0 }) // Pengeluaran kWh
            table.addCell(PdfPCell(Phrase(transaction.peakConsumption.toString())).apply { border = 0 }) // Puncak konsumsi tertinggi
            table.addCell(PdfPCell(Phrase(transaction.averageIntensity.toString())).apply { border = 0 }) // ratarata intensitas
            table.addCell(PdfPCell(Phrase(transaction.averageVoltage.toString())).apply { border = 0 }) // Rata-rata voltase
            table.addCell(PdfPCell(Phrase(transaction.expenditure.toString())).apply { border = 0 }) // Pengeluaran Rupiah
        }
    }


}