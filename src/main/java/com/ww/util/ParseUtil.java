/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ww.util;

import java.util.List;
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

    private static final String SEARCH_HISTORY = "SEARCH_HISTORY";
    private static final String USER_ID = "USER_ID";
    private static final String SEARCH_TEXT = "SEARCH_TEXT";
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
        ParseQuery<ParseObject> searchQuery = ParseQuery.getQuery(SEARCH_HISTORY);
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
                        ParseObject searchHistory = new ParseObject(SEARCH_HISTORY);
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

}
