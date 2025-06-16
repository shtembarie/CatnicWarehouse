package com.example.zebraScanner.presentation.activity

import android.content.Context
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.catnicwarehouse.databinding.ActivityZebraTestBinding
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKManager.EMDKListener
import com.symbol.emdk.EMDKManager.FEATURE_TYPE
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.barcode.BarcodeManager
import com.symbol.emdk.barcode.ScanDataCollection
import com.symbol.emdk.barcode.Scanner
import com.symbol.emdk.barcode.Scanner.DataListener
import com.symbol.emdk.barcode.Scanner.StatusListener
import com.symbol.emdk.barcode.ScannerConfig
import com.symbol.emdk.barcode.ScannerConfig.ReaderParams.ReaderSpecific.CameraSpecific
import com.symbol.emdk.barcode.ScannerException
import com.symbol.emdk.barcode.ScannerInfo
import com.symbol.emdk.barcode.ScannerResults
import com.symbol.emdk.barcode.StatusData


class ZebraTestActivity : AppCompatActivity(), EMDKListener,
    StatusListener, DataListener, OnCheckedChangeListener {

    private var _binding: ActivityZebraTestBinding? = null
    private val binding get() = _binding!!

    // Declare a variable to store EMDKManager object
    private var emdkManager: EMDKManager? = null

    // Declare a variable to store Barcode Manager object
    private var barcodeManager: BarcodeManager? = null

    // Declare a variable to hold scanner device to scan
    private var scanner: Scanner? = null


    // Array Adapter to hold arrays that are used in various drop downs
    private var spinnerDataAdapter: ArrayAdapter<String>? = null

    // List of supported scanner devices
    private var deviceList: List<ScannerInfo>? = null

    // Provides current scanner index in the device Selection Spinner
    private var scannerIndex = 0

    // Boolean to avoid calling setProfile() method again in the scan tone
    // listener
    private var isScanToneFirstTime = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityZebraTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        with(binding) {
            checkBoxCode11.setOnCheckedChangeListener(this@ZebraTestActivity)
            checkBoxCode39.setOnCheckedChangeListener(this@ZebraTestActivity)
            checkBoxCode128.setOnCheckedChangeListener(this@ZebraTestActivity)
            checkBoxUPCA.setOnCheckedChangeListener(this@ZebraTestActivity)
            checkBoxEan8.setOnCheckedChangeListener(this@ZebraTestActivity)
            checkBoxEan13.setOnCheckedChangeListener(this@ZebraTestActivity)
            illumination.setOnCheckedChangeListener(this@ZebraTestActivity)
            vibration.setOnCheckedChangeListener(this@ZebraTestActivity)
        }

        spinnerDataAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item, resources
                .getStringArray(com.example.catnicwarehouse.R.array.scan_tone_array)
        )

        spinnerDataAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.scanToneSpinner.adapter = spinnerDataAdapter

        addScanButtonListener()
        addSpinnerScannerDevicesListener()
        addSpinnerScanToneListener()

        val results = EMDKManager.getEMDKManager(
            applicationContext, this
        )

        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            binding.textViewStatus.text = "EMDKManager Request Failed"
        }
    }


    private fun addSpinnerScannerDevicesListener() {


        binding.deviceSelectionSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                scannerIndex = position
                try {
                    deInitScanner()
                    initializeScanner()
                    setProfile()
                } catch (e: ScannerException) {
                    e.printStackTrace()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do something when nothing is selected
            }
        }
    }

    // Listener for Scan Tone Spinner
    private fun addSpinnerScanToneListener() {

        binding.scanToneSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (isScanToneFirstTime)
                    setProfile()
                else
                    isScanToneFirstTime = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do something when nothing is selected
            }
        }
    }

    private fun addScanButtonListener() {
        binding.btnScan.setOnTouchListener { v, event -> // Scan Button Press Event
            if (event.action == MotionEvent.ACTION_DOWN) {
                try {
                    // Enable Soft scan
                    scanner?.triggerType = Scanner.TriggerType.SOFT_ONCE
                    // cancel any pending reads before reading barcodes
                    if (scanner?.isReadPending == true) scanner?.cancelRead()
                    // Puts the device in a state where it can scan barcodes
                    scanner?.read()
                } catch (e: ScannerException) {
                    e.printStackTrace()
                }
                // Scan Button Release Event
            } else if (event.action == MotionEvent.ACTION_UP) {
                try {
                    // cancel any pending reads before reading barcodes
                    if (scanner?.isReadPending == true) scanner?.cancelRead()
                } catch (e: ScannerException) {
                    e.printStackTrace()
                }
            }
            false
        }
    }

    // Method to initialize and enable Scanner and its listeners
    @Throws(ScannerException::class)
    private fun initializeScanner() {
        if (deviceList?.isNotEmpty() == true) {
            scanner = barcodeManager!!.getDevice(deviceList!![scannerIndex])
        } else {
            binding.textViewStatus.text =
                "Status: Failed to get the specified scanner device! Please close and restart the application."

        }
        if (scanner != null) {

            // Add data and status listeners
            scanner!!.addDataListener(this)
            scanner!!.addStatusListener(this)
            try {
                // Enable the scanner
                scanner!!.enable()
            } catch (e: ScannerException) {
                binding.textViewStatus.text = "Status: " + e.message
            }
        }
    }


    // Disable the scanner instance
    private fun deInitScanner() {
        if (scanner != null) {
            try {
                scanner?.cancelRead()
                scanner?.removeDataListener(this)
                scanner?.removeStatusListener(this)
                scanner?.disable()
            } catch (e: ScannerException) {
                binding.textViewStatus.text = "Status: " + e.message
            }
            scanner = null
        }
    }


    private fun setProfile() {
        try {

            if (scanner?.isReadPending == true)
                scanner?.cancelRead()

            val config = scanner?.config

            with(binding) {
                config?.decoderParams?.code11?.enabled = checkBoxCode11.isChecked

                // Set code39
                config?.decoderParams?.code39?.enabled = checkBoxCode39.isChecked

                // Set code128
                config?.decoderParams?.code128?.enabled = checkBoxCode128.isChecked

                // set codeUPCA
                config?.decoderParams?.upca?.enabled = checkBoxUPCA.isChecked

                // set EAN8
                config?.decoderParams?.ean8?.enabled = checkBoxEan8.isChecked

                // set EAN13
                config?.decoderParams?.ean13?.enabled = checkBoxEan13.isChecked


                if (binding.illumination.isChecked && deviceSelectionSpinner.selectedItem.toString()
                        .contains("Camera")
                ) {
                    config?.readerParams?.readerSpecific?.cameraSpecific?.illuminationMode =
                        ScannerConfig.IlluminationMode.ON
                } else {
                    config?.readerParams?.readerSpecific?.cameraSpecific?.illuminationMode =
                        ScannerConfig.IlluminationMode.OFF
                }
                config?.scanParams?.decodeHapticFeedback = binding.vibration.isChecked
            }
            // Set the Scan Tone selected from the Scan Tone Spinner
            config?.scanParams?.audioStreamType = ScannerConfig.AudioStreamType.RINGER
            val scanTone = binding.scanToneSpinner.selectedItem.toString()
            if (scanTone.contains("NONE"))
            // Silent Mode (No scan tone will be played)
                config?.scanParams?.decodeAudioFeedbackUri = ""
            else
            // Other selected scan tones from the drop-down
                config?.scanParams?.decodeAudioFeedbackUri =
                    "system/media/audio/notifications/$scanTone"

            scanner?.config = config

            // Starts an asynchronous Scan. The method will not turn
            // ON the
            // scanner. It will, however, put the scanner in a state
            // in which
            // the scanner can be turned ON either by pressing a
            // hardware
            // trigger or can be turned ON automatically.
            scanner?.read()

            Toast.makeText(
                this@ZebraTestActivity,
                "Changes Applied. Press Scan Button to start scanning...",
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: ScannerException) {
            binding.textViewStatus.text = (e.toString())
        }


    }

    private fun enumerateScannerDevices() {

        if (barcodeManager != null) {

            var friendlyNameList = ArrayList<String>()
            var spinnerIndex = 0
            var defaultIndex = 0

            deviceList = barcodeManager?.supportedDevicesInfo

            if (deviceList?.size != 0) {

                val it = deviceList?.iterator()
                while (it?.hasNext() == true) {
                    val scnInfo = it.next()
                    friendlyNameList.add(scnInfo.friendlyName)
                    if (scnInfo.isDefaultScanner) {
                        defaultIndex = spinnerIndex
                    }
                    ++spinnerIndex
                }

            } else {
                binding.textViewStatus.text =
                    "Status: Failed to get the list of supported scanner devices! Please close and restart the application."

            }

            spinnerDataAdapter =
                ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, friendlyNameList);
            spinnerDataAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            binding.deviceSelectionSpinner.adapter = spinnerDataAdapter
            binding.deviceSelectionSpinner.setSelection(defaultIndex)

        }


    }

    // Update the scan data on UI
    var dataLength = 0

    // AsyncTask that configures the scanned data on background
// thread and updates the result on UI thread with scanned data and type of
// label
    private inner class AsyncDataUpdate : AsyncTask<ScanDataCollection, Void, String>() {

        override fun doInBackground(vararg params: ScanDataCollection): String {
            val scanDataCollection = params[0]

            // Status string that contains both barcode data and type of barcode
            // that is being scanned
            var statusStr = ""

            // The ScanDataCollection object gives scanning result and the
            // collection of ScanData. So check the data and its status
            if (scanDataCollection != null
                && scanDataCollection.result == ScannerResults.SUCCESS
            ) {

                val scanData = scanDataCollection.scanData

                // Iterate through scanned data and prepare the statusStr
                for (data in scanData) {
                    // Get the scanned data
                    val barcodeData = data.data
                    // Get the type of label being scanned
                    val labelType = data.labelType
                    // Concatenate barcode data and label type
                    statusStr = "$barcodeData $labelType"
                }
            }

            // Return result to populate on UI thread
            return statusStr
        }

        override fun onPostExecute(result: String) {
            // Update the dataView EditText on UI thread with barcode data and
            // its label type
            if (dataLength++ > 50) {
                // Clear the cache after 50 scans
                binding.editText1.text.clear()
                dataLength = 0
            }
            binding.editText1.append("$result\n")
        }

        override fun onPreExecute() {
        }

        override fun onProgressUpdate(vararg values: Void?) {
        }
    }

    var isScanning = false

    private inner class AsyncStatusUpdate :
        AsyncTask<StatusData, Void, String>() {

        override fun doInBackground(vararg params: StatusData): String {
            // Get the current state of scanner in background
            val statusData = params[0]
            var statusStr = ""
            val state = statusData.state
            // Different states of Scanner
            when (state) {
                StatusData.ScannerStates.IDLE -> {
                    statusStr = "The scanner enabled and its idle"
                    isScanning = false
                }

                StatusData.ScannerStates.SCANNING -> {
                    statusStr = "Scanning.."
                    isScanning = true
                }

                StatusData.ScannerStates.WAITING -> {
                    statusStr = "Waiting for trigger press.."
                }

                StatusData.ScannerStates.DISABLED -> {
                    statusStr = "Scanner is not enabled"
                }

                else -> {}
            }
            // Return result to populate on UI thread
            return statusStr
        }

        override fun onPostExecute(result: String) {
            // Update the status text view on UI thread with current scanner
            // state
            binding.textViewStatus.text = result
        }

        override fun onPreExecute() {
        }

        override fun onProgressUpdate(vararg values: Void?) {
        }
    }


    override fun onOpened(emdkManager: EMDKManager?) {
        this.emdkManager = emdkManager
        barcodeManager = this.emdkManager?.getInstance(FEATURE_TYPE.BARCODE) as BarcodeManager
        enumerateScannerDevices()
    }

    override fun onClosed() {
        if (this.emdkManager != null) {
            this.emdkManager?.release()
            this.emdkManager = null
        }
    }

    override fun onStatus(statusData: StatusData?) {
        AsyncStatusUpdate().execute(statusData)
    }

    override fun onData(scanDataCollection: ScanDataCollection?) {
        AsyncDataUpdate().execute(scanDataCollection)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (barcodeManager != null)
            barcodeManager = null;

        if (emdkManager != null) {
            emdkManager?.release();
            emdkManager = null;
        }
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        setProfile()
    }

    override fun onStop() {
        super.onStop()
        deInitScanner()
    }


}