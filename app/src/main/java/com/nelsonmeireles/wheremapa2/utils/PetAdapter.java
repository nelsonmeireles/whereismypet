package com.nelsonmeireles.wheremapa2.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nelsonmeireles.wheremapa2.R;
import com.nelsonmeireles.wheremapa2.entity.Pet;
import com.nelsonmeireles.wheremapa2.fragments.PetFormFragment;

import java.util.List;

/**
 * Created by nelsonmeireles on 14/11/16.
 */

public class PetAdapter extends ArrayAdapter<Pet> {
    private final Context context;
    private final List<Pet> values;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference petsRef = database.getReference("pets");

    public PetAdapter(Context context, List<Pet> values) {
        super(context, R.layout.row_layout_pet, values);
        this.context = context;
        this.values = values;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // inflate the layout
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.row_layout_pet, parent, false);
        }
        /*
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout_pet, parent, false);
        */
        TextView text = (TextView) convertView.findViewById(R.id.rowTextViewName);
        TextView textCategoria = (TextView) convertView.findViewById(R.id.rowTextViewCategoria);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewRow);
        ImageButton deleteImageButton = (ImageButton) convertView.findViewById(R.id.deleteImageButton);
        deleteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(values.get(position).getUuid());
            }
        });
        ImageButton editImageButton = (ImageButton) convertView.findViewById(R.id.editImageButton);
        editImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pet pet = values.get(position);
                goToEditPet(pet);
            }
        });

        if(values.get(position).getPhoto()!=null){
            Bitmap mbitmap = base64ToBitmap(values.get(position).getPhoto());
            imageView.setImageBitmap(mbitmap);
        }

        text.setText(values.get(position).getName());
        textCategoria.setText(values.get(position).getType());

        return convertView;
    }
    public Bitmap base64ToBitmap(String base64){
        byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    public void deletePet(String uid){
        petsRef.child(uid).removeValue();
    }
    public void showAlertDialog(String uid){
        final String uidd = uid;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Deletar Pet?");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deletePet(uidd);
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "Nao",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }
    private void goToEditPet(Pet pet){
        Bundle bundle = new Bundle();
        bundle.putSerializable("EDIT_PET", pet);
        Fragment fragment = null;
        Class fragmentClass = PetFormFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            fragment.setArguments(bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentManager fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
    }
}
