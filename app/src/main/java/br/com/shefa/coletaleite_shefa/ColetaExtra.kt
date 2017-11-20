package br.com.shefa.coletaleite_shefa

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import br.com.shefa.coletaleite_shefa.BD_Interno.DB_Interno
import br.com.shefa.coletaleite_shefa.Datas.Datas

class ColetaExtra : AppCompatActivity() {
      var datas : Datas? = null
      var banco: DB_Interno? = null
      var id_produtor:String  = ""
      var seleciona:String = ""
      var salvou:String? = null
      var latitude:String? = null
      var longitude:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coleta_extra)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Voltar"


    }//fim do oncreate






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
