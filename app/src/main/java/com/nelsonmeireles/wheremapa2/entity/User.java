package com.nelsonmeireles.wheremapa2.entity;

/**
 * Created by nelsonmeireles on 10/11/16.
 */

public class User {
    private String uid;
    private String displayName;
    private String  photo;
    private String phone;

    public User() {
    }

    public User(String uid, String displayName, String photo) {
        this.uid = uid;
        this.displayName = displayName;
        this.photo = photo;
    }

    public User(String uid, String displayName) {
        this.uid = uid;
        this.displayName = displayName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User(String displayName) {
        this.displayName = displayName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
