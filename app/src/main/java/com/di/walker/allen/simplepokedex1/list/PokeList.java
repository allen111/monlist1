
package com.di.walker.allen.simplepokedex1.list;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PokeList {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("previous")
    @Expose
    private Object previous;
    @SerializedName("results")
    @Expose
    private java.util.ArrayList<Result> results = new ArrayList<Result>();
    @SerializedName("next")
    @Expose
    private String next;

    /**
     * 
     * @return
     *     The count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * 
     * @param count
     *     The count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * 
     * @return
     *     The previous
     */
    public Object getPrevious() {
        return previous;
    }

    /**
     * 
     * @param previous
     *     The previous
     */
    public void setPrevious(Object previous) {
        this.previous = previous;
    }

    /**
     * 
     * @return
     *     The results
     */
    public java.util.ArrayList<Result> getResults() {
        return results;
    }

    /**
     * 
     * @param results
     *     The results
     */
    public void setResults(java.util.ArrayList<Result> results) {
        this.results = results;
    }

    /**
     * 
     * @return
     *     The next
     */
    public String getNext() {
        return next;
    }

    /**
     * 
     * @param next
     *     The next
     */
    public void setNext(String next) {
        this.next = next;
    }

}
