package com.example.alfredo.nutritionapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import dmax.dialog.SpotsDialog;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.ColorInfo;
import com.google.api.services.vision.v1.model.DominantColorsAnnotation;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.ImageProperties;
import com.google.api.services.vision.v1.model.SafeSearchAnnotation;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ProgressBar;
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
import com.google.api.services.vision.v1.model.Image;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import butterknife.BindView;
import butterknife.ButterKnife;
import org.tensorflow.lite.Interpreter;

import static android.content.ContentValues.TAG;

public class CameraActivity extends Activity   {
    FirebaseFirestore db;
    RecyclerView.LayoutManager layoutManager;
    private static final int CAMERA_REQUEST_CODE= 1888;
    private static final String MODEL_PATH = "model2.lite";
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static double Kcal_t,PS_t, HC_t, LP_t;
    public static String cantidad, Cantidad_t;
    private static final int RECORD_REQUEST_CODE = 101;
    private static final String CLOUD_VISION_API_KEY = "AIzaSyDIeGAhrjKV2vSHgdbxHc4dzo5dG_k1uMU";
    Spinner spinner;
    String URL="https://api.myjson.com/bins/704ym";
    ArrayList<String> foodName;
    ArrayList<JSONObject> objects;
    String detectado ="";
    String platillo = "Platillo";
    ArrayList<Ingrediente> ingredientes;
    RecyclerView listItem;
    ListItemAdapter adapter;
    String modelFile="model2.lite";
    Interpreter tflite;
    AlertDialog dialog;
    TextView t_kcal, t_marca, t_lp, t_hc, t_ps, t_cantidad;
    DatabaseReference mDataRef;
    Button saveBtn;
    FirebaseAuth mAuth;
    @BindView(R.id.takePicture)
    Button takePicture;

    @BindView(R.id.imageView)
    ImageView imageView;


    private Feature feature;
    private Bitmap bitmap;
    private String[] visionAPI = new String[]{"LANDMARK_DETECTION", "LOGO_DETECTION", "SAFE_SEARCH_DETECTION", "IMAGE_PROPERTIES", "LABEL_DETECTION"};

    private String api = visionAPI[4];




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
        t_cantidad = (TextView)findViewById(R.id.text_cantidad);
        Button saveBtn = (Button)findViewById(R.id.saveBtn2);
        ButterKnife.bind(this);

        feature = new Feature();
        feature.setType(visionAPI[4]);
        feature.setMaxResults(1);
        imageView.setImageResource(R.drawable.cameraicon);
        try {
            tflite=new Interpreter(loadModelFile(CameraActivity.this));
        } catch (IOException e) {
            e.printStackTrace();
        }


        takePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureFromCamera();
            }
        });



        listItem.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listItem.setLayoutManager(layoutManager);



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
                                        //System.out.println("Ing: "+ ing.nombre);
                                        ing.nombre = ingobj.getString("Ingrediente");
                                        ing.Kcal = ingobj.getDouble("Kcal totales");
                                        ing.Cantidad = ingobj.getString("Cantidad");
                                        ing.PS = ingobj.getDouble("PS");
                                        ing.HC = ingobj.getDouble("HC");
                                        System.out.println("HSE: "+ing.HC);
                                        ing.LP = ingobj.getDouble("LP");
                                        //BigDecimal bigkcal = new BigDecimal(Kcal_t, MathContext.DECIMAL64);
                                        Kcal_t+= Math.round(ing.Kcal * 100.0) / 100.0;
                                        PS_t+= Math.round(ing.PS * 100.0) / 100.0;
                                        HC_t+=Math.round(ing.HC * 100.0) / 100.0;
                                        LP_t+= Math.floor(ing.LP * 100.0) / 100.0;
                                        System.out.println("LEPE: "+LP_t);
                                        ingredientes.add(ing);
                                    }
                                    t_kcal.setText("Kcal : "+Math.round(Kcal_t*100.0)/100.0);
                                    t_ps.setText("PS : "+round(PS_t,2));
                                    t_hc.setText("HC : "+HC_t);
                                    t_lp.setText("LP : "+round(LP_t,2));
                                    t_marca.setText("Hecho en casa");
                                    t_cantidad.setText("Cantidad: -");
                                    cantidad = "-";

                                    loadData();

                                }
                                else{loadData();
                                    Kcal_t = objects.get(x).getDouble("Kcal totales");
                                    PS_t = objects.get(x).getDouble("PS");
                                    HC_t = objects.get(x).getDouble("HC");
                                    LP_t = objects.get(x).getDouble("LP");
                                    Cantidad_t = objects.get(x).getString("Cantidad");

                                    t_kcal.setText("Kcal : "+ Kcal_t);
                                    t_ps.setText("PS : "+ round(PS_t,2));
                                    t_hc.setText("HC : "+ HC_t);
                                    t_lp.setText("LP : "+round(LP_t,2));
                                    cantidad = objects.get(x).getString("Cantidad");
                                    t_cantidad.setText("Cantidad : "+cantidad);
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


    }


    private void loadSpinnerData(String url) {
        foodName.clear();
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
                            //System.out.println("platillo :"+jsonObject1);
                            System.out.println("platillo :"+platillo);
                            System.out.println("detected :"+detectado);
                            System.out.println(platillo.contains(detectado));

                            if(platillo!=null && platillo.contains(detectado)) {
                                System.out.println("entre");
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
            Kcal_t+=  x.Kcal;
            Kcal_t = round(Kcal_t,2);
            PS_t+= x.PS;
            PS_t = round(PS_t,2);
            HC_t+=x.HC;
            HC_t = round(HC_t,2);
            LP_t+= x.LP;
            LP_t = round(LP_t,2);
        t_kcal.setText("Kcal : "+ Kcal_t);
        t_ps.setText("PS : "+ PS_t);
        t_hc.setText("HC : "+ HC_t);
        t_lp.setText("LP : "+LP_t);

        return super.onContextItemSelected(item);

    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (checkPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            takePicture.setVisibility(View.VISIBLE);
        } else {
            takePicture.setVisibility(View.INVISIBLE);
            makeRequest(Manifest.permission.CAMERA);
        }
    }

    private int checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission);
    }

    private void makeRequest(String permission) {
        ActivityCompat.requestPermissions(this, new String[]{permission}, RECORD_REQUEST_CODE);
    }

    public void takePictureFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            callCloudVision(bitmap, feature);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == RECORD_REQUEST_CODE) {
            if (grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_DENIED
                    && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                finish();
            } else {
                takePicture.setVisibility(View.VISIBLE);
            }
        }
    }

    private void callCloudVision(final Bitmap bitmap, final Feature feature) {
        final List<Feature> featureList = new ArrayList<>();
        featureList.add(feature);

        final List<AnnotateImageRequest> annotateImageRequests = new ArrayList<>();

        AnnotateImageRequest annotateImageReq = new AnnotateImageRequest();
        annotateImageReq.setFeatures(featureList);
        annotateImageReq.setImage(getImageEncodeImage(bitmap));
        annotateImageRequests.add(annotateImageReq);


        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {

                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    VisionRequestInitializer requestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY);

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(annotateImageRequests);

                    Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    return convertResponseToString(response);
                } catch (GoogleJsonResponseException e) {
                    Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                    Log.d(TAG, "failed to make API request because of other IOException " + e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                detectado =  (detectado.substring(0, 1).toUpperCase() + detectado.substring(1)).trim();
                if(detectado.equals("Hamburger")){
                    detectado = "Hamburguesa";
                }
                if(detectado.equals("Popcorn")){
                    detectado = "Palomitas";
                }
                Toast.makeText(getApplicationContext(),"Detectado: "+detectado,Toast.LENGTH_LONG).show();

            }
        }.execute();
    }

    @NonNull
    private Image getImageEncodeImage(Bitmap bitmap) {
        Image base64EncodedImage = new Image();
        // Convert the bitmap to a JPEG
        // Just in case it's a format that Android understands but Cloud Vision
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();

        // Base64 encode the JPEG
        base64EncodedImage.encodeContent(imageBytes);
        return base64EncodedImage;
    }

    private String convertResponseToString(BatchAnnotateImagesResponse response) {

        AnnotateImageResponse imageResponses = response.getResponses().get(0);

        List<EntityAnnotation> entityAnnotations;

        String message = "";

                entityAnnotations = imageResponses.getLabelAnnotations();
                message = formatAnnotation(entityAnnotations);

        return message;
    }


    private String formatAnnotation(List<EntityAnnotation> entityAnnotation) {
        String message = "";

        if (entityAnnotation != null) {
            for (EntityAnnotation entity : entityAnnotation) {
                message = message + entity.getDescription();
                message += "\n";
            }
        } else {
            message = "Nothing Found";
        }
        detectado = (message.substring(0, 1).toUpperCase() + message.substring(1)).trim();
        if(detectado.equals("Hamburger")){
            detectado = "Hamburguesa";
        }
        if(detectado.equals("Popcorn")){
            detectado = "Palomitas";
        }

        loadSpinnerData(URL);
        return message;
    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("model2.tfile");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }










}










