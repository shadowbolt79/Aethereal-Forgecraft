package com.shadowking97.forgecraft.util;

import net.minecraftforge.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

/**
 * Created by Shadow Bolt on 8/9/2017.
 */
public class CommonUtil {
    public static final boolean DEBUG = true;

    /***
     * Static method to make removing logs easier for release build
     */
    public static void debug(String out)
    {
        if(DEBUG)
            FMLLog.log(Level.DEBUG,"Forgecraft: "+out);
    }
}
