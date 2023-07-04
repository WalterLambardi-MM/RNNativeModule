package com.rnnativemodules

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.react.bridge.*
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import android.os.Bundle

class CustomMethods(private val reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val REACT_CLASS = "CustomMethods"
        const val SCAN_QR_REQUEST_CODE = 1
    }

    private var qrCodePromise: Promise? = null

    override fun getName(): String = REACT_CLASS

    override fun getConstants(): MutableMap<String, Any> {
        return hashMapOf("SCAN_QR_REQUEST_CODE" to SCAN_QR_REQUEST_CODE)
    }

    @ReactMethod
    fun scanQRCode(promise: Promise) {
        qrCodePromise = promise

        if (ContextCompat.checkSelfPermission(
                reactContext,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                reactContext.currentActivity!!,
                arrayOf(Manifest.permission.CAMERA),
                SCAN_QR_REQUEST_CODE
            )
        } else {
            startScanQRActivity()
        }
    }

    private fun startScanQRActivity() {
        val scanQRIntent = Intent(reactContext, ScanQRActivity::class.java)
        reactContext.currentActivity?.startActivityForResult(scanQRIntent, SCAN_QR_REQUEST_CODE)
    }

    private val activityEventListener: ActivityEventListener = object : BaseActivityEventListener() {
        override fun onActivityResult(activity: Activity?, requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(activity, requestCode, resultCode, data)

            if (requestCode == SCAN_QR_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    val qrCode = data?.getStringExtra("qrCode")
                    qrCodePromise?.resolve(qrCode)
                } else {
                    qrCodePromise?.reject("Scan Error", "Failed to scan QR code")
                }
                qrCodePromise = null
            }
        }
    }

    init {
        reactContext.addActivityEventListener(activityEventListener)
    }

    class ScanQRActivity : Activity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            
            val integrator = IntentIntegrator(this@ScanQRActivity)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            integrator.setPrompt("Scan QR Code")
            integrator.setBeepEnabled(false)

            // Initialize the QR code scanning process
            integrator.initiateScan()
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            // Handle the result of the QR code scanning process
            val result: IntentResult? =
                IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null && result.contents != null) {
                // QR code scanned successfully
                val qrCode = result.contents
                val returnIntent = Intent().apply {
                    putExtra("qrCode", qrCode)
                }
                setResult(Activity.RESULT_OK, returnIntent)
            } else {
                // Failed to scan QR code
                setResult(Activity.RESULT_CANCELED)
            }
            finish()
        }
    }
}
