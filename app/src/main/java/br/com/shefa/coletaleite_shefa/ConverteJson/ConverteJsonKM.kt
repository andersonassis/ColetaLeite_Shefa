package br.com.shefa.coletaleite_shefa.ConverteJson

import android.util.Log
import br.com.shefa.coletaleite_shefa.Objetos.ObjetosPojo
import org.json.JSONStringer
import java.util.ArrayList

/**
 * Created by aassis on 17/11/2017.
 */
class ConverteJsonKM  {
    fun toJson(listaColetas: ArrayList<ObjetosPojo>): String? {
        try {
            val jsonStringer = JSONStringer()
            jsonStringer.`object`().key("coletaKM").array()
            for (coletaPojo in listaColetas) {
                jsonStringer.`object`()
                        .key("_idkm").value(coletaPojo.idlinha)
                        .key("_datakm").value(coletaPojo.datakm)
                        .key("_rotakm").value(coletaPojo.rotaKM)
                        .key("_subRotakm").value(coletaPojo.subRotaKM)
                        .key("_imeikm").value(coletaPojo.imeiKM)
                        .key("_qtdkm").value(coletaPojo.qtdKM)
                        .endObject()
            }
            jsonStringer.endArray().endObject()
            return jsonStringer.toString()

        } catch (e: Exception) {
            Log.i("coletakm", e.message)
            return null
        }

    }
}