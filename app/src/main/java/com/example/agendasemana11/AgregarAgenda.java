package com.example.agendasemana11;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.nfc.tech.TagTechnology;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

public class AgregarAgenda extends AppCompatActivity {
    String accion ="nuevo";
    String id="";
    String rev="";
    JSONObject datosJSON;
    String resp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_agenda);

        Bundle parametros = getIntent().getExtras();
        accion=parametros.getString("accion");

        if (accion.equals("modificar")){
            try {
                datosJSON = new JSONObject(parametros.getString("valores"));

                TextView temp = (TextView) findViewById(R.id.txtcodigo);
                temp.setText(datosJSON.getString("codigo"));

                temp = (TextView) findViewById(R.id.txtnombre);
                temp.setText(datosJSON.getString("nombre"));

                temp = (TextView) findViewById(R.id.txtDireccion);
                temp.setText(datosJSON.getString("direccion"));

                temp = (TextView) findViewById(R.id.txtTelefono);
                temp.setText(datosJSON.getString("telefono"));

                temp = (TextView) findViewById(R.id.txtDui);
                temp.setText(datosJSON.getString("dui"));

                id = datosJSON.getString("id");
                rev = datosJSON.getString("_rev");

            }catch (Exception ex){
                Toast.makeText(AgregarAgenda.this, "Error al recuperar datos: "+ex.getMessage().toString(),
                        Toast.LENGTH_LONG).show();

            }
        }
        Button btn= (Button)findViewById(R.id.btnGuardar);
        btn.setOnClickListener((v) -> {

            TextView temp = (TextView) findViewById(R.id.txtcodigo);
            String codigo = temp.getText().toString();

            temp = (TextView) findViewById(R.id.txtnombre);
            String nombre = temp.getText().toString();

            temp = (TextView) findViewById(R.id.txtDireccion);
            String direccion = temp.getText().toString();

            temp = (TextView) findViewById(R.id.txtTelefono);
            String telefono = temp.getText().toString();

            temp = (TextView) findViewById(R.id.txtDui);
            String dui = temp.getText().toString();

            JSONObject miData = new JSONObject();

            try {
                if (accion.equals("modificar")) {
                    miData.put("_id", id);
                    miData.put("_rev", rev);
                }
                miData.put("codigo", codigo);
                miData.put("nombre", nombre);
                miData.put("direccion", direccion);
                miData.put("telefono", telefono);
                miData.put("dui", dui);

                enviarDatos objEnviar = new enviarDatos();
                objEnviar.execute(miData.toString());

            } catch (Exception ex) {
                Toast.makeText(AgregarAgenda.this, "Error al guardar: " + ex.getMessage(),
                        Toast.LENGTH_LONG).show();
            }

        });
        FloatingActionButton btnRegresar=(FloatingActionButton) findViewById(R.id.btnRegresar);
        btnRegresar.setOnClickListener((v) -> {
            Intent regresar = new Intent(AgregarAgenda.this, MainActivity.class);
            startActivity(regresar);
        });

        }
        private class enviarDatos extends AsyncTask<String,String,String>{
        HttpURLConnection urlConnection;

        @Override
            protected String doInBackground(String...params){
            StringBuilder result=new StringBuilder();

            String JsonResponse=null;
            String JsonDATA=params[0];
            BufferedReader reader=null;

            try {
                URL url = new URL("http://10.0.2.2:5984/db_agenda/");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(JsonDATA);
                writer.close();

                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                resp = reader.toString();
                String inputLine;
                StringBuffer buffer = new StringBuffer();
                while ((inputLine = reader.readLine()) != null)
                    buffer.append(inputLine + "\n");

                if (buffer.length() == 0) {
                    return null;
                }
                JsonResponse = buffer.toString();

                Log.i(TAG,JsonResponse);

                return JsonResponse;

            }catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);

                try {
                    JSONObject jsonObject=new JSONObject(s);

                    if (jsonObject.getBoolean("ok")){
                    Toast.makeText(AgregarAgenda.this,"Registro almacenado con exito.",Toast.LENGTH_LONG).show();
                    Intent regresar=new Intent(AgregarAgenda.this,MainActivity.class);
                    startActivity(regresar);
                } else{
                    Toast.makeText(AgregarAgenda.this, "Error al intentar almacenar el registro", Toast.LENGTH_LONG).show();
                }
                }catch (Exception ex){
                    Toast.makeText(AgregarAgenda.this,"Error al enviar a la red: "+ ex.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        }

    }
