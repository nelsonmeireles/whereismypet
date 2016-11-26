package com.nelsonmeireles.wheremapa2.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nelsonmeireles.wheremapa2.R;
import com.nelsonmeireles.wheremapa2.entity.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private EditText nameRegister;
    private EditText emailRegister;
    private EditText passwordRegister;
    private EditText confirmPasswordRegister;
    private EditText phoneRegister;
    private String email_register;
    private String password_register;
    private String confirm_password_register;
    private String phone_register;
    private ImageView registerImageView;
    private Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nameRegister = (EditText) findViewById(R.id.name_register);
        emailRegister = (EditText) findViewById(R.id.email_register);
        passwordRegister = (EditText) findViewById(R.id.password_register);
        registerImageView = (ImageView) findViewById(R.id.imageViewProfileRegister);
        confirmPasswordRegister = (EditText) findViewById(R.id.confirm_password_register);
        phoneRegister = (EditText) findViewById(R.id.phone_register);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference("users");
        Button registerButton = (Button) findViewById(R.id.email_register_btn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email_register = emailRegister.getText().toString();
                password_register = passwordRegister.getText().toString();
                confirm_password_register = confirmPasswordRegister.getText().toString();
                phone_register = phoneRegister.getText().toString();
                if(email_register.isEmpty() || password_register.isEmpty() || confirm_password_register.isEmpty() || phone_register.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Todos os campos devem ser preenchidos!",
                            Toast.LENGTH_SHORT).show();
                }else if(!password_register.equals(confirm_password_register)){
                    Toast.makeText(RegisterActivity.this, "As senhas devem ser iguais!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email_register,password_register).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {

                                Toast.makeText(RegisterActivity.this, "Cadastro falhou!",
                                        Toast.LENGTH_SHORT).show();
                            } else if(task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Cadastrado com sucesso!",
                                        Toast.LENGTH_SHORT).show();
                                createNewUser(task.getResult().getUser().getUid());
                            }

                            finish();
                        }
                    });
                }
            }
        });
        registerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        Button backButton = (Button) findViewById(R.id.back_register_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void createNewUser(String userId){
        User user = new User(nameRegister.getText().toString());
        user.setPhone(phoneRegister.getText().toString());
        if(bitmap!=null){
            user.setPhoto(bitmapToBase64(bitmap));
        }
        usersRef.child(userId).setValue(user);
    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                registerImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String bitmapToBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }
}
