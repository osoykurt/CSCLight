package ca.uqac.csclight;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    EditText nom;
    EditText prenom;
    EditText tel;
    EditText mail;

    Contact c1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String  name,surname,telephone,email;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }



    public void valider(View view){
        nom= (EditText) findViewById(R.id.editText1);
        prenom= (EditText) findViewById(R.id.editText2);
        mail= (EditText) findViewById(R.id.editText3);
        tel= (EditText) findViewById(R.id.editText4);
        Log.e("Contact crée",prenom.getText().toString());

        c1 = new Contact(nom.getText().toString(),prenom.getText().toString(),tel.getText().toString());


    }

    
}
