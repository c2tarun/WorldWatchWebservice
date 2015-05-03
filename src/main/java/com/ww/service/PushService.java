/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ww.service;

import com.ww.util.ParseUtil;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Level;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tarun
 */
@Path("/ww/{userEmail}/sendPush/{searchText}")
public class PushService {

    private static final Logger logger = LoggerFactory.getLogger(PushService.class);

    @GET
    public void sendPush(@PathParam("userEmail") String userEmail, @PathParam("searchText") String searchText) {
        try {
            ParseUtil util = ParseUtil.getInstance();
            searchText = URLDecoder.decode(searchText, "UTF-8");
            util.push(searchText, userEmail);
        } catch (UnsupportedEncodingException ex) {
            java.util.logging.Logger.getLogger(PushService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
