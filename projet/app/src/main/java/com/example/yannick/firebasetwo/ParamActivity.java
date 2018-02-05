package com.example.yannick.firebasetwo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParamActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;
    private ImageView imageView;
    private EditText UserName;

    private Button bt3;

    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;
    private String userID;

    private String mName;
    private String profileImageUrl;

    private Uri resultUri;
    private Bitmap imageBitmap;

    private long nbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_param);

        UserName = (EditText) findViewById(R.id.editTextUsername);
        imageView = (ImageView) findViewById(R.id.profileImage);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        bt3 = (Button) findViewById(R.id.button3);
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);


        getUserInfo();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();

            }
        });

        bt3.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                selectImage2();
            }
        });

        findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveUserInformation();

            }
        });
    }

    private void getUserInfo()
    {
        mCustomerDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name") != null)
                    {
                        mName = map.get("name").toString();
                        UserName.setText(mName);

                    }
                    if(map.get("profileImageUrl") != null)
                    {
                        profileImageUrl = map.get("profileImageUrl").toString();
                        Glide.with(getApplication()).load(profileImageUrl).into(imageView);
                    }
                    mCustomerDatabase.child("Image");
                    if(dataSnapshot.exists())
                    {
                        nbr = dataSnapshot.getChildrenCount();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }

    private void saveUserInformation()
    {
        mName = UserName.getText().toString();

        String idPlanet = mName + "Planet";

        Map userInfo = new HashMap();
        userInfo.put("name",mName);
        userInfo.put("idPlanet",idPlanet);
        mCustomerDatabase.updateChildren(userInfo);

        FirebaseUser user = mAuth.getCurrentUser();

        if(user != null && resultUri != null)
        {
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);
            Bitmap bitmap = null;

            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Map newImage = new HashMap();
                    newImage.put("profileImageUrl", downloadUrl.toString());
                    mCustomerDatabase.updateChildren(newImage);

                    finish();
                    return;

                }
            });

        }
        else if(user != null && imageBitmap != null)
        {
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID);

            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos2);
            byte[] data2 = baos2.toByteArray();
            UploadTask uploadTask2 = filePath.putBytes(data2);

            uploadTask2.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Map newImage = new HashMap();
                    newImage.put("profileImageUrl", downloadUrl.toString());
                    mCustomerDatabase.updateChildren(newImage);

                    finish();
                    return;

                }
            });

        }
        else
        {
            finish();
            return;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == Activity.RESULT_OK)
        {
            onSelectFromGalleryResult(data);

        }
        else if(requestCode == 0 && resultCode == Activity.RESULT_OK)
        {
            onCaptureImageResult(data);
        }
        else if(requestCode == 3 && resultCode == Activity.RESULT_OK)
        {
            onSelectFromGalleryResult2(data);
        }
        else if(requestCode == 2 && resultCode == Activity.RESULT_OK)
        {
            onCaptureImageResult2(data);
        }

    }
    private void selectImage()
    {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ParamActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(items[i].equals("Camera"))
                {
                    cameraIntent();
                }
                else if(items[i].equals("Gallery"))
                {
                    galleryIntent();
                }
                else if(items[i].equals("Cancel"))
                {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takePictureIntent, 0);
        }
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),1);
    }

    private void onSelectFromGalleryResult(Intent data)
    {
       final Uri imageUri = data.getData();
       resultUri = imageUri;
       imageView.setImageURI(resultUri);
    }

    private void onCaptureImageResult(Intent data)
    {
        Bundle extras = data.getExtras();
        imageBitmap = (Bitmap) extras.get("data");
        imageView.setImageBitmap(imageBitmap);
    }




    private void selectImage2()
    {
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ParamActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if(items[i].equals("Camera"))
                {
                    cameraIntent2();
                }
                else if(items[i].equals("Gallery"))
                {
                    galleryIntent2();
                }
                else if(items[i].equals("Cancel"))
                {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent2()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(takePictureIntent, 2);
        }
    }

    private void galleryIntent2()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),3);
    }

    private void onSelectFromGalleryResult2(Intent data)
    {
        final Uri imageUri = data.getData();
        resultUri = imageUri;
        savePicture();
    }

    private void onCaptureImageResult2(Intent data)
    {
        Bundle extras = data.getExtras();
        imageBitmap = (Bitmap) extras.get("data");
        savePicture();
    }

    private void savePicture()
    {
        mCustomerDatabase.child("Image").push();
        
        FirebaseUser user2 = mAuth.getCurrentUser();

        nbr = nbr + 1;

       final String nbrUser = userID + String.valueOf(nbr);
       StorageReference filePath = FirebaseStorage.getInstance().getReference().child("images").child(nbrUser);

       if(user2 != null && resultUri != null)
        {
            Bitmap bitmap = null;

            try
            {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filePath.putBytes(data);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Map newImage = new HashMap();
                    newImage.put(nbrUser, downloadUrl.toString());
                    mCustomerDatabase.updateChildren(newImage);

                    finish();
                    return;

                }
            });

        }
        else if(user2 != null && imageBitmap != null)
        {

            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos2);
            byte[] data2 = baos2.toByteArray();
            UploadTask uploadTask2 = filePath.putBytes(data2);

            uploadTask2.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                    return;
                }
            });
            uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Map newImage = new HashMap();
                    newImage.put(nbrUser, downloadUrl.toString());
                    mCustomerDatabase.updateChildren(newImage);

                    finish();
                    return;

                }
            });

        }
        else
        {
            finish();
            return;
        }

    }
}
