package ru.practicum.model;

import lombok.Data;

@Data
public class AuthResponse {
    private Boolean success;
    private String accessToken;
    private String refreshToken;
    private UserData user;
    private String message;

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public UserData getUser() { return user; }
    public void setUser(UserData user) { this.user = user; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "success=" + success +
                ", accessToken='" + (accessToken != null ? "PROVIDED" : "null") + '\'' +
                '}';
    }
}


