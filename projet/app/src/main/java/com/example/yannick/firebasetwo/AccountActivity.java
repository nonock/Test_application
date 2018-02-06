package com.example.yannick.firebasetwo;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {

    private Button logout, param;
    private TextView planet, nbrUser;
    private FirebaseAuth user = FirebaseAuth.getInstance();

    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;
    private String userID;

    private String idplanet;
    private long nbr = 0;
    private int a;

    private ImageView img;

    private ListView lv;
    private ArrayList<ImageView> list=new ArrayList<>();
    private ArrayAdapter<ImageView> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        logout = (Button) findViewById(R.id.buttonLogOut);
        param = (Button) findViewById(R.id.buttonParam);
        planet = (TextView) findViewById(R.id.textP);
        nbrUser = (TextView) findViewById(R.id.textNbr);

        img = (ImageView) findViewById(R.id.test1);

        //lv=(ListView)findViewById(R.id.listview_contacts);

        //adapter = new ArrayAdapter<ImageView>(this,android.R.layout.simple_dropdown_item_1line,list);
        //.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();


        getUserInfo();
        getAllUser();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.signOut();

                startActivity(new Intent(AccountActivity.this, MainActivity.class));
            }
        });

        param.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(AccountActivity.this, ParamActivity.class));

            }
        });
    }

    @Override
    public void onBackPressed() {
        user.signOut();

        startActivity(new Intent(AccountActivity.this, MainActivity.class));
    }

    private void getUserInfo()
    {
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
        mCustomerDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("idPlanet") != null)
                    {
                        idplanet = map.get("idPlanet").toString();
                        //planet.setText(idplanet);

                    }
                    nbr = dataSnapshot.getChildrenCount();
                    for(int i = 0; i < nbr + 1; i++)
                    {
                        String buff = userID + String.valueOf(i);
                        planet.setText(buff);
                        if(map.get(buff) != null)
                        {
                            Glide.with(getApplication()).load(buff).into(img);
                            //adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }

    private void getAllUser()
    {
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mCustomerDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                /*if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0)
                {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name") != null)
                    {
                        userID = map.get("name").toString();

                    }
                    if(map.get("idPlanet") != null)
                    {
                        idplanet = map.get("idPlanet").toString();
                    }
                }*/

                nbr = dataSnapshot.getChildrenCount();

                nbrUser.setText(String.valueOf(nbr));

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }
}