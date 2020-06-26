package net.coding.program.common.model.entity;

import java.io.Serializable;

public class UserInfoObject extends UserEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String token;
    private long expire;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }
}
