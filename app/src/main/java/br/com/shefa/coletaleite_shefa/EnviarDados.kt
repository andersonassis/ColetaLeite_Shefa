package br.com.shefa.coletaleite_shefa

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import br.com.shefa.coletaleite_shefa.BD_Interno.DB_Interno
import br.com.shefa.coletaleite_shefa.Conexao.TestarConexao
import br.com.shefa.coletaleite_shefa.ConverteJson.ConverteJson
import br.com.shefa.coletaleite_shefa.ConverteJson.ConverteJsonKM
import br.com.shefa.coletaleite_shefa.Datas.Datas
import br.com.shefa.coletaleite_shefa.Toast.ToastManager
import com.android.volley.DefaultRetryPolicy
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
    lateinit var requestQueueKm: RequestQueue
    var banco: DB_Interno? = null
    var datas: Datas? = null
    var progress: ProgressDialog? = null
    var conexao:Boolean = false
    var qtd_litros:Double  = 0.0
    var jsonEnvia:String = ""
    var jsonEnviaKM:String = ""
    var data:String = ""
    var envioKm:Int = 0
    var data_deletar:String = ""
    var dataColetaDia:String = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enviar_dados)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Voltar"
        banco = DB_Interno(this)//chama o banco
        datas = Datas()//chama a classe datas
        data = datas!!.data()
        data_deletar = datas!!.dataMenosUm()
        dataColetaDia = banco!!.dataColetaDia()
        val alert = AlertDialog.Builder(this)



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


        //botão enviar dados
        btn_enviar_dados2.setOnClickListener{
            conexao = TestarConexao().verificaConexao(this)
            if (conexao) {
                if (dataColetaDia.equals(data)){
                    val resposta = banco!!.retornoServ(data)
                    if(resposta == 0 && qtd_litros>0 )  { //ajuste aqui paa enviar apenas qtd maior que zero   dia 14/02/2018
                        enviardados()
                    }else{
                        ToastManager.show(this@EnviarDados, "ARQUIVOS NÃO PODE SER ENVIADO", ToastManager.INFORMATION)
                    }

                }else{
                    alert.setTitle("ATENÇÃO !!!")
                    alert.setMessage("A DATA DA COLETA NÃO É DO MESMO DIA E NÃO PODE SER ENVIADA")
                    alert.setNegativeButton("CANCELAR") { dialog, which -> }
                    alert.show()
                }

            } else {
                ToastManager.show(this@EnviarDados, "SEM CONEXÃO COM INTERNET, VERIFIQUE", ToastManager.INFORMATION)
            }
        }//fim do botao enviar dados


       /* btn_envia_km.setOnClickListener{
            conexao = TestarConexao().verificaConexao(this)
            envioKm = banco!!.verificaKM()
            if (conexao) {
                if (envioKm >0 && qtd_litros>0 ) {//ajuste aqui paa enviar apenas qtd maior que zero   dia 14/02/2018
                    envioKM()
                }else{
                    ToastManager.show(this@EnviarDados, "NÃO EXISTE ARQUIVO DE KM A SER ENVIADO", ToastManager.INFORMATION)
                }
            } else {
                ToastManager.show(this@EnviarDados, "SEM CONEXÃO COM INTERNET, VERIFIQUE", ToastManager.INFORMATION)
            }
        }//fim do botao enviar dados   */


    }// fim do oncreate

    //FUNÇÃO PARA ENVIAR OS DADOS (TABELA COLETA)
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
                        try {
                            Thread.sleep(3000)
                            alterarContato()
                            envio()
                            banco!!.deletar(data_deletar)//deleta o arquivo do dia anterior a do sistema
                            progress!!.dismiss()
                            ToastManager.show(this@EnviarDados, " ENVIADO COM SUCESSO", ToastManager.INFORMATION)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                            progress!!.dismiss()
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
        val socketTimeout = 50000
        val policy = DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        postRequest.retryPolicy = policy
        requestQueue.add(postRequest)
        banco!!.close()

    }//FIM enviar dados



    //FUNÇÃO PARA O ENVIO DO KM(TABELA DE KM)
   private fun envioKM(){
        progress = ProgressDialog(this);
        progress!!.setMessage("Enviando por favor aguarde...")
        progress!!.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress!!.show();//inicio progress
        val coletaKM = banco!!.enviaKM(data)
        var jsonkm = ConverteJsonKM().toJson(coletaKM)
        jsonEnviaKM = jsonkm.toString()
        requestQueueKm = Volley.newRequestQueue(this)
       // val urlkm = "http://www.shefa-comercial.com.br:8080/coleta/ArquivoGPS/gps.php"
        val urlkm = "http://www.shefa-comercial.com.br:8080/coleta/ArquivoGPS/gps.php"
        val postRequest = object : StringRequest(Request.Method.POST, urlkm,
                Response.Listener { resposta ->
                        try {
                            Thread.sleep(3000)
                            progress!!.dismiss()
                            deletarKM()
                            ToastManager.show(this@EnviarDados, " KM ENVIADO COM SUCESSO", ToastManager.INFORMATION)
                            val intentdados = Intent(this@EnviarDados, MainActivity::class.java)
                            startActivity(intentdados)
                            finish()
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                            progress!!.dismiss()
                        }
                },
                Response.ErrorListener { error ->
                    progress!!.dismiss()
                    ToastManager.show(this@EnviarDados, "ATENÇÃO !!! \n FALHA NO ENVIO,POR FAVOR TENTAR NOVAMENTE ", ToastManager.INFORMATION)
                    error.printStackTrace()
                }
        )

        {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                // the POST parameters:
                params.put("site", jsonEnviaKM)
                return params
            }
        }

        val socketTimeout = 50000
        val policy = DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        postRequest.retryPolicy = policy
        requestQueueKm.add(postRequest)
        banco!!.close()

    }


    //FUNÇÃO DO MENU VOLTAR
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    //altera os dados com status
    protected fun alterarContato() {
        val db = openOrCreateDatabase("captacao.db", Context.MODE_PRIVATE, null)
        val  confirmaEnvio = "1"
        val ctv = ContentValues()
        ctv.put("_salvou", confirmaEnvio)
        val updtade1 = "UPDATE  tabela_coleta  SET   _confirmaEnvio = ' $confirmaEnvio' WHERE  _dataColeta = '$data'"
        db.execSQL(updtade1)
        db.close()
    }


    //função para acrescentar "s" de enviado para não enviar duas vezes
    fun envio(){
       banco!!.resposta(data)
    }

    fun  deletarKM(){
        banco!!.deletarKM()
    }


}//fim da Activity
