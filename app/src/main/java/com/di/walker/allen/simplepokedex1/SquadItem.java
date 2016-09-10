package com.di.walker.allen.simplepokedex1;


public class SquadItem implements Comparable<SquadItem>{
    private String name;
    private int num;

    public SquadItem(String name,int num){

        this.name = name;
        this.num = num;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public int compareTo(SquadItem another) {
        return this.getNum()-another.getNum();
    }
}
