package br.com.shefa.coletaleite_shefa

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteCursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.RadioGroup
import br.com.shefa.coletaleite_shefa.BD_Interno.DB_Interno
import br.com.shefa.coletaleite_shefa.Datas.Datas
import br.com.shefa.coletaleite_shefa.Gps.Gps
import br.com.shefa.coletaleite_shefa.Permissoes.Desbloquear
import br.com.shefa.coletaleite_shefa.Toast.ToastManager
import kotlinx.android.synthetic.main.activity_altera_dados.*
import kotlinx.android.synthetic.main.activity_coleta_extra.*

class ColetaExtra : AppCompatActivity() {
      var datas : Datas? = null
      var banco: DB_Interno? = null
      var id_produtor:String  = ""
      var seleciona:String = ""
      var salvou:String? = null
      var latitude:String? = null
      var longitude:String? = null
      var data_sistemahora:String? = null
      var linha:String? = null
      var id2:String= ""
      var rota:String= ""
      var subrota:String= ""
      var data:String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coleta_extra)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Voltar"

        banco = DB_Interno(this)//chama o banco
        datas = Datas()//chama as datas
        data_sistemahora =  datas!!.dataHora() //data e hora
        data             =  datas!!.data()
        linha = getIntent().getStringExtra("linha");// pegando a linha da tela Listar produtos

        //radio grupo
        radioGrupoExtra.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGrupo, checkedId ->
            val sim:Boolean = R.id.radioSim == checkedId
            val nao:Boolean = R.id.radioNao == checkedId
            if (sim) {
                seleciona = "SIM"
            } else if (nao) {
                seleciona = "NAO"
            }
        })// fim radio grupo extra


        btn_salvar_extra.setOnClickListener{
            gps()
            buscarDados()
            alterarContato()
            ToastManager.show(this@ColetaExtra, "SALVO COM SUCESSO", ToastManager.INFORMATION)
            //aqui um intent para voltar na tela
            val intentdados = Intent(this@ColetaExtra, ListarProdutores::class.java)
            startActivity(intentdados)
            finish()


        }//fim do botão salvar

    }//fim do oncreate



     fun buscarDados(){
         val db = openOrCreateDatabase("captacao.db", Context.MODE_PRIVATE, null)
         val QUERY = "SELECT * FROM  tabela_coleta  where  _dataColeta = '$data'    AND   _subRota = '$linha'     "
         val c = db.rawQuery(QUERY, null) as SQLiteCursor
         if (c.moveToFirst()) {
             id2             = c.getString(c.getColumnIndex("_idt"))
             rota            = c.getString(c.getColumnIndex("_rota"))
             subrota         = c.getString(c.getColumnIndex("_subRota"))
         }
         c.close()//fecha a conexão com o banco
         db.close()
     }


    fun alterarContato(){
        val db2 = openOrCreateDatabase("captacao.db", Context.MODE_PRIVATE, null)
        val tabelacoleta = "tabela_coleta"
        try {
        val  confirmaExtra  = "1"
        val  confirmaEnvio  = "0"
        val date = datas!!.data()//data do sistema
        val codigoProdutor = "F" + editCodigoProdutorExtra.text.toString()
        val nomeProdutor = "COLETA EXTRA"
        val qtd = edit_qtd_litros_extra.text.toString()
        val tempe = edit_temperatura_extra.text.toString()
        val alisa = seleciona
        val boca  = edit_boca_extra.text.toString()
        val ped   = edit_pedagio_extra.text.toString()
        val obs   = edit_obs_extra.text.toString()
        val lati  = latitude
        val long  = longitude

        val ctv = ContentValues()
        ctv.put("_codProdutor", codigoProdutor)
        ctv.put("_nomeProdutor", nomeProdutor)
        ctv.put("_dataColeta", date)
        ctv.put("_qtd", qtd)
        ctv.put("_temperatura", tempe)
        ctv.put("_alisarol", alisa)
        ctv.put("_obs", obs)
        ctv.put("_latitudeLocal", lati)//insere latitude
        ctv.put("_longitudeLocal", long)//insere longitude
        ctv.put("_dataHora", data_sistemahora)
        ctv.put("_salvou",  confirmaExtra)
        ctv.put("_idt", id2)
        ctv.put("_rota", rota)
        ctv.put("_subRota", subrota)
        ctv.put("_confirmaEnvio", confirmaEnvio)
        ctv.put("_boca", boca)
        ctv.put("_pedagio",ped)
            db2.insert(tabelacoleta, null, ctv)
            db2.close()
        } catch (e: Exception) {
            e.printStackTrace();
        }

    }//FIM DA FUNÇÃO alterarContato




    //função para buscar a latitude do produtor
    private fun gps(){
        val gps   = Gps(this) //inicia a classe do gps
        latitude  =  gps.posicaolatitude()
        longitude =  gps.posicaolongitude()

        if(latitude==null) {
            ToastManager.show(this@ColetaExtra, "SEM SINAL DE GPS", ToastManager.INFORMATION)
        }
    }//fim do gps




    //menu voltar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            val intentdados = Intent(this@ColetaExtra, ListarProdutores::class.java)
            startActivity(intentdados)
            finish()
            return true
        }



        return super.onOptionsItemSelected(item)
    }//fim do menu voltar
}//fim da classe
