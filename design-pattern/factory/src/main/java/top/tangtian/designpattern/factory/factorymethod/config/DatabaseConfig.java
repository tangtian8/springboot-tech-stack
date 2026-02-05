package top.tangtian.designpattern.factory.factorymethod.config;

public class DatabaseConfig {
    private String url;
    private String username;
    private String password;
    private int maxConnections;
    private int minConnections;
    private int connectionTimeout;

    public DatabaseConfig(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.maxConnections = 10;
        this.minConnections = 2;
        this.connectionTimeout = 30000;
    }

    // Getters and Setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getMaxConnections() { return maxConnections; }
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public int getMinConnections() { return minConnections; }
    public void setMinConnections(int minConnections) {
        this.minConnections = minConnections;
    }

    public int getConnectionTimeout() { return connectionTimeout; }
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @Override
    public String toString() {
        return String.format("DatabaseConfig{url='%s', username='%s', maxConn=%d}",
                url, username, maxConnections);
    }
}