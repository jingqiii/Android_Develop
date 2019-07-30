package com.example.yangjingqi.assignment4;

import java.io.Serializable;

/**
 * Created by yangjingqi on 7/24/17.
 */

public class Stock  implements Serializable {

    private String symbol;
    private String company;
    private String price;
    private String change;
    private String percentage;

    public Stock(String sy, String com, String pr , String ch, String perce) {
        symbol = sy;
        company = com;
        price = pr;
        change = ch;
        percentage = perce;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompany() {
        return company;
    }

    public String getPrice() {
        return price;
    }

    public String getChange() {
        return change;
    }

    public String getPercentage() {
        return percentage;
    }
}