/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ww.util;

import org.junit.Test;

/**
 *
 * @author tarun
 */
public class ParseUtilTest {
    
    @Test
    public void testNewsTableMethod() {
        ParseUtil parseUtil = ParseUtil.getInstance();
        parseUtil.updateNewsTable(null, null);
    }
    
}
