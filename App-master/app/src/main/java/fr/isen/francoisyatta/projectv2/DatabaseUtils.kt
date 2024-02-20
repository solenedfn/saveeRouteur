package fr.isen.francoisyatta.projectv2

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.francoisyatta.projectv2.ble.BluetoothDataStorage

object DatabaseUtils {
    fun sendDataToFirestore() {
        val bluetoothDataList = BluetoothDataStorage.getAllData()
        val db = FirebaseFirestore.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        Log.d("boutonExport", "fonction sendData")

        val list = hashMapOf<String, Any>(
            "2" to "6  w 05h10 07/02/2024",
            "3" to "8  w 05h10 07/02/2024",
            "4" to "18 w 14h05 07/02/2024",
            "5" to "174w 18h10 08/02/2024",
            "6" to "5 w 18h05 05/02/2024",
            "7" to "12 w 14h05 08/02/2024",
            "8" to "16 w 14h05 09/02/2024",
            "9" to "4  w 14h05 10/02/2024"
        )

        /*val newData = hashMapOf(
            "0" to "80 w 14h05 10/03/2024",
            "11" to "908w 12h05 12/02/2024",
            "12" to "80 w 18h05 12/02/2024",
            "13" to "70 w 08h05 10/02/2024",
            "14" to "40 w 14h05 14/02/2024",
            "15" to "28 w 10h05 14/02/2024",
            "16" to "16 w 08h05 14/02/2024",
            "17" to "89 w 19h05 14/02/2024",
            "18" to "28 w 10h05 14/03/2023",
            "19" to "28 w 10h05 13/02/2024",
            "2" to "60 w 02h10 14/02/2024",
            "20" to "280w 10h05 14/05/2023",
            "3" to "8  w 05h10 15/02/2024",
            "4" to "18 w 14h05 16/02/2024",
            "5" to "174w 19h10 12/02/2024",
            "6" to "5  w 18h05 13/02/2024",
            "7" to "12 w 12h05 13/02/2024",
            "8" to "16 w 14h05 09/02/2024",
            "9" to "40 w 14h05 18/02/2024"
        )*/
        if (currentUser != null) {
            // on recupère l'uid de l'utilisateur
            val uid = currentUser.uid
            Log.d("conso uid", "1: $uid")

            /*db.collection("id").document(uid)
                .update(list as Map<String, Any>)
                .addOnSuccessListener {
                    Log.d("Firestore", "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error writing document", e)
                }*/

            db.collection("id").document(uid)
                .get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Récupérer les données actuelles du document
                        val data = documentSnapshot.data

                        // Vérifier si le document contient des données
                        if (data != null) {
                            // Trouver la plus grande clé dans les données actuelles
                            var nextKey = (data.keys.maxByOrNull { it.toIntOrNull() ?: 0 }?.toIntOrNull() ?: 0) + 1
                            Log.d("MaxKey", "nextKey: $nextKey")
                            // Créer la nouvelle entrée dans la map avec la clé incrémentée

                            /*val newData = hashMapOf<String, Any>()
                            list.forEach { (key, value) ->
                                val newKey = nextKey.toString()
                                newData[newKey] = value
                                nextKey++ // Incrémente nextKey pour la prochaine clé
                            }*/
                            val newData = hashMapOf<String, Any>()
                            bluetoothDataList.forEach { data ->
                                val newKey = nextKey.toString()
                                newData[newKey] = data
                                nextKey++
                            }

                            // Mettre à jour le document avec les nouvelles données
                            db.collection("id").document(uid)
                                .update(newData)
                                .addOnSuccessListener {
                                    Log.d("Firestore", "Document successfully updated!")
                                    BluetoothDataStorage.clearAllData()
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Firestore", "Error updating document", e)
                                }
                        }
                    }
                }
        }




    }
}