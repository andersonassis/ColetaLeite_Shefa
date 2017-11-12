package br.com.shefa.coletaleite_shefa.Conexao

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Created by AndersonLuis on 11/11/2017.
 */

@Suppress("DEPRECATION")
class TestarConexao  {
    fun verificaConexao(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager == null) {
            return false
        } else {
            val networkInfo = connectivityManager.allNetworkInfo
            if (networkInfo != null) {
                for (i in networkInfo.indices) {
                    if (networkInfo[i].state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
        }
        return false
    }
}