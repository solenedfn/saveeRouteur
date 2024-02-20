package fr.isen.francoisyatta.projectv2.ble

//Ici on définit les attibuts qu'on veut recevoir de notre carte ST , y compris nos 4 caractéristiques
enum class BLEUUIDAttributes(val uuid: String, val title: String) {
    GENERIC_ACCESS("00001800-0000-1000-8000-00805f9b34fb", "Accès générique"),
    GENERIC_ATTRIBUTE("00001801-0000-1000-8000-00805f9b34fb", "Attribut générique"),
    CUSTOM_SERVICE("466c1234-f593-11e8-8eb2-f2801f1b9fd1", "Service spécifique"),
    DEVICE_NAME("00002a00-0000-1000-8000-00805f9b34fb", "Nom du périphérique"),
    APPEARANCE("00002a01-0000-1000-8000-00805f9b34fb", "Apparance"),
    CUSTOM_CHARACTERISTIC("0000beef-8e22-4541-9d4c-21edae82ed19", "Caracteristique 1"),
    CUSTOM_CHARACTERISTIC_2("0000feed-8e22-4541-9d4c-21edae82ed19", "Caracteristique 2"),
    CUSTOM_CHARACTERISTIC_3("0000aabb-8e22-4541-9d4c-21edae82ed19", "Caractéristique 3"),
    CUSTOM_CHARACTERISTIC_4("0000ccdd-8e22-4541-9d4c-21edae82ed19","Caractéristique 4"),

    UNKNOWN_SERVICE("", "Inconnu");

    companion object {
        fun getBLEAttributeFromUUID(uuid: String) = values().firstOrNull { it.uuid == uuid } ?: UNKNOWN_SERVICE
    }
}