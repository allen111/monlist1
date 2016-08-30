package com.di.walker.allen.simplepokedex1;


import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.di.walker.allen.simplepokedex1.list.PokeList;
import com.di.walker.allen.simplepokedex1.list.Result;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class PokeListAdapter extends RecyclerView.Adapter<PokeListAdapter.ViewHolder> {
    ArrayList<Result> pokeList;
    OnCardClikListner onCardClickListner;
    Context context;

    public PokeListAdapter(ArrayList<Result> l,Context context){
        pokeList=l;
        this.context=context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.pk_name.setText(pokeList.get(position).getName());
        holder.pk_url.setText(pokeList.get(position).getUrl());
        String url = pokeList.get(position).getUrl();
        String[] splitted = url.split("/");
        String num = splitted[splitted.length - 1];
        holder.pk_num.setText("#: " +num);

        String mDrawableName = "icon_"+num/*+".png"*/;

        int resID =  getId(mDrawableName,R.drawable.class);
        Log.d("img2", "onBindViewHolder: "+resID);
        holder.pk_icon.setImageResource(resID);

        holder.cardView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                onCardClickListner.OnCardClicked(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pokeList.size();
    }
    public void setOnCardClickListner(OnCardClikListner onCardClickListner){
        this.onCardClickListner =onCardClickListner;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView pk_name,pk_num,pk_url;
        CardView cardView;
        private ImageView pk_icon;
        public ViewHolder(View view) {
            super(view);
            cardView=(CardView)view.findViewById(R.id.cardV);
            pk_name=(TextView)view.findViewById(R.id.pkmL_name);
            pk_url=(TextView)view.findViewById(R.id.pkmL_url);
            pk_num=(TextView)view.findViewById(R.id.pkmL_num);
            pk_icon=(ImageView) view.findViewById(R.id.pkmL_icon);


        }


    }
    public interface  OnCardClikListner {
        void OnCardClicked(View view,int position);
    }



    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            throw new RuntimeException("No resource ID found for: "
                    + resourceName + " / " + c, e);
        }
    }


}
