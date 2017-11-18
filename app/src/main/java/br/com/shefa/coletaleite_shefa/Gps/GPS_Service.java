package br.com.shefa.coletaleite_shefa.Gps;

import android.Manifest;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.com.shefa.coletaleite_shefa.BD_Interno.DB_Interno;
import br.com.shefa.coletaleite_shefa.Objetos.ObjetosPojo;
import br.com.shefa.coletaleite_shefa.Toast.ToastManager;

/**
 * Created by aassis on 13/11/2017.
 */

public class GPS_Service extends Service {
    private LocationListener listener;
    private LocationManager locationManager;
    String data_sistema;
    String data_sistemahr;
    DB_Interno banco;
    String idt;
    ArrayList<ObjetosPojo> coletaArrayList;
    double latitude2 = 0;
    double longitude2 = 0;
    String lat2;
    String long2;
    double latAnte = 0.0;
    double LongAnte = 0.0;
    double distancia = 0.0;
    double distanciaFinal = 0.0;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude2 = location.getLatitude();
                longitude2 = location.getLongitude();
                lat2 = String.valueOf(latitude2);
                long2 = String.valueOf(longitude2);

                distancia = calculaDistanciaEmKM(latAnte, LongAnte, latitude2, longitude2);//novo
                banco(distancia);//chama o metodo do banco de dados para gravar
                latAnte = latitude2;//guarda a ultima latitude
                LongAnte = longitude2;//guarda a ultima longitude

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        //recebendo  a data  do sistema TABLET
        SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
        final Date data = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        Date data_atual = cal.getTime();
        String data_sistema2 = date.format(data_atual);
        data_sistema = data_sistema2;
        banco = new DB_Interno(this);

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationManager!=null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, listener);  // A CADA 0 SEGUNDOS E 0 MTS ATUALIZA
        }else{
            ToastManager.show(this, "SEM SINAL DE GPS", ToastManager.INFORMATION);
        }

    }//fim do oncreate


    public void banco(Double distancia) {//metodo para inserir no banco de dados tabela 2
        SQLiteDatabase db = openOrCreateDatabase("captacao.db", Context.MODE_PRIVATE, null);
        // recebendo  a data e hora do sistema
        SimpleDateFormat datehr = new SimpleDateFormat("dd-MM-yyyy");// dia mes ano - hora minuto segundo
        final Date datahr = new Date();
        Calendar calhr = Calendar.getInstance();
        calhr.setTime(datahr);
        Date data_atualhr = calhr.getTime();
        String data_hora = datehr.format(data_atualhr);
        data_sistemahr = data_hora;
        Double distKM = distancia; // aqui novo
        distanciaFinal = distanciaFinal + distKM;

        // formatar numero com duas casas decimais apos virgula
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        DecimalFormat df = (DecimalFormat) nf;
        df.applyPattern("#0.###");
        String ss = df.format(distanciaFinal);
        distanciaFinal = Double.valueOf(ss);
        distKM = 0.0;

       try {
           String qtddkm = String.valueOf(distanciaFinal);
           String updtade1 = "UPDATE  somakm  SET  _qtdkm = ' " + qtddkm + "'  WHERE  _datakm  = '" + data_sistema + "'";
           db.execSQL(updtade1);
           db.close();

       }catch(Exception e){

           Toast.makeText(GPS_Service.this, "ERRO AO SALVAR KM :", Toast.LENGTH_SHORT).show();
       }


        Intent i = new Intent("location_update");
        i.putExtra("distancia", distanciaFinal); //valor km
        sendBroadcast(i);
    }

    //metodo para calcular o km CORE DO ANDROID
    public double calculaDistanciaEmKM(double lat1, double lon1, double lat2, double lon2) {

        if (lat1 != 0.0) {
            final Location start = new Location("Start Point");
            start.setLatitude(lat1);
            start.setLongitude(lon1);
            final Location finish = new Location("Finish Point");
            finish.setLatitude(lat2);
            finish.setLongitude(lon2);
            final float distance = start.distanceTo(finish);
            return distance / 1000;
        }
        else{
            return 0.0;
        }
    }


    //  parar a atualização do gps
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(listener);
            Toast.makeText(GPS_Service.this, "GPS REMOVIDO :", Toast.LENGTH_SHORT).show();
        }
    }







































}//fim da classe GPS SERVICES
