package br.com.shefa.coletaleite_shefa.BD_Interno;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import br.com.shefa.coletaleite_shefa.Interfaces.DadosInterface;
import br.com.shefa.coletaleite_shefa.Objetos.ObjetosPojo;

/**
 * Created by AndersonLuis on 12/11/2017.
 */

public class DB_Interno extends SQLiteOpenHelper implements DadosInterface {
    //Campos da tabela tabela_mapeamento
    private static final int DB_VERSION            = 1;
    private static final String DB_NAME            = "captacao.db";
    private static final String TABLE_NAME         = "tabela_coleta";
    private static final String ID                 = "_id";//id do ax e banco
    private static final String ID2                = "_id2";//id da coleta
    private static final String DATACOLETA         = "_dataColeta";
    private static final String ROTA               = "_rota";
    private static final String SUBROTA            = "_subRota";
    private static final String CODTRANSPORTADORA  = "_codTransportadora";
    private static final String COD_PRODUTOR       = "_codProdutor";
    private static final String NOME_PRODUTOR      = "_nomeProdutor";
    private static final String ENDERECO_PRODUTOR  = "_enderecoProdutor";
    private static final String CIDADE             = "_cidade";
    private static final String QTD                = "_qtd";
    private static final String IMEI               = "_imei";
    private static final String TEMPERATURA        = "_temperatura";
    private static final String ALISAROL           = "_alisarol";
    private static final String BOCA               = "_boca";
    private static final String LATITUDE           = "_latitude";
    private static final String LONGITUDE          = "_longitude";
    private static final String OBS                = "_obs";
    private static final String DATAHORA           = "_dataHora";
    private static final String SALVOU             = "_salvou";
    private static final String PEDAGIO             = "_pedagio";

    //VARIAVEIS DA TABELA DO KM
    private static final String TABLE_NAME_KM       = "somakm";
    private static final String IDKM                = "_idkm";//id
    private static final String DATAKM              = "_datakm";
    private static final String ROTAKM              = "_rotakm";
    private static final String SUBROTAKM           = "_subRotakm";
    private static final String IMEIKM              = "_imeikm";
    private static final String QTDKM               = "_qtdkm";



    //criando a tabela que vai conter os dados em geral
    String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + ID + " INTEGER PRIMARY KEY," + ID2 + " TEXT, " + DATACOLETA + " TEXT," + ROTA + " TEXT," + SUBROTA + " TEXT," + CODTRANSPORTADORA + " TEXT, " + COD_PRODUTOR + " TEXT," + NOME_PRODUTOR + " TEXT,"
            + ENDERECO_PRODUTOR + " TEXT," + CIDADE + " TEXT," + QTD +" TEXT, "+ IMEI + " TEXT," + TEMPERATURA +" TEXT, " + ALISAROL + " TEXT, " + BOCA + " TEXT, "  + LATITUDE + " REAL," + LONGITUDE + " REAL,"
            + OBS + " CHAR(150)," + DATAHORA + " TEXT," + SALVOU + " TEXT," + PEDAGIO + " TEXT  )";


    String CREATE_TABLEKM = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_KM + " (" + IDKM + " INTEGER PRIMARY KEY, " + DATAKM +" TEXT, " + ROTAKM +" TEXT, "  + SUBROTAKM + " TEXT, " + IMEIKM + " TEXT, " + QTDKM + " TEXT  )";


    String DROP_TABLE  = "DROP TABLE IF EXISTS " + TABLE_NAME;
    String DROP_TABLEKM  = "DROP TABLE IF EXISTS " + TABLE_NAME_KM;

    //construtor
    public DB_Interno(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLEKM);

