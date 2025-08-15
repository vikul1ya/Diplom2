package ru.practicum.model;
import java.util.Map;

public class AuthResponse {
    private Boolean success;
    private String accessToken;
    private String refreshToken;
    private Map<String, Object> user;

    public AuthResponse() {}

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Map<String, Object> getUser() {
        return user;
    }

    public void setUser(Map<String, Object> user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "AuthResponse{success=" + success + ", accessToken=" + (accessToken != null ? "[PROVIDED]" : "null") + "}";
    }
}


