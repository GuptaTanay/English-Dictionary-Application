package com.example.tanaygupta.englishdictionary.utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Tanay Gupta on 7/11/2017.
 */
public class Operation {
    private String errormessage;

    public String getErrormessage(){
        return errormessage;
    }
    public InputStream openGetURLConnection(String urlstring)throws IOException {
        InputStream in = null;
        int response = -1;
        URL url = new URL(urlstring);
        URLConnection conn = url.openConnection();
        if(!( conn instanceof HttpURLConnection)){
            throw new IOException("Not a Valid Http Connection");
        }
        try{
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if( response == HttpURLConnection.HTTP_OK){
                in = httpConn.getInputStream();
            }
        }catch(Exception ex){
            errormessage = ex.toString();
        }
        return in;
    }
}
