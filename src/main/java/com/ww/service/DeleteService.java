/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ww.service;

import com.ww.util.ParseUtil;
import java.util.logging.Level;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import org.parse4j.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tarun
 */
@Path("/ww/{userId}/delete/{searchText}")
public class DeleteService {
    private static final Logger logger = LoggerFactory.getLogger(DeleteService.class);
    
    @GET
    public void deleteText(@PathParam("searchText") String searchText, @PathParam("userId") String userId) {
        logger.info("Delete request by {} for {}", userId, searchText);
        try {
            
            ParseUtil util = ParseUtil.getInstance();
            util.deleteSearchText(userId, searchText);
        } catch (ParseException ex) {
            java.util.logging.Logger.getLogger(DeleteService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
