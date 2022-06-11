package com.zey.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

import io.realm.Realm;

public class notekran extends AppCompatActivity {

    EditText baslik;
    EditText icerik;
    Spinner spinner;
    ImageView foto;
    Button addbutton;
    TextView tarih ;
    String [] modlar = new String[]{"Mod Seç","Mutlu","Üzgün", "Kızgın", "Sakin","Şaşkın"  };
    Context context = this;
    ArrayAdapter<String> modadapter ;
    int secilenmod ;

    String tarih_st ;

    Bitmap bitmap;
    Uri fotoUri;
    String bitmap_st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notekran);

        getID();

        modadapter = new ArrayAdapter<String>(context, R.layout.spin_satir_layout, modlar);
        spinner.setAdapter(modadapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=0) Toast.makeText(context, "Seçilen mod:"+ modlar[i], Toast.LENGTH_SHORT).show();
                secilenmod=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent anIntent = new Intent();
                anIntent.setType("image/*");
                anIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(anIntent,"Bir anı resmi ekleyin"),1);
            }
        });

        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();


        addbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checktexts())
                    return;

                String baslik_st = baslik.getText().toString();
                String icerik_st = icerik.getText().toString();

                realm.beginTransaction();

                Not note = realm.createObject(Not.class);
                note.setBaslik(baslik_st);
                note.setIcerik(icerik_st);
                note.setTarih(getCurrentdate());
                note.setMode(secilenmod);      //spinnerla modu al
                note.setResim(bitmap_st);

                realm.commitTransaction();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Durum Mesajı").setMessage("Anı Eklendi")
                        .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
//                finish();

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode == RESULT_OK){
            fotoUri=data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),fotoUri);
                bitmap_st =bitmap.toString();
                foto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getCurrentdate(){
        Calendar calendar = Calendar.getInstance();

        int gun = calendar.get(Calendar.DAY_OF_MONTH);

        int ay = calendar.get(Calendar.MONTH)+1;

        int yil = calendar.get(Calendar.YEAR);

        return tarih_st = gun+"."+ay+"."+yil;
    }

    public boolean checktexts(){

        if(baslik.getText().toString().length() == 0){
            Toast.makeText(notekran.this, "Başlık boş bırakılamaz", Toast.LENGTH_LONG).show();
            return false;
        }
        if(icerik.getText().toString().length() ==0){
            Toast.makeText(notekran.this, "İçerik boş bırakılamaz", Toast.LENGTH_LONG).show();
            return false;
        }
        if (secilenmod==0)
        {
            Toast.makeText(getApplicationContext(),"Modunuzu seçmediniz",Toast.LENGTH_SHORT).show();
            return false;
        }if (bitmap==null) {   //bitmap == null
            Toast.makeText(notekran.this, "Resim Yüklenmedi", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void getID(){
        baslik = findViewById(R.id.editbaslik);
        icerik = findViewById(R.id.editicerik);
        spinner = findViewById(R.id.spinner);
        foto = findViewById(R.id.fotoekle);
        addbutton =  findViewById(R.id.notuekle);
        tarih = findViewById(R.id.ust_yazi);
    }
}