package com.sysnote.common;

import com.sysnote.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

/**
 * Created by root on 15-6-12.
 */
public class ConfigFiles {
    protected static Logger logger = LoggerFactory.getLogger(ConfigFiles.class);
    private ResourceBundle rb = null;
    private static ConfigFiles cluster = null;

    static {
        logger.info("load cluster properities.");
        cluster = new ConfigFiles(ResourceBundle.getBundle("rock"));
    }

    public ConfigFiles(ResourceBundle rb) {
        this.rb = rb;
    }

    public static ConfigFiles cluster(){
        return cluster;
    }

    public String get(String item, String defaultValue) {
        String value = null;
        if (rb != null) {
            try {
                value = rb.getString(item.trim());
                value = value.trim();
            } catch (Exception e) {
                value = defaultValue;
            }
        }
        if (StringUtil.isEmpty(value)) {
            value = defaultValue;
        }
        return value;
    }

    public int getInt(String item, String defaultValue) {
        int i = 0;
        String value = get(item, defaultValue);
        try {
            i = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.info(e.getMessage());
        }
        return i;
    }

    public boolean getBoolean(String item, boolean defaultValue) {
        boolean b = false;
        String value = get(item, (new Boolean(defaultValue)).toString());
        if (value != null && value.equalsIgnoreCase("true")) {
            b = true;
        }
        return b;
    }
}
