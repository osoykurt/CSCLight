package ca.uqac.csclight;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {

    NfcAdapter nfcAdapter;
    EditText nom;
    EditText prenom;
    EditText tel;
    EditText mail;
    FileOutputStream fOut = null;

    private Button buttonValider, buttonEnvoyer, buttonRecevoir;

    Contact c1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nom = (EditText) findViewById(R.id.edit_name);
        prenom = (EditText) findViewById(R.id.edit_surname);
        mail = (EditText) findViewById(R.id.edit_mail);
        tel = (EditText) findViewById(R.id.edit_tel);

        buttonValider = (Button) findViewById(R.id.button_valider);
        buttonValider.setOnClickListener(this);

        buttonEnvoyer = (Button) findViewById(R.id.button_envoyer);
        buttonEnvoyer.setOnClickListener(this);

        buttonRecevoir = (Button) findViewById(R.id.button_recevoir);
        buttonRecevoir.setOnClickListener(this);



    }

    //DEMANDE DE PERMISSIONS
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_valider) {
            isStoragePermissionGranted(); //verifie la permission d'écriture dans le fichier
            checkSwitch();

            Log.e("Contact name", nom.getText().toString());
            Log.e("Contact surname", prenom.getText().toString());
            Log.e("Contact mail", mail.getText().toString());
            Log.e("Contact tel", tel.getText().toString());

            //creationvCard
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
        } else if (view.getId() == R.id.button_envoyer) {
            //envoie le fichier situé dans Downloads dans le stream NFC
            envoiNFC();
        } else if (view.getId() == R.id.button_recevoir) {
            //synchronization
        }


    }

    private void checkSwitch() {

        c1 = new Contact();

        Switch simpleSwitch = (Switch) findViewById(R.id.switch_name);
        Boolean switchState = simpleSwitch.isChecked();


        if (switchState == false) {
            c1.setNom("");
        } else {
            c1.setNom(nom.getText().toString());
        }

        simpleSwitch = (Switch) findViewById(R.id.switch_surname);
        switchState = simpleSwitch.isChecked();


        if (switchState == false) {
            c1.setPrenom("");
        } else {
            c1.setPrenom(prenom.getText().toString());
        }

        simpleSwitch = (Switch) findViewById(R.id.switch_mail);
        switchState = simpleSwitch.isChecked();

        checkMail(mail.getText().toString());
        if (switchState == false) {
            c1.setMail("");
        } else {
            checkMail(mail.getText().toString());
            c1.setMail(mail.getText().toString());
        }

        simpleSwitch = (Switch) findViewById(R.id.switch_tel);
        switchState = simpleSwitch.isChecked();

        if (switchState == false) {
            c1.setTel("");
        } else {

            c1.setTel(tel.getText().toString());
        }
    }

    private void checkMail(String mail) {
        Pattern p  = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(mail);
        if (!m.matches()) {
            Toast.makeText(MainActivity.this, R.string.email_format_error,
                    Toast.LENGTH_SHORT).show();
        }
    }

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

    public void envoiNFC() {

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


}

