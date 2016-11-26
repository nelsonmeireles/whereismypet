package com.nelsonmeireles.wheremapa2.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

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
import com.nelsonmeireles.wheremapa2.entity.Pet;
import com.nelsonmeireles.wheremapa2.utils.PetAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyPetsFragment extends Fragment {
    private List<Pet> pets = new ArrayList<Pet>();
    private ListView listView;
    private ImageButton deleteImageButton;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference petsRef = database.getReference("pets");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        petsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pets.clear();
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                for(DataSnapshot petsnap: dataSnapshot.getChildren()){
                    Pet newPet = petsnap.getValue(Pet.class);
                    if(newPet!=null && user!=null){
                        if(newPet.getOwnerUId().equals(user.getUid())){
                            pets.add(newPet);
                            refreshList();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                System.out.println("Nao buscou a lista");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_pets, container, false);
       listView = (ListView) root.findViewById(R.id.listView);
        // Inflate the layout for this fragment
        return root;
    }

    protected void refreshList(){
        if(getActivity()!=null){
            PetAdapter adapter = new PetAdapter(this.getContext(),pets);
            listView.setAdapter(adapter);
        }
    }
}
