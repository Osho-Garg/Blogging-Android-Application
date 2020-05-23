package com.example.osho.bloggingapp.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.osho.bloggingapp.Data.BlogRecyclerAdapter;
import com.example.osho.bloggingapp.Model.Blog;
import com.example.osho.bloggingapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.*;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//maybe we add the libraries in dependencies and tehn use import statement
// This class is also used to populate all the posts we have created
import java.util.ArrayList;
import java.util.List;

public class PostListActivity extends AppCompatActivity {


    private DatabaseReference mDatabaseReference;

    private RecyclerView recyclerView;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    private List<Blog> blogList;

    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MBlog");

        mDatabaseReference.keepSynced(true);


        blogList = new ArrayList<>();


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }


    // https://developer.android.com/guide/topics/ui/layout/recyclerview
    //If your app needs to display a scrolling list of elements based on large data sets (or data that frequently changes), you should use RecyclerView as described on this page.

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.action_add:
                if (mUser != null && mAuth != null) {

                    startActivity(new Intent(PostListActivity.this, AddPostActivity.class));
                    finish();

                }

                break;
            case R.id.action_signout:

                if (mUser != null && mAuth != null) {
                    mAuth.signOut();

                    startActivity(new Intent(PostListActivity.this, MainActivity.class));
                    finish();

                }
                break;

        }

        return super.onOptionsItemSelected(item);


    }

    // Retreiving the post from the database
    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseReference.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //this method means something is added to our database


                Blog blog = dataSnapshot.getValue(Blog.class);

                blogList.add(blog);

                //Collections.reverse(blogList);

                blogRecyclerAdapter = new BlogRecyclerAdapter(PostListActivity.this,blogList);
                recyclerView.setAdapter(blogRecyclerAdapter);
                blogRecyclerAdapter.notifyDataSetChanged();


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
