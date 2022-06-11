package com.zey.myapplication;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.RealmResults;

public class MyAdapter  extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    Context context;
    RealmResults<Not> notListe;


    public MyAdapter(Context context, RealmResults<Not> notListe) {
        this.context = context;
        this.notListe = notListe;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder,  int position) {

        Not currentNot = notListe.get(position);
        holder.baslik.setText(currentNot.getBaslik());
        holder.icerik.setText(currentNot.getIcerik());
        holder.tarih.setText(currentNot.getTarih());

        Drawable myDrawable;
       switch (currentNot.getMode()){
           case 1:
               myDrawable = context.getResources().getDrawable(R.drawable.smile);
               holder.icon.setImageDrawable(myDrawable);
               break;
           case 2:
               myDrawable = context.getResources().getDrawable(R.drawable.sad);
               holder.icon.setImageDrawable(myDrawable);
               break;
           case 3:
               myDrawable = context.getResources().getDrawable(R.drawable.anger);
               holder.icon.setImageDrawable(myDrawable);
               break;
           case 4:
               myDrawable = context.getResources().getDrawable(R.drawable.calm);
               holder.icon.setImageDrawable(myDrawable);
               break;
           case 5:
               myDrawable = context.getResources().getDrawable(R.drawable.saskin);
               holder.icon.setImageDrawable(myDrawable);
               break;
       }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                anı güncelleme


            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onLongClick(View view) {
                String [] modlar = new String[]{"Mod Seç","Mutlu","Üzgün", "Kızgın", "Sakin","Şaşkın"  };
                String text = "Bu anıda modunuz: "+modlar[currentNot.getMode()];

                Toast.makeText(context, text, Toast.LENGTH_LONG).show();

                return false;

            }
        });


    }//onbind sonu



    @Override
    public int getItemCount() {
        return notListe.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView baslik;
        TextView tarih;
        TextView icerik;
        ImageView icon;
        LinearLayout parentLayout;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon  = itemView.findViewById(R.id.iconn);
            baslik  = itemView.findViewById(R.id.baslik);
            icerik = itemView.findViewById(R.id.icerigi);
            tarih  = itemView.findViewById(R.id.tarihi);
            parentLayout  = itemView.findViewById(R.id.parentLayout);

        }
    }




}

