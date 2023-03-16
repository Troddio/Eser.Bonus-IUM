package com.example.esercitazioneBonusIUM.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.widget.Toast;
import com.example.esercitazioneBonusIUM.Person;
import java.util.ArrayList;
import java.util.List;

public class FeedReaderDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "my_db";
    private static final String TABLE_PERSONS = "persons";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_BDAY = "birthDate";
    private static final String KEY_PHOTO = "photo";
    private static final String KEY_ISADMIN = "isAdmin";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //crea la tabella persons
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_PERSONS + "("
                + KEY_USERNAME + " TEXT PRIMARY KEY,"
                + KEY_PASSWORD + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_BDAY + " DATE,"
                + KEY_PHOTO + " TEXT,"
                + KEY_ISADMIN + " BOOLEAN" + ");";
        db.execSQL(CREATE_USERS_TABLE);
    }

    //per il drop della tabella persons
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSONS);
        onCreate(db);
    }

    //per aggiungere un'utente al database
    public void addPerson(Person person) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, person.getUsername());
        values.put(KEY_PASSWORD, person.getPassword());
        values.put(KEY_ADDRESS, person.getAddress());
        values.put(KEY_BDAY, person.getBirthDate());
        values.put(KEY_ISADMIN, person.getIsAdmin());

        db.insert(TABLE_PERSONS, null, values);
        db.close();
    }

    //aggiorna l'uri del campo foto
    public void addPhoto (String usernameUtente, Uri uri, Context context){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PHOTO, uri.toString());
        int rowsUpdated = db.update(TABLE_PERSONS, values, KEY_USERNAME + " = ?", new String[] { usernameUtente });
        if(rowsUpdated == -1){
            Toast.makeText(context, "Caricamento foto fallita!", Toast.LENGTH_LONG);
        }
        db.close();
    }

    //aggiorna la password dell'utente
    public void updatePassword (String usernameUtente, String newPassword, Context context){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PASSWORD, newPassword);
        int rowsUpdated = db.update(TABLE_PERSONS, values, KEY_USERNAME + " = ?", new String[] { usernameUtente });
        if(rowsUpdated == -1){
            Toast.makeText(context, "Aggiornamento fallito nel DB!", Toast.LENGTH_LONG);
        }
        db.close();
    }

    //aggiorna i permessi admin dell'utente
    public void updateIsAdmin (String usernameUtente, Boolean newIsAdmin, Context context){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ISADMIN, newIsAdmin);
        int rowsUpdated = db.update(TABLE_PERSONS, values, KEY_USERNAME + " = ?", new String[] { usernameUtente });
        if(rowsUpdated == -1){
            Toast.makeText(context, "Aggiornamento fallito nel DB!", Toast.LENGTH_LONG);
        }
        db.close();
    }

    //restituisce l'username dell'utente
    public String getUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String personUsername;

        Cursor cursor = db.query(TABLE_PERSONS, new String[] { KEY_USERNAME,
                        KEY_PASSWORD, KEY_ADDRESS, KEY_BDAY, KEY_PHOTO, KEY_ISADMIN }, KEY_USERNAME + "=?",
                new String[] { username }, null, null, null, null);
        if (cursor != null){
            if(cursor.moveToFirst()) {
                personUsername = cursor.getString(0);
                cursor.close();
                return personUsername;
            }else{
                cursor.close();
                return null;
            }
        }else{
            return null;
        }
    }
    //restituisce la password NON criptata dell'utente
    public String getPassword(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PERSONS, new String[] { KEY_USERNAME,
                        KEY_PASSWORD, KEY_ADDRESS, KEY_BDAY, KEY_PHOTO, KEY_ISADMIN }, KEY_USERNAME + "=?",
                new String[] { username }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        String personPassword = cursor.getString(1);
        cursor.close();
        return personPassword;
    }
    //restituisce la residenza dell'utente
    public String getAddress(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PERSONS, new String[] { KEY_USERNAME,
                        KEY_PASSWORD, KEY_ADDRESS, KEY_BDAY, KEY_PHOTO, KEY_ISADMIN }, KEY_USERNAME + "=?",
                new String[] { username }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        String personAddress = cursor.getString(2);
        cursor.close();
        return personAddress;
    }
    //restituisce la data di nascita dell'utente nel formato yyyy/mm/dd
    public String getBirthDate(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PERSONS, new String[] { KEY_USERNAME,
                        KEY_PASSWORD, KEY_ADDRESS, KEY_BDAY, KEY_PHOTO, KEY_ISADMIN }, KEY_USERNAME + "=?",
                new String[] { username }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        String personBirthDate = cursor.getString(3);
        cursor.close();
        return personBirthDate;
    }
    //restituisce l'uri della foto dell'utente sottoforma di string
    public String getPhoto(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PERSONS, new String[] { KEY_USERNAME,
                        KEY_PASSWORD, KEY_ADDRESS, KEY_BDAY, KEY_PHOTO, KEY_ISADMIN }, KEY_USERNAME + "=?",
                new String[] { username }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        String personPhoto = cursor.getString(4);
        cursor.close();
        return personPhoto;
    }
    //restituisce true se l'utente è admin false altrimenti
    public boolean getIsAdmin(String username) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PERSONS, new String[] { KEY_USERNAME,
                        KEY_PASSWORD, KEY_ADDRESS, KEY_BDAY, KEY_PHOTO, KEY_ISADMIN }, KEY_USERNAME + "=?",
                new String[] { username }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        boolean personIsAdmin = (cursor.getInt(5) == 1);
        cursor.close();
        return personIsAdmin;
    }
    //verifica se la tabella ha almeno un'istanza
    public boolean isTableEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT count(*) FROM " + TABLE_PERSONS;
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        return count == 0;
    }
    //restituisce tutti gli utenti registrati sotto forma di Lista<Person>
    public List<Person> getAllPersons() {
        List<Person> persons = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_PERSONS;
        Cursor cursor = db.rawQuery(selectQuery, null);
        Uri photo= null;
        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(0);
                String password = cursor.getString(1);
                String address = cursor.getString(2);
                String dataNascita = cursor.getString(3);
                String photoS = cursor.getString(4);
                Boolean isAdmin = (cursor.getInt(5) == 1);
                if(photoS != null) {
                    photo = Uri.parse(photoS);
                }
                Person person = new Person(username, password, address, dataNascita, photo, isAdmin);
                persons.add(person);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return persons;
    }
}

