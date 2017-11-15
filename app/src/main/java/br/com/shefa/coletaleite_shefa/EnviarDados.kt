package br.com.shefa.coletaleite_shefa

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_enviar_dados.*
import java.text.SimpleDateFormat
import java.util.*

class EnviarDados : AppCompatActivity() {
    var data:String?=null
    var progress: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enviar_dados)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Voltar"


        btn_enviar_dados2.setOnClickListener{
            enviardados()
            text_soma_litros.setText("litros")

        }//fim do botao enviar dados2



    }// fim do oncreate

    private fun enviardados() {
        data=   data()
        progress = ProgressDialog(this);
        progress!!.setMessage("Enviando por favor aguarde...")
        progress!!.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress!!.show();//inicio progress
        
    }


    fun data():String{
        //DATA E HORA DO SISTEMA
        val date = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
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













}//fim da Activity
