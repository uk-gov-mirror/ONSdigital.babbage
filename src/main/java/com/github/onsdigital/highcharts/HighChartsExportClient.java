package com.github.onsdigital.highcharts;

import com.github.onsdigital.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bren on 17/06/15.
 */
public class HighChartsExportClient {

    CloseableHttpClient client;
    CloseableHttpResponse response;

    public void openConnection() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    client = HttpClients.createDefault();
                }
            }
        }
    }

   public InputStream getImage(BaseChart chart) throws IOException {
        if (client == null) {
            openConnection();
        }

        InputStream data = null;
        System.out.println("Calling Highcharts export server");

       HttpPost post = new HttpPost(Configuration.getHighchartsExportSeverUrl());

       List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
       postParameters.add(new BasicNameValuePair("options", chart.toString()));
       postParameters.add(new BasicNameValuePair("type", "png"));
       postParameters.add(new BasicNameValuePair("width", "1500"));
       postParameters.add(new BasicNameValuePair("async", "false"));
       post.setEntity(new UrlEncodedFormEntity(postParameters));

       response = client.execute(post);
       HttpEntity responseEntity = response.getEntity();

        if (responseEntity != null && responseEntity.getContent() != null) {
            data = responseEntity.getContent();
        }

       System.out.println("Highcharts export response: " + response.getStatusLine());

        return data;

    }

    public void closeConnection() {
        IOUtils.closeQuietly(response);
        IOUtils.closeQuietly(client);
    }


}
