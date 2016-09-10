package com.di.walker.allen.simplepokedex1;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;


public class SquadListAdapter extends RecyclerView.Adapter<SquadListAdapter.SquadViewHolder> {
    private ArrayList<SquadItem> squadItems;
    OnSquadCardClikListner onSquadCardClikListner;

    public SquadListAdapter(ArrayList<SquadItem> squadItems){
        this.squadItems=squadItems;
    }


    @Override
    public SquadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_row,parent,false);
        return new SquadViewHolder(view);

    }

    @Override
    public void onBindViewHolder(SquadViewHolder holder, final int position) {

        holder.pk_name.setText(squadItems.get(position).getName());
        int num=squadItems.get(position).getNum();
        String s_num="#: "+num;
        holder.pk_num.setText(s_num);
        final int f_num=num;

        String drawableName = "icon_"+num;
        int resID=getId(drawableName,R.drawable.class);
        holder.pk_icon.setImageResource(resID);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSquadCardClikListner.OnCardClicked(v,f_num,position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return squadItems.size();
    }
    public void setOnSquadCardClikListener(OnSquadCardClikListner onSquadCardClikListner){
        this.onSquadCardClikListner=onSquadCardClikListner;
    }










    public class SquadViewHolder extends RecyclerView.ViewHolder {
        private TextView pk_name,pk_num;
        CardView cardView;
        private ImageView pk_icon;


        public SquadViewHolder(View view) {
            super(view);
            cardView=(CardView)view.findViewById(R.id.cardV);
            pk_name=(TextView)view.findViewById(R.id.pkmL_name);
            pk_num=(TextView)view.findViewById(R.id.pkmL_num);
            pk_icon=(ImageView) view.findViewById(R.id.pkmL_icon);
        }
    }

    public interface  OnSquadCardClikListner {
        void OnCardClicked(View view,int poke_num,int position);
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
