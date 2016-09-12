package com.di.walker.allen.simplepokedex1;

import com.di.walker.allen.simplepokedex1.list.Result;

import java.util.ArrayList;

//parametri della ricerca
public class MyParams {
    public ArrayList<Result> rLis;
    public String query;
    public ArrayList<Result> rets;

    public MyParams(ArrayList<Result> a, String quer, ArrayList<Result> results) {
        rLis = a;
        query = quer;
        rets = results;

    }
}