package com.example.esercitazioneBonusIUM;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.esercitazioneBonusIUM.dataBase.FeedReaderDbHelper;
import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Calendar;

public class LoginActivity extends AppCompatActivity {

    //Per il passaggio dati ad un'altra activity
    public static final String USER_EXTRA = "com.example.progettoIUMcorretto.Person";

    private String stringUsername, stringPassword;
    private EditText usernameInserito, passwordInserita;
    private Button accedi, nuovoUtente;
    private TextView sezioneErrore;
    private TextInputLayout textInputPW,textInputUsername;
    private Person admin;
    private Calendar cal = Calendar.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        //cambia il titolo dell'ActionBar (barra blu in alto)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Login");

        sezioneErrore = findViewById(R.id.loginErrore);
        usernameInserito = findViewById(R.id.loginUsername);
        passwordInserita = findViewById(R.id.loginPassword);
        accedi = findViewById(R.id.toHome);

        //Per connettersi al database
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(LoginActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //drop del database da usare prima della consegna e poi cancellare
        //dbHelper.onUpgrade(db,1,1);

        //creazione admin
        if(dbHelper.isTableEmpty()){
            cal.set(Calendar.YEAR, 1997);
            cal.set(Calendar.MONTH, Calendar.FEBRUARY); // Mese: 0-based (Gennaio = 0)
            cal.set(Calendar.DAY_OF_MONTH, 26);
            admin = new Person("admin", "admin", "casa tua", cal, true);
            dbHelper.addPerson(admin);
        }


/*******************  Listener button "non sei registrato?..." ***************/
        nuovoUtente = findViewById(R.id.nonRegistrato);
        nuovoUtente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showResult = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(showResult);
            }
        });

/************************ Listener button "Accedi" ************************/
        textInputPW = findViewById(R.id.layoutPassword);
        textInputUsername = findViewById(R.id.textInputUsername);

        accedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringUsername = usernameInserito.getText().toString();
                stringPassword = passwordInserita.getText().toString();

                dbHelper.getAllPersons();

                // controllo che i dati inseriti dall'utente siano corretti e
                if (checkInput() == 0) {
                    //se il form è stato compilato correttamente
                    //controlla che l'username inserito sia presente nel database
                    if (dbHelper.getUsername(stringUsername)!= null) {
                        //se l'username è presente controlla che la password inserita sia quella corretta
                        if (dbHelper.getPassword(stringUsername).equals(stringPassword)) {
                            //se la password è corretta

                            textInputPW.setHelperTextEnabled(false);

                            // abilito il tasto accedi
                            accedi.setClickable(true);
                            if (dbHelper.getIsAdmin(stringUsername)) {
                                //se l'utente è admin

                                //viene lanciata l'AdminHomeActivity
                                Intent showResult = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                showResult.putExtra(USER_EXTRA, stringUsername);
                                startActivity(showResult);
                                usernameInserito.setText("");
                                passwordInserita.setText("");
                                finish();
                            } else {
                                //se l'utente non è admin

                                //viene lanciata l'UserHomeActivity
                                Intent showResult = new Intent(LoginActivity.this, UserHomeActivity.class);
                                showResult.putExtra(USER_EXTRA, stringUsername);
                                startActivity(showResult);
                                usernameInserito.setText("");
                                passwordInserita.setText("");
                                finish();
                            }
                        }else{
                            //se la password non è corretta

                            //rende visibili gli errori e vengono impostati il corrispettivo messaggio d'errore
                            sezioneErrore.setVisibility(View.VISIBLE);
                            sezioneErrore.setText("Password errata!");
                            textInputPW.setHelperTextEnabled(true);
                            textInputPW.setHelperText("Password non corretta!");
                        }
                    }else{
                        //se l'utente inserito non è presente nel database
                        sezioneErrore.setVisibility(View.VISIBLE);
                        sezioneErrore.setText("L'username inserito non esiste!");
                    }

                }else{
                    //se il form non è stato compilato correttamente
                    int err = checkInput();
                    sezioneErrore.setVisibility(View.VISIBLE);
                    if(err == 1) {
                        sezioneErrore.setText("inserisci un username");

                    }else if(err == 2){
                        sezioneErrore.setText("Inserisci una password");

                    }else{
                        sezioneErrore.setText("Inserisci un username e una password");
                    }
                }
            }

        });

/*************************************** TextChangedListener *****************************************/
        passwordInserita.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                textInputPW.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputPW.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                textInputPW.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });

/*************************** Listener onFocusChange ************************************/
        //campo username (EditText)
        usernameInserito.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                sezioneErrore = findViewById(R.id.loginErrore);
                sezioneErrore.setVisibility(View.GONE);
                textInputUsername.setHint("Username");
            }
        });

        //campo password (EditText)
        passwordInserita.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                textInputPW = findViewById(R.id.layoutPassword);
                textInputPW.setHint("Password");
                //Nasconde l'icona dell'errore
                textInputPW.setErrorEnabled(false);
            }
        });
    }

/******************************** metodi di supporto **********************************/
    /* Controllo se i campi del form sono stati compilati dall'utente */
    int checkInput(){
        int errors =0;
        textInputPW = findViewById(R.id.layoutPassword);
        if(stringPassword.length()==0) {
            showError(passwordInserita, textInputPW,"Inserire la password");
            errors += 2;
        }
        if(stringUsername.length()==0){
            showError(usernameInserito ,"Inserire l'username");
            errors += 1;
        }
        return errors;
    }
    /*  evidenzia i campi del form in cui è presente un errore  */
    private void showError(EditText input, String Error_msg){
        input.setError(Error_msg);
        input.requestFocus();
    }
    /*  evidenzia i campi del form in cui è presente un errore
    *   usata per i campi password */
    private void showError(EditText input, TextInputLayout textInputPW, String Error_msg){
        input.setError(Error_msg);
        //Nasconde il toogle per mostrare la password
        textInputPW.setEndIconMode(TextInputLayout.END_ICON_NONE);
    }
}