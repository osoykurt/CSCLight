package ca.uqac.csclight;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    NfcAdapter nfcAdapter;
    EditText nom;
    EditText prenom;
    EditText tel;
    EditText mail;
    FileOutputStream fOut = null;

    private Button button;

    Contact c1;
    private static String[] PERMISSIONS_MODIF = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nom = (EditText) findViewById(R.id.edit_name);
        prenom = (EditText) findViewById(R.id.edit_surname);
        mail = (EditText) findViewById(R.id.edit_mail);
        tel = (EditText) findViewById(R.id.edit_tel);

        button = (Button) findViewById(R.id.button_valider);

        button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                isStoragePermissionGranted();
                Switch simpleSwitch = (Switch) findViewById(R.id.switch1);
                Boolean switchState=simpleSwitch.isChecked();
                c1=new Contact();

                if (switchState==false){
                    c1.setNom("");
                }
                else{
                    c1.setNom(nom.getText().toString());
                }

                simpleSwitch = (Switch) findViewById(R.id.switch2);
                switchState=simpleSwitch.isChecked();



                if (switchState==false){
                    c1.setPrenom("");
                }
                else{
                    c1.setPrenom(prenom.getText().toString());
                }


                simpleSwitch = (Switch) findViewById(R.id.switch3);
                switchState=simpleSwitch.isChecked();


                if (switchState==false){
                    c1.setMail("");
                }
                else{
                    c1.setMail(mail.getText().toString());
                }

                simpleSwitch = (Switch) findViewById(R.id.switch4);
                switchState=simpleSwitch.isChecked();

                if (switchState==false){
                    c1.setTel("");
                }
                else{
                    c1.setTel(tel.getText().toString());
                }



                Log.e("Contact name", nom.getText().toString());
                Log.e("Contact srname", prenom.getText().toString());
                Log.e("Contact mail", mail.getText().toString());
                Log.e("Contact tel", tel.getText().toString());


                String infoContact = "BEGIN:VCARD\n" +
                        "VERSION:3.0\n" +
                        "N:" + c1.getPrenom() + ";" + c1.getNom() + "\n" +
                        "FN:" + c1.getPrenom() + " " + c1.getNom() + "\n" +
                        "ORG: \n" +
                        "TITLE: \n" +
                        "LOGO;VALUE=URL;TYPE=GIF: \n" +
                        "TEL;TYPE=WORK;VOICE: " + c1.getTel() + "\n" +
                        "ADR;TYPE=WORK: \n" +
                        "LABEL;TYPE=WORK: \n" +
                        "ADR;TYPE=HOME: \n" +
                        "LABEL;TYPE=HOME: \n" +
                        "EMAIL;TYPE=PREF,INTERNET:" + c1.getMail() + " \n" +
                        "REV:20080454T195242Z\n" +
                        "END:VCARD";

                Log.e("MESSAGE VCARD", infoContact);
                //creationvCard
                File vcfFile = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), "CSCLight.vcf");
                Log.e("Nom fichier", vcfFile.getName());
                FileWriter fw = null;
                try {
                    fw = new FileWriter(vcfFile);

                    fw.write(infoContact);

                    fw.close();
                    Log.e("Fichier créé", "Fichier créé");
                } catch (IOException e) {
                    e.printStackTrace();
                }


                if(Build.VERSION.SDK_INT>=24){
                    Method m = null;
                    try {
                        m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    try {
                        m.invoke(null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                }


                Intent i=new Intent();

                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                i.setAction(android.content.Intent.ACTION_VIEW);
                i.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/CSCLight.vcf")),"text/x-vcard");
                startActivity(i);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.v("PASSAGE  PERMISSIOM", "BEFORE IF");

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("PERMISSION", "Permission: " + permissions[0] + "was " + grantResults[0]);
            Log.e("PASSAGE  PERMISSION", "ACCORDÉE");
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Log.e("PASSAGE  PERMISSION", "REFUSEE");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Log.e("PASSAGE  PERMISSION", "REFUSEE");
            } else {
                //Never ask again selected, or device policy prohibits the app from having that permission.
                //So, disable that feature, or fall back to another situation...
            }

        }
    }


    public void envoyer(View view) {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Check whether NFC is enabled on device
        if (!nfcAdapter.isEnabled()) {
            // NFC is disabled, show the settings UI
            // to enable NFC
            Toast.makeText(this, "Please enable NFC.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        // Check whether Android Beam feature is enabled on device
        else if (!nfcAdapter.isNdefPushEnabled()) {
            // Android Beam is disabled, show the settings UI
            // to enable Android Beam
            Toast.makeText(this, "Please enable Android Beam.",
                    Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        } else {
            // NFC and Android Beam both are enabled

            // File to be transferred
            // For the sake of this tutorial I've placed an image
            // named 'wallpaper.png' in the 'Pictures' directory
            String fileName = "CSCLIGHT.vcf";

            // Retrieve the path to the user's public pictures directory
            File fileDirectory = Environment
                    .getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS);

            // Create a new file using the specified directory and name
            File fileToTransfer = new File(fileDirectory, fileName);
            fileToTransfer.setReadable(true, false);

            nfcAdapter.setBeamPushUris(
                    new Uri[]{Uri.fromFile(fileToTransfer)}, this);


        }
    }


    // SAVE PERMISSION INUTILE SI SDK VERSION < 23
    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("PERMISSION", "Permission is granted");
                return true;
            } else {
                Log.v("PERMISSION", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("PERMISSION", "Permission is granted");
            return true;
        }
    }


}

