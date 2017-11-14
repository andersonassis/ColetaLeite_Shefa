package br.com.shefa.coletaleite_shefa.Interfaces

import br.com.shefa.coletaleite_shefa.Objetos.ObjetosPojo
import java.util.ArrayList

/**
 * Created by AndersonLuis on 11/11/2017.
 */
interface DadosInterface {
    fun addColeta(objetos: ObjetosPojo);
    fun getALLColeta(): ArrayList<ObjetosPojo>

    fun addTabelaKM(objetos: ObjetosPojo)
    fun getTabelaKM():ArrayList<ObjetosPojo>

}