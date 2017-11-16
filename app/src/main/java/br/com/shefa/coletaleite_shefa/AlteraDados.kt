package br.com.shefa.coletaleite_shefa

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.widget.RadioGroup
import br.com.shefa.coletaleite_shefa.BD_Interno.DB_Interno
import br.com.shefa.coletaleite_shefa.Gps.GPS_Service
import br.com.shefa.coletaleite_shefa.Gps.Gps
import br.com.shefa.coletaleite_shefa.R.id.radioGrupo
import br.com.shefa.coletaleite_shefa.Toast.ToastManager
import kotlinx.android.synthetic.main.activity_altera_dados.*
import java.text.SimpleDateFormat
import java.util.*

class AlteraDados : AppCompatActivity() {
    var db: SQLiteDatabase? = null
    var banco: DB_Interno? = null
    var data_sistemahora:String? = null
    var id_produtor:String  = ""
    var seleciona:String = ""
    var salvou:String? = null
    var latitude:String? = null
    var longitude:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_altera_dados)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Voltar"
        banco = DB_Interno(this)//chama o banco
        id_produtor =  getIntent().getStringExtra("id_Produtor");
        buscarProdutor(id_produtor)
        data_sistemahora =  datahora()

        //radio grupo
        radioGrupo.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGrupo, checkedId ->
            val sim:Boolean = R.id.radioSim == checkedId
            val nao:Boolean = R.id.radioNao == checkedId
            if (sim) {
                seleciona = "SIM"
            } else if (nao) {
                seleciona = "NAO"
            }
        })// fim radio grupo


        //botão salvar
        btn_salvar.setOnClickListener{
            gps()
           salvou = banco!!.foiSalvo(id_produtor)
            if (salvou.equals("1")){
                val alerta = AlertDialog.Builder(this@AlteraDados)
                alerta.setTitle("Ops...")
                alerta.setMessage("Registro ja foi salvo,você deseja salvar novamente ?")
                alerta.setPositiveButton("SIM", DialogInterface.OnClickListener { dialog, whichButton ->
                   val res = alteraContato(id_produtor)
                   if (res>0){
                       ToastManager.show(this@AlteraDados, "SALVO COM SUCESSO", ToastManager.INFORMATION)
                       val intentdados = Intent(this@AlteraDados, ListarProdutores::class.java)
                       startActivity(intentdados)
                       finish()
                   }

                })
                alerta.setNegativeButton("NÃO") { dialog, which ->
                    val intentdados = Intent(this@AlteraDados, ListarProdutores::class.java)
                    startActivity(intentdados)
                    finish()
                }
                alerta.show()

            }else {
                val res:Int = alteraContato(id_produtor)
                if (res>0) {
                    ToastManager.show(this@AlteraDados, "SALVO COM SUCESSO", ToastManager.INFORMATION)
                    val intentdados = Intent(this@AlteraDados, ListarProdutores::class.java)
                    startActivity(intentdados)
                    finish()
                }else{
                    ToastManager.show(this@AlteraDados, "ERRO AO SALVAR", ToastManager.INFORMATION)
                }
            }

        }//fim do botão salvar


    }//fim do oncreate

     private fun  buscarProdutor(id_do_produtor:String){
         val db = openOrCreateDatabase("captacao.db", Context.MODE_PRIVATE, null)//abrindo conexão com banco
         val sql = "SELECT * FROM  tabela_coleta  where _id = ?"//select para pegar o produtor clicado de acordo com o ID
         val c = db.rawQuery(sql, arrayOf(id_do_produtor)) as SQLiteCursor
         if (c.moveToFirst()) {
             val nomeProdutor = c.getString(c.getColumnIndex("_nomeProdutor"))
             val qtd = c.getString(c.getColumnIndex("_qtd"))
             val temperatura = c.getString(c.getColumnIndex("_temperatura"))
             val alisarol = c.getString(c.getColumnIndex("_alisarol"))
             val obs = c.getString(c.getColumnIndex("_obs"))
             val boca2 = c.getString(c.getColumnIndex("_boca"))
             val peda  = c.getString(c.getColumnIndex("_pedagio"))

             txt_nomeProdutor.setText(nomeProdutor.toString())
             edit_qtd.setText(qtd.toString())
             edit_temperatura.setText(temperatura.toString())
             edit_obs.setText(obs.toString())
             edit_boca.setText(boca2.toString())
             edit_pedagio.setText(peda.toString())

             if (alisarol == "SIM") {
                 radioSim.setChecked(true)
             } else if (alisarol == "NAO") {
                 radioNao.setChecked(true)
             }
         }
         c.close()
         db.close()//fecha a conexão com o banco

     }//fim buscarProdutor


     private fun alteraContato(id: String):Int{
         val db = openOrCreateDatabase("captacao.db", Context.MODE_PRIVATE, null)//abrindo conexão com banco
         val confirma:String = "1"
         val nome_produtor   = txt_nomeProdutor.text.toString()
         val qtd             = edit_qtd.text.toString()
         val temp            = edit_temperatura.text.toString()
         val alis            = seleciona
         val boc             = edit_boca.text.toString()
         val ped             = edit_pedagio.text.toString()
         val obs             = edit_obs.text.toString()
         val datahora        = data_sistemahora

         val ctv = ContentValues()
         ctv.put("_salvou", confirma)
         ctv.put("_qtd", qtd)
         ctv.put("_temperatura", temp)
         ctv.put("_alisarol", alis)
         ctv.put("_boca", boc)
         ctv.put("_pedagio", ped)
         ctv.put("_obs", obs)
         ctv.put("_dataHora", datahora)

         if (!salvou.equals("1")) {
             ctv.put("_latitude", latitude)
             ctv.put("_longitude", longitude)
         }

         val res = db.update("tabela_coleta", ctv, "_id=?", arrayOf(id))
         db.close()
         return res
       }

      private fun gps(){
          val gps   = Gps(this) //inicia a classe do gps
          latitude  =  gps.posicaolatitude()
          longitude =  gps.posicaolongitude()

          if(latitude==null) {
              ToastManager.show(this@AlteraDados, "SEM SINAL DE GPS", ToastManager.INFORMATION)
          }
      }


    private fun datahora():String {
        //DATA E HORA DO SISTEMA
        val date = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val data = Date()
        val cal = Calendar.getInstance()
        cal.time = data
        val data_atual = cal.time
        val data_sistema2 = date.format(data_atual)
        val dataHora = data_sistema2
        return dataHora
    }


    //menu voltar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            val intentdados = Intent(this@AlteraDados, ListarProdutores::class.java)
            startActivity(intentdados)
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }//fim do menu voltar



}
