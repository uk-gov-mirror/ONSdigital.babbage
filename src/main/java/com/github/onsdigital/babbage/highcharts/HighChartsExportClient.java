package com.github.onsdigital.babbage.highcharts;

import com.github.onsdigital.babbage.configuration.Configuration;
import com.github.onsdigital.babbage.util.http.PooledHttpClient;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bren on 17/06/15.
 */
public class HighChartsExportClient {


    private static PooledHttpClient client;
    private static HighChartsExportClient instance;

    //singleton
    private HighChartsExportClient() {
    }

    public static HighChartsExportClient getInstance() {
        if (instance == null) {
            synchronized (HighChartsExportClient.class) {
                if (instance == null) {
                    instance = new HighChartsExportClient();
                    System.out.println("Initializing Highcharts export server client connection pool");
                    client = new PooledHttpClient(Configuration.HIGHCHARTS.getExportSeverUrl());
                    client.getConfiguration().setMaxConnection(Configuration.HIGHCHARTS.getMaxHighchartsServerConnection());
                }
            }
        }
        return instance;
    }

    public InputStream getImage(String chartConfig) throws IOException {
        System.out.println("Calling Highcharts export server");
        List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        postParameters.add(new BasicNameValuePair("options", chartConfig));
        postParameters.add(new BasicNameValuePair("type", "png"));
//        postParameters.add(new BasicNameValuePair("width", "1500"));
        postParameters.add(new BasicNameValuePair("async", "false"));
        CloseableHttpResponse response = client.sendPost("/", null, postParameters);
        System.out.println("Highcharts export response: " + response.getStatusLine());
        return response.getEntity().getContent();
    }

}
