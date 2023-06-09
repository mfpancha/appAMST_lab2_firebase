package com.example.appamst_lab2_firebase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PerfilUsuario extends AppCompatActivity {
    TextView txt_id, txt_name, txt_email, txt_tweet;
    ImageView imv_photo;
    DatabaseReference db_reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);
        Intent intent = getIntent();
        HashMap<String, String> info_user = (HashMap<String, String>) intent.getSerializableExtra("info_user");
        System.out.println("Informacion"+ info_user);
        txt_id = findViewById(R.id.txt_userId);
        txt_name = findViewById(R.id.txt_nombre);
        txt_email = findViewById(R.id.txt_correo);
        imv_photo = findViewById(R.id.imv_foto);
        txt_tweet = findViewById(R.id.txt_tweet);

        txt_id.setText(info_user.get("user_id"));
        txt_name.setText(info_user.get("user_name"));
        txt_email.setText(info_user.get("user_email"));
        String photo = info_user.get("user_photo");
        Picasso.get().load(photo).into(imv_photo);

        iniciarBaseDeDatos();
        leerTweets();

        Button btnEnviarTweet = findViewById(R.id.btn_enviar);
        btnEnviarTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publicarTweet(info_user.get("user_name"));
            }
        });
    }
    public void cerrarSesion(View view) {
        FirebaseAuth.getInstance().signOut();
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("msg", "cerrarSesion");
        startActivity(intent);
    }

    public void iniciarBaseDeDatos() {
        db_reference = FirebaseDatabase.getInstance().getReference().child("Grupos");
    }
    public void leerTweets() {
        db_reference.child("Grupo 2").child("Tweets")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            System.out.println(snapshot);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        System.out.println(error.toException());
                    }
                });
    }

    public void escribirTweets(String autor, String tweet, String fecha) {
        Map<String, String> hola_tweet = new HashMap<String, String>();
        hola_tweet.put("autor", autor);
        hola_tweet.put("fecha", fecha);
        DatabaseReference tweets = db_reference.child("Grupo 2").child("Tweets");
        DatabaseReference nuevoTweetRef = tweets.push(); //  Generar una nueva clave única
        nuevoTweetRef.child(tweet).child("autor").setValue(autor);
        nuevoTweetRef.child(tweet).child("fecha").setValue(fecha);
    }

    public void publicarTweet(String autor) {
        // Obtén el texto del tweet desde txt_tweet
        String tweetText = txt_tweet.getText().toString();

        // Verifica si el tweet no está vacío
        if (!tweetText.isEmpty()) {
            // Obtiene la fecha actual
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String fecha = dateFormat.format(new Date());

            // Llama al método escribirTweets con el autor, texto del tweet y fecha
            escribirTweets(autor, tweetText, fecha);
            Toast.makeText(this, "El tweet ha sido enviado", Toast.LENGTH_SHORT).show();

            // Limpia el contenido del EditText después de publicar el tweet
            txt_tweet.setText("");
        } else {
            // El tweet está vacío, muestra un mensaje de error o realiza alguna acción apropiada
            Toast.makeText(this, "Ingrese un tweet antes de enviarlo", Toast.LENGTH_SHORT).show();
        }

    }

}