package com.cmliy.springweb.dto;

/**
 * 🔐 登录响应DTO
 *
 * 用于登录接口的响应数据，包含JWT令牌和用户信息。
 */
public class LoginResponseDTO {
    private String token;
    private String tokenType;
    private Long expiresIn;
    private UserDTO user;
    private String timestamp;

    // 无参构造函数
    public LoginResponseDTO() {}

    // 全参构造函数
    public LoginResponseDTO(String token, String tokenType, Long expiresIn, UserDTO user, String timestamp) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.user = user;
        this.timestamp = timestamp;
    }

    // Getter和Setter方法
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "LoginResponseDTO{" +
                "token='" + token + '\'' +
                ", tokenType='" + tokenType + '\'' +
                ", expiresIn=" + expiresIn +
                ", user=" + user +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}