package ca.uqac.csclight;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.EditText;
import android.widget.Toast;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    EditText nom;
    EditText prenom;
    EditText tel;
    EditText mail;

    Contact c1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String name, surname, telephone, email;


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }


    public void valider(View view) {
        nom = (EditText) findViewById(R.id.editText1);
        prenom = (EditText) findViewById(R.id.editText2);
        mail = (EditText) findViewById(R.id.editText3);
        tel = (EditText) findViewById(R.id.editText4);


        c1 = new Contact(nom.getText().toString(), prenom.getText().toString(), mail.getText().toString(), tel.getText().toString());

        Log.e("Contact name", nom.getText().toString());
        Log.e("Contact srname", prenom.getText().toString());
        Log.e("Contact mail", mail.getText().toString());
        Log.e("Contact tel", tel.getText().toString());

    }


    public void envoyer(View view) {
        NfcAdapter nfcAdapter;

        PackageManager pm = this.getPackageManager();
        // Check whether NFC is available on device

        if (!pm.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            // NFC is not available on the device.
            Toast.makeText(this, "The device does not has NFC hardware.",
                    Toast.LENGTH_SHORT).show();
        }
        // Check whether device is running Android 4.1 or higher
        else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            // Android Beam feature is not supported.
            Toast.makeText(this, "Android Beam is not supported.",
                    Toast.LENGTH_SHORT).show();
        } else {
            // NFC and Android Beam file transfer is supported.
            Toast.makeText(this, "Android Beam is supported on your device.",
                    Toast.LENGTH_SHORT).show();

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
                String fileName = "sharingan.png";

                // Retrieve the path to the user's public pictures directory
                File fileDirectory = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);

                // Create a new file using the specified directory and name
                File fileToTransfer = new File(fileDirectory, fileName);
                fileToTransfer.setReadable(true, false);

                nfcAdapter.setBeamPushUris(
                        new Uri[]{Uri.fromFile(fileToTransfer)}, this);
            }
        }
    }
}
