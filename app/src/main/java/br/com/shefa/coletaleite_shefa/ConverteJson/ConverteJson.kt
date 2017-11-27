package br.com.shefa.coletaleite_shefa.ConverteJson

import android.util.Log
import br.com.shefa.coletaleite_shefa.Objetos.ObjetosPojo
import org.json.JSONStringer
import java.util.ArrayList

/**
 * Created by aassis on 16/11/2017.
 */
class ConverteJson  {
    fun toJson(listaColetas: ArrayList<ObjetosPojo>): String? {
        try {
            val jsonStringer = JSONStringer()
            jsonStringer.`object`().key("coletas").array()
            for (coletaPojo in listaColetas) {
                jsonStringer.`object`()
                        .key("_idt").value(coletaPojo.id2)
                        .key("_dataColeta").value(coletaPojo.dataColeta)
                        .key("_rota").value(coletaPojo.rota)
                        .key("_subRota").value(coletaPojo.subRota)
                        .key("_codTransportadora").value(coletaPojo.codTransportadora)
                        .key("_codProdutor").value(coletaPojo.codProdutor)
                        .key("_nomeProdutor").value(coletaPojo.nomeProdutor)
                        .key("_cidade").value(coletaPojo.cidade)
                        .key("_qtd").value(coletaPojo.quantidade)
                        .key("_imei").value(coletaPojo.imei)
                        .key("_temperatura").value(coletaPojo.temperatiura)
                        .key("_alisarol").value(coletaPojo.alisarol)
                        .key("_boca").value(coletaPojo.boca)
                        .key("_obs").value(coletaPojo.obs)
                        .key("_latitudeLocal").value(coletaPojo.latitude)
                        .key("_longitudeLocal").value(coletaPojo.longitude)
                        .key("_dataHora").value(coletaPojo.datahora)
                        .key("_pedagio").value(coletaPojo.pedagio)

                        .endObject()
            }
            jsonStringer.endArray().endObject()
            return jsonStringer.toString()

        } catch (e: Exception) {
            Log.i("coleta", e.message)
            return null
        }

    }
}