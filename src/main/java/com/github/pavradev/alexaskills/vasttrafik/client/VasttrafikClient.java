package com.github.pavradev.alexaskills.vasttrafik.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.pavradev.alexaskills.vasttrafik.client.model.Departure;
import com.github.pavradev.alexaskills.vasttrafik.client.model.DepartureBoardWrapper;
import com.github.pavradev.alexaskills.vasttrafik.client.model.Token;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class VasttrafikClient {
    private static final Logger log = LoggerFactory.getLogger(VasttrafikClient.class);

    private static final String AUTH_URI = "https://api.vasttrafik.se/token";
    private static final String DEPARTURE_BOARD_URI = "https://api.vasttrafik.se/bin/rest.exe/v2/departureBoard?id=%s&date=%s&time=%s&format=json";

    private static final String stopId = "9021014001900000";
    private static final String vasttraficDevToken;

    static {
        vasttraficDevToken = System.getenv("VASTTRAFIK_TOKEN");
    }

    private ObjectMapper objectMapper;
    private CloseableHttpClient httpClient;

    public VasttrafikClient() {
        httpClient = HttpClients.createDefault();
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    public List<Departure> getNextDepartures(LocalDateTime dateTime) throws Exception {
        Token token = httpClient.execute(accessTokenRequest(), getResponseHandler(Token.class));
        String access_token = token.getAccessToken();

        DepartureBoardWrapper departures = httpClient.execute(
                departureBoardRequest(access_token, dateTime),
                getResponseHandler(DepartureBoardWrapper.class)
        );
        return departures.getDepartureBoard().getDepartureList();
    }

    private <T> ResponseHandler<T> getResponseHandler(Class<T> classOfT) {
        return (response) -> {
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            if (statusLine.getStatusCode() >= 300) {
                throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
            }
            if (entity == null) {
                throw new ClientProtocolException("Response contains no content");
            }
            Reader reader = new InputStreamReader(entity.getContent());
            return objectMapper.readValue(reader, classOfT);
        };
    }

    private HttpGet departureBoardRequest(String access_token, LocalDateTime dateTime) {
        String getNextDepartures = String.format(DEPARTURE_BOARD_URI,
                stopId,
                dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")),
                dateTime.format(DateTimeFormatter.ofPattern("HH:mm")));
        log.info("Get departures: {}", getNextDepartures);

        HttpGet httpGet = new HttpGet(getNextDepartures);
        httpGet.addHeader("Authorization", "Bearer " + access_token);
        return httpGet;
    }

    private HttpPost accessTokenRequest() throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(AUTH_URI);
        List<NameValuePair> formEntries = Collections.singletonList(new BasicNameValuePair("grant_type", "client_credentials"));
        httpPost.setEntity(new UrlEncodedFormEntity(formEntries));
        httpPost.addHeader("Authorization", "Basic " + vasttraficDevToken);
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
        return httpPost;
    }
}
