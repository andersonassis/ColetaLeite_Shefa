package br.com.shefa.coletaleite_shefa.Datas

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by AndersonLuis on 20/11/2017.
 */
class Datas  {
    //FUNÇÃO PARA PEGAR A  DATA DO SISTEMA
    fun data():String{
        val date = SimpleDateFormat("dd-MM-yyyy")
        val data = Date()
        val cal = Calendar.getInstance()
        cal.time = data
        val data_atual = cal.time
        val data_sistema2 = date.format(data_atual)
        var datasistema = data_sistema2
        return datasistema
    }


    //FUNÇÃO PARA PEGAR A DATA DE UM DIA ANTES
    fun dataMenosUm():String{
        //  Pegando a data do dia anterior da data atual
        val datas = Calendar.getInstance()
        datas.add(Calendar.DATE, -1)
        val testeData = SimpleDateFormat("dd-MM-yyyy")
        val dataAnterior = testeData.format(datas.time)
        val data = dataAnterior
        return data
    }


    //FUNÇÃO PARA PEGAR A DATA E HORA
    fun dataHora():String{
        val date = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val data = Date()
        val cal = Calendar.getInstance()
        cal.time = data
        val data_atual = cal.time
        val data_sistema2 = date.format(data_atual)
        var datasistema = data_sistema2
        return datasistema
    }



}