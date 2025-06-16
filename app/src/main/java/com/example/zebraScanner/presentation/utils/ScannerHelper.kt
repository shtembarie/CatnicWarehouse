package com.example.zebraScanner.presentation.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.InputDevice
import android.view.MotionEvent
import com.example.zebraScanner.presentation.enums.ScannerType
import com.example.zebraScanner.presentation.enums.ScannerType.*
import com.symbol.emdk.EMDKManager
import com.symbol.emdk.EMDKResults
import com.symbol.emdk.barcode.*
import com.symbol.emdk.barcode.StatusData.ScannerStates
import javax.inject.Inject

class ScannerHelper(
    private val context: Context,
    private var scannerType: ScannerType,
    private val updateStatus: (String,ScannerStates) -> Unit,
    private val updateData: (String) -> Unit
) : EMDKManager.EMDKListener, Scanner.StatusListener, Scanner.DataListener {

    private var emdkManager: EMDKManager? = null
    private var barcodeManager: BarcodeManager? = null
    private var scanner: Scanner? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        val results = EMDKManager.getEMDKManager(context, this)
        Log.d("ScannerHelper", "init")
        if (results.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            postUpdateStatus("EMDKManager initialization failed.",ScannerStates.ERROR)
        }
    }

    override fun onOpened(emdkManager: EMDKManager) {
        Log.d("ScannerHelper", "onOpened")
        this.emdkManager = emdkManager
        initBarcodeManager()
    }

    override fun onClosed() {
        cleanUp()
        postUpdateStatus("EMDKManager has been released.",ScannerStates.DISABLED)
    }

    private fun initBarcodeManager() {
        barcodeManager =
            emdkManager?.getInstance(EMDKManager.FEATURE_TYPE.BARCODE) as? BarcodeManager
        if (barcodeManager == null) {
            postUpdateStatus("Failed to get BarcodeManager instance.",ScannerStates.ERROR)
        } else {
            postUpdateStatus("BarcodeManager initialized.",ScannerStates.IDLE)
            enumerateScannerDevices()
        }
    }

    fun changeScannerType(newScannerType: ScannerType) {
        if (scannerType != newScannerType) {
            scannerType = newScannerType
            enumerateScannerDevices()
        }
    }

    private fun enumerateScannerDevices() {
        val supportedDevices = barcodeManager?.supportedDevicesInfo
            ?: return postUpdateStatus("No scanner devices found.",ScannerStates.ERROR)
        val scannerInfo = when (scannerType) {
            DEFAULT_SCANNER -> supportedDevices.find { it.isDefaultScanner }
            CAMERA -> supportedDevices.find { it.friendlyName.contains("Camera") }
        }
        if (scannerInfo != null) {
            initializeScanner(scannerInfo)
        } else {
            postUpdateStatus("Requested scanner type not found.",ScannerStates.ERROR)
        }
    }

    private fun initializeScanner(scannerInfo: ScannerInfo) {
        try {
            releaseScanner()
            scanner = barcodeManager?.getDevice(scannerInfo)
            scanner?.addDataListener(this)
            scanner?.addStatusListener(this)
            scanner?.enable()
            when (scannerType) {
                DEFAULT_SCANNER -> scanner?.triggerType = Scanner.TriggerType.HARD
                CAMERA -> scanner?.triggerType = Scanner.TriggerType.SOFT_ONCE
            }

            setProfile()
            postUpdateStatus("Scanner initialized: ${scannerInfo.friendlyName}",ScannerStates.IDLE)
        } catch (e: ScannerException) {
            postUpdateStatus("Error initializing scanner: ${e.printStackTrace()}",ScannerStates.ERROR)
        }
    }

    private fun setProfile() {
        scanner?.let { scanner ->
            try {
                if (scanner.isReadPending) {
                    scanner.cancelRead()
                }
                val config = scanner.config

                config.decoderParams.code11.enabled = true
                config.decoderParams.code39.enabled = true
                config.decoderParams.code128.enabled = true
                config.decoderParams.upca.enabled = true
                config.decoderParams.ean8.enabled = true
                config.decoderParams.ean13.enabled = true
                config.decoderParams.ean13.enabled = true
                config.decoderParams.dataMatrix.enabled = true
                config.decoderParams.pdf417.enabled = true
                config.decoderParams.qrCode.enabled = true
                config.decoderParams.upca.enabled = true
                config.decoderParams.upce0.enabled = true
                config.decoderParams.aztec.enabled = true
                config.decoderParams.microQR.enabled = true
                config.decoderParams.maxiCode.enabled = true
                config.decoderParams.gs1Databar.enabled = true
                config.decoderParams.gs1DatabarExp.enabled = true
                config.decoderParams.gs1DatabarLim.enabled = true
                config.decoderParams.usPostNet.enabled = true
                config.decoderParams.usPlanet.enabled = true
                config.decoderParams.ukPostal.enabled = true
                config.decoderParams.japanesePostal.enabled = true
                config.decoderParams.australianPostal.enabled = true
                config.decoderParams.dutchPostal.enabled = true
                config.decoderParams.canadianPostal.enabled = true

                //Enable Haptic Feedback
                config?.scanParams?.decodeHapticFeedback = true

                // Apply updated configuration to the scanner
                scanner.config = config
                scanner.read()  // Optional: Restart scanning with new settings

            } catch (e: ScannerException) {
                postUpdateStatus("Error applying scanner settings: ${e.message}", ScannerStates.ERROR)
            }
        } ?: postUpdateStatus("Scanner is not initialized.",ScannerStates.ERROR)
    }

    private fun releaseScanner() {
        scanner?.let {
            it.removeDataListener(this)
            it.removeStatusListener(this)
            it.disable()
        }
        scanner = null
    }
    fun startScanning() {
        try {
            if (scanner == null) {
                postUpdateStatus("Scanner is not initialized.",ScannerStates.ERROR)
                return
            }

            if (scanner?.isReadPending == true) {
                // Cancel any pending read before starting a new one
                scanner?.cancelRead()
                postUpdateStatus("Scanning canceled.",ScannerStates.IDLE)
            }
        } catch (e: ScannerException) {
            postUpdateStatus("Error starting scanner: ${e.message}",ScannerStates.ERROR)
        }
    }
    fun stopScanning() {
        try {
            scanner?.cancelRead()
            postUpdateStatus("Scanning stopped.",ScannerStates.IDLE)
        } catch (e: ScannerException) {
            postUpdateStatus("Error stopping scanner: ${e.message}",ScannerStates.ERROR)
        }
    }

    override fun onStatus(statusData: StatusData) {
        val status = when (statusData.state) {
            StatusData.ScannerStates.IDLE -> { if (scanner != null && !scanner!!.isReadPending) {
                scanner?.read()
                "Scanner is re-initialized"
            }else{
                "Scanner is idle."
            }
            }
            StatusData.ScannerStates.SCANNING -> "Scanner is active."
            StatusData.ScannerStates.WAITING -> "Waiting for trigger press."
            StatusData.ScannerStates.DISABLED -> "Scanner is disabled."
            else -> "Unknown state."
        }
        postUpdateStatus(status,statusData.state)
    }

    override fun onData(scanDataCollection: ScanDataCollection) {
        if (scanDataCollection.result == ScannerResults.SUCCESS) {
            scanDataCollection.scanData.forEach {
                postUpdateData(it.data)
            }
        } else {
//            postUpdateStatus("No scan data received.",)
        }
    }

    fun cleanUp() {
        releaseScanner()
        barcodeManager = null
        emdkManager?.release()
        emdkManager = null
        postUpdateStatus("Scanner resources released.",ScannerStates.DISABLED)
    }

    fun handleScanButton(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                try {
                    scanner?.triggerType = Scanner.TriggerType.SOFT_ONCE
                    if (scanner?.isReadPending == true) scanner?.cancelRead()
                    startScanning()
                } catch (e: ScannerException) {
                    postUpdateStatus("Error stopping scanner: ${e.message}",ScannerStates.ERROR)
                }
                true
            }

            MotionEvent.ACTION_UP -> {
                try {
                    scanner?.triggerType = Scanner.TriggerType.HARD
                    if (scanner?.isReadPending == true) {
                        //stopScanning()
                    }

                } catch (e: ScannerException) {
                    postUpdateStatus("Error stopping scanner: ${e.message}",ScannerStates.ERROR)
                }
                true
            }

            else -> false
        }
    }


    private fun postUpdateStatus(status: String,scannerStates: ScannerStates) {
        Log.d("ScannerHelper", status  )
        mainHandler.post {
            updateStatus(status,scannerStates)
        }
    }

    private fun postUpdateData(data: String) {
        mainHandler.post {
            updateData(data)
        }
    }
}








