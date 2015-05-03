/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ww.util;

import java.text.ParseException;
import org.junit.Test;

/**
 *
 * @author tarun
 */
public class ParseUtilTest {
    
//    @Test
//    public void testNewsTableMethod() {
//        ParseUtil parseUtil = ParseUtil.getInstance();
//        parseUtil.updateNewsTable(null, null);
//    }
//    
    @Test
    public void testPush() throws ParseException {
        ParseUtil parseUtil = ParseUtil.getInstance();
        parseUtil.push("toyota", "praful@gmail.com");
    }
//    
}
