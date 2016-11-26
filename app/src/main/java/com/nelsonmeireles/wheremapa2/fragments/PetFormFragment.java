package com.nelsonmeireles.wheremapa2.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nelsonmeireles.wheremapa2.R;
import com.nelsonmeireles.wheremapa2.activities.RegisterActivity;
import com.nelsonmeireles.wheremapa2.entity.Pet;
import com.nelsonmeireles.wheremapa2.entity.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class PetFormFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public DatabaseReference mDatabase;
    public FirebaseUser loggedUser;
    private ImageButton mapImageButton;
    private RadioButton maleRadio;
    private RadioButton femaleRadio;
    private EditText petName;
    private EditText petType;
    private EditText petBreed;
    private Location petLocation;
    private Button savePetBtn;
    private boolean isMale = false;
    private boolean isFemale = false;
    private Bitmap bitmap;
    private ImageView imageView;
    private GoogleMap mMap;
    private Button backFormButton;
    private Dialog dialog;
    private boolean edit;
    private Pet petEdit;
    private User user;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference usersRef = database.getReference("users");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        loggedUser = FirebaseAuth.getInstance().getCurrentUser();
        edit = false;
        if (getArguments() != null) {
            petEdit = (Pet) getArguments().getSerializable("EDIT_PET");
            if(petEdit!=null){
                edit=true;
                getArguments().remove("EDIT_PET");
            }
        }
        if(loggedUser!=null){
            usersRef.child(loggedUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                        user = dataSnapshot.getValue(User.class);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_pet_form, container, false);
        mapImageButton = (ImageButton) root.findViewById(R.id.mapImageButton);
        maleRadio = (RadioButton) root.findViewById(R.id.radio_male);
        femaleRadio = (RadioButton) root.findViewById(R.id.radio_female);
        imageView = (ImageView) root.findViewById(R.id.imageViewForm);
        petName = (EditText) root.findViewById(R.id.nomePetEditText);
        petType = (EditText) root.findViewById(R.id.categoriaEditText);
        backFormButton = (Button) root.findViewById(R.id.CancelPetBtn);
        petBreed = (EditText) root.findViewById(R.id.textEditBreed);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        if(edit){
            petName.setText(petEdit.getName());
            petType.setText(petEdit.getType());
            if(petEdit.getBreed()!=null){
                petBreed.setText(petEdit.getBreed());
            }
            petLocation = new Location("editLocation");
            petLocation.setLongitude(petEdit.getLongitude());
            petLocation.setLatitude(petEdit.getLatitude());
            if(petEdit.getGender()!=null){
                if(petEdit.getGender().equals("male")){
                    maleRadio.setChecked(true);
                } else if(petEdit.getGender().equals("female")){
                    femaleRadio.setChecked(true);
                }
            }
            if(petEdit.getPhoto()!=null){
                imageView.setImageBitmap(base64ToBitmap(petEdit.getPhoto()));
            }
        }
        maleRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isMale = true;
                    isFemale = false;
                }
            }
        });
        femaleRadio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isMale = false;
                    isFemale = true;
                }
            }
        });
        savePetBtn = (Button) root.findViewById(R.id.savePetBtn);
        savePetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit){
                    updatePet();
                } else{
                    saveNewPet(loggedUser.getUid());
                }
            }
        });
        backFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPetsListFragment();
            }
        });
        mapImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMapDialog();
            }
        });
        // Inflate the layout for this fragment
        return root;
    }
    public void saveNewPet(String userId){
        if(petName.getText()!=null&&petType.getText()!=null&&petLocation!=null){
            Pet newPet = new Pet();
            newPet.setUuid(UUID.randomUUID().toString());
            newPet.setName(petName.getText().toString());
            newPet.setType(petType.getText().toString());
            newPet.setLatitude(petLocation.getLatitude());
            newPet.setLongitude(petLocation.getLongitude());
            newPet.setOwnerUId(userId);
            if(petBreed!=null){
                newPet.setBreed(petBreed.getText().toString());
            } else {
                newPet.setBreed(null);
            }
            if(bitmap!=null){
                newPet.setPhoto(bitmapToBase64(bitmap));
            } else {
                newPet.setPhoto(null);
            }
            if(isMale){
                newPet.setGender("male");
            }else if(isFemale){
                newPet.setGender("female");
            } else {
                newPet.setGender(null);
            }
            if(user!=null){
                newPet.setOwnerName(user.getDisplayName());
                newPet.setOwnerPhone(user.getPhone());
            }
            mDatabase.child("pets").child(newPet.getUuid()).setValue(newPet);
            goToPetsListFragment();

        } else{
            Toast.makeText(this.getActivity(), "Nome, Categoria e Localização obrigatorios!",
                    Toast.LENGTH_SHORT).show();
        }
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
                bitmap = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
    private void showMapDialog(){
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.map_form);
        dialog.show();
        backFormButton = (Button) dialog.findViewById(R.id.backPetMapForm);
        backFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final MapView mMapView = (MapView) dialog.findViewById(R.id.mapViewFormDialog);
        MapsInitializer.initialize(getActivity());

        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                if(getLastLocation()!=null){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getLastLocation().getLatitude(),getLastLocation().getLongitude()), 100));
                }
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        petLocation = new Location("PetLocation");
                        petLocation.setLatitude(latLng.latitude);
                        petLocation.setLongitude(latLng.longitude);
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(latLng));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                    }
                });
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private Location getLastLocation(){
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this.getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        if ( ContextCompat.checkSelfPermission( this.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this.getActivity(), new String[] {  android.Manifest.permission.ACCESS_COARSE_LOCATION  },1
            );

        }
        Location mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
        return mLastLocation;
    }
    public void goToPetsListFragment(){
        Fragment fragment = null;
        Class fragmentClass = MyPetsFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }

    public String bitmapToBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encoded;
    }
    public Bitmap base64ToBitmap(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    public void updatePet(){
        if(petName.getText()!=null&&petType.getText()!=null&&petLocation!=null){
            petEdit.setName(petName.getText().toString());
            petEdit.setType(petType.getText().toString());
            petEdit.setLatitude(petLocation.getLatitude());
            petEdit.setLongitude(petLocation.getLongitude());
            if(petBreed!=null){
                petEdit.setBreed(petBreed.getText().toString());
            } else {
                petEdit.setBreed(null);
            }
            if(bitmap!=null){
                petEdit.setPhoto(bitmapToBase64(bitmap));
            }
            if(isMale){
                petEdit.setGender("male");
            }else if(isFemale){
                petEdit.setGender("female");
            } else {
                petEdit.setGender(null);
            }
            if(user!=null){
                petEdit.setOwnerName(user.getDisplayName());
                petEdit.setOwnerPhone(user.getPhone());
            }
            mDatabase.child("pets").child(petEdit.getUuid()).setValue(petEdit);
            goToPetsListFragment();

        } else{
            Toast.makeText(this.getActivity(), "Nome, Categoria e Localização obrigatorios!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
