package com.di.walker.allen.simplepokedex1;


import android.os.AsyncTask;
import android.util.Log;

import com.di.walker.allen.simplepokedex1.list.Result;

import java.util.ArrayList;

public class SearchTask extends AsyncTask<MyParams,Long,MyParams>{
    @Override
    protected MyParams doInBackground(MyParams... params) {


        for (Result x : params[0].rLis) {
            String q= params[0].query;
            if (x.getName().matches("(?i)("+q+").*")) {
                Log.d("SRE1", "OnSearch: " + x.getName());
                params[0].rets.add(x);
            }
        }

        return null;
    }

}


