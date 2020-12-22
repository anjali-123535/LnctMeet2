package com.example.lnctmeet2.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lnctmeet2.R;
import com.example.lnctmeet2.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = AddActivity.class.getName();
    private ImageButton btn_img;
    private static final int PICK_IMAGE = 100;
    private static final int PICK_PDF = 200;
    private EditText edt_desc;
    TextView txt_doc,txt_pdf;
    ImageView img_doc,img_pdf;
    private Uri imageUri;
    private Uri pdfUri;
    Spinner spinner_tag;
   // Spinner spinner_to;
    String tag;
    String link=null;
    private Button btn_submit;
    private ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    private StorageReference mStorage;
    TextView txtWebUrl;
    private LinearLayout layoutweburl;
    private AlertDialog dialogAddUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        setUIViews();
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tags_Array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner_tag.setAdapter(adapter);
        ArrayAdapter<CharSequence>adapter1=ArrayAdapter.createFromResource(this,
                R.array.sendto_Array,android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
     //   spinner_to.setAdapter(adapter1);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });
        txt_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
openGallery();
            }
        });
        /*txt_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDoc();
            }
        });*/
        layoutweburl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
showAddUrlDialog();
            }
        });
    }
    private void openDoc() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PDF);
    }
    private void openGallery() {
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK )
        switch (requestCode) {
            case PICK_IMAGE:
            imageUri = data.getData();
            img_doc.setImageURI(imageUri);
            break;
            case PICK_PDF:
                pdfUri=data.getData();
                img_doc.setImageURI(pdfUri);
        }
        }

    void setUIViews(){
        edt_desc=findViewById(R.id.edit_desc);
        spinner_tag = (Spinner) findViewById(R.id.spinner_tag);
        //spinner_to=(Spinner)findViewById(R.id.spinner_send_to);
        spinner_tag.setOnItemSelectedListener(this);
       // spinner_to.setOnItemSelectedListener(this);
        btn_submit=findViewById(R.id.btn_submit);
        img_doc=findViewById(R.id.img_doc);
        img_pdf=findViewById(R.id.img_pdf);
        txt_doc=findViewById(R.id.txt_doc);
        //txt_pdf=findViewById(R.id.txt_pdf);
        progressDialog=new ProgressDialog(this );
        firestore=FirebaseFirestore.getInstance();
        mStorage= FirebaseStorage.getInstance().getReference();
        layoutweburl=findViewById(R.id.layoutweburl);
        txtWebUrl=findViewById(R.id.textweburl);
    }
    private void startPosting() {
        final String desc=edt_desc.getText().toString().trim();
        if(!TextUtils.isEmpty(tag) && !TextUtils.isEmpty(desc))
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date=new Date();
            //progressDialog.setMessage("Please wait....");
            //progressDialog.show();
            final Map<String,Object>post=new HashMap<>();
            post.put(Constants.TAG,tag);
            post.put(Constants.DESCRIPTION,desc);
            post.put(Constants.DATE_CREATION,getDateFromString(dateFormat,dateFormat.format(date)));
            post.put(Constants.AUTHOR,"ANJALI");
            post.put(Constants.VISIBLE_TO,"ALL");
            post.put(Constants.WEB_URL,link);
            final DocumentReference reference=firestore.collection("posts").document();
            final String id=reference.getId();
            post.put(Constants.POST_ID,id);
            Log.d(TAG,"id      "+id) ;
            if(imageUri!=null){
                progressDialog.setMessage("Uploading Image....");
                progressDialog.show();
                final StorageReference filepath=mStorage.child("post_images").child(imageUri.getLastPathSegment());
                UploadTask uploadTask=filepath.putFile(imageUri);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        //Uri downloaduri=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                                                        Task<Uri> downloadUrl = filepath.getDownloadUrl();
                                                        progressDialog.cancel();
                                                        downloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                progressDialog.setMessage("Uploading the data....");
                                                                progressDialog.show();
                                                                post.put(Constants.UPLOADING_IMAGEURI,imageUri.toString());
                                                                post.put(Constants.IMAGE_URI,uri.toString());
                                                                post.put(Constants.SAVED_IMAGE,imageUri.getLastPathSegment());
                                                                reference.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(getApplicationContext(), "Posted successfully", Toast.LENGTH_SHORT).show();
                                                                        progressDialog.cancel();
                                                                        finish();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(getApplicationContext(), "Error in processing the request", Toast.LENGTH_SHORT).show();
                                                                        progressDialog.cancel();
                                                                        Log.d(TAG, e.getMessage());
                                                                    }
                                                                });
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.d(TAG, "Failed to download the image");
                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.cancel();
                        Log.d(TAG, "Failed to upload the image,Please try later");
                    }
                });
            }
            else {
                progressDialog.setMessage("Uploading the data....");
                progressDialog.show();
                post.put(Constants.IMAGE_URI, null);
                post.put(Constants.SAVED_IMAGE,null);
                post.put(Constants.UPLOADING_IMAGEURI,null);
                reference.set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Posted successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error in processing the request", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                        Log.d(TAG, e.getMessage());
                    }
                });
            }
        }
        else
            Toast.makeText(getApplicationContext(), "TAG/DESCRIPTION fields are mandatory!!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        tag=String.valueOf(parent.getItemAtPosition(position));
        Toast.makeText(getApplicationContext(), tag, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
tag="";
    }
    public Date getDateFromString(SimpleDateFormat dateFormat,String datetoSaved){
        try {
            Date date = dateFormat.parse(datetoSaved);
            return date ;
        } catch (ParseException e){
            return null ;
        }

    }
    private void showAddUrlDialog(){
        if(dialogAddUrl==null){
            AlertDialog.Builder builder=new AlertDialog.Builder(AddActivity.this);
            View view= LayoutInflater.from(this).inflate(R.layout.layout_add_url,(ViewGroup)findViewById(R.id.layoutaddUrlContainer));
            builder.setView(view);
            dialogAddUrl=builder.create();
            if(dialogAddUrl.getWindow()!=null){
                dialogAddUrl.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
             final TextView edturl=view.findViewById(R.id.edt_url);
            edturl.requestFocus();
            view.findViewById(R.id.add_btntxt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(edturl.getText().toString().trim().isEmpty())
                        Toast.makeText(AddActivity.this,"Please enter the url",Toast.LENGTH_SHORT).show();
                    else if(!Patterns.WEB_URL.matcher(edturl.getText().toString()).matches())
                        Toast.makeText(AddActivity.this,"Please enter the valid url",Toast.LENGTH_SHORT).show();
                    else
                    {
                        link=edturl.getText().toString();
                        txtWebUrl.setText(link);
                        dialogAddUrl.dismiss();
                    }
                }
            });
            view.findViewById(R.id.cancel_btntxt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAddUrl.dismiss();
                }
            });
        }
        dialogAddUrl.show();
    }
}