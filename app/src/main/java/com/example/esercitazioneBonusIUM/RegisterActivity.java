package com.example.esercitazioneBonusIUM;


import static android.view.View.GONE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import com.example.esercitazioneBonusIUM.dataBase.FeedReaderDbHelper;
import com.example.esercitazioneBonusIUM.fragments.DatePickerFragment;
import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText editUsername, editPassword, editRepeatPsw, editIndirizzo, editDataNascita;
    private String stringUsername, stringPassword, stringRepeatPsw, stringIndirizzo;
    private Calendar dataNascita;
    private Person person;
    private Button registrati;
    private TextView erroreRegistrazione;
    private TextInputLayout textInputPW, textInputRPW, textInputUsername, textInputDataNascita;
    private FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(RegisterActivity.this);;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);

        //cambia il titolo dell'ActionBar (barra blu in alto)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Registrati");

        /** Inizializzazione degli EditText **/
        editUsername = findViewById(R.id.registerUsername);
        editPassword = findViewById(R.id.registerPassword);
        editRepeatPsw = findViewById(R.id.registerRepeatPassword);
        editIndirizzo = findViewById(R.id.Residenza);
        editDataNascita = findViewById(R.id.dataDiNascita);
        registrati = findViewById(R.id.registerButton);

        /** Inizializzazione degli TextInputLayout **/
        textInputPW = findViewById(R.id.layoutPassword);
        textInputRPW = findViewById(R.id.layoutRepeatPassword);

        /** Inizializzazione input provenienti da EditText sottoforma di stringa **/
        stringUsername = editUsername.getText().toString();
        stringPassword = editPassword.getText().toString();
        stringRepeatPsw= editRepeatPsw.getText().toString();
        stringIndirizzo = editIndirizzo.getText().toString();

/************************ Listener button "Registrati" ************************/
        registrati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInputPW.setEndIconMode(TextInputLayout.END_ICON_NONE);
                int err = checkInput();
                if(err == 0) {
                    //se il form è stato compilato correttamente

                    //crea una nuova persona
                    stringUsername = editUsername.getText().toString();
                    stringPassword = editPassword.getText().toString();
                    stringIndirizzo = editIndirizzo.getText().toString();
                    person = new Person(stringUsername, stringPassword, stringIndirizzo, dataNascita, false);

                    //Si connette al database
                    SQLiteDatabase db = dbHelper.getReadableDatabase();
                    //aggiunge il nuovo utente al DB
                    dbHelper.addPerson(person);

                    //Viene controllato che il nuovo appena registrato sia stato inserito correttamente nel DB
                    for(Person item : dbHelper.getAllPersons()) {
                        if (item.getUsername().equals(stringUsername)) {
                            //se l'username è presente nel DB

                            //Pop-up per feedback positivo all'utente
                            AlertDialog.Builder builderSuccess = new AlertDialog.Builder(RegisterActivity.this, R.style.CustomDialogThemeSuccess);
                            builderSuccess.setMessage("Ti sei registrato correttamente! Ora verrai reindirizzato alla schermata di Login così potrai accedere al tuo account")
                                    .setTitle("Registrazione avvenuta con successo")
                                    .setIcon(R.drawable.ic_correct);
                            //listener tasto per chiudere il pop-up
                            builderSuccess.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                            //per chiudere il pop-up
                            AlertDialog dialog = builderSuccess.create();
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.show();
                        }
                    }
                }else{
                    //se il form non è stato compilato correttamente
                            AlertDialog.Builder builderError = new AlertDialog.Builder(RegisterActivity.this, R.style.CustomDialogThemeError);

                            builderError.setTitle("Registrazione non avvenuta!")
                                    .setIcon(R.drawable.ic_warning);
                    //tutti i possibili errori
                    switch (err){
                        case 2:
                            textInputPW.setEndIconMode(TextInputLayout.END_ICON_NONE);
                            textInputPW.setHelperTextEnabled(true);
                            textInputPW.setHelperText("Inseririsci una password");
                            builderError.setMessage("Perfavore inseririsci una password");
                            break;
                        case 3:
                            textInputPW.setHelperTextEnabled(true);
                            textInputPW.setHelperText("La password è troppo corta");
                            builderError.setMessage("La password è troppo corta. Perfavore assicurati che la nuova password contenga almeno 8 caratteri ;)");
                            break;
                        case 4:
                            textInputPW.setHelperTextEnabled(true);
                            textInputPW.setHelperText("La password deve contenere una lettera minuscola, maiuscola e un carattere speciale (@,#,?,^, ecc...)");
                            builderError.setMessage("La password non rispetti i requisti di sicurezza. Ricorda la password deve contenere: una lettera minuscola, maiuscola e un carattere speciale (@,#,?,^, ecc...) ;)");
                            break;

                        case 5:
                            textInputPW.setHelperTextEnabled(true);
                            textInputPW.setHelperText("Le password non corrispondono");
                            textInputRPW.setHelperTextEnabled(true);
                            textInputRPW.setHelperText("Le password non corrispondono");
                            builderError.setMessage("Le password non corrispondono. Assicurati di aver scritto la stessa password nei campi \"Nuova password\" e \"Conferma nuova password\" ;)");break;
                        default: builderError.setMessage("Ci sono alcuni campi del form che non sono stati compilati correttamente.\nPerfavore riprova a compilare i campi indicati.");break;
                    }
                    builderError.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builderError.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
            }
        });
        //se il testo del campo editPassword cambia appare il toogle per la visualizzazione in chiaro
        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                textInputPW.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });
        //se il testo del campo editRepeatPassword cambia appare il toogle per la visualizzazione in chiaro
        editRepeatPsw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textInputRPW.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                textInputRPW.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });
        //listener del campo username se viene selezionato
        editUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                textInputUsername = findViewById(R.id.layoutUsername);
                textInputUsername.setHint("Username");
            }
        });
        //listener del campo password se viene selezionato
        editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                textInputPW = findViewById(R.id.layoutPassword);
                textInputPW.setHint("Password");
                //Nasconde l'icona dell'errore
                textInputPW.setError(null);
                textInputPW.setHelperTextEnabled(true);
            }
        });
        //se il testo del campo editDataNascita cambia appare il calendario
        editDataNascita.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() <5) {
                    showDialog();
                    textInputDataNascita = findViewById(R.id.layoutDataNascita);
                    textInputDataNascita.setHint("Data di Nascita");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

    }
    /** Dopo che l'utente ha selezionato e confermato la data di nascita dal calendario
     *  si controlla che l'utente abbia almeno 18 anni                                **/
    public void doPositiveClick(Calendar date){
        this.dataNascita = date;
        //today contiene la data odierna
        Calendar today = Calendar.getInstance();
        //calcola l'età dell'utente (in fase di registrazione)
        int age = today.get(Calendar.YEAR) - date.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < date.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        textInputDataNascita = findViewById(R.id.layoutDataNascita);
        editDataNascita = findViewById(R.id.dataDiNascita);
        erroreRegistrazione = findViewById(R.id.registerErrore);
        registrati = findViewById(R.id.registerButton);
        if (age < 18) {
            // l'utente ha meno di 18 anni
            textInputDataNascita.setHelperText("Per registrarti devi avere almeno 18 anni");
            textInputDataNascita.setHelperTextEnabled(true);
            showError(editDataNascita,"Età inferiore ai 18 anni" );
            registrati.setVisibility(GONE);
            erroreRegistrazione.setText("Per registrarti devi essere maggiorenne!");
            erroreRegistrazione.setVisibility(View.VISIBLE);


        } else {
            // l'utente ha almeno 18 anni
            registrati.setVisibility(View.VISIBLE);
            erroreRegistrazione.setVisibility(GONE);
            editDataNascita.setError(null);
            textInputDataNascita.setErrorEnabled(false);
            textInputDataNascita.setHelperTextEnabled(false);
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            editDataNascita.setText(format.format(date.getTime()));
        }
    }
 //funzione che fa apparire il calendario come popo-up
    void showDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "dialog");
    }
    // controllo che i dati inseriti dall'utente siano corretti
    int checkInput(){
        List<Person> personList = new ArrayList<>();
        int errors =0, e = 0;
        String regex = "^(?=.*[a-z])(?=."
                + "*[A-Z])(?=.*\\d)"
                + "(?=.*[-+_!@#$%^&*., ?]).+$";
        // Compile the ReGex
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(editPassword.getText().toString());
        //sia stato scritto un username
        if(editUsername.getText().toString().length()==0){
            errors = 1;
            e++;
            showError(editUsername,"Inserire lo username");
        }else{
            //controlla che non sia già stato utilizzato
            personList.addAll(dbHelper.getAllPersons());
            for(Person item : personList) {
                if (item.getUsername().equals(editUsername.getText().toString())) {
                    errors = 1;
                    showError(editUsername, "Username già utilizzato!");
                    break;
                }
            }
        }
        //sia stata scritta una password
        if(editPassword.getText().toString().length()==0){
            errors = 2;
            e++;
            showError(editPassword,"Inserire la password");

        //la password sia di almeno 8 caratteri
        }else if(editPassword.getText().toString().length()<8){
            errors = 3;
            textInputPW = findViewById(R.id.layoutPassword);
            showError(editPassword, "La password è troppo corta");

        //controlla che la password rispetti i requisiti di sicurezza
        }else if(!m.matches()){
            errors = 4;
            textInputRPW = findViewById(R.id.layoutRepeatPassword);
            showError(editPassword, "La password deve contenere una lettera minuscola, maiuscola, un numero e un carattere speciale (es. @,#,?,^, ecc...)");
        }else if(!editPassword.getText().toString().equals(editRepeatPsw.getText().toString())){
            //se i campi "password" e "ripeti password" non coincidono
            errors = 5;
            showError(editPassword, editRepeatPsw,textInputPW, textInputRPW,"Le password non corrispondono");
        }
        //controlla che sia stato inserito un indirizzo valido
        if(!(editIndirizzo.getText().toString().length()>=3)){
            errors = 1;
            e++;
            showError(editIndirizzo,"Inseririsci un indirizzo valido");
        }
        //controlla che sia stata inserita una data di nascita valida
        if(!(editDataNascita.getText().toString().length() >=8)){
            errors = 1;
            e++;
            textInputDataNascita = findViewById(R.id.layoutDataNascita);
            textInputDataNascita.setHelperText("Inserisci la data nel formato gg/mm/aaaa");
            showError(editDataNascita,"Inseririsci una data valida");
        }
        //controlla che tutti i campi del form siano stati compilati
        if(e == 4){
            erroreRegistrazione = findViewById(R.id.registerErrore);
            erroreRegistrazione.setText("Compila tutti i campi del form!");
            erroreRegistrazione.setVisibility(View.VISIBLE);
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
    private void showError(EditText input, EditText input2, TextInputLayout textInputPW, TextInputLayout textInputRPW, String Error_msg){
        input.setError(Error_msg);
        input2.setError(Error_msg);
        //Nasconde il toogle per mostrare la password
        textInputPW.setEndIconMode(TextInputLayout.END_ICON_NONE);
        textInputRPW.setEndIconMode(TextInputLayout.END_ICON_NONE);
    }
}
