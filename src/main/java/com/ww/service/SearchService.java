/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ww.service;

import com.ww.util.HttpUtil;
import com.ww.util.ParseUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tarun
 */
@Path("/ww/{userId}/search/{searchText}")
public class SearchService {

    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String searchText(@PathParam("searchText") String searchText, @PathParam("userId") String userId) {
        try {
            searchText = URLDecoder.decode(searchText, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            logger.error("Decoding failed ", ex);
        }
        logger.info("Search Request by {} received: {}", userId, searchText);
        searchText = HttpUtil.encodeString(searchText);
        ParseUtil parseUtil = ParseUtil.getInstance();

        String newsJson = parseUtil.getNewsJson(searchText);
        if (newsJson == null) {
            String[] params = {"http://www.faroo.com/api", "q", searchText, "length", "10", "l", 
                "en", "src", "news", "i", "true", "f", "json", "key", "FuZMJiD@y11PASdasEzBHKIYn2Q_"};
            newsJson = HttpUtil.getStringFromURL(params);
            parseUtil.updateNewsTable(newsJson, searchText);
        }

        parseUtil.saveSearchText(userId, searchText);
        return newsJson;
    }

}
