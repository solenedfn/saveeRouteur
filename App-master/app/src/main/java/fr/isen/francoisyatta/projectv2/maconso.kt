package fr.isen.francoisyatta.projectv2

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.francoisyatta.projectv2.ble.BluetoothDataStorage
import fr.isen.francoisyatta.projectv2.databinding.ActivityMaconsoBinding
import java.util.*
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter





class maconso : AppCompatActivity() {

    private lateinit var binding: ActivityMaconsoBinding
    val taille = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maconso)

        binding = ActivityMaconsoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //recupérationDonnées()

            supportFragmentManager.executePendingTransactions()
            //initializeScreen(fragmentSemaine.getFragmentSemaineBinding().consoGraph)
            //fetchDataAndFillList(bouton)
        }

    private fun sendDataToFirestore() {
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
                                }
                                .addOnFailureListener { e ->
                                    Log.w("Firestore", "Error updating document", e)
                                }
                        }
                    }
                }
        }




    }
    /*private fun recupérationDonnées() {
        val db = FirebaseFirestore.getInstance()
        val mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            // on recupère l'uid de l'utilisateur
            val uid = currentUser.uid
            Log.d("conso uid", "1: $uid")

            // on récupère les données de la collection id qui a pour id l'uid de l'utilisateur
            db.collection("id").document(uid).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val document = task.result
                        if (document != null && document.exists()) {
                            //on recupere les données
                            val data = document.data
                            Log.d("donnée de firebase", "1: $data")

                            if (data != null) {
                                taille = data.size
                                Log.d("taille du tableau", "1: $taille")

                                val monTableau = mutableListOf<String>()
                                //on cree un tableau qui va prendre les valeurs de data
                                for (i in 0 until taille) {
                                    val value = data.entries.elementAtOrNull(i)?.value as? String
                                    Log.d("value", "1: $value")
                                    if (value != null) {
                                        monTableau.add(value)
                                        Log.d("valeur du tableau", "1: ${monTableau[i]}")
                                    }
                                }

                                val tableaustring = monTableau.toTypedArray()
                                var anneeALenversInt: Int = 0
                                //on decoupe de string en 3 parties
                                tableaufinal = tableaustring.map { chaine ->
                                    val conso = charArrayOf(chaine[0], chaine[1], chaine[2])
                                    val heure = charArrayOf(chaine[5], chaine[6])
                                    val minutesChar = charArrayOf(chaine[8], chaine[9])
                                    val annee = charArrayOf(
                                        chaine[11],
                                        chaine[12],
                                        chaine[14],
                                        chaine[15],
                                        chaine[17],
                                        chaine[18],
                                        chaine[19],
                                        chaine[20]
                                    )
                                    val anneeALenvers = charArrayOf(
                                        chaine[17],
                                        chaine[18],
                                        chaine[19],
                                        chaine[20],
                                        chaine[14],
                                        chaine[15],
                                        chaine[11],
                                        chaine[12]
                                    )

                                    // Modification minute en base 10
                                    val minutesString = minutesChar.joinToString("")
                                    val minutesFloat = minutesString.toFloatOrNull() ?: 0.0f
                                    val minuteBase10 = String.format("%.2f", minutesFloat / 60)

                                    val minuteBase10Char = minuteBase10.toCharArray()
                                    Log.d(
                                        "minuteBase10Char",
                                        "1: ${minuteBase10Char.joinToString("")}"
                                    )
                                    val deuxDernierChiffres = charArrayOf(
                                        minuteBase10Char[1],
                                        minuteBase10Char[2],
                                        minuteBase10Char[3]
                                    )
                                    Log.d(
                                        "deuxDernierChiffres",
                                        "1: ${deuxDernierChiffres.joinToString("")}"
                                    )
                                    // Concaténation des heures et des minutes
                                    val heureMinute = heure + deuxDernierChiffres
                                    Log.d("heureMinute", "1: ${heureMinute.joinToString("")}")

                                    val anneeALenversHeure = anneeALenvers + heureMinute

                                    arrayOf(conso, heureMinute, annee, anneeALenversHeure)
                                }
                                //trie le tableau par rapport a l'annee
                                tableaufinal = tableaufinal.sortedBy { ligne ->
                                    val anneeALenversHeure = String(ligne[3])
                                    anneeALenversHeure
                                }
                                for (ligne in tableaufinal) {
                                    val conso = String(ligne[0])
                                    val heure = String(ligne[1])
                                    val annee = String(ligne[2])
                                    val anneeALenversHeure = String(ligne[3])

                                    Log.d(
                                        "Tableaufinal2",
                                        "Conso: $conso, Heure: $heure, Année: $annee, anneeALenversHeure : $anneeALenversHeure"
                                    )
                                }


                            }
                        } else {
                            Log.e("Activite", "Erreur: Document nul ou inexistant")
                        }
                    } else {
                        Log.e(
                            "Activite",
                            "Erreur lors de la récupération des données de 'id'",
                            task.exception
                        )
                    }
                }
        }
    }*/
}