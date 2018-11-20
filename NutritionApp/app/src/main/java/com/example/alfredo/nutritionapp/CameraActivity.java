package com.example.alfredo.nutritionapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import dmax.dialog.SpotsDialog;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.content.ContentValues.TAG;


public class CameraActivity extends Activity {
    FirebaseFirestore db;
    RecyclerView.LayoutManager layoutManager;
    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static double Kcal_t,PS_t, HC_t, LP_t;
    public static String cantidad;
    Spinner spinner;
    String URL="https://api.myjson.com/bins/al3pi";
    ArrayList<String> foodName;
    ArrayList<JSONObject> objects;
    String detectado ="Ensalada";
    String platillo = "Platillo";
    ArrayList<Ingrediente> ingredientes;
    RecyclerView listItem;
    ListItemAdapter adapter;
    AlertDialog dialog;
    TextView t_kcal, t_marca, t_lp, t_hc, t_ps;
    DatabaseReference mDataRef;
    Button saveBtn;
    FirebaseAuth mAuth;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_camera);
        final KeyUser keyUser = new KeyUser();
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        foodName=new ArrayList<>();
        objects=new ArrayList<>();
        ingredientes= new ArrayList<>();
        dialog  = new SpotsDialog(this);
        listItem = (RecyclerView)findViewById(R.id.listTodo2);
        t_hc = (TextView) findViewById(R.id.text_hc);
        t_kcal = (TextView) findViewById(R.id.text_kcal);
        t_marca = (TextView) findViewById(R.id.text_marca);
        t_lp = (TextView) findViewById(R.id.text_lp);
        t_ps = (TextView)findViewById(R.id.text_ps);
        Button saveBtn = (Button)findViewById(R.id.saveBtn2);



        listItem.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listItem.setLayoutManager(layoutManager);


        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
        Button photoButton = (Button) this.findViewById(R.id.button1);
         spinner = findViewById(R.id.spinner1);
       // String[] items = new String[]{"1", "2", "three"};
        loadSpinnerData(URL);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String id = UUID.randomUUID().toString();
                    Map<String,Object> todo = new HashMap<>();
                    todo.put("id",id);
                    todo.put("Platillo",platillo);
                    todo.put("Kcal",Kcal_t);
                    todo.put("LP",LP_t);
                    todo.put("PS",PS_t);
                    todo.put("HC",HC_t);
                    todo.put("Marca",t_marca.getText().toString());
                    todo.put("userID",keyUser.KU);
                    todo.put("Cantidad",cantidad);
                    todo.put("userUUID",currentFirebaseUser.getUid());


                db.collection("Comida").document(id)
                            .set(todo).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"Comida añadida a tu registro",Toast.LENGTH_LONG).show();


                        }
                    });

                }



        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ingredientes.clear();
                Kcal_t= 0; LP_t = 0; HC_t = 0; PS_t = 0;
                 platillo=   spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
                Toast.makeText(getApplicationContext(),platillo,Toast.LENGTH_LONG).show();
                for(int x=0;x<objects.size();x++) {
                    try {
                            String p =objects.get(x).getString("Platillo");
                            System.out.println("p:" + p);
                        System.out.println("plat:" + platillo);
                        System.out.println(p.equals(platillo));

                        if(p.equals(platillo)) {
                                if(objects.get(x).getString("Marca").equals("Casa")){
                                   JSONArray ingarray =objects.get(x).getJSONArray("Ingredientes");
                                   System.out.println("ing: "+ingarray);
                                    for(int b=0; b<ingarray.length();b++){
                                        JSONObject ingobj = ingarray.getJSONObject(b);
                                        Ingrediente ing = new Ingrediente();
                                        System.out.println("Ing: "+ ing.nombre);
                                        ing.nombre = ingobj.getString("Ingrediente");
                                        ing.Kcal = ingobj.getDouble("Kcal totales");
                                        ing.Cantidad = ingobj.getString("Cantidad");
                                        ing.PS = ingobj.getDouble("PS");
                                        ing.HC = ingobj.getDouble("HC");
                                        ing.LP = ingobj.getDouble("LP");
                                        Kcal_t+= Math.round(ing.Kcal * 100.0) / 100.0;
                                        PS_t+= Math.round(ing.PS * 100.0) / 100.0;
                                        HC_t+=Math.round(ing.HC * 100.0) / 100.0;
                                        System.out.println("kcalt: "+PS_t);
                                        LP_t+= Math.round(ing.LP * 100.0) / 100.0;
                                        ingredientes.add(ing);
                                    }
                                    t_kcal.setText("Kcal : "+Kcal_t);
                                    t_ps.setText("PS : "+PS_t);
                                    t_hc.setText("HC : "+HC_t);
                                    t_lp.setText("LP : "+LP_t);
                                    t_marca.setText("Hecho en casa");
                                    cantidad = "-";

                                    loadData();

                                }
                                else{loadData();
                                    Kcal_t = objects.get(x).getDouble("Kcal totales");
                                    PS_t = objects.get(x).getDouble("PS");
                                    HC_t = objects.get(x).getDouble("HC");
                                    LP_t = objects.get(x).getDouble("LP");
                                    t_kcal.setText("Kcal : "+ Kcal_t);
                                    t_ps.setText("PS : "+ PS_t);
                                    t_hc.setText("HC : "+ HC_t);
                                    t_lp.setText("LP : "+LP_t);
                                    cantidad = objects.get(x).getString("Cantidad");
                                    if(objects.get(x).getString("Marca") != null)
                                    t_marca.setText(objects.get(x).getString("Marca"));
                                }

                            }

                    }
                    catch (JSONException e){e.printStackTrace();}
                }


            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        photoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_CAMERA_PERMISSION_CODE);
                } else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }
    private void loadSpinnerData(String url) {
        RequestQueue requestQueue=Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonObject=new JSONObject(response);
                    //if(jsonObject.getInt("success")==1){
                        JSONArray jsonArray=jsonObject.getJSONArray("Platillos");
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject jsonObject1=jsonArray.getJSONObject(i);
                            objects.add(jsonObject1);
                            String platillo =jsonObject1.getString("Platillo");
                            System.out.println("platillo :"+jsonObject1);
                            System.out.println("platillo :"+platillo);
                            if(platillo!=null &&platillo.contains(detectado)) {
                                foodName.add(platillo);
                            }
                        }
                   // }
                    spinner.setAdapter(new ArrayAdapter<String>(CameraActivity.this, android.R.layout.simple_spinner_dropdown_item, foodName));
                }catch (JSONException e){e.printStackTrace();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }
    private void loadData() {
        dialog.show();
        Context context = CameraActivity.this;
                        adapter = new ListItemAdapter(CameraActivity.this, ingredientes, context);
                        listItem.setAdapter(adapter);
        listItem.getRecycledViewPool().clear();
        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
    public boolean onContextItemSelected(MenuItem item) {
        Ingrediente x = new Ingrediente();
        if(item.getTitle().equals("Añadir porcion"))
             x = ingredientes.get(item.getOrder());
            Kcal_t+= x.Kcal;
            PS_t+= x.PS;
            HC_t+=x.HC;
            LP_t+= x.LP;
        t_kcal.setText("Kcal : "+ Kcal_t);
        t_ps.setText("PS : "+ PS_t);
        t_hc.setText("HC : "+ HC_t);
        t_lp.setText("LP : "+LP_t);

        return super.onContextItemSelected(item);

    }



}










