package request;

import common.BaseDeviceRequest;

public class TokenRequest extends BaseDeviceRequest {

    private String dFWVer;
    private String companyCode;

    public TokenRequest() {
        super();
    }

    public TokenRequest(Long mdn, String dFWVer, String companyCode) {
        super();
        BaseDeviceRequest.setDefaults(this, mdn);
        this.dFWVer = dFWVer;
        this.companyCode = companyCode;
    }

    public String getdFWVer() {
        return dFWVer;
    }
    public void setdFWVer(String dFWVer) {
        this.dFWVer = dFWVer;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }
}