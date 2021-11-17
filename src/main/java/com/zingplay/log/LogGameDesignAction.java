package com.zingplay.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogGameDesignAction {
    private static final Logger log = LoggerFactory.getLogger(LogGameDesignAction.class);
    public static Logger getInstance(){
        return log;
    }
}
