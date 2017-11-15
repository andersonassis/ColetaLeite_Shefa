package br.com.shefa.coletaleite_shefa

import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.RadioGroup
import br.com.shefa.coletaleite_shefa.BD_Interno.DB_Interno
import br.com.shefa.coletaleite_shefa.R.id.radioGrupo
import kotlinx.android.synthetic.main.activity_altera_dados.*
import java.text.SimpleDateFormat
import java.util.*

class AlteraDados : AppCompatActivity() {
    var db: SQLiteDatabase? = null
    var banco: DB_Interno? = null
    var data_sistemahora:String? = null
    var id_produtor:String? = null
    var linha:String? = null
    var seleciona:String = ""
    var salvou:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_altera_dados)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Voltar"
        banco = DB_Interno(this)//chama o banco

        id_produtor =  getIntent().getStringExtra("id_Produtor");
        linha       =  getIntent().getStringExtra("linha");
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


        btn_salvar.setOnClickListener{
           salvou = banco!!.foiSalvo(id_produtor)

        }




    }//fim do oncreate

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
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }



}
