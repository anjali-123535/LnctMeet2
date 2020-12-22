package com.example.lnctmeet2.fragment;

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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.lnctmeet2.R;
import com.example.lnctmeet2.data.Post;
import com.example.lnctmeet2.utils.Constants;
import com.example.lnctmeet2.view.AddActivity;
import com.example.lnctmeet2.view.MainActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public abstract class BaseFragment extends Fragment {
    Query q1;
    FirestoreRecyclerAdapter<Post, PostViewHolder> postAdapter;
    FirebaseFirestore firestore;
    FirestoreRecyclerOptions<Post> postsList;
    private static final String TAG_NAME = MainActivity.class.getName();
public BaseFragment(){

}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_notice, container, false);
        RecyclerView recyclerView;
        recyclerView = rootview.findViewById(R.id.recycler_view);
Log.d(getContext().toString(),"inOnCreateview");
        firestore = FirebaseFirestore.getInstance();
      q1=getQuery();
        postsList = new FirestoreRecyclerOptions.Builder<Post>().setQuery(q1, Post.class).build();
        postAdapter = new FirestoreRecyclerAdapter<Post, PostViewHolder>(postsList) {
            @Override
            protected void onBindViewHolder(@NonNull PostViewHolder postViewHolder, int i, @NonNull Post post) {
                if (post.getIMAGE_URI() != null) {
                    Glide.with(getActivity()).load(post.getIMAGE_URI()).into(postViewHolder.img_doc);
                    postViewHolder.img_doc.setVisibility(View.VISIBLE);
                }
                postViewHolder.tag.setText(post.getTAG());
                postViewHolder.desc.setText(post.getDESCRIPTION());
                postViewHolder.author.setText(post.getAUTHOR());
                if(post.getWEB_URL()!=null){
                    postViewHolder.link.setText(post.getWEB_URL());
                    postViewHolder.link.setVisibility(View.VISIBLE);
                }
                postViewHolder.created.setText(post.getDATE_CREATION().toString().substring(0, 11));
                final String id = postsList.getSnapshots().getSnapshot(i).getId();
                postViewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteConfirmationDialog(id);
                    }
                });
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
                return new PostViewHolder(view);
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(postAdapter);
        return rootview;
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        TextView tag, desc, created, author,link;
        View view;
        ImageView img_doc;
        ImageButton delete, share;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            tag = itemView.findViewById(R.id.txt_tag);
            desc = itemView.findViewById(R.id.txt_desc);
            created = itemView.findViewById(R.id.txt_date);
            author = itemView.findViewById(R.id.txt_author);
            link=itemView.findViewById(R.id.web_url);
            view = itemView;
            img_doc = itemView.findViewById(R.id.imageview);
            delete = itemView.findViewById(R.id.btn_delete);
            share = itemView.findViewById(R.id.btn_share);
        }
    }
    void DeletePost(String id)
    {
        final DocumentReference reference=firestore.collection("posts").document(id);
        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        if(document.getString(Constants.SAVED_IMAGE)!=null)
                        {
                            StorageReference storageReference= FirebaseStorage.getInstance()
                                    .getReference("post_images").child(document.getString(Constants.SAVED_IMAGE));
                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(),"Image deleted successfully",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(),"Image deleted successfully",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "DELETION FAILED", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else
                        Log.d(TAG_NAME,"No such document");
                }
                else
                    Log.d(TAG_NAME,"Task failed");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    void showDeleteConfirmationDialog(final String id)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setMessage("Delete note?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DeletePost(id);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface!=null)
                    dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }
abstract public Query getQuery();
    @Override
    public void onStart() {
        super.onStart();
        Log.d(getContext().toString(),"startlisteninf");
        postAdapter.startListening();
    }
}
