package com.gocam.goscamdemopro.cloud.data.entity;

public class OssInfo {


    /**
     * creater : common@goscam.com
     * deviceid : A9976100KJ769WNWV3A6FYLB111A
     * Expiration : 2017-10-23T02:52:38Z
     * key : STS.KYiPwPYzs7BhANrYikftkhTr5
     * secret : 2ZJY4cQoKSzVdSvq1tSG9yRxgdjV4GQE2LDBDwZsujAW
     * token : CAISkgR1q6Ft5B2yfSjIqprcG83ktKVSgICDQ2jDvWk+avtHh5HZ1zz2IHtKdXRpBOkcsf8zmWhX6fsTlrh4TJpAQkrsYsxr5Z1StwimZtIQOTFkcf9W5qe+EE2/VjRDtq27OpcjJbGwU/OpbE++100X6LDmdDKkckW4OJmS8/BOZcgWWQ/KFFghA8xNdC9js9M9P3ncPuq2SDSI5FDdF011oAFxpHpi4KCkuK2m5wHZkUfxx51bxcj4KYP2aNJ3btUtCo/kgLwnLPuQj3UNt0IbqvV5i7cB/DrLv9jnUTsz2AmcMuzb1txsIXUbYbMhSZZeofb9hY8w2JqIoK2mmkkTZLkNcAaAGtGf5vT6Q8r3GOcdX++iYl2/L7ntW7DxtwQ7Gz15UCpBZ9smLFJpFBUoUUu0A6S7+VfMEGDBLq+ey/MZ3IZSxVfl9sbwLDrpebiC1jsCMZIRdl4hMwVsvVbsaagbaQdBAXwFKr+eVtd0dwsM6/Gx5VyOBnw6nyAI5KCiPaKI6K8YcsD6U55H2M1GU70X4jR0FgyvaJDx1BYrUlNbGp8I/pjJE8bmsZbCgsfrOLaXVqBW4AQLLmuM83DcF0k9d3OhuocRDnOD4NnUwaD29JdtLRAj/Ng3D3veKog88gU7uvHttEjOqLC8DyGwjWM8+8jF+Y5U8ldjffqijOKUsyTXoHuKe649ldcH9rem44UnEhqAAXQDan6soeQ/1S9uQWAs67NAA0kykbzN36L4n1HgDrRmh7xL78wf+au93ONbePimNhQ3P4BhC0I5eSMD6G4WvXk/WC0+q66OEr+5bH15wUN8XoNC88Snpvj99mqg6/MVI8olNaryffQda/nw+s9hw+J9IWeqvQdT52YwzflyNg+K
     * endPoint : http://oss-cn-shanghai.aliyuncs.com
     * durationSeconds : 3600
     * timeStamp : 1508723558
     */

    private String creater;
    private String deviceid;
    private String Expiration;
    private String key;
    private String secret;
    private String token;
    private String endPoint;
    private int durationSeconds;
    private int timeStamp;

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getExpiration() {
        return Expiration;
    }

    public void setExpiration(String Expiration) {
        this.Expiration = Expiration;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "OssInfo{" +
                "creater='" + creater + '\'' +
                ", deviceid='" + deviceid + '\'' +
                ", Expiration='" + Expiration + '\'' +
                ", key='" + key + '\'' +
                ", secret='" + secret + '\'' +
                ", token='" + token + '\'' +
                ", endPoint='" + endPoint + '\'' +
                ", durationSeconds=" + durationSeconds +
                ", timeStamp=" + timeStamp +
                '}';
    }
}
