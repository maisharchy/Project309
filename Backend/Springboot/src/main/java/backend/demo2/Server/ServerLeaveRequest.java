package backend.demo2.Server;


import org.springframework.lang.NonNull;

public class ServerLeaveRequest {
    @NonNull
    private Integer serverId;
    @NonNull
    private Long userId;

    // Getters and setters
    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
