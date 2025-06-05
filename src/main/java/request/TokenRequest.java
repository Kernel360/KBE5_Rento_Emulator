package request;

import common.BaseDeviceRequest;

public class TokenRequest extends BaseDeviceRequest {

    private String dFWVer;

    public TokenRequest() {
        super();
    }

    public TokenRequest(Long mdn, String dFWVer) {
        super();
        BaseDeviceRequest.setDefaults(this, mdn);
        this.dFWVer = dFWVer;
    }

    public String getdFWVer() {
        return dFWVer;
    }
    public void setdFWVer(String dFWVer) {
        this.dFWVer = dFWVer;
    }
}