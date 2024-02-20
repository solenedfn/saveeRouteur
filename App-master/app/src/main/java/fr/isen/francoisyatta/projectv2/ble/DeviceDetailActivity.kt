package fr.isen.francoisyatta.projectv2.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import fr.isen.francoisyatta.projectv2.R
import fr.isen.francoisyatta.projectv2.databinding.ActivityDeviceDetailBinding
import java.util.*
import kotlin.concurrent.timer


@SuppressLint("MissingPermission")
class DeviceDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeviceDetailBinding
    private var bluetoothGatt: BluetoothGatt? = null
    private var timer: Timer? = null
    private lateinit var adapter: BleServiceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceDetailBinding.inflate(layoutInflater)
        setContentView((binding.root))

        val device = intent.getParcelableExtra<BluetoothDevice?>(BleScanActivity.DEVICE_KEY)
        Toast.makeText(this, device?.address, Toast.LENGTH_SHORT).show()
        binding.deviceName.text = device?.name ?: "Nom inconnu"
        binding.deviceStatus.text = getString(R.string.ble_device_disconnected)

        connectToDevice(device)
    }

    //A enlever si on ne deconnecte pas du Ble à la fin
    override fun onStop() {
        super.onStop()
        closeBluetoothGatt()
    }

    private fun closeBluetoothGatt() {
        bluetoothGatt?.close()
        bluetoothGatt = null
    }


    private fun connectToDevice(device: BluetoothDevice?) {
        this.bluetoothGatt = device?.connectGatt(this, true, gattCallback)
        this.bluetoothGatt?.connect()
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    gatt?.discoverServices()
                    runOnUiThread { binding.deviceStatus.text = "Connected" }
                }
                BluetoothGatt.STATE_CONNECTING -> {
                    runOnUiThread { binding.deviceStatus.text = "Connection..." }
                }
                else -> {
                    runOnUiThread { binding.deviceStatus.text = "No connection" }
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)

            val bleServices =
                gatt?.services?.map { BleService(it.uuid.toString(), it.characteristics) }
                    ?: arrayListOf()
            runOnUiThread {
                binding.serviceList.layoutManager = LinearLayoutManager(this@DeviceDetailActivity)

                adapter = BleServiceAdapter(bleServices,
                    this@DeviceDetailActivity,
                    { characteristic -> gatt?.readCharacteristic(characteristic) },
                    { characteristic -> writeIntoCharacteristic(gatt!!, characteristic) },
                    { characteristic, enable ->
                        toggleNotificationOnCharacteristic(
                            gatt!!,
                            characteristic,
                            enable
                        )
                    }
                )
                binding.serviceList.adapter = adapter
            }

            Log.d("BluetoothLeService", "onServicesDiscovered()")
            val gattServices: List<BluetoothGattService> = gatt!!.services
            Log.d("onServicesDiscovered", "Count service: " + gattServices.size)
            for (gattService in gattServices) {
                val serviceUUID = gattService.uuid.toString()
                Log.d("onServicesDiscovered", "UUID service $serviceUUID")

                val gattChara: List<BluetoothGattCharacteristic> = gattService.characteristics
                for (gattC in gattChara) {
                    val charaUUID = gattC.uuid.toString()
                    Log.d("onServicesDiscovered", "        UUID character $charaUUID")
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int
        ) {
            super.onCharacteristicRead(gatt, characteristic, status)
            runOnUiThread {
                adapter.updateFromChangedCharacteristic(characteristic)
                adapter.notifyDataSetChanged()
            }
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?
        ) {
            super.onCharacteristicChanged(gatt, characteristic)
        }


        private fun toggleNotificationOnCharacteristic(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            enable: Boolean
        ) {
            characteristic.descriptors.forEach {
                it.value =
                    if (enable) BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE else BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(it)
            }
            gatt.setCharacteristicNotification(characteristic, enable)
            timer = Timer()

            fun convertHexToTime(hexValue: String): String {
                    val decimalValue = hexValue.toLongOrNull(16)
                     if (decimalValue != null) {
                         val hours = decimalValue / 3600
                         val minutes = (decimalValue % 3600) / 60
                         val seconds = decimalValue % 60

                         return String.format("%02d:%02d:%02d", hours, minutes, seconds)
                     }

                return "Invalid hex value"
            }

            //val T = convertHexToTime(timer)


            timer?.schedule(object : TimerTask() {
                override fun run() {
                    gatt.readCharacteristic(characteristic)
                }
            }, 0, 1000)
        }


        private fun writeIntoCharacteristic(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            runOnUiThread {
                val input = EditText(this@DeviceDetailActivity)
                input.inputType = InputType.TYPE_CLASS_TEXT
                val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(16, 0, 16, 0)
                input.layoutParams = params

                AlertDialog.Builder(this@DeviceDetailActivity)
                    .setTitle("Write value")
                    .setView(input)
                    .setPositiveButton("Validate") { _, _ ->
                        characteristic.value = input.text.toString().toByteArray()
                        gatt.writeCharacteristic(characteristic)
                        gatt.readCharacteristic(characteristic)
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                    .create()
                    .show()
            }
        }
    }
}