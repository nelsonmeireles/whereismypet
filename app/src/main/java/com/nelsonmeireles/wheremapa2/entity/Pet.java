package com.nelsonmeireles.wheremapa2.entity;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by nelsonmeireles on 14/11/16.
 */

public class Pet implements Serializable{
    private String uuid;
    private String name;
    private String type;
    private String breed;
    private String gender;
    private String ownerUId;
    private String photo;
    private String ownerPhone;
    private String ownerName;
    private double latitude;
    private double longitude;

    public Pet() {

    }

    public Pet(String name, String type, String breed, String gender, String ownerUId, double latitude, double longitude) {
        this.name = name;
        this.type = type;
        this.breed = breed;
        this.gender = gender;
        this.ownerUId = ownerUId;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOwnerUId() {
        return ownerUId;
    }

    public void setOwnerUId(String ownerUId) {
        this.ownerUId = ownerUId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
