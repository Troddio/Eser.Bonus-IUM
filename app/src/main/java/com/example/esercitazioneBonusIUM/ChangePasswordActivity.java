package com.example.esercitazioneBonusIUM;

import static com.example.esercitazioneBonusIUM.LoginActivity.USER_EXTRA;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.esercitazioneBonusIUM.dataBase.FeedReaderDbHelper;
import com.example.myapplication.R;
import com.google.android.material.textfield.TextInputLayout;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {

    String utente, password;
    TextView oldPwd, username;
    EditText editNewPwd, editRepeatNewPwd;
    TextInputLayout layoutNewPwd, layoutRNewPwd;
    Button confermaPwd, tornaHome;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_change_password);

        //cambia il titolo dell'ActionBar (barra blu in alto)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cambia password");

        //Per connettersi al database
        FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(ChangePasswordActivity.this);

        //recupero dall'activity precedente l'username dell'utente loggato
        utente = (String) getIntent().getSerializableExtra(USER_EXTRA);

        //recuper dal DB la password attuale
        password = dbHelper.getPassword(utente);

        username = findViewById(R.id.username);
        oldPwd = findViewById(R.id.passwordAttuale);
        editNewPwd = findViewById(R.id.newPassword);
        editRepeatNewPwd = findViewById(R.id.repeatNewPassword);
        confermaPwd = findViewById(R.id.confirm_button);
        layoutNewPwd = findViewById(R.id.textNewPassword);
        layoutRNewPwd = findViewById(R.id.textRepNewPassword);

        username.setText("Username: "+ utente);
        oldPwd.setText("Password attuale: " +password);

        confermaPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutNewPwd.setEndIconMode(TextInputLayout.END_ICON_NONE);
                int err = checkInput();
                if(err == 0) {
                    //se il form è stato compilato senza errori

                    //pop-up successo
                    AlertDialog.Builder builderSuccess = new AlertDialog.Builder(ChangePasswordActivity.this, R.style.CustomDialogThemeSuccess);
                    builderSuccess.setMessage("Il cambio password è avvenuto correttamente. Ora verrai reindirizzato alla Home")
                            .setTitle("Hai cambiato la password!")
                            .setIcon(R.drawable.ic_correct);

                    //tasto per chiudere il pop-up
                    builderSuccess.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            String newPassword = editNewPwd.getText().toString();
                            ////Per connettersi al database
                            FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(ChangePasswordActivity.this);
                            dbHelper.updatePassword(utente, newPassword, ChangePasswordActivity.this);

                            //controllo se l'utente che ha cambiato la password è un admin
                            // così da poterlo rimandare alla relativa Home
                            if (dbHelper.getIsAdmin(utente)){
                                //se l'utente è un admin
                                Intent showResult = new Intent(ChangePasswordActivity.this, AdminHomeActivity.class);
                                startActivity(showResult);
                                finish();
                            }else {
                                //se l'utente non è admin
                                Intent showResult = new Intent(ChangePasswordActivity.this, UserHomeActivity.class);
                                startActivity(showResult);
                                finish();
                            }

                            finish();
                        }
                    });

                    AlertDialog dialog = builderSuccess.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();


                }else{
                    //se il form non è stato compilato correttamente
                    AlertDialog.Builder builderError = new AlertDialog.Builder(ChangePasswordActivity.this, R.style.CustomDialogThemeError);

                    //tutti i possibili errori
                    switch (err){
                        case 2:
                            layoutNewPwd.setEndIconMode(TextInputLayout.END_ICON_NONE);
                            layoutNewPwd.setHelperTextEnabled(true);
                            layoutNewPwd.setHelperText("Inseririsci una password");
                            builderError.setMessage("Perfavore inseririsci una password");
                            break;
                        case 3:
                            layoutNewPwd.setHelperTextEnabled(true);
                            layoutNewPwd.setHelperText("La password è troppo corta");
                            builderError.setMessage("La password è troppo corta. Perfavore assicurati che la nuova password contenga almeno 8 caratteri ;)");
                            break;
                        case 4:
                            layoutNewPwd.setHelperTextEnabled(true);
                            layoutNewPwd.setHelperText("La password deve contenere una lettera minuscola, maiuscola e un carattere speciale (@,#,?,^, ecc...)");
                            builderError.setMessage("La password non rispetti i requisti di sicurezza. Ricorda la password deve contenere: una lettera minuscola, maiuscola e un carattere speciale (@,#,?,^, ecc...) ;)");
                            break;

                        case 5:
                            layoutNewPwd.setHelperTextEnabled(true);
                            layoutNewPwd.setHelperText("Le password non corrispondono");
                            layoutRNewPwd.setHelperTextEnabled(true);
                            layoutRNewPwd.setHelperText("Le password non corrispondono");
                            builderError.setMessage("Le password non corrispondono. Assicurati di aver scritto la stessa password nei campi \"Nuova password\" e \"Conferma nuova password\" ;)");
                            break;
                        default: builderError.setMessage("Perfavore riprova a rinserire i dati o contatta lo sviluppatore"); break;
                    }
                    //settaggi pop-up errore
                           builderError.setTitle("Errore cambio password!")
                            .setIcon(R.drawable.ic_warning);
                    //tasto per chiudere il pop up
                    builderError.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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

        //ogni volta che viene digitato un nuovo carattere nel campo "Nuova password"
        //riappare il toogle per mostrare la password
        editNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                layoutNewPwd.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });

        //ogni volta che viene digitato un nuovo carattere nel campo "Conferma nuova password"
        //riappare il toogle per mostrare la password
        editRepeatNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                layoutRNewPwd.setPasswordVisibilityToggleEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                layoutRNewPwd.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
            }
        });

        //ogni volta che il focus è sul campo nuova password rimuovo gli errori
        //e mostro il toogle per mostrare la password
        editNewPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                layoutNewPwd = findViewById(R.id.textNewPassword);
                //Nasconde l'icona dell'errore
                layoutNewPwd.setErrorEnabled(false);
                layoutNewPwd.setHelperTextEnabled(true);
            }
        });
        //ogni volta che il focus è sul campo nuova password rimuovo gli errori
        //e mostro il toogle per mostrare la password
        layoutRNewPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                layoutRNewPwd = findViewById(R.id.textRepNewPassword);
                //Nasconde l'icona dell'errore
                layoutRNewPwd.setErrorEnabled(false);
                layoutRNewPwd.setHelperTextEnabled(true);
            }
        });
