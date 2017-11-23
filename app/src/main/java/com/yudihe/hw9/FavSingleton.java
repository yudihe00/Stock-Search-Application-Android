// FavSingleton
package com.yudihe.hw9;

import java.util.ArrayList;

/**
 * Created by heyudi on 11/22/17.
 */

public class FavSingleton {
    private static FavSingleton mInstance;
    private ArrayList<String> list = null;

    public static FavSingleton getInstance() {
        if(mInstance == null)
            mInstance = new FavSingleton();

        return mInstance;
    }

    private FavSingleton() {
        list = new ArrayList<>();
    }

    //Add element to array
    public void addToFav(String value) {
        list.add(value);
    }

    // Add symbol into FavList
    public void deleteFromFav(String value) {
        list.remove(value);
    }

    // retrieve array from anywhere
    public ArrayList<String> getFavList() {
        return this.list;
    }
}
