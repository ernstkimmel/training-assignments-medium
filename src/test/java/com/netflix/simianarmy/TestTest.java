package com.netflix.simianarmy;

import org.testng.annotations.Test;
import org.testng.Assert;

public class TestTest {
    
    @Test
    public void Test1() {
        String testString = "blabla";
        int a = 1;
        int b = 2;
        Assert.assertEquals(testString.toUpperCase(), "BLABLA");
        Assert.assertEquals(a+b, 3);
        Assert.assertTrue(a < b);
    }
    
}
