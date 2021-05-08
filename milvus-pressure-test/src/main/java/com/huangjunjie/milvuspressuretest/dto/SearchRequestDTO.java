package com.huangjunjie.milvuspressuretest.dto;

/**
 * @Author: huangJunJie  2021-03-30 11:02
 */
public class SearchRequestDTO {
    private  int TOPK;
    private  int NPROBE;
    private  String COLLECTION;

    public int getTOPK() {
        return TOPK;
    }

    public void setTOPK(int TOPK) {
        this.TOPK = TOPK;
    }

    public int getNPROBE() {
        return NPROBE;
    }

    public void setNPROBE(int NPROBE) {
        this.NPROBE = NPROBE;
    }

    public String getCOLLECTION() {
        return COLLECTION;
    }

    public void setCOLLECTION(String COLLECTION) {
        this.COLLECTION = COLLECTION;
    }
}
