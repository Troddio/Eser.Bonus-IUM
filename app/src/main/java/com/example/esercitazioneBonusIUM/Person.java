package com.example.esercitazioneBonusIUM;

import android.net.Uri;

import com.example.myapplication.R;

import java.io.Serializable;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Person implements Serializable {

    private String username;
    private String password;
    private String address;
    private String dataNascita;
    private Calendar birthDate;
    private Boolean isAdmin;
    public Boolean photoIsSet;
    private Uri photo;
    private List<Person> listaUtenti = new ArrayList<>(); // da cancellare se creo il DB
    private Calendar cal = Calendar.getInstance();

    public Person(String username, String password, String indirizzo, Calendar birthDate, Boolean isAdmin) {
        if (listaUtenti.isEmpty()) {
            this.username = username;
            this.password = password;
            this.address = indirizzo;
            this.birthDate = birthDate;
            this.isAdmin = isAdmin;
        }
    }

    public Person(String username, String password, String address, String dataNascita, Uri photo, Boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.address = address;
        this.dataNascita = dataNascita;
        this.photo = photo;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public String getBirthDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String bdayString = sdf.format(this.birthDate.getTime());
        return bdayString;
    }

    public Boolean getIsAdmin(){
        return this.isAdmin;
    }

    public String getPhoto() {
        Uri uri;
        if(this.photo == null && this.isAdmin){
            uri = Uri.parse("android.resource://com.example.myapplication/" + R.drawable.foto_default_admin);
            return uri.toString();
        }else if(this.photo == null && !this.isAdmin) {
            uri = Uri.parse("android.resource://com.example.myapplication/" + R.drawable.foto_default_user);
            return uri.toString();
        }else{
            return photo.toString();
        }
    }
}
