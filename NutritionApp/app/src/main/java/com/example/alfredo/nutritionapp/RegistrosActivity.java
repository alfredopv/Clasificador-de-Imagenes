package com.example.alfredo.nutritionapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dmax.dialog.SpotsDialog;

import static android.content.ContentValues.TAG;

public class RegistrosActivity extends Activity {
    List<Alimento> alimentoList = new ArrayList<>();
    FirebaseFirestore db;
    String KU;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    RecyclerView listItem;
    RecyclerView.LayoutManager layoutManager;
    FloatingActionButton fab;
    public TextView  PS_t,LP_t,HC_t,Kcal_t;
    TextView kcal_h, hc_h,lp_h,ps_h;
    public double t_PS,t_LP, t_HC, t_Kcal;
    public boolean isUpdate = false;
    public String idUpdate = "";
    ListItemAdapter2 adapter;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registros);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        System.out.println("KURRENT USER" + currentFirebaseUser.getUid());
        KeyUser keyUser = new KeyUser();
        KU = keyUser.KU;
        db = FirebaseFirestore.getInstance();
        dialog  = new SpotsDialog(this);
        Kcal_t = (TextView)findViewById(R.id.Kcal_t);
        PS_t = (TextView)findViewById(R.id.PS_t);
        HC_t = (TextView)findViewById(R.id.HC_t);
        LP_t = (TextView)findViewById(R.id.LP_t);
        kcal_h = (TextView)findViewById(R.id.Kcal_t);
        hc_h = (TextView)findViewById(R.id.HC_t);
        ps_h = (TextView)findViewById(R.id.PS_t);
        lp_h = (TextView)findViewById(R.id.LP_t);
        listItem = (RecyclerView)findViewById(R.id.listTodo3);
        listItem.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listItem.setLayoutManager(layoutManager);
        loadData();


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals("DELETE"))
                deleteItem(item.getOrder());

        return super.onContextItemSelected(item);

    }

    private void deleteItem(int index) {
        db.collection("Comida")
                .document(alimentoList.get(index).getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadData();
                    }
                });
    }

    private void updateData(String name, String description, String price) {
        db.collection("Products").document(idUpdate)
                .update("name",name,"description",description,"price",price,"imgurl",
                        "https://broxtechnology.com/images/iconos/box.png","userID", KU)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegistrosActivity.this, "Updated !", Toast.LENGTH_SHORT).show();
                    }
                });

        db.collection("Products").document(idUpdate)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                        loadData();
                    }
                });

    }

    private void setData(String name, String description, String price) {
        String id = UUID.randomUUID().toString();
        Map<String,Object> todo = new HashMap<>();
        todo.put("id",id);
        todo.put("name",name);
        todo.put("description",description);
        todo.put("price",price);
        todo.put("imgurl","https://broxtechnology.com/images/iconos/box.png");
        todo.put("userID",KU);

        db.collection("Products").document(id)
                .set(todo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadData();

            }
        });

    }

    private void loadData() {
        dialog.show();
        t_Kcal = 0;
        t_HC = 0;
        t_PS = 0;
        t_LP = 0;

        if(alimentoList.size()>0)
            alimentoList.clear();
        db.collection("Comida")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot doc:task.getResult())
                        {

                            if(doc.getString("userID").equals(KU)){
                            Alimento alimento = new Alimento(doc.getString("Platillo"),
                                    doc.getString("Cantidad"),
                                    doc.getString("Marca"),
                                    doc.getString("id"),
                                   doc.getDouble("HC"),
                                   doc.getDouble("PS"),
                                   doc.getDouble("LP"),
                                    doc.getDouble("Kcal")


                            );
                            t_Kcal+= alimento.getKcal();
                            System.out.println("kcal: "+t_Kcal);
                            System.out.println();
                            t_HC+= alimento.getHC();
                            t_PS+= alimento.getPS();
                            t_LP+= alimento.getLP();
                            System.out.println("id: "+alimento.getId());
                            alimentoList.add(alimento);
                        }
                        }
                        kcal_h.setText("Calorias totales: "+t_Kcal);
                        lp_h.setText("LP: "+t_LP);
                        ps_h.setText("PS : "+t_PS);
                        hc_h.setText("HC : "+t_HC);
                        Context context = RegistrosActivity.this;
                        adapter = new ListItemAdapter2(RegistrosActivity.this, alimentoList, context);
                        listItem.setAdapter(adapter);
                        dialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegistrosActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();


                    }
                });


    }

    public void click(int index) {

/*
        db.collection("Products")
                .document(productList.get(index).getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    Product todo2 = new Product(doc.getString("id"),
                            doc.getString("name"),
                            doc.getString("description"),
                            doc.getString("price"),
                            doc.getString("imgurl"),
                            doc.getString("userID")
                    );
                    Intent it = new Intent( MainActivity.this, DetailActivity.class);
                    it.putExtra("id", todo2.getId());
                    it.putExtra("name", todo2.getName());
                    it.putExtra("description", todo2.getDescription());
                    it.putExtra("imgurl",todo2.getImgurl());
                    it.putExtra("price",todo2.getPrice());
                    startActivity(it);
                    finish();

                    if (doc != null && doc.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + doc.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent a = new Intent(this,FirstActivity.class);
            a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(a);
            return true;
        }
        return super.onKeyDown(keyCode, event);
        */
    }

}
