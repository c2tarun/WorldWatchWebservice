/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ww.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.parse4j.Parse;
import org.parse4j.ParseException;
import org.parse4j.ParseObject;
import org.parse4j.ParseQuery;
import org.parse4j.callback.FindCallback;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tarun
 */
public class ParseUtil {

    private static final String SEARCH_HISTORY_TB = "SEARCH_HISTORY";
    private static final String USER_ID = "USER_ID";
    private static final String SEARCH_TEXT = "SEARCH_TEXT";
    private static final String NEWS_JSON_TB = "NEWS_JSON";
    protected static final String MAX_TS = "MAX_TS";
    protected static final String JSON = "JSON";
    protected static final String URL = "https://api.parse.com/1/push";
    private String fetchedNewsJson;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ParseUtil.class);

    private static ParseUtil SINGLETON;

    private ParseUtil() {
        Parse.initialize("WhqWj009luOxOtIH3rM9iWJICLdf0NKbgqdaui8Q", "lThhKObAz1Tkt092Cl1HeZv4KLUsdATvscOaGN2y");
    }

    public static ParseUtil getInstance() {
        if (SINGLETON == null) {
            SINGLETON = new ParseUtil();
        }
        return SINGLETON;
    }

    public void saveSearchText(final String userId, final String searchText) {
        ParseQuery<ParseObject> searchQuery = ParseQuery.getQuery(SEARCH_HISTORY_TB);
        searchQuery.whereEqualTo(USER_ID, userId).findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> list, ParseException pe) {
                if (pe == null) {
                    if (list != null && list.size() > 0) {
                        ParseObject searchHistory = list.get(0);
                        String searchHistoryText = appendSearchText(searchHistory.getString(SEARCH_TEXT), searchText);
                        searchHistory.put(SEARCH_TEXT, searchHistoryText);
                        searchHistory.saveInBackground();
                    } else {
                        ParseObject searchHistory = new ParseObject(SEARCH_HISTORY_TB);
                        searchHistory.put(SEARCH_TEXT, searchText);
                        searchHistory.put(USER_ID, userId);
                        searchHistory.saveInBackground();
                    }
                } else {
                    logger.error("Failed to fetch user data ", pe);
                }
            }
        });

    }

    /**
     *
     * @param searchHistoryText
     * @param textToAppend
     * @return
     */
    private String appendSearchText(String searchHistoryText, String textToAppend) {
        // avoid duplicates
        String[] searches = searchHistoryText.split(";");
        for (String search : searches) {
            if (textToAppend != null && textToAppend.equals(search)) {
                return searchHistoryText;
            }
        }
        searchHistoryText = searchHistoryText + ";" + textToAppend;
        searches = searchHistoryText.split(";");
        if (searches.length <= 5) {
            return searchHistoryText;
        } else {
            searchHistoryText = "";
            for (int i = searches.length - 5; i < searches.length; i++) {
                searchHistoryText = searchHistoryText + ";" + searches[i];
            }
            searchHistoryText = searchHistoryText.substring(1);
            return searchHistoryText;
        }
    }

    public void updateNewsTable(String newsJson, String searchText) {
        long maxTS = 0;
        JsonFactory factory = new JsonFactory();
        try {
            JsonParser parser = factory.createParser(newsJson);
            while (!parser.isClosed()) {
                JsonToken token = parser.nextToken();
                if (token == null) {
                    break;
                }
                String fieldName = parser.getCurrentName();
                if (fieldName != null && fieldName.equals("date")) {
                    parser.nextToken();
                    long date = Long.parseLong(parser.getText());
                    if (maxTS < date) {
                        maxTS = date;
                    }
                }
            }
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            logger.error("File not found ", e);
        }

        ParseObject newsObject = new ParseObject(NEWS_JSON_TB);
        newsObject.put(SEARCH_TEXT, searchText);
        newsObject.put(JSON, newsJson);
        newsObject.put(MAX_TS, maxTS);
        newsObject.saveInBackground();

    }

    public String getNewsJson(String searchText) {
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(NEWS_JSON_TB);
            query.whereEqualTo(SEARCH_TEXT, searchText);
            List<ParseObject> list = query.find();
            if (list == null) {
                fetchedNewsJson = null;
            } else {
                ParseObject obj = list.get(0);
                fetchedNewsJson = obj.getString(JSON);
            }
            return fetchedNewsJson;
        } catch (ParseException ex) {
            Logger.getLogger(ParseUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void deleteSearchText(String userId, String searchText) throws ParseException {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(SEARCH_HISTORY_TB);
        query.whereEqualTo(USER_ID, userId);
        List<ParseObject> searchHistories = query.find();
        if (!searchHistories.isEmpty()) {
            ParseObject searchHistory = searchHistories.get(0);
            String searchTexts = searchHistory.getString(SEARCH_TEXT);
            searchTexts = searchTexts.replaceAll(searchText, "").replaceAll(";;", ";");
            char ch = searchTexts.charAt(searchTexts.length() - 1);
            if (ch == ';') {
                searchTexts = searchTexts.substring(0, searchTexts.length() - 1);
            }
            ch = searchTexts.charAt(0);
            if (ch == ';') {
                searchTexts = searchTexts.substring(1);
            }
            searchHistory.put(SEARCH_TEXT, searchTexts);
            searchHistory.save();
        }
    }
    public void push(String searchKeyword, String userEmail) {
        try {
            String data = "{\"where\": {\"useremail\": \""+userEmail+"\"},\"data\": {\"keywords\": [\""+searchKeyword+"\"]}}";
            HttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().build()).build();
            HttpPost post = new HttpPost(URL);
            post.setHeader("X-Parse-Application-Id", "WhqWj009luOxOtIH3rM9iWJICLdf0NKbgqdaui8Q");
            post.setHeader("X-Parse-REST-API-Key", "lThhKObAz1Tkt092Cl1HeZv4KLUsdATvscOaGN2y");
            post.setHeader("Content-Type", "application/json");
            StringEntity strEntity = new StringEntity(data);
            post.setEntity(strEntity);
            httpClient.execute(post);
            logger.debug("Pushed {} to userId {}", data.toString(), userEmail);
        } catch (Exception ex) {
            logger.error("Push Failed for {} ", userEmail, ex);
        }

    }
}