        Log.e("criar",   "banco criado com sucesso");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        db.execSQL(DROP_TABLEKM);
        onCreate(db);
    }

    @Override
    public void addColeta(@NotNull ObjetosPojo objetos) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(ID, objetos.getId());
            values.put(ID2,objetos.getId2());
            values.put(DATACOLETA, objetos.getDataColeta());
            values.put(ROTA, objetos.getRota());
            values.put(SUBROTA, objetos.getSubRota());
            values.put(CODTRANSPORTADORA, objetos.getCodTransportadora());
            values.put(COD_PRODUTOR, objetos.getCodProdutor());
            values.put(NOME_PRODUTOR, objetos.getNomeProdutor());
            values.put(ENDERECO_PRODUTOR, objetos.getEnderecoProdutor());
            values.put(CIDADE, objetos.getCidade());
            values.put(QTD, objetos.getQuantidade());
            values.put(IMEI, objetos.getImei());
            values.put(TEMPERATURA, objetos.getTemperatiura());
            values.put(ALISAROL, objetos.getAlisarol());
            values.put(BOCA, objetos.getBoca());
            values.put(LATITUDE, objetos.getLatitude());
            values.put(LONGITUDE, objetos.getLongitude());
            values.put(OBS, objetos.getObs());
            values.put(DATAHORA, objetos.getDatahora());
            values.put(SALVOU,objetos.getSalvou());
            values.put(PEDAGIO,objetos.getPedagio());
            db.insert(TABLE_NAME, null, values);
            db.close();
        }catch (Exception e){
            e.printStackTrace();
            Log.e("Problema", e + "Problema ao gravar a tabela");
        }

    }// fim addColeta

    @NotNull
    @Override
    public ArrayList<ObjetosPojo> getALLColeta() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ObjetosPojo> objetos = null;
        try {
            objetos = new ArrayList<ObjetosPojo>();
            String QUERY = "SELECT * FROM " + TABLE_NAME + " WHERE  _salvou = '1'  ";
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {
                while (cursor.moveToNext()) {
                    ObjetosPojo coleta = new ObjetosPojo();
                    coleta.setId(cursor.getInt(0));
                    coleta.setId2(cursor.getString(1));
                    coleta.setDataColeta(cursor.getString(2));
                    coleta.setRota(cursor.getString(3));
                    coleta.setSubRota(cursor.getString(4));
                    coleta.setCodTransportadora(cursor.getString(5));
                    coleta.setCodProdutor(cursor.getString(6));
                    coleta.setNomeProdutor(cursor.getString(7));
                    coleta.setEnderecoProdutor(cursor.getString(8));
                    coleta.setCidade(cursor.getString(9));
                    coleta.setQuantidade(cursor.getString(10));
                    coleta.setImei(cursor.getString(11));
                    coleta.setTemperatiura(cursor.getString(12));
                    coleta.setAlisarol(cursor.getString(13));
                    coleta.setBoca(cursor.getString(14));
                    coleta.setLatitude(cursor.getString(15));
                    coleta.setLongitude(cursor.getString(16));
                    coleta.setObs(cursor.getString(17));
                    coleta.setDatahora(cursor.getString(18));
                    coleta.setSalvou(cursor.getString(19));
                    coleta.setPedagio(cursor.getString(20));
                    objetos.add(coleta);
                }
            }
            db.close();
        }catch (Exception e){
            e.printStackTrace();
            Log.e("Problemas", e + "Problema ao ler a tabela");
        }
        return objetos;
    }//FIM ArrayList<ObjetosPojo> getALLColeta()

    //funcao deletar
    public String deletar(){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            String QUERY = ("DELETE  FROM " + TABLE_NAME);
            db.execSQL(QUERY );
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return "";
    }//fim deletar


    // funçao para contar quantos registros tem no banco
    public int contandoregistros(String id){
        int num = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String QUERY = "SELECT * FROM " + TABLE_NAME + " WHERE  _id = " + id;
            Cursor cursor = db.rawQuery(QUERY, null);
            num = cursor.getCount();
            db.close();
            return num;
        } catch (Exception e) {
            Log.e("ERRO", e + "");
        }
        return 0;
    }//fim do contandoregistros


    //buscando o idt para começar a gravar a latitude longitude por minuto da tabela2
    public String buscaIdt(String data){
        String QUERY = "SELECT  _id  FROM  " + TABLE_NAME + "  WHERE   _dataColeta  = '" + data + "'  ";
        String num = "";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery(QUERY, null);
            cursor.moveToLast();
            num = cursor.getString(cursor.getColumnIndex("_id"));
            StringBuilder sb = new StringBuilder();
            sb.append(num);
            db.close();
            return num.toString();

        }catch (Exception e){
            Log.e("ERRO ", e + "");
        }
        return null;
    }

    //buscando o buscaLinha   para a tabela 2
    public String buscaLinha(String data){
        String QUERY = "SELECT  _subRota  FROM " + TABLE_NAME + " WHERE  _dataColeta = '" + data + "'  ";
        String linha = "";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery(QUERY, null);
            cursor.moveToLast();
            linha = cursor.getString(cursor.getColumnIndex("_subRota"));
            StringBuilder sb = new StringBuilder();
            sb.append(linha);
            db.close();
            return linha.toString();

        }catch (Exception e){
            Log.e("ERRO ", e + "");
        }
        return null;
    }



    //buscando o buscaLinha   para a tabela 2
    public String buscarota(String data){
        String QUERY = "SELECT  _rota   FROM " + TABLE_NAME + " WHERE   _dataColeta = '" + data + "'  ";
        String rota = "";
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            Cursor cursor = db.rawQuery(QUERY, null);
            cursor.moveToLast();
            rota = cursor.getString(cursor.getColumnIndex("_rota"));
            StringBuilder sb = new StringBuilder();
            sb.append(rota);
            db.close();
            return rota.toString();

        }catch (Exception e){
            Log.e("ERRO ", e + "");
        }
        return null;
    }


    //update na linhas
    public void updateLinhas(String Linha){
        SQLiteDatabase db = this.getReadableDatabase();
        String updtade2 = "UPDATE  tabela_coleta  SET   _salvou  = '2'   WHERE    _subRota <> '"+Linha+"'   ";
        try {
            SQLiteDatabase db2 = this.getWritableDatabase();
            String QUERY = (updtade2);
            db2.execSQL(QUERY );
            db2.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }







    //*******************aqui começa a tabela de km****************************

    //aqui para a tabela do KM adicionar
    @Override
    public void addTabelaKM(@NotNull ObjetosPojo objetos) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(IDKM, objetos.getIdlinha());
            values.put(DATAKM, objetos.getDatakm());
            values.put(ROTAKM, objetos.getRotaKM());
            values.put(SUBROTAKM, objetos.getSubRotaKM());
            values.put(IMEIKM, objetos.getImeiKM());
            values.put(QTDKM, objetos.getQuantidade());
            db.insert(TABLE_NAME_KM, null, values);

            db.close();
        }catch (Exception e){
            e.printStackTrace();
            Log.e("Problema", e + "Problema ao gravar a tabela");
        }

    }


    @NotNull
    @Override  //buscar
    public ArrayList<ObjetosPojo> getTabelaKM() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<ObjetosPojo> objetos = null;
        try {
            objetos = new ArrayList<ObjetosPojo>();
            String QUERY = "SELECT * FROM " + TABLE_NAME_KM ;
            Cursor cursor = db.rawQuery(QUERY, null);
            if (!cursor.isLast()) {
                while (cursor.moveToNext()) {
                    ObjetosPojo coleta = new ObjetosPojo();
                    coleta.setIdlinha(cursor.getString(0));
                    coleta.setDatakm(cursor.getString(1));
                    coleta.setSubRotaKM(cursor.getString(2));
                    coleta.setImeiKM(cursor.getString(3));
                    coleta.setQtdKM(cursor.getString(4));
                    objetos.add(coleta);
                }
            }
            db.close();
        }catch (Exception e){
            e.printStackTrace();
            Log.e("Problemas", e + "Problema ao ler a tabela");
        }
        return objetos;
    }

}//fim da classe
