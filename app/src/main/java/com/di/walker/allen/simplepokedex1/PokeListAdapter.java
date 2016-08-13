package com.di.walker.allen.simplepokedex1;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.di.walker.allen.simplepokedex1.list.PokeList;
import com.di.walker.allen.simplepokedex1.list.Result;

import java.util.ArrayList;


public class PokeListAdapter extends RecyclerView.Adapter<PokeListAdapter.ViewHolder> {
    ArrayList<Result> pokeList;
    OnCardClikListner onCardClickListner;

    public PokeListAdapter(ArrayList<Result> l){
        pokeList=l;
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
        holder.pk_num.setText("#: " + (position + 1));
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
        public ViewHolder(View view) {
            super(view);
            cardView=(CardView)view.findViewById(R.id.cardV);
            pk_name=(TextView)view.findViewById(R.id.pkmL_name);
            pk_url=(TextView)view.findViewById(R.id.pkmL_url);
            pk_num=(TextView)view.findViewById(R.id.pkmL_num);


        }


    }
    public interface  OnCardClikListner {
        void OnCardClicked(View view,int position);
    }


}
