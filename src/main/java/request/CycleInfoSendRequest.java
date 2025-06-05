package request;

import gps.CycleInfo;

import java.util.List;

public class CycleInfoSendRequest {
    private Long mdn; //차량 식별 key
    private String tid; //터미널 아이디, 차량관제 'A001'로 고정
    private String mid; //제조사 아이디, '6'값 사용
    private String pv; //패킷 버전 범위: 0~65535, 5로 고정
    private String did; //디바이스 아이디, 1로 고정
    private String oTime; //발생 시간 yyyyMMddHHmm
    private String cCnt; //주기정보 개수
    private List<CycleInfo> cList; //주기정보 리스트

    public List<CycleInfo> getcList() {
        return cList;
    }

    public void setCList(List<CycleInfo> cList) {
        this.cList = cList;
    }

    public String getcCnt() {
        return cCnt;
    }

    public void setCCnt(String cCnt) {
        this.cCnt = cCnt;
    }

    public String getoTime() {
        return oTime;
    }

    public void setOTime(String oTime) {
        this.oTime = oTime;
    }

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public String getPv() {
        return pv;
    }

    public void setPv(String pv) {
        this.pv = pv;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public Long getMdn() {
        return mdn;
    }

    public void setMdn(Long mdn) {
        this.mdn = mdn;
    }

    @Override
    public String toString() {
        return "request.CycleInfoSendRequest{" +
                "mdn=" + mdn +
                ", tid='" + tid + '\'' +
                ", mid='" + mid + '\'' +
                ", pv='" + pv + '\'' +
                ", did='" + did + '\'' +
                ", oTime='" + oTime + '\'' +
                ", cCnt='" + cCnt + '\'' +
                ", cList=" + cList +
                '}';
    }
}