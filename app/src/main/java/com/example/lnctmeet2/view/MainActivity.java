package com.example.lnctmeet2.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.lnctmeet2.R;
import com.example.lnctmeet2.adapters.CategoryFragmentPagerAdapter;
import com.example.lnctmeet2.data.Post;
import com.example.lnctmeet2.utils.Constants;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ViewPager viewPager;
    TabLayout tablayout;
    Toolbar toolbar;
    private static final String TAG_NAME = MainActivity.class.getName();
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUIViews();
        toolbar.setTitle("Lnct Meet 2");
        tablayout.setupWithViewPager(viewPager);
        // Set gravity for tab bar
        tablayout.setTabGravity(TabLayout.GRAVITY_FILL);
        // Set category fragment pager adapter
        CategoryFragmentPagerAdapter pagerAdapter =
                new CategoryFragmentPagerAdapter(this, getSupportFragmentManager());
        // Set the pager adapter onto the view pager
        viewPager.setAdapter(pagerAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddActivity.class));
            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG_NAME,"onResume");
    }
    /* class PostViewHolder extends RecyclerView.ViewHolder {
    TextView tag,desc,created,author;
    View view;
    ImageView img_doc;
    ImageButton delete,share;

    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        tag = itemView.findViewById(R.id.txt_tag);
        desc = itemView.findViewById(R.id.txt_desc);
        created=itemView.findViewById(R.id.txt_date);
        author=itemView.findViewById(R.id.txt_author);
        view = itemView;
        img_doc=itemView.findViewById(R.id.imageview);
        delete=itemView.findViewById(R.id.btn_delete);
        share=itemView.findViewById(R.id.btn_share);
    }*/

    void setUIViews(){
        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recycler_view);
        viewPager = findViewById(R.id.view_pager);
        // Give the TabLayout the ViewPager
        tablayout = findViewById(R.id.tabs);
        toolbar=findViewById(R.id.toolbar);
    }
}