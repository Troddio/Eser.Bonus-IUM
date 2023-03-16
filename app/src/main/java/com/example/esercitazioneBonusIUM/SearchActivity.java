package com.example.esercitazioneBonusIUM;

import static android.view.View.GONE;
import static com.example.esercitazioneBonusIUM.LoginActivity.USER_EXTRA;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.esercitazioneBonusIUM.dataBase.FeedReaderDbHelper;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private String utente;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private PersonAdapter  personAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Person> personList = new ArrayList<>();
    private List<Person> searchList = new ArrayList<>();
    private TextView tutorial1, tutorial2;
    private ImageView tutorial3;
    private Button tornaHome;
    private FeedReaderDbHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search);

        //Per connettersi al database
        dbHelper = new FeedReaderDbHelper(SearchActivity.this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        utente = (String) getIntent().getSerializableExtra(USER_EXTRA);

        tutorial1 = findViewById(R.id.tutorial_1);
        tutorial2 = findViewById(R.id.tutorial_2);
        tutorial3 = findViewById(R.id.tutorial_3);
        personList.addAll(dbHelper.getAllPersons());

        //controlla che il database contenga almeno un'utente
        if (personList.isEmpty()) {
            tutorial1.setText("Nessun utente trovato!");
            tutorial1.setTextColor(getResources().getColor(R.color.redError));
            tutorial1.setVisibility(View.VISIBLE);
            tutorial2.setVisibility(GONE);
            tutorial3.setVisibility(GONE);
        }

        //inizializza la recyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        searchView = findViewById(R.id.searchView);
/*********************** Listener Barra di ricerca" ************************/
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Recupera l'username dell'utente corrente dalla MainActivity
                utente = (String) getIntent().getSerializableExtra(USER_EXTRA);

                // Se la lunghezza della stringa cercata è maggiore di zero
                if (query.length() > 0) {
                    // Cancella la lista degli elementi cercati in precedenza
                    searchList.clear();

                    // Cicla attraverso ogni persona nella lista completa delle persone (personList)
                    for (Person item : personList) {
                        // Se il nome utente dell'elemento contiene la stringa cercata, lo aggiunge alla lista degli elementi trovati (searchList)
                        if (item.getUsername().toLowerCase().contains(query.toLowerCase())) {
                            searchList.add(item);
                        }
                    }

                    // Nasconde i tutorial
                    tutorial1.setVisibility(GONE);

                    // Recupera la recyclerView e la aggiorna con la nuova lista degli elementi trovati
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    PersonAdapter personAdapter = new PersonAdapter(SearchActivity.this, searchList, utente);
                    recyclerView.setAdapter(personAdapter);
                } else {
                    // Se la stringa cercata è vuota, mostra tutti gli elementi nella lista completa delle persone (personList)

                    // Recupera la recyclerView e la aggiorna con la lista completa degli elementi
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    PersonAdapter personAdapter = new PersonAdapter(SearchActivity.this, personList, utente);
                    recyclerView.setAdapter(personAdapter);
                }
                return false;
            }


            // Metodo chiamato quando l'utente digita o cancella un carattere nella searchView
            @Override
            public boolean onQueryTextChange(String query) {
                // Ottiene l'username dell'utente attualmente loggato dalla Intent che ha avviato questa Activity
                utente = (String) getIntent().getSerializableExtra(USER_EXTRA);
                if (query.length() > 0) { // se la query non è vuota
                    searchList.clear(); // cancello tutti gli elementi dalla lista dei risultati
                    for (Person user : personList) { // per ogni utente nella lista completa
                        if (user.getUsername().toLowerCase().contains(query.toLowerCase()) && !utente.equals(user.getUsername())) {
                            // se il nome utente contiene la query (ignorando le maiuscole e minuscole) e non è l'utente attualmente loggato
                            searchList.add(user); // aggiunge l'utente alla lista dei risultati
                        }
                    }
                    tutorial1 = findViewById(R.id.tutorial_1);
                    tutorial2 = findViewById(R.id.tutorial_2);
                    tutorial3 = findViewById(R.id.tutorial_3);
                    tutorial1.setVisibility(GONE);
                    tutorial2.setVisibility(GONE);
                    tutorial3.setVisibility(GONE);
                    // Imposta il LayoutManager per la RecyclerView
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    // Crea un nuovo adapter con la lista dei risultati filtrati e lo imposta sulla RecyclerView
                    PersonAdapter personAdapter = new PersonAdapter(SearchActivity.this, searchList, utente);
                    recyclerView.setAdapter(personAdapter);
                } else { // se la query è vuota
                    searchList.clear(); // cancello tutti gli elementi dalla lista dei risultati
                    for (Person user : personList) { // per ogni utente nella lista completa
                        if (user.getUsername().toLowerCase().contains(query.toLowerCase())) {
                            // se il nome utente contiene la query (ignorando le maiuscole e minuscole)
                            searchList.add(user); // aggiunge l'utente alla lista dei risultati
                        }
                    }
                    // Imposta il LayoutManager per la RecyclerView
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(SearchActivity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    // Crea un nuovo adapter con la lista dei risultati filtrati e lo imposta sulla RecyclerView
                    PersonAdapter personAdapter = new PersonAdapter(SearchActivity.this, searchList, utente);
                    recyclerView.setAdapter(personAdapter);
                }
                return false;
            }
        });

/*************************** Listener button "Torna alla Home" ****************************/
        //recupera il riferimento al pulsante "Torna alla Home"
        tornaHome = findViewById(R.id.toHome);
        //imposta il listener di onClick sul pulsante
        tornaHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dichiara un'intent per mostrare il risultato
                Intent showResult;
                //controlla se l'utente è un admin e lo reindirizza alla relativa Home
                if (dbHelper.getIsAdmin(utente)) {
                    showResult = new Intent(SearchActivity.this, AdminHomeActivity.class);
                } else {
                    showResult = new Intent(SearchActivity.this, UserHomeActivity.class);
                }
                //aggiunge il parametro extra all'intent
                showResult.putExtra(USER_EXTRA, utente);
                //avvia l'activity per mostrare il risultato
                startActivityIfNeeded(showResult, 0);
                //chiude l'activity corrente
                finish();
            }
        });

    }
}

