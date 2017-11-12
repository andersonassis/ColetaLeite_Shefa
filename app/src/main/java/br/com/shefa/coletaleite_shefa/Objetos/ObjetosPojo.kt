package br.com.shefa.coletaleite_shefa.Objetos

/**
 * Created by AndersonLuis on 11/11/2017.
 */
class ObjetosPojo  {
    //variaveis
    var id: Int = 0  //id do banco
    var id2:String? = null // id do AX
    var dataColeta: String? = null  //data da coleta no sitio
    var rota: String? = null        // rota exemplo LEIITAP
    var subRota: String? = null   // Linha
    var codTransportadora:String? =null
    var codProdutor:String? = null   // codigo do produtor
    var nomeProdutor: String? = null  // nome do produtor
    var enderecoProdutor:String? = null // endereço do produtor
    var cidade:String? = null  // cidade
    var quantidade:String? = null // quantidades de litros
    var imei:String? = null   //imei do aparelho
    var temperatiura:String? = null // temperatura do leite no produtor
    var alisarol:String? = null //analise de  alisarol
    var boca:String? = null // boca do caminhão
    var latitude:String? = null  // latitude
    var longitude:String? = null  // longitude
    var obs:String? = null        //obs
    var datahora:String? = null    // data e hora da coleta
    var salvou:String? = null
    var pedagio:String? = null


    constructor(){
    }

    constructor(id: Int,id2:String, rota: String, dataColeta:String, subRota:String, codTransportadora:String, codProdutor:String,nomeProdutor:String,
                enderecoProdutor:String, cidade:String,quantidade:String,imei:String,temperatiura:String,alisarol:String,boca:String,
                latitude:String, longitude:String, dataHora:String, obs:String, salvou:String, pedagio:String) {
        this.id = id
        this.id2 = id2
        this.rota = rota
        this.dataColeta = dataColeta
        this.subRota    = subRota
        this.codTransportadora = codTransportadora
        this.codProdutor = codProdutor
        this.nomeProdutor = nomeProdutor
        this.enderecoProdutor = enderecoProdutor
        this.cidade = cidade
        this.quantidade = quantidade
        this.imei   = imei
        this.temperatiura = temperatiura
        this.alisarol = alisarol
        this.boca = boca
        this.latitude = latitude
        this.longitude = longitude
        this.obs  = obs
        this.datahora  = datahora
        this.salvou  = salvou
        this.pedagio = pedagio
    }







}//fim da classe