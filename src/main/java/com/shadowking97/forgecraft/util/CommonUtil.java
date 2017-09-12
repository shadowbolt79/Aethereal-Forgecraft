package com.shadowking97.forgecraft.util;

import org.apache.logging.log4j.Logger;

/**
 * Created by Shadow Bolt on 8/9/2017.
 */
public class CommonUtil {
    public static final boolean DEBUG = true;

    static Logger LOGGER;

    public static void setLogger(Logger l)
    {
        LOGGER = l;
    }

    /***
     * Static method to make removing logs easier for release build
     */
    public static void debug(String out)
    {
        if(DEBUG)
            LOGGER.debug("Forgecraft: "+out);
    }

    public static void info(String out)
    {
        LOGGER.info("Forgecraft: "+out);
    }

    public static void error(String out)
    {
        LOGGER.error("Forgecraft: "+out);
    }

    public static void error(String out, Throwable e)
    {
        LOGGER.error("Forgecraft: "+out,e);
    }

    public static void fatal(String out, Throwable e)
    {
        LOGGER.fatal("Forgecraft: "+out,e);
    }
}
