package com.zey.myapplication;

import io.realm.RealmObject;

public class Not extends RealmObject {
    String baslik;
    String icerik;
    int mode;
    String tarih;
//    String pass;
    String resim;

    public String getBaslik() {
        return baslik;
    }

    public void setBaslik(String baslik) {
        this.baslik = baslik;
    }

    public String getIcerik() {
        return icerik;
    }

    public void setIcerik(String icerik) {
        this.icerik = icerik;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public String getResim() {
        return resim;
    }

    public void setResim(String resim) {
        this.resim = resim;
    }
}
