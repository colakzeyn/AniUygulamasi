package com.zey.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class listActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button button;
    Context context;
    MyAdapter myAdapter;

    RealmResults<Not> notListesi;

    int pickContact;

    int pos;
    private static final int PERMISSION_REQUEST_CODE = 200;

    int pageHeight = 1120;
    int pagewidth = 792;

    Bitmap bmp, scaledbmp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recycler_view);
        button = findViewById(R.id.yeninotbutton);

        context=this;


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(listActivity.this,notekran.class));
            }
        });

        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();

        notListesi = realm.where(Not.class).findAll();

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(context,notListesi);

        recyclerView.setAdapter(myAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context,DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);



    }//oncreate son
//
//

    ItemTouchHelper.SimpleCallback simpleCallback   = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT |ItemTouchHelper.RIGHT | ItemTouchHelper.DOWN) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position =viewHolder.getAdapterPosition();
            pos=position;
            switch (direction){
                case ItemTouchHelper.LEFT:

                    //diyalog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    builder.setTitle("Uyarı Mesajı").setMessage("Silmek istediğinizden emin misiniz?")
                            .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Realm realm =Realm.getDefaultInstance();
                                    realm.beginTransaction();
                                    Not silineceknot = notListesi.get(position);
                                    if( silineceknot != null)
                                        silineceknot.deleteFromRealm();
                                    else
                                        System.out.println("Silinecek not yok");
                                    realm.commitTransaction();

                                    recyclerView.setAdapter( myAdapter);
                                }
                            }).setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    recyclerView.setAdapter( myAdapter);
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();

                    //
                    break;

                case ItemTouchHelper.RIGHT:
                    Intent contactsIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    pickContact = 1;
                    startActivityForResult(contactsIntent, pickContact);
                    break;

                case ItemTouchHelper.DOWN:

                    bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.manzara);
                    scaledbmp = Bitmap.createScaledBitmap(bmp, 140, 140, false);

                    if (checkPermission()) {
                        Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
                    } else {
                        requestPermission();
                    }
                    generatePDF();
                    break;
            }
        }



        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            final int position =viewHolder.getAdapterPosition();
            new RecyclerViewSwipeDecorator.Builder(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(context,R.color.red))
                    .addSwipeLeftLabel("SİL")
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(context,R.color.gray))
                    .addSwipeRightLabel("SMS gönder")
                    .create()
                    .decorate();

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };



    @Override
    protected void onResume() {
        super.onResume();
        if(recyclerView!=null){
            recyclerView.setAdapter(new MyAdapter(getApplicationContext(),notListesi));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.hakkinda:
                Toast.makeText(this, "Bu uygulama Zeynep Çolak tarafından Haziran 20222'de yazılmıştır.", Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data){
        super.onActivityResult(reqCode, resultCode, data);

        if(reqCode == pickContact){  //req coda bak
            if (resultCode == Activity.RESULT_OK) {
                Log.d("Contacts", "istek ok");
                Uri contactData = data.getData();
                Cursor contact =  getContentResolver().query(contactData, null, null, null, null);

                if (contact.moveToFirst()) {
                    String name = contact.getString(contact.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    ContentResolver cr = getContentResolver();
                    Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                            "DISPLAY_NAME = '" + name + "'", null, null);
                    if (cursor.moveToFirst()) {
                        String contactId =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                        //
                        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                        while (phones.moveToNext()) {
                            String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            int type = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                            if (type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                                Log.d("Contacts geldi", number);
                                numarageldi(number);
                            }
                        }
                        phones.close();
                    }
                    cursor.close();
                }
            }
        }else{
            Log.d("Contact", "İptal");
        }
    }

    public void numarageldi(String num){
        System.out.println(num);
        Log.d("Contact", "numara geldi");

        Not gonderileceknot = notListesi.get(pos);
        String message= "Sana bir anımı göndermek istiyorum!"+gonderileceknot.getTarih()+"tarihinde yazmıştım"+
                "Şimdi başlıyorum!"+gonderileceknot.getIcerik();

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(num, null, message, null, null);
    }

    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(context,  Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(context,  Manifest.permission.READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE},   PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(context, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Permission Denined.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void generatePDF() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint title = new Paint();

        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        Canvas canvas = myPage.getCanvas();
        canvas.drawBitmap(scaledbmp, 56, 40, paint);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        title.setTextSize(15);
        title.setColor(ContextCompat.getColor(context, R.color.mor));

        canvas.drawText("ANILARIM.", 209, 100, title);
        canvas.drawText("Günlerden bir gün:", 209, 80, title);

        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(context, R.color.mor));
        title.setTextSize(15);

        title.setTextAlign(Paint.Align.CENTER);

        Not pdfnot = notListesi.get(pos);
        String ani_icerik= pdfnot.getIcerik();

        canvas.drawText(ani_icerik, 360, 120, title);

        pdfDocument.finishPage(myPage);
        File file = new File(Environment.getExternalStorageDirectory(), "anım.pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));

            Toast.makeText(context, "PDF hazır.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
    }
}