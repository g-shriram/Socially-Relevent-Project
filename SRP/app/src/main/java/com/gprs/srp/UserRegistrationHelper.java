package com.gprs.srp;


public class UserRegistrationHelper {

    String fname;
    String email;
    double lat, lon;
    String phone;
    String role;
    String pass;
    Boolean verify;

    public UserRegistrationHelper() {
    }

    public UserRegistrationHelper(String fname, String email, double lat, double lon, String phone, String role, String pass) {
        this.fname = fname;
        this.email = email;
        this.lat = lat;
        this.lon = lon;
        this.phone = phone;
        this.role = role;
        this.pass = pass;
        verify = false;
    }

    public Boolean getVerify() {
        return verify;
    }

    public void setVerify(Boolean verify) {
        this.verify = verify;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public double getLat() {
        return lat;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
