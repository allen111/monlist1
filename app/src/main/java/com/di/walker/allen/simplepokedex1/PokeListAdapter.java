package com.di.walker.allen.simplepokedex1;


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

    public PokeListAdapter(ArrayList<Result> l){
        pokeList=l;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_row, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.pk_name.setText(pokeList.get(position).getName());
        holder.pk_url.setText(pokeList.get(position).getUrl());
        holder.pk_num.setText("#: "+ (position+1));//TODO check number or shift
    }

    @Override
    public int getItemCount() {
        return pokeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView pk_name,pk_num,pk_url;
        public ViewHolder(View view) {
            super(view);
            pk_name=(TextView)view.findViewById(R.id.pkmL_name);
            pk_url=(TextView)view.findViewById(R.id.pkmL_url);
            pk_num=(TextView)view.findViewById(R.id.pkmL_num);


        }


    }
}
