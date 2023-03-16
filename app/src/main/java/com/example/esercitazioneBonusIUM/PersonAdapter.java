package com.example.esercitazioneBonusIUM;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.esercitazioneBonusIUM.dataBase.FeedReaderDbHelper;
import com.example.myapplication.R;
import java.util.ArrayList;
import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.MyHolder> {

    private Context context;
    private List<Person> arrayUtenti = new ArrayList<>();
    private String user;

    public PersonAdapter(Context context, List<Person> arrayUtenti, String user) {
        this.context = context;
        this.arrayUtenti = arrayUtenti;
        this.user = user;
        layoutInflater = LayoutInflater.from(context);
    }

    LayoutInflater layoutInflater;

    //crea un nuovo ItemViewHolder
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //crea una nuova vista a partire dal layout xml
        View view = layoutInflater.inflate(R.layout.preview_user_search, parent, false);
        //crea un nuovo ItemViewHolder e lo restituisce
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    /**nel onBindViewHolder vengono svolte tre operazioni:
     - l'invocazione del metodo bind della classe ItemViewHolder per impostare
       la posizione corrente dell'elemento;
     - il caricamento dei dati dalla lista alla vista;
     - la cattura dell'interazione dello switch*/
    @Override
    public void onBindViewHolder(@NonNull PersonAdapter.MyHolder holder, int position) {

        //salvo l'ultima posizione dell'elemento corrente
        int lastPosition = holder.getAdapterPosition();

        //inizializza i campi dell'elemento corrente
        holder.username.setText((arrayUtenti.get(position).getUsername()));
        holder.userImg.setImageURI(Uri.parse(arrayUtenti.get(position).getPhoto()));
        holder.aSwitch.setChecked(arrayUtenti.get(position).getIsAdmin());

        //cambia il testo dello switch in base ai suoi permessi
        if(arrayUtenti.get(position).getIsAdmin()) {
            holder.aSwitch.setText("Permessi abilitati");
        }else {
            holder.aSwitch.setText("Permessi non abilitati");
        }

        //lo stesso utente non può togliersi i permessi di ADMIN e nessun
        //utente con questi permessi può toglierli all'utente "admin"
        if(this.user.equals(arrayUtenti.get(position).getUsername()) || arrayUtenti.get(position).getUsername().equals("admin")){
            holder.aSwitch.setVisibility(View.GONE);
        }

        //se lo switch cambia "valore" aggiorna i permessi dell'utente nel database
        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Per connettersi al database
                FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                //salva l'username dell'Adapter in cui è stato sollevato l'onCheckedChanged
                user = arrayUtenti.get(lastPosition).getUsername();

                //aggiorna isAdmin nel database
                dbHelper.updateIsAdmin(user, isChecked,context);

                //aggiorna il testo dello switch
                if(isChecked) {
                    buttonView.setText("Permessi abilitati");
                }else {
                    buttonView.setText("Permessi non abilitati");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayUtenti.size();
    }


/**   È la chiave di volta tra il RecyclerView e l'Adapter e permette la
      riduzione nel numero di view da creare.
      Un oggetto MyHolder infatti fornisce il layout da popolare con i dati
      presenti nel DataSource e viene riutilizzato dal RecyclerView per
      ridurre il numero di layout da creare per popolare la lista.          **/
    public class MyHolder extends RecyclerView.ViewHolder {
        TextView username;
        ImageView userImg;
        Switch aSwitch;

        //Costruttore
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            for(Person item : arrayUtenti) {
                username = itemView.findViewById(R.id.user_nickname);
                userImg = itemView.findViewById(R.id.user_img);
                aSwitch = itemView.findViewById(R.id.switcher);

            }
        }
    }
}