package com.gprs.srp;

public class UserLocationHelper {

    String fname;
    String email;
    double lat, lon;
    String role;
    String phone;

    public UserLocationHelper(String fname, double lat, double lon, String role, String email, String phone) {
        this.fname = fname;
        this.lat = lat;
        this.lon = lon;
        this.role = role;
        this.email = email;
        this.phone = phone;
    }

    public UserLocationHelper() {
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public double getLat() {
        return lat;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
