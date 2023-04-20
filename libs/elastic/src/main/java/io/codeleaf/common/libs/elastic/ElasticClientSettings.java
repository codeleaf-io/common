package io.codeleaf.common.libs.elastic;

public class ElasticClientSettings {

    private final String host;
    private final int port;
    private final String fingerprint;
    private final String user;
    private final String password;
    private final String scheme;

    public ElasticClientSettings(String host, int port, String fingerprint, String user, String password, String scheme) {
        this.host = host;
        this.port = port;
        this.fingerprint = fingerprint;
        this.user = user;
        this.password = password;
        this.scheme = scheme;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getScheme() {
        return scheme;
    }
}

