package br.com.shefa.coletaleite_shefa

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SimpleCursorAdapter
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import br.com.shefa.coletaleite_shefa.BD_Interno.DB_Interno
import br.com.shefa.coletaleite_shefa.Gps.GPS_Service
import br.com.shefa.coletaleite_shefa.Objetos.ObjetosPojo
import br.com.shefa.coletaleite_shefa.Toast.ToastManager
import kotlinx.android.synthetic.main.activity_listar_produtores.*
import java.text.SimpleDateFormat
import java.util.*

class ListarProdutores : AppCompatActivity() {
    var db: SQLiteDatabase? = null
    internal lateinit var cursorSpinner: Cursor
    internal lateinit var cursor: Cursor
    var label3: String? = null
    var ad: SimpleCursorAdapter? = null
    internal var posicao: Int = 0
    var banco: DB_Interno? = null
    var imei:String? = null
    var data_sistemaListar:String? = null
    var inicio:Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_produtores)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Voltar"
        banco = DB_Interno(this)//chama o banco
        val alert = AlertDialog.Builder(this)
        imei =   getIntent().getStringExtra("imei");
        data()
        inicio = banco!!.inicio()

        if (inicio >0){
            listView.setEnabled(false)
        }else{
            btn_inicio.setEnabled(false)
        }

        btn_inicio.setOnClickListener{
            alert.setTitle("ATENÇÃO !!!")
            alert.setMessage("DESEJA INICIAR A " + label3 + " ?" )
            alert.setPositiveButton("INICIAR", DialogInterface.OnClickListener { dialog, whichButton ->
                 val iniciar ="s"
                 alterarData(iniciar)//altera data se for de dias diferentes
                 updateLinha()
                 atualizandoGPS()
                 onRestart()
            })
            alert.setNegativeButton("CANCELAR") { dialog, which ->  }
            alert.show()

        }//FIM DO BOTÃO INICIAR


        ListagemSpinner()

    }//fim do oncreate

    //inicia o gps que pega passo a passo
    fun atualizandoGPS() {
        val intent = Intent(this@ListarProdutores, GPS_Service::class.java)
        startService(intent);
        ToastManager.show(this@ListarProdutores, "INICIO OK", ToastManager.INFORMATION)
    }


    //altera a data do registro se for clicado quando o arquivo é de outro dia
    fun alterarData(inici:String) {
        val db = openOrCreateDatabase("captacao.db", Context.MODE_PRIVATE, null)
        try {
            val data = data_sistemaListar
            val ctv = ContentValues()
            ctv.put("_dataColeta", data)
            val updtade2 = "UPDATE  tabela_coleta  SET   _clickinicio  = '$inici' "
            val updtade1 = "UPDATE  tabela_coleta  SET   _dataColeta   = '$data_sistemaListar'  WHERE   _confirmaEnvio  = '0' "
            db.execSQL(updtade1)
            db.execSQL(updtade2)
            db.close()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //update na linha e km
    fun updateLinha(){
        banco!!.updateLinhas(label3,data_sistemaListar)// aqui vai escoher apenas a linha faz um update
        //variaveis para o km
        val  idt   = banco!!.buscaIdt(data_sistemaListar)
        val rotakm = banco!!.buscarota(data_sistemaListar)

        val coletakm = ObjetosPojo()
        coletakm.idprimary = 1
        coletakm.idlinha = idt
        coletakm.datakm  = data_sistemaListar
        coletakm.rotaKM  = rotakm
        coletakm.subRotaKM = label3
        coletakm.imeiKM    = imei
        coletakm.qtdKM     = "0"
        banco!!.addTabelaKM(coletakm)//inserindo no banco de dados

    }

    //pegar a data do sistema
    fun data(){
        //recebendo  a data do sistema atual
        val date = SimpleDateFormat("dd-MM-yyyy")
        val data = Date()
        val cal = Calendar.getInstance()
        cal.time = data
        val data_atual = cal.time
        val data_sistema2 = date.format(data_atual)
        data_sistemaListar = data_sistema2
    }


    //metodo para buscar as linhas
    private fun ListagemSpinner() {
        val lables2: ArrayList<String> = subRotaLinhas() as ArrayList<String>
        val arraySpinner = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item)
        arraySpinner.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        arraySpinner.addAll(lables2)
        spinner.adapter = arraySpinner

        //pegando o valor clicado
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                label3 = parent.getItemAtPosition(position).toString()//valor clicado
                buscarProdutores()
                criarListagem()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }//fim do click do spinner
    }//fim do metodo ListagemSpinner

    private fun subRotaLinhas(): Any {
        val labels = ArrayList<String>()//para guardar as linhas em um array
        db = openOrCreateDatabase("captacao.db", Context.MODE_PRIVATE, null)

        try {
            cursorSpinner = db!!.rawQuery("SELECT _subRota  FROM  tabela_coleta  WHERE   _confirmaEnvio  = '0'    GROUP BY  _subRota  ", null);//SELECT PARA PEGAR
            if (cursorSpinner.moveToFirst()) {
                do {
                    try {
                        labels.add(cursorSpinner.getString(0))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                } while (cursorSpinner.moveToNext())
            }
            cursorSpinner.close()
            db!!.close()
        }catch (e: Exception){
            ToastManager.show(this@ListarProdutores, "FAVOR IMPORTAR AS LINHAS", ToastManager.INFORMATION)
        }
           if (labels.size <=0){
            ToastManager.show(this@ListarProdutores, "FAVOR IMPORTAR AS LINHAS", ToastManager.INFORMATION)
            }
        return labels
    }// fim subRotaLinhas

    //função buscar produtores
    fun buscarProdutores() {
        try {
            db = openOrCreateDatabase("captacao.db", Context.MODE_PRIVATE, null)
            cursor = db!!.rawQuery("SELECT * FROM  tabela_coleta  WHERE   _subRota = '$label3'  AND  _confirmaEnvio  = '0'  ORDER BY   _nomeProdutor ", null)//SELECT PARA PEGAR SOMENTE O QUE NÃO FOI ENVIADO e  A LINHA ESCOLHIDA PELO SPINNER
        } catch (e: Exception) {
            Toast.makeText(this@ListarProdutores,"ERROR", Toast.LENGTH_LONG).show()
        }
    }//fim buscarProdutores


    //função para criar a listagem no listview
    fun criarListagem() {
        val from = arrayOf("_id","_dataColeta", "_subRota", "_nomeProdutor", "_enderecoProdutor", "_salvou")
        val to = intArrayOf(R.id.txtId,R.id.txtdata,R.id.txtsuRota, R.id.txtNomeProdutor, R.id.txtendereco, R.id.star)
        try {
            ad = SimpleCursorAdapter(applicationContext, R.layout.itens_produtores, cursor, from, to, 0);
            ad!!.setViewBinder(CustomViewBinder())//chamando este adaptador para acrescentar o check caso o produtor ja foi salvo
        } catch (e: Exception) {
            e.printStackTrace()
        }

        //habilita o click no item da lista
        listView.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val sqlCursor = ad!!.getItem(position) as SQLiteCursor
            val idProdutor = sqlCursor.getString(sqlCursor.getColumnIndex("_id"))
            //chama a tela para inserir os dados
            val altera = Intent(applicationContext, AlteraDados::class.java)
            altera.putExtra("id_Produtor", idProdutor)
            startActivity(altera)
            finish()
        })
        listView.setAdapter(ad)//chama o adaptador que monta a lista
    }//fim criarListagem


    // coloca o check na lista se o produtor foi salvo
    inner class CustomViewBinder : android.widget.SimpleCursorAdapter.ViewBinder, SimpleCursorAdapter.ViewBinder {
        override fun setViewValue(view: View, cursor: Cursor, columnIndex: Int): Boolean {
            if (columnIndex == cursor.getColumnIndex("_salvou")) {  // obs: o campo  _salvou serve para verifica se o produtor foi preenchido e salvo
                posicao = cursor.position
                val sqlCursor = ad!!.getItem(posicao) as SQLiteCursor
                val gravou = sqlCursor.getString(sqlCursor.getColumnIndex("_salvou")) // obs: o campo  _salvo serve para verifica se o produtor foi preenchido e salvo
                if (gravou != "1") {
                    view.visibility = View.GONE//  esconde o check
                } else {
                    view.visibility = View.VISIBLE// MOSTRA o check
                }
                return true
            }
            return false
        }
    }//fim CustomViewBinder


    //menu voltar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }



    //metodo para refresh na tela
    override fun onRestart() {
        super.onRestart()
        val i = Intent(this@ListarProdutores, ListarProdutores::class.java)  //your class
        startActivity(i)
        finish()
    }




}//fim da Activity
