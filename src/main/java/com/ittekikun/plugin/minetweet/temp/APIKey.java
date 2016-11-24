package com.ittekikun.plugin.minetweet.temp;

import java.io.Serializable;

//難読化(笑)の為可読性皆無
public class APIKey implements Serializable
{
    private static final long serialVersionUID = 346364364L;

    private String asuka;
    private String nono;
    private String anya;
    private String miku;


    public APIKey(String asuka, String nono, String anya, String miku)
    {
        this.asuka = asuka;
        this.nono = nono;
        this.anya = anya;
        this.miku = miku;
    }

    public String getAsuka()
    {
        return asuka;
    }

    public String getNono()
    {
        return nono;
    }

    public String getAnya()
    {
        return anya;
    }

    public String getMiku()
    {
        return miku;
    }
}