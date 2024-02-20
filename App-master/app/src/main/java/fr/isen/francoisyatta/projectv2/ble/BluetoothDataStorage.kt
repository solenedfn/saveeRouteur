package fr.isen.francoisyatta.projectv2.ble

object BluetoothDataStorage {
    private val bluetoothData = mutableListOf<String>()

    fun addData(data: String) {
        bluetoothData.add(data)
    }

    fun getAllData(): List<String> {
        return bluetoothData.toList()
    }

    fun clearAllData() {
        bluetoothData.clear()
    }
}