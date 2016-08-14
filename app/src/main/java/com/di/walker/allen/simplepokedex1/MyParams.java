package com.di.walker.allen.simplepokedex1;

import com.di.walker.allen.simplepokedex1.list.Result;

import java.util.ArrayList;


public class MyParams{
    public ArrayList<Result> rLis;
    public String query;
    public MyParams(ArrayList<Result> a,String quer){
        rLis=a;
        query=quer;

    }
}