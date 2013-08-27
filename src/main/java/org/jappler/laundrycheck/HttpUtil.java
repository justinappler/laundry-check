package org.jappler.laundrycheck;

import java.io.IOException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.apache.ApacheHttpTransport;

public class HttpUtil {

    /**
     * Generic method to get the response HTML of a URL. Returns an empty string
     * on any errors.
     * 
     * @param url
     *            The URL of the page to load
     * @return The page's HTML
     */
    public static String getHTMLForURL(GenericUrl url) {
        ApacheHttpTransport transport = new ApacheHttpTransport(ApacheHttpTransport.newDefaultHttpClient());
        HttpRequestFactory requestFactory = transport.createRequestFactory();

        try {
            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse response = request.execute();

            return response.parseAsString();
        } catch (IOException e) {
            System.err.println("Error when getting HTML for a URL");
            e.printStackTrace();
        }

        return "";
    }
}
