package br.com.shefa.coletaleite_shefa

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import br.com.shefa.coletaleite_shefa.BD_Interno.DB_Interno
import br.com.shefa.coletaleite_shefa.Conexao.TestarConexao
import br.com.shefa.coletaleite_shefa.ConverteJson.ConverteJson
import br.com.shefa.coletaleite_shefa.Toast.ToastManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_enviar_dados.*
import java.text.SimpleDateFormat
import java.util.*

class EnviarDados : AppCompatActivity() {
    lateinit var requestQueue: RequestQueue
    var banco: DB_Interno? = null
    var data:String?=null
    var progress: ProgressDialog? = null
    var conexao:Boolean = false
    var qtd_litros:Double  = 0.0
    var jsonEnvia:String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enviar_dados)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Voltar"
        banco = DB_Interno(this)//chama o banco
        data=   data()

        //somando a quantidade de litros
        qtd_litros = banco!!.qtdLitros(data)
        try {
            val numCon = Math.round(qtd_litros).toInt()
            val totalLitros = numCon.toString()
            text_soma_litros.setText(totalLitros +" - LITROS COLETADOS")
        } catch (e: Exception) {
            ToastManager.show(applicationContext, "ALGO ERRADO NA DIGITAÇÃO DOS LITROS,VERIFIQUE", ToastManager.WARNING)
            e.printStackTrace()
        }


        btn_enviar_dados2.setOnClickListener{
            conexao = TestarConexao().verificaConexao(this)
            if (conexao) {
                val resposta = banco!!.retornoServ()
                 if(resposta == 0) {
                     enviardados()
                 }else{
                     ToastManager.show(this@EnviarDados, "ARQUIVOS JA FORAM ENVIADOS", ToastManager.INFORMATION)
                 }

            } else {
                ToastManager.show(this@EnviarDados, "SEM CONEXÃO COM INTERNET, VERIFIQUE", ToastManager.INFORMATION)
            }
        }//fim do botao enviar dados2



    }// fim do oncreate

    private fun enviardados() {
        progress = ProgressDialog(this);
        progress!!.setMessage("Enviando por favor aguarde...")
        progress!!.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress!!.show();//inicio progress
        val coleta = banco!!.Envia(data)
        var json = ConverteJson().toJson(coleta)
        jsonEnvia = json.toString()

        //inicio envio com volley
        requestQueue = Volley.newRequestQueue(this)
        val url = "http://www.shefa-comercial.com.br:8080/coleta/ArquivoRecebimento/coleta.php"
        val postRequest = object : StringRequest(Request.Method.POST, url,
                Response.Listener { resposta ->
                    val site = ""
                    if (resposta.equals("Arquivo gerado com sucesso")) {
                        try {
                            Thread.sleep(3000)
                            val reposta = "3"
                            alterarContato(reposta)
                            progress!!.dismiss()
                            ToastManager.show(this@EnviarDados, " ENVIADO COM SUCESSO" + resposta, ToastManager.INFORMATION)
                            val intentdados = Intent(this@EnviarDados, MainActivity::class.java)
                            startActivity(intentdados)
                            finish()
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                            progress!!.dismiss()
                        }
                    } else {
                        progress!!.dismiss()
                        ToastManager.show(this@EnviarDados, "FALHA NA RESPOSTA DO SERVIDOR: " + resposta, ToastManager.INFORMATION)
                    }
                },
                Response.ErrorListener { error ->
                    progress!!.dismiss()
                    ToastManager.show(this@EnviarDados, "ATENÇÃO !!! \n FALHA NO ENVIO,POR FAVOR TENTAR NOVAMENTE ", ToastManager.INFORMATION)
                    error.printStackTrace()
                }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                // the POST parameters:
                params.put("site", jsonEnvia)
                return params
            }
        }
        requestQueue.add(postRequest)
        banco!!.close()

    }//FIM enviar dados



    //função DATA E HORA DO SISTEMA
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



    //menu voltar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    //altera os dados com status de salvou = 3
    protected fun alterarContato(resp:String) {
        val db = openOrCreateDatabase("captacao.db", Context.MODE_PRIVATE, null)
        val  confirmaEnvio = "1"
        val ctv = ContentValues()
        ctv.put("_salvou", confirmaEnvio)
        ctv.put("_respostaServ", resp)

        val updtade1 = "UPDATE  tabela_coleta  SET   _confirmaEnvio = ' $confirmaEnvio' WHERE  _dataColeta = '$data'"
        val updtade2 = "UPDATE  tabela_coleta  SET   _respostaServ = ' $resp'  WHERE  _dataColeta = '$data' "
        db.execSQL(updtade1)
        db.execSQL(updtade2)
        db.close()
    }

}//fim da Activity
