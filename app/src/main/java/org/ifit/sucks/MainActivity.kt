package org.ifit.sucks

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.hardware.usb.*
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import org.ifit.sucks.data.*
import org.ifit.sucks.databinding.ActivityMainBinding
import org.slf4j.LoggerFactory

const val IDLE_SCREEN_ID = 0
const val WORKOUT_SCREEN_ID = 1
const val PAUSE_SCREEN_ID = 2

class MainActivity : AppCompatActivity() {

    private val logger = LoggerFactory.getLogger(MainActivity::class.java)

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    val mainModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private val usbManager: UsbManager by lazy { getSystemService(Context.USB_SERVICE) as UsbManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.viewModel = mainModel
        binding.lifecycleOwner = this

        setupControls()

        val deviceList = usbManager.deviceList
        if (deviceList.size == 0) {
            logger.warn("Usb device not found")
            Toast.makeText(this, "Usb device not found", Toast.LENGTH_LONG).show()
            return
        }
        val device = deviceList.values.iterator().next()
        connect(device)
    }

    private fun setupControls() {
        mainModel.workoutInfo.observe(this, Observer {
            val displayChild = when (it.extendedInfo.stage) {
                ExtendedInfoResponse.Stage.IDLE -> IDLE_SCREEN_ID
                ExtendedInfoResponse.Stage.WORKOUT -> WORKOUT_SCREEN_ID
                ExtendedInfoResponse.Stage.PAUSE -> PAUSE_SCREEN_ID
                else -> WORKOUT_SCREEN_ID
            }
            if (binding.viewFlipper.displayedChild != displayChild) {
                binding.viewFlipper.displayedChild = displayChild
            }
        })
        binding.idleScreen.startBtn.setOnClickListener {
            mainModel.startWorkout()
        }
        binding.idleScreen.startStructuredBtn.setOnClickListener {
            startStructuredWorkout()
        }
        binding.pauseScreen.resumeBtn.setOnClickListener {
            mainModel.pauseWorkout()
        }
        binding.pauseScreen.stopBtn.setOnClickListener {
            mainModel.stopWorkout()
        }
    }

    private fun startStructuredWorkout() {

        val structuredWorkouts = mainModel.listStructuredWorkouts()
        val workoutTitles = structuredWorkouts.map { it.title }.toTypedArray()

        var selectedWorkout = structuredWorkouts[0]

        AlertDialog.Builder(this).apply {
            setTitle("Select structured workout")
            setPositiveButton("Start selected") { dialog, _ ->
                mainModel.startWorkout(selectedWorkout)
                dialog.dismiss()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            setSingleChoiceItems(workoutTitles, -1) { _, which ->
                selectedWorkout = structuredWorkouts[which]
            }
            show()
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onStart() {
        super.onStart()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val intentFilter = IntentFilter().apply {
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
            addAction(ACTION_USB_PERMISSION)
        }
        registerReceiver(usbPermissionReceiver, intentFilter)
        logger.debug("MainActivity.onStart()")
    }

    override fun onStop() {
        super.onStop()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        unregisterReceiver(usbPermissionReceiver)
        logger.debug("MainActivity.onStop()")
    }

    private val usbPermissionReceiver = object : BroadcastReceiver() {

        @Suppress("DEPRECATION")
        override fun onReceive(context: Context, intent: Intent) {

            logger.debug("usbPermissionReceiver.onReceive() ${intent.action}")

            when (intent.action) {
                UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    logger.debug("UsbManager.ACTION_USB_DEVICE_ATTACHED $device")
                    device?.apply {
                        connect(device)
                    }
                }

                UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    logger.debug("UsbManager.ACTION_USB_DEVICE_DETACHED $device")
                    device?.apply {
                        // call your method that cleans up and closes communication with the device
                        mainModel.disconnect()
                    }
                }

                ACTION_USB_PERMISSION -> synchronized(this) {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
                    logger.debug("UsbManager.ACTION_USB_PERMISSION + $device")
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        logger.debug("UsbManager.EXTRA_PERMISSION_GRANTED $device")
                        device?.apply {
                            mainModel.init(usbManager, this)
                        }
                    } else {
                        logger.warn("permission denied for device $device")
                    }
                }
            }
        }
    }

    private fun connect(device: UsbDevice) {

        val deviceId = device.deviceId      // 1002
        val deviceName = device.deviceName  // /dev/bus/usb/001/002
        val manufacturerName = device.manufacturerName  // ICON Fitness

        Toast.makeText(this, "$manufacturerName connected", Toast.LENGTH_LONG).show()

        logger.debug("USB device connected ($manufacturerName, $deviceId, $deviceName)")

        val permissionIntent = PendingIntent.getBroadcast(
            application,
            0,
            Intent(ACTION_USB_PERMISSION),
            PendingIntent.FLAG_MUTABLE
        )
        if (usbManager.hasPermission(device)) {
            mainModel.init(usbManager, device)
        } else {
            usbManager.requestPermission(device, permissionIntent)
        }
    }
}