package com.example.esercitazioneBonusIUM;

import static com.example.esercitazioneBonusIUM.LoginActivity.USER_EXTRA;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.esercitazioneBonusIUM.dataBase.FeedReaderDbHelper;
import com.example.myapplication.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminHomeActivity extends AppCompatActivity {

    TextView welcomeMsg, username, pwd, citta, dataNascita, modificaPassword;
    String utente;
    Button logout, gestisciUtenti;
    ImageView userPhoto;
    FloatingActionButton addPhoto;
    Boolean flag = false;
    FeedReaderDbHelper db;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_admin_home);

        //cambia il titolo dell'ActionBar (barra blu in alto)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Home Admin");

        //Per connettersi al database
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(AdminHomeActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        utente = (String) getIntent().getSerializableExtra(USER_EXTRA);

/***************** tasto "+ - Aggiungi Immagine Profilo" *******************/

        userPhoto = findViewById(R.id.imgUtente);
        addPhoto = findViewById(R.id.aggiungiFoto);

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(AdminHomeActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
                flag = true;
            }
        });

/*************** riempi i vari campi con i dati dell'utente ******************/

        welcomeMsg = findViewById(R.id.adminWelcomeMsg);
        username = findViewById(R.id.UsernameHomeAdmin);
        pwd = findViewById(R.id.passwordAdmin);
        citta = findViewById(R.id.cittaAdmin);
        dataNascita = findViewById(R.id.dataNascita);

        //se l'utente non ha una foto la inizializza con una di default
        if(dbHelper.getPhoto(utente) != null){
            String uriString = dbHelper.getPhoto(utente);
            Uri uri = Uri.parse(uriString);
            userPhoto.setImageURI(uri);
        }else{
            //se l'utente ha già una foto
            Uri uri = Uri.parse("android.resource://com.example.myapplication/" + R.drawable.foto_default);
            userPhoto.setImageURI(uri);
        }
        // Creazione di un drawable rettangolare con bordo nero
        ShapeDrawable border = new ShapeDrawable(new RectShape());
        border.getPaint().setColor(Color.BLACK);
        border.getPaint().setStyle(Paint.Style.STROKE);
        border.getPaint().setStrokeWidth(35);

        // Impostazione del drawable rettangolare come sfondo dell'ImageView
        addPhoto.setBackground(border);


        welcomeMsg.setText("Benvenuto:   " + dbHelper.getUsername(utente) + "!");
        username.setText("Username:   " + dbHelper.getUsername(utente));
        pwd.setText("Password:  " + dbHelper.getPassword(utente));
        citta.setText("Città:  " + dbHelper.getAddress(utente));
        dataNascita.setText("Data di nascita: "+ dbHelper.getBirthDate(utente));


/************************** tasto "modifica password" *************************/

        modificaPassword = findViewById(R.id.modificaPasswordAdmin);
    if(dbHelper.getUsername(utente).equals("admin")){
        //se è l'utente "admin" viene nascoscosto il tasto modifica password
        modificaPassword.setVisibility(View.GONE);
        modificaPassword.setClickable(false);
    }else{
        //se non lo è viene mostrato il tasto modifica password
        modificaPassword.setVisibility(View.VISIBLE);
        modificaPassword.setClickable(true);
    }
        modificaPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lancia la ChangePasswordActivity
                Intent showResult = new Intent(AdminHomeActivity.this, ChangePasswordActivity.class);
                showResult.putExtra(USER_EXTRA, utente);
                startActivity(showResult);
            }
        });
//    }


/********************** tasto "Gestisci Utenti" *********************/
        gestisciUtenti = findViewById(R.id.menageUsers);
        gestisciUtenti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lancia la SearchActivity
                Intent showResult = new Intent(AdminHomeActivity.this, SearchActivity.class);
                showResult.putExtra(USER_EXTRA, utente);
                startActivity(showResult);
            }
        });


/********************** tasto "Logout" *********************/
        logout = findViewById(R.id.logoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //lancia la LoginActivity
                Intent showResult = new Intent(AdminHomeActivity.this, LoginActivity.class);
                startActivity(showResult);
                finish();
            }
        });

    }

    //Viene chiamata quando si vuole aggiungere una foto profilo
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = data.getData();

        if (uri != null){
            //collegamento al database
            FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(AdminHomeActivity.this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            //salvo l'uri della foto nel database
            dbHelper.addPhoto(utente, uri, AdminHomeActivity.this);

            //imposto (tramite l'uri) la foto appena scattata o presa dalla
            //galleria come immagine del profilo dell'utente
            userPhoto.setImageURI(uri);
        }
    }
}
