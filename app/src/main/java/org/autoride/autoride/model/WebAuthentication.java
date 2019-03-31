package org.autoride.autoride.model;

public class WebAuthentication {

    private String accessToken;
    private String rememberToken;
    private String publicKey;
    private String privateKey;
    private String riderFireBaseToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRememberToken() {
        return rememberToken;
    }

    public void setRememberToken(String rememberToken) {
        this.rememberToken = rememberToken;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getRiderFireBaseToken() {
        return riderFireBaseToken;
    }

    public void setRiderFireBaseToken(String riderFireBaseToken) {
        this.riderFireBaseToken = riderFireBaseToken;
    }
}