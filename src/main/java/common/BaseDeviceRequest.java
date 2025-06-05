package common;

public abstract class BaseDeviceRequest {
    protected Long mdn; //차량 식별 키
    protected String tid; //터미널 아이디
    protected Integer mid; //제조사 아이디
    protected Integer pv; //패킷 버전
    protected Integer did; //디바이스 아이디

    public BaseDeviceRequest() {
    }

    public BaseDeviceRequest(Long mdn, String tid, Integer mid, Integer pv, Integer did) {
        this.mdn = mdn;
        this.tid = tid;
        this.mid = mid;
        this.pv = pv;
        this.did = did;
    }

    public static void setDefaults(BaseDeviceRequest baseDeviceRequest, Long mdn) {
        baseDeviceRequest.setMdn(mdn);
        baseDeviceRequest.setTid("A001");
        baseDeviceRequest.setMid(6);
        baseDeviceRequest.setPv(5);
        baseDeviceRequest.setDid(1);
    }

    public Long getMdn() {
        return mdn;
    }

    public void setMdn(Long mdn) {
        this.mdn = mdn;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public Integer getMid() {
        return mid;
    }

    public void setMid(Integer mid) {
        this.mid = mid;
    }

    public Integer getPv() {
        return pv;
    }

    public void setPv(Integer pv) {
        this.pv = pv;
    }

    public Integer getDid() {
        return did;
    }

    public void setDid(Integer did) {
        this.did = did;
    }
}