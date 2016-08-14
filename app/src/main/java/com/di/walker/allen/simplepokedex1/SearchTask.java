package com.di.walker.allen.simplepokedex1;


import android.os.AsyncTask;
import android.util.Log;

import com.di.walker.allen.simplepokedex1.list.Result;

import java.util.ArrayList;

public class SearchTask extends AsyncTask<MyParams,Long,ArrayList<String>>{
    @Override
    protected ArrayList<String> doInBackground(MyParams... params) {
        ArrayList<String> SearchRes=new ArrayList<String>();
        for (Result x : params[0].rLis) {
            String q= params[0].query;
            if (x.getName().matches("(?i)("+q+").*")) {
                Log.d("SRE1", "OnSearch: " + x.getName());
                SearchRes.add(x.getName());
            }
        }

        return SearchRes;
    }

}


