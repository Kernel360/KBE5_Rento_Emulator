package request;

import common.BaseDeviceRequest;

public class CheckInfoRequest extends BaseDeviceRequest {
    private String onTime;
    private Integer ctrCnt;
    private Integer getCnt;

    public CheckInfoRequest() {
        super();
    }

    public CheckInfoRequest(Long mdn, String onTime, Integer ctrCnt, Integer getCnt) {
        super();
        BaseDeviceRequest.setDefaults(this, mdn);
        this.onTime = onTime;
        this.ctrCnt = ctrCnt;
        this.getCnt = getCnt;
    }

    public String getOnTime() {
        return onTime;
    }

    public void setOnTime(String onTime) {
        this.onTime = onTime;
    }

    public Integer getCtrCnt() {
        return ctrCnt;
    }

    public void setCtrCnt(Integer ctrCnt) {
        this.ctrCnt = ctrCnt;
    }

    public Integer getGetCnt() {
        return getCnt;
    }

    public void setGetCnt(Integer getCnt) {
        this.getCnt = getCnt;
    }
}
