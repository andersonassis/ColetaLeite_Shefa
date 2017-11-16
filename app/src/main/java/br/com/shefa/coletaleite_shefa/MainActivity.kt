package br.com.shefa.coletaleite_shefa

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.TextView
import br.com.shefa.coletaleite_shefa.BD_Interno.DB_Interno
import br.com.shefa.coletaleite_shefa.Conexao.TestarConexao
import br.com.shefa.coletaleite_shefa.Gps.GPS_Service
import br.com.shefa.coletaleite_shefa.Gps.Gps
import br.com.shefa.coletaleite_shefa.Objetos.ObjetosPojo
import br.com.shefa.coletaleite_shefa.Permissoes.PermissionUtils
import br.com.shefa.coletaleite_shefa.R.id.btn_coleta_extra
import br.com.shefa.coletaleite_shefa.R.id.kmtext
import br.com.shefa.coletaleite_shefa.Toast.ToastManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import java.util.ArrayList


class MainActivity : AppCompatActivity() {
    lateinit var requestQueue: RequestQueue
    lateinit var coletaArrayList: ArrayList<ObjetosPojo>
    var conexao:Boolean = false
    var numeroImei:String = ""
    var telephonyManager: TelephonyManager? = null
    var progress: ProgressDialog? = null
    var banco: DB_Interno? = null
    var contando_registros:Int = 0
    var enviaDados:Int = 0
    var km: String? = null
    private var broadcastReceiver: BroadcastReceiver? = null


    override fun onResume() {//serve para mostrar o texto da outra classe
        super.onResume()
        if (broadcastReceiver == null) {
            broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    //  ToastManager.show(getApplicationContext(), "Atualizando GPS: "  +intent.getExtras().get("coordinates"), ToastManager.INFORMATION);
                    km = intent.extras!!.get("distancia").toString()  //dia 02/01/2017
                    kmtext.setText("Distancia KM: " + km)  //dia 02/01/2017

                }
            }
        }
        registerReceiver(broadcastReceiver, IntentFilter("location_update"))
    }
    override fun onDestroy() {
        super.onDestroy()
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.title = "COLETA LEITE SHEFA"
        banco = DB_Interno(this)//chama o banco
        val gps   = Gps(this) //inicia a classe do gps
        val alert = AlertDialog.Builder(this)

        // Solicita as permissoes gps,imei
        val permissoes = arrayOf(Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET)
        PermissionUtils.validate(this, 0, *permissoes)

        //click botao baixar linhas
        btn_baixar_linhas.setOnClickListener {
            conexao = TestarConexao().verificaConexao(this)
            if (conexao) {
                numeroImei = imei()
                importaLinhas(numeroImei)
            } else {
                ToastManager.show(this@MainActivity, "SEM CONEXÃO COM INTERNET, VERIFIQUE", ToastManager.INFORMATION)
            }
        }//fim botao baixar linhas

        //click botao exibir linhas vai para a tela listar produtores
        btn_exibir_linhas.setOnClickListener{
            numeroImei = imei()
            val intent = Intent(this@MainActivity, ListarProdutores::class.java)
            intent.putExtra("imei",numeroImei)
            startActivity(intent)
        }//fim botao exibir linhas


        //click botão enviar os dados
        btn_enviar_dados.setOnClickListener{
            conexao = TestarConexao().verificaConexao(this)
            enviaDados = banco!!.enviarDados() //verificar quantos registros tem salvo pra ser enviado
            if (conexao) {
                if (enviaDados >0) {
                    alert.setTitle("ATENÇÃO !!!")
                    alert.setMessage("DESEJA ENVIAR OS DADOS ?")
                    alert.setPositiveButton("ENVIAR", DialogInterface.OnClickListener { dialog, whichButton ->
                        //intent para parar o serviço gps
                        val intent = Intent(this@MainActivity, GPS_Service::class.java)
                        stopService(intent)
                        //intent para chamar a tela enviar dados
                        val intentdados = Intent(this@MainActivity, EnviarDados::class.java)
                        startActivity(intentdados)
                    })
                    alert.setNegativeButton("CANCELAR") { dialog, which -> }
                    alert.show()
                }else{
                    ToastManager.show(this@MainActivity, "NÃO EXISTE DADOS A SEREM ENVIADOS", ToastManager.INFORMATION)
                }

            }else{
                ToastManager.show(this@MainActivity, "SEM CONEXÃO COM INTERNET, VERIFIQUE", ToastManager.INFORMATION)
            }
        }//FIM DO BOTAO ENVIAR OS DADOS


        //click botão coleta extra
        btn_coleta_extra.setOnClickListener{

        }


    }//fim do oncreate


    //subescreve o metodo para as permissoes
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (result in grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                // Alguma permissÃ£o foi negada, agora Ã© com vocÃª :-)
                alertAndFinish()
                return
            }
        }
    }//FIM onRequestPermissionsResult

    //entra aqui se o usuario não conceder alguma permissão
    private fun alertAndFinish() {
        run {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(R.string.app_name).setMessage("Para utilizar este aplicativo, voce precisa aceitar as permissoes.")
            // Add the buttons
            builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id -> finish() })
            val dialog = builder.create()
            dialog.show()
        }
    }//FIM alertAndFinish


    //funçao importar as linhas
    private fun importaLinhas(imei: String) {
        progress = ProgressDialog(this);
        progress!!.setMessage("Baixando as linhas por favor aguarde");
        progress!!.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress!!.show();//inicio progress
        requestQueue = Volley.newRequestQueue(this)//inicio volley
        val url = "http://www.shefa-comercial.com.br:8080/coleta/ArquivoEnvio/$imei/$imei.txt"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url,
                Response.Listener { response ->
                    try {
                        coletaArrayList = ArrayList<ObjetosPojo>()
                        val jsonArray = response.getJSONArray("rotas")
                        for (i in 0 until jsonArray.length()) {
                            val rotas = jsonArray.getJSONObject(i)
                            val idJson = rotas.getString("sid")
                            val idt = rotas.getString("id")
                            val dataColetaJson = rotas.getString("datacoleta")
                            val rotaJson = rotas.getString("rota")
                            val subRotaJson = rotas.getString("subrota")
                            val codTransportadoraJson = rotas.getString("codTransportadora")
                            val codProdutorJson = rotas.getString("codProdutor")
                            val nomeProdutorJson = rotas.getString("nomeProdutor")
                            val enderecoProdutorJson = rotas.getString("enderecoProdutor")
                            val cidadeJson = rotas.getString("cidade")
                            val qtdJson = rotas.getString("qtd")
                            val imeiJson = rotas.getString("imei")
                            val temperaturaJson = rotas.getString("temperatura")
                            val latitudeJson = rotas.getString( "latitude")
                            val longitudeJson = rotas.getString("longitude")
                            val alisarolJson = rotas.getString("alisarol")
                            val obsJson = rotas.getString("obs")
                            val latitudeLocalJson = rotas.getString("latitudeLocal")
                            val longitudeLocalJson = rotas.getString("longitudeLocal")
                            var origemLatJosn = rotas.getString("origemlat")
                            var origemLongJson = rotas.getString("origemlog")
                            val datahoraJson = rotas.getString("datahora")

                            val coleta = ObjetosPojo()
                            coleta.id = idJson.toInt()
                            coleta.id2 = idt
                            coleta.dataColeta = dataColetaJson
                            coleta.rota = rotaJson
                            coleta.subRota = subRotaJson
                            coleta.codTransportadora = codTransportadoraJson
                            coleta.codProdutor = codProdutorJson
                            coleta.nomeProdutor = nomeProdutorJson
                            coleta.enderecoProdutor = enderecoProdutorJson
                            coleta.cidade = cidadeJson
                            coleta.quantidade = qtdJson
                            coleta.imei   = imeiJson
                            coleta.temperatiura   = temperaturaJson
                            coleta.alisarol   = alisarolJson
                            coleta.boca       = ""
                            coleta.latitude = latitudeJson
                            coleta.longitude = longitudeJson
                            coleta.obs       = obsJson
                            coleta.datahora = datahoraJson
                            coleta.salvou   = "0"
                            coleta.pedagio  = ""
                            coleta.confirmaEnvio = "0"
                            coleta.respostaServidor = ""

                            //aqui vai salvar no banco
                            //verificar se o arquivo ja foi importado
                            contando_registros = banco!!.contandoregistros(idJson)
                            if (contando_registros == 0) {
                                banco!!.addColeta(coleta)//inserindo no banco de dados
                            } else {
                                progress!!.dismiss();//encerra progress
                                ToastManager.show(this@MainActivity, "ATENÇÃO!!! \n ARQUIVO JA IMPORTADO", ToastManager.WARNING)
                                break
                            }

                        }//fim do for
                        progress!!.dismiss();//encerra progress
                    } catch (e: JSONException) {
                        // ToastManager.show(this@MainActivity, "Falha no arquivo,favor entrar em contato com a TI", ToastManager.ERROR)
                        progress!!.dismiss();//encerra progress
                        e.printStackTrace()
                    }
                },
                Response.ErrorListener {
                    Log.e("Falha", "ERRO")
                    ToastManager.show(this@MainActivity, "Falha na conexão ou arquivo não existe,por favor tentar Novamente", ToastManager.ERROR)
                    progress!!.dismiss();//encerra progress
                }
        ) //fim do volley
        requestQueue.add(jsonObjectRequest)
    }// fim funçao importar as linhas




    //função para pegar  IMEI
    @SuppressLint("MissingPermission")
    fun imei():String{
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val deviceId = telephonyManager!!.getDeviceId()
        return  deviceId
    }//fim da função pegar IMEI


    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("ATENÇÃO!!")
        alert.setMessage("DESEJA SAIR DO APLICATIVO ?" )
        alert.setPositiveButton("SAIR", DialogInterface.OnClickListener { dialog, whichButton ->
            super.onBackPressed()
            finish();
            System.exit(0);
        })
        alert.setNegativeButton("CANCELAR") { dialog, which ->  }
        alert.show()

    }




}//fim da classe
