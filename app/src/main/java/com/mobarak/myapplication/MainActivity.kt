package com.mobarak.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jiebao.nfc.uartnfc.CardReaderDevice
import com.jiebao.util.CardReaderUtils
import com.jiebao.util.L
import com.mobarak.myapplication.ui.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        CardReaderDevice.getInstance().initCardReader()
        L.setDebug(true)

        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column (modifier = Modifier.fillMaxSize().padding(16.dp),verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                        GetInfoButton(
                            name = "get nfc information")
                        Spacer(modifier = Modifier.height(16.dp)) // Space between buttons

                        ReadButton(
                            name = "read nfc")
                        Spacer(modifier = Modifier.height(16.dp)) // Space between buttons

                        WriteButton(
                            name = "write nfc",
                        )
                    }

                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        CardReaderDevice.getInstance().deInitCardReader()
    }
}
@Composable
fun GetInfoButton(name:String){
    Button(onClick = {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = Date()
        val fwVersion = CardReaderDevice.getInstance().nfchwVersion
        val cardNo = CardReaderDevice.getInstance().readCardNo()

        var stringData = dateFormat.format(date) + " NFC version " + fwVersion + " card no " + cardNo
        Log.v("[DEBUG]", "GetInfoButton cardNo $cardNo")
        Log.v("[DEBUG]",stringData)

    }) {
        Text(text = name)
    }
}

@Composable
fun ReadButton(name:String){
    Button(onClick = {
        val KEY_READ = byteArrayOf(
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte()
        )
        var str = ""
        for (i in 0..63) {
            val bReturn = CardReaderDevice.getInstance().readM1CardChunkData(0, i, KEY_READ)
            if (bReturn != null) {
                Log.e(
                    "[DEBUG]",
                    "bReturn " + i + " length " + bReturn.size + " " + CardReaderUtils.byteArray2HexString(
                        bReturn
                    )
                )
                str += CardReaderUtils.byteArray2HexString(bReturn) + "\n"
            }
        }
        Log.v("[DEBUG]", "ReadButton $str")

    },) {
        Text(text = name)
    }
}


@Composable
fun WriteButton(name:String){
    Button(onClick = {

        val KEY_READ = byteArrayOf(
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte()
        )
        val DATA_WRITE = byteArrayOf(
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()
        )

        CardReaderDevice.getInstance().writeM1CardChunkData(0, 4, KEY_READ, DATA_WRITE)

    }) {
        Text(text = name)
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    MyApplicationTheme {
//        Greeting("Android")
//    }
//}