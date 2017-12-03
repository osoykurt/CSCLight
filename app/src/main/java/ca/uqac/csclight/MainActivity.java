package ca.uqac.csclight;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, View.OnClickListener {

    NfcAdapter nfcAdapter;
    EditText nom;
    EditText prenom;
    EditText tel;
    EditText mail;
    FileOutputStream fOut = null;
    SharedPreferences sharedPref;

    private Button buttonValider, buttonEnvoyer, buttonRecevoir;

    public static final String SurnameKey = "surnameKey";
    public static final String NameKey = "nameKey";
    public static final String PhoneKey = "phoneKey";
    public static final String EmailKey = "emailKey";

    Contact c1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        setContentView(R.layout.activity_main);

        nom = (EditText) findViewById(R.id.edit_name);
        prenom = (EditText) findViewById(R.id.edit_surname);
        mail = (EditText) findViewById(R.id.edit_mail);
        tel = (EditText) findViewById(R.id.edit_tel);

        buttonValider = (Button) findViewById(R.id.button_valider);

        sharedPref = getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE);

        if (sharedPref.contains(SurnameKey) && sharedPref.contains(NameKey)) {
            nom.setText(sharedPref.getString(SurnameKey, null));
            prenom.setText(sharedPref.getString(NameKey, null));
            mail.setText(sharedPref.getString(EmailKey, null));
            tel.setText(sharedPref.getString(PhoneKey, null));
        }

        buttonValiderManager();
        buttonValider.setOnClickListener(this);

        nom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                buttonValiderManager();
            }
        });
        prenom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                buttonValiderManager();
            }
        });
        mail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                buttonValiderManager();
            }
        });
        tel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                buttonValiderManager();
            }
        });

        buttonEnvoyer = (Button) findViewById(R.id.button_envoyer);
        buttonEnvoyer.setOnClickListener(this);

        buttonRecevoir = (Button) findViewById(R.id.button_recevoir);
        buttonRecevoir.setOnClickListener(this);


    }

    //Activation, désactivation button Valider
    private void buttonValiderManager() {
        Log.v("Test", "function");
        if (sharedPref.contains(SurnameKey) && sharedPref.contains(NameKey)) {
            Log.v("Test", "First if");

            if (sharedPref.getString(SurnameKey, null).equals(nom.getText().toString())
                    && sharedPref.getString(NameKey, null).equals(prenom.getText().toString())) {
                Log.v("Test", "First if - first if ");

                if ((sharedPref.getString(PhoneKey, null).equals(tel.getText().toString())
                        && sharedPref.getString(EmailKey, null).equals(mail.getText().toString()))
                        || (!mail.getText().toString().isEmpty() && !isValidMail(mail.getText().toString()))) {

                    buttonValider.setEnabled(false);
                } else {
                    buttonValider.setEnabled(true);
                }
            } else {
                Log.v("Test", "First if - else");
                if (!mail.getText().toString().isEmpty() && !isValidMail(mail.getText().toString())) {
                    buttonValider.setEnabled(false);
                } else {
                    buttonValider.setEnabled(true);
                }
            }
        } else if ((nom.getText().toString().equals("") || prenom.getText().toString().equals("")
                || (!isValidMail(mail.getText().toString()) && !isValidMail(mail.getText().toString())))) {
            Log.v("Test", "Else - first if");
            buttonValider.setEnabled(false);
        } else {
            Log.v("Test", "Else - else");
            buttonValider.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkNFCactivation(nfcAdapter);
        File f = new File(Environment.getExternalStoragePublicDirectory("beam") + "/CSCLight.vcf");
        f.delete();

    }

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

            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putString(SurnameKey, nom.getText().toString());
            editor.putString(NameKey, prenom.getText().toString());
            editor.putString(PhoneKey, tel.getText().toString());
            editor.putString(EmailKey, mail.getText().toString());
            editor.commit();

            Toast.makeText(this, "Contact Saved.", Toast.LENGTH_SHORT).show();

            buildVcard(); //creationvCard
        } else if (view.getId() == R.id.button_envoyer) {
            //envoie le fichier situé dans Downloads dans le stream NFC
            envoiNFC();
        } else if (view.getId() == R.id.button_recevoir) {
            //synchronization
            if (Build.VERSION.SDK_INT >= 24) {
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


            Intent i = new Intent();

            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            i.setAction(android.content.Intent.ACTION_VIEW);
            File f = new File(Environment.getExternalStoragePublicDirectory("beam") + "/CSCLight.vcf");
            i.setDataAndType(Uri.fromFile(f), "text/x-vcard");
            startActivity(i);
        }
    }

    private void buildVcard() {
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

        File vcfDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
        vcfDir.mkdirs();

        File vcfFile = new File(vcfDir, "CSCLight.vcf");
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
    }

    private void checkSwitch() {

        c1 = new Contact();
        c1.setNom(nom.getText().toString());
        c1.setPrenom(prenom.getText().toString());

        Switch simpleSwitch = (Switch) findViewById(R.id.switch_mail);
        Boolean switchState = simpleSwitch.isChecked();

        isValidMail(mail.getText().toString());
        if (switchState == false) {
            c1.setMail("");
        } else {
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

    private boolean isValidMail(String mail) {
        Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
        Matcher m = p.matcher(mail);
        if (!m.matches()) {
            /*Toast.makeText(MainActivity.this, R.string.email_format_error,
                    Toast.LENGTH_SHORT).show();*/
            return false;
        } else {
            return true;
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

    public boolean checkNFCactivation(NfcAdapter adapter) {

        // Check whether NFC is enabled on device
        if (!adapter.isEnabled()) {
            // NFC is disabled, show the settings UI
            // to enable NFC
            Toast.makeText(this, "Please enable NFC.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        }
        // Check whether Android Beam feature is enabled on device
        else if (!adapter.isNdefPushEnabled()) {
            // Android Beam is disabled, show the settings UI
            // to enable Android Beam
            Toast.makeText(this, "Please enable Android Beam.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_NFCSHARING_SETTINGS));
        }

        return true;
    }


    public void envoiNFC() {
        Log.e("PASSAGE NFC", "NFC");

        // Check whether NFC is enabled on device
        if (checkNFCactivation(nfcAdapter)) {
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

            Log.e("PASSAGE NFC", "OK");


        }
    }

}