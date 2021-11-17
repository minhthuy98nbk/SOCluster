package com.zingplay.socket;

import com.zingplay.service.user.UserService;
import org.springframework.stereotype.Service;

public abstract class SocketRequest {

    public abstract void execute(SocketInfo info, UserService userService);

}
