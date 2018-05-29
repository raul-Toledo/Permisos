package com.example.sir_c.permisos;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.support.annotation.*;


public class MainActivity extends Activity {

    //Componentes que emplearemos para la dirección web y num telefonico.
    ImageButton ibtnPhone, ibtnUrl;
    EditText edtPhone, edtUrl;

    //Constante que almacena la "clave" del permiso solicitado
    private final static int PHONE_CALL_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Enlazamos los elementos en Java con el XML
        ibtnPhone = (ImageButton) findViewById(R.id.ibtnPhone);
        ibtnUrl = (ImageButton) findViewById(R.id.ibtnUrl);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        edtUrl = (EditText) findViewById(R.id.edtUrl);

        //Evento del botón para llamar
        ibtnPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Obtenemos el número de teléfono
                String strPhone = edtPhone.getText().toString();

                //Creamos un arreglo de tipo string donde vamos a guardar
                //los permisos que requerimos
                String[] arrPermissions = {Manifest.permission.CALL_PHONE};

                //Si strPhone NO esta vacio marcamos
                if (!strPhone.isEmpty()) {
                    //revisamos la version del dispositivo para determinar
                    //que tipo de verificación de permisos requerimos
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        //Versiones superiores a Marshmellow emplean RunTime
                        requestPermissions(arrPermissions, PHONE_CALL_CODE);
                    } else {
                        //Llamamos a un método definido por nosotros para
                        //verificar los permisos
                        OlderVersion(strPhone);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "El campo esta vacio", Toast.LENGTH_SHORT).show();
                }
            }

            //Método propio para revizar los permisos en versiones anteriores
            //a Marshmellow
            private void OlderVersion(String strPhone) {
                //Creamos un Intent explicito donde realizaremos la llamada
                Intent intent = new
                        Intent(Intent.ACTION_CALL, Uri.parse("tel:" + strPhone));
                //Llamamos a un procedimiento personalizado en donde checamos
                //si ya se le concedieron permisos. al ser una versión antigua
                //los permisos se conceden al momento de instalar la App
                if (CheckPermissions(Manifest.permission.CALL_PHONE)) {
                    //Al estar programando en una versión reciente de android studio 3.2.1
                    //forzosamente debemos realizar una validación de permisos
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE)
                           != PackageManager.PERMISSION_GRANTED) return;

                    startActivity(intent);
                } else {
                    //Si no concedimos permisos mostramos un mensaje
                    Toast.makeText(MainActivity.this, "No tienes permisos", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Método para abrir en algúna App la dirección web proporcionada
        ibtnUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Leemos la dirección web
                String strUrl = edtUrl.getText().toString();
                //Si la dirección web NO esta vacia
                if (!strUrl.isEmpty()) {
                   //Creamos un Intent tipo Action_View para abrir la  dirección web
                   Intent intent = new Intent
                           (Intent.ACTION_VIEW, Uri.parse("http://"+strUrl));
                   startActivity(intent);
                }
            }
        });
    }

    //Al solicitar permisos de manera asincrona (y por lo tanto no sabemos cuando, ni cuanto tiempo tardara el usuario
    //en notificar si concede o rechaza los permisos requerimos emplear el siguiente método
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*Parametros del método onRequestPermssionsResult
        * int requestCode - Clave del permiso que se quiere saber
        * String[] permissions - Arreglo con la totalidad de los permisos solicitados
        * int[] grantResults - Arreglo con la totalidad de los permisos concedidos o negados*/

        //Con el switch evaluaremos de acuerdo al permiso solicitado la acción a realizar
        switch (requestCode) {
            //Llamada de teléfono
            case PHONE_CALL_CODE:
                //Verificacmos si el permiso Solicitado coincide con el permiso deseado
                if (permissions[0].equals(Manifest.permission.CALL_PHONE)) {
                    //Verificamos si el permiso fue Concedido o denegado
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(Intent.ACTION_CALL,
                                Uri.parse("tel:" + edtPhone.getText().toString()));
                        //Al estar programando en una versión reciente de android studio 3.2.1
                        //forzosamente debemos realizar una validación de permisos
                        if (ActivityCompat.checkSelfPermission(this,
                                Manifest.permission.CALL_PHONE) !=
                                PackageManager.PERMISSION_GRANTED) return;
                        startActivity(intent);
                    } else {
                        //Notificamos que no se concedio el permiso
                        Toast.makeText(MainActivity.this,
                                "Denegaste el permiso",
                                Toast.LENGTH_SHORT).show();
                    }
                }
                break;

                default:
                    super.onRequestPermissionsResult
                            (requestCode, permissions, grantResults);
                    break;
        }
    }

    //Método personalizado en el que verificamos que permisos fueron concedidos
    private boolean CheckPermissions(String permission){
        int result = this.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }
}
