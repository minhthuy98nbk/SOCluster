package com.zingplay.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUserAction {
    private static final Logger log = LoggerFactory.getLogger(LogUserAction.class);
    public static Logger getInstance(){
        return log;
    }
}
