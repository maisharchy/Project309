package backend.demo2.Server;

public class ServerDetailsDTO {
    private Integer serverId;
    private String serverName;

    public ServerDetailsDTO(Integer serverId, String serverName) {
        this.serverId = serverId;
        this.serverName = serverName;
    }

    // Getters and setters
    public Integer getServerId() {
        return serverId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}
