package common;

public class EmulatorConfig {
    private String serverUrl;
    private Long deviceMdn;
    private String firmwareVersion;
    private int batchSize;
    private int dataGenerationInterval; // 초
    private long sendInterval; // 밀리초

    // Builder 패턴
    public static class Builder {
        private EmulatorConfig config = new EmulatorConfig();

        public Builder serverUrl(String serverUrl) {
            config.serverUrl = serverUrl;
            return this;
        }

        public Builder deviceMdn(Long deviceMdn) {
            config.deviceMdn = deviceMdn;
            return this;
        }

        public Builder firmwareVersion(String firmwareVersion) {
            config.firmwareVersion = firmwareVersion;
            return this;
        }

        public Builder batchSize(int batchSize) {
            config.batchSize = batchSize;
            return this;
        }

        public Builder dataGenerationInterval(int seconds) {
            config.dataGenerationInterval = seconds;
            return this;
        }

        public Builder sendInterval(long milliseconds) {
            config.sendInterval = milliseconds;
            return this;
        }

        public EmulatorConfig build() {
            return config;
        }
    }

    // Getters
    public String getServerUrl() { return serverUrl; }
    public Long getDeviceMdn() { return deviceMdn; }
    public String getFirmwareVersion() { return firmwareVersion; }
    public int getBatchSize() { return batchSize; }
    public int getDataGenerationInterval() { return dataGenerationInterval; }
    public long getSendInterval() { return sendInterval; }

    @Override
    public String toString() {
        return String.format("common.EmulatorConfig{serverUrl='%s', deviceMdn=%d, batchSize=%d, interval=%dms}",
                serverUrl, deviceMdn, batchSize, sendInterval);
    }
}