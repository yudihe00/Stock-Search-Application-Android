// FavSymbol
package com.yudihe.hw9;

/**
 * Created by heyudi on 11/22/17.
 */

public class FavoriteSymbol {
    private String symbol;
    private String price;
    private int timestamp;
    private String change;
    private String changePercent;
    private String changeString;

    public FavoriteSymbol(String symbol, String price, int timestamp,
                          String change,String changePercent) {
        this.symbol = symbol;
        this.price = price;
        this.timestamp = timestamp;
        this.change = change;
        this.changePercent = changePercent;
        this.changeString = change+"("+changePercent+")";
    }

    public String getSymbolName() {
        return symbol;
    }

    public String getPrice() {
        return price;
    }

    public int getTimeStampt() {
        return timestamp;
    }
    public String getChangeString() {
        return changeString;
    }

    public float getChangeFloat(){
        return Float.parseFloat(change);
    }

    public float getPriceFloat(){
        return Float.parseFloat(price);
    }
}
