package request;

import common.BaseDeviceRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GetSetInfoRequest extends BaseDeviceRequest {
    private String onTime; //"yyyyMMddHHmmss"
    private String dFWVer;

    public GetSetInfoRequest() {
        super();
    }

    public GetSetInfoRequest(Long mdn, String dFWVer) {
        super();
        BaseDeviceRequest.setDefaults(this, mdn);
        //todo: 차량 시동 On 시간을 어디서 구하나?
        this.onTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        this.dFWVer = dFWVer;
    }

    public String getOnTime() {return onTime;}
    public void setOnTime(String onTime) {
        this.onTime = onTime;
    }
    public String getDFWVer() {
        return dFWVer;
    }
    public void setDFWVer(String dFWVer) {
        this.dFWVer = dFWVer;
    }
}