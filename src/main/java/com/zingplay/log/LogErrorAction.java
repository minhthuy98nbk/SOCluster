package com.zingplay.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogErrorAction {
    private static final Logger log = LoggerFactory.getLogger(LogErrorAction.class);
    public static Logger getInstance(){
        return log;
    }
}
