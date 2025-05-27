package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.adapters.DocumentAdapter;
import com.example.test.models.Document;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.List;

public class DocumentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DocumentAdapter adapter;
    private List<Document> documentList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private Button btnAjouter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);

        recyclerView = findViewById(R.id.recyclerViewDocs);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnAjouter = findViewById(R.id.btnAjouterDocument);

        documentList = new ArrayList<>();
        adapter = new DocumentAdapter(this, documentList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        btnAjouter.setOnClickListener(v -> {
            Intent intent = new Intent(DocumentsActivity.this, AjouterDocumentActivity.class);
            startActivity(intent);
        });

        loadDocuments();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDocuments(); // recharge la liste quand on revient
    }

    private void loadDocuments() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();

        db.collection("documents")
                .whereEqualTo("professeur_id", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    documentList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Document d = doc.toObject(Document.class);
                        documentList.add(d);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur Firestore : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