/*************************** Listener button "Torna alla Home" ****************************/
        tornaHome = findViewById(R.id.toHome);
        tornaHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent showResult;
                if(dbHelper.getIsAdmin(utente)) {
                    showResult = new Intent(ChangePasswordActivity.this, AdminHomeActivity.class);
                }else{
                    showResult = new Intent(ChangePasswordActivity.this, UserHomeActivity.class);
                }
                showResult.putExtra(USER_EXTRA, utente);
                startActivityIfNeeded(showResult, 0);
                finish();
            }
        });
    }

    // controllo che i dati inseriti dall'utente siano corretti
    int checkInput(){
        int errors =0;
        String regex = "^(?=.*[a-z])(?=."
                + "*[A-Z])(?=.*\\d)"
                + "(?=.*[-+_!@#$%^&*., ?]).+$";
        // Compile the ReGex
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(editNewPwd.getText().toString());

        if(editNewPwd.getText().toString().length()==0){
            errors = 2;
            showError(editNewPwd,"Inserire la password");

            //la password sia di almeno 8 caratteri
        }else if(editNewPwd.getText().toString().length()<8){
            errors = 3;
            layoutNewPwd = findViewById(R.id.textNewPassword);
            showError(editNewPwd, "La password è troppo corta");

            //controlla che la password rispetti i requisiti di sicurezza
        }else if(!m.matches()){
            errors = 4;
            layoutRNewPwd = findViewById(R.id.textRepNewPassword);
            showError(editNewPwd, "La password deve contenere una lettera minuscola, maiuscola e un carattere speciale (@,#,?,^, ecc...)");
        }else if(!editNewPwd.getText().toString().equals(editRepeatNewPwd.getText().toString())){
            errors = 5;
            showError(editNewPwd, editRepeatNewPwd,layoutNewPwd, layoutRNewPwd,"Le password non corrispondono");
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
    private void showError(EditText input, EditText input2, TextInputLayout layoutNewPwd, TextInputLayout layoutRNewPwd, String Error_msg){
        input.setError(Error_msg);
        input2.setError(Error_msg);
        //Nasconde il toogle per mostrare la password
        layoutNewPwd.setEndIconMode(TextInputLayout.END_ICON_NONE);
        layoutRNewPwd.setEndIconMode(TextInputLayout.END_ICON_NONE);
    }
}




