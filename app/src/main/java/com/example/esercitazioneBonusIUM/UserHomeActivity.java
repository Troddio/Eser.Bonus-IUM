package com.example.esercitazioneBonusIUM;

import static com.example.esercitazioneBonusIUM.LoginActivity.USER_EXTRA;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.esercitazioneBonusIUM.dataBase.FeedReaderDbHelper;
import com.example.myapplication.R;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class UserHomeActivity extends AppCompatActivity {

    private TextView welcomeMsg, username, pwd, citta, dataNascita;
    private String utente;
    private Button logout, modificaPassword;
    private ImageView userPhoto;
    private FloatingActionButton addPhoto;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_user_home);

        //cambia il titolo dell'ActionBar (barra blu in alto)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Home Utente");

        //Per connettersi al database
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(UserHomeActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //recupero dall'activity precedente l'username dell'utente loggato
        utente = (String) getIntent().getSerializableExtra(USER_EXTRA);

/******************** Listener "Aggiungi Immagine Profilo" ********************/

        // Recupera le view relative all'immagine utente e al pulsante di aggiunta immagine
        userPhoto = findViewById(R.id.imgUtente);
        addPhoto = findViewById(R.id.aggiungiFoto);

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Apre la libreria di selezione immagini
                ImagePicker.with(UserHomeActivity.this)
                        .crop()                                  // Abilita la funzione di ritaglio dell'immagine (opzionale)
                        .compress(1024)                          // Imposta la dimensione massima del file immagine (opzionale)
                        .maxResultSize(1080, 1080)   // Imposta la risoluzione massima dell'immagine (opzionale)
                        .start();
            }
        });

/**************** Riempi i vari campi con i dati dell'utente ****************/

        // Recupera le view relative ai campi dati dell'utente
        welcomeMsg = findViewById(R.id.userWelcome);
        username = findViewById(R.id.usernameHome);
        pwd = findViewById(R.id.userPassword);
        citta = findViewById(R.id.userCitta);
        dataNascita = findViewById(R.id.userData);

        // Verifica se l'utente ha una foto del profilo e la visualizza, altrimenti mostra quella di default
        if (dbHelper.getPhoto(utente) != null) {
            String uriString = dbHelper.getPhoto(utente);
            Uri uri = Uri.parse(uriString);
            userPhoto.setImageURI(uri);
        } else { // altrimenti viene impostata un'immagine di default
            Uri uri = Uri.parse("android.resource://com.example.myapplication/" + R.drawable.foto_default);
            userPhoto.setImageURI(uri);
        }

        // Recupera i dati dell'utente dal database e li mostra nelle relative view
        welcomeMsg.setText("Benvenuto: " + dbHelper.getUsername(utente) + "!");
        username.setText("Username: " + dbHelper.getUsername(utente));
        pwd.setText("Password: " + dbHelper.getPassword(utente));
        citta.setText("Città: " + dbHelper.getAddress(utente));
        dataNascita.setText("Data di nascita: " + dbHelper.getBirthDate(utente));


/************************** Listener del bottone "Modifica Password" *************************/

        // Trova il bottone nella view
        modificaPassword = findViewById(R.id.modificaPasswordUser);

        // Imposta un listener al bottone
        modificaPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un'intent per passare all'activity di cambio password
                Intent showResult = new Intent(UserHomeActivity.this, ChangePasswordActivity.class);
                showResult.putExtra(USER_EXTRA, utente); // Aggiunge l'username dell'utente come extra
                startActivity(showResult); // Avvia l'activity
            }
        });

/********************** Listener del bottone "Logout" *********************/

        // Trova il bottone nella view
        logout = findViewById(R.id.logoutButton);

        // Imposta un listener al bottone
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crea un'intent per passare all'activity di login
                Intent showResult = new Intent(UserHomeActivity.this, LoginActivity.class);
                startActivity(showResult); // Avvia l'activity
                finish(); // Chiude l'activity corrente (UserHomeActivity)
            }
        });
    }

    // Metodo chiamato quando l'utente seleziona o scatta una foto
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Prende l'URI dell'immagine selezionata o scattata
        Uri uri = data.getData();

        // Controlla che l'URI non sia nullo
        if (uri == null){
            // Mostra un messaggio di errore se l'URI è nullo
            Toast.makeText(UserHomeActivity.this, "Caricamento foto non riuscito", Toast.LENGTH_LONG).show();
        } else {
            // Crea una connessione al database SQLite
            FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(UserHomeActivity.this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // Salva l'URI dell'immagine nel database
            dbHelper.addPhoto(utente, uri, UserHomeActivity.this);

            // Imposta l'immagine selezionata o scattata come immagine del profilo dell'utente
            userPhoto.setImageURI(uri);
        }
    }

}