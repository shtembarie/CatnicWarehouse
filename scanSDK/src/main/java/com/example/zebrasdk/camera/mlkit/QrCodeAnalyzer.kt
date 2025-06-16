package com.example.zebrasdk.camera.mlkit

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QrCodeAnalyzer(
    private val barcodeListener: (barcode: String, Bitmap?) -> Unit
) : ImageAnalysis.Analyzer {

    var isBarCodeDetected = false

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val img = image.image
        if (img != null) {
            // Update scale factors
//            val height = img.height
//            val width = img.width
//            //Top    : (far) -value > 0 > +value (closer)
//            val c1x = (width * 0.125).toInt() + 150
//            //Right  : (far) -value > 0 > +value (closer)
//            val c1y = (height * 0.25).toInt() - 25
//            //Bottom : (closer) -value > 0 > +value (far)
//            val c2x = (width * 0.875).toInt() - 150
//            //Left   : (closer) -value > 0 > +value (far)
//            val c2y = (height * 0.75).toInt() + 25
//
//            val rect = Rect(c1x, c1y, c2x, c2y)

//            val ori: Bitmap = image.toBitmap()!!
//            val crop = Bitmap.createBitmap(ori, rect.left, rect.top, rect.width(), rect.height())
//            val rImage = crop.rotate(90F)

            val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)

            // Process image searching for barcodes
            val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
                Barcode.FORMAT_ALL_FORMATS
            ).build()

            val scanner = BarcodeScanning.getClient(options)
            scanner.process(inputImage).addOnSuccessListener { barcodes ->
                // Task completed successfully
                for (barcode in barcodes) {
                    if (!isBarCodeDetected) {
                        barcodeListener(barcode.rawValue ?: "", null)
                        image.close()
                        isBarCodeDetected = true
                    }

                }
            }.addOnFailureListener {
                // You should really do something about Exceptions
                image.close()
            }.addOnCompleteListener {
                // It's important to close the imageProxy
                image.close()
            }
        }

        image.close()
    }
}