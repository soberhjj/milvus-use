package com.huangjunjie.milvuspressuretest.controller;

import com.huangjunjie.milvuspressuretest.dto.SearchRequestDTO;
import com.huangjunjie.milvuspressuretest.service.MilvusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: huangJunJie  2021-03-19 16:25
 */
@RestController
public class MilvusController {

    @Autowired
    MilvusService milvusService;

    @PostMapping("/search")
    public int search(@RequestBody SearchRequestDTO searchRequestDTO) {
        return milvusService.search(searchRequestDTO.getCOLLECTION(),searchRequestDTO.getNPROBE(),searchRequestDTO.getTOPK());
    }

//    @PostMapping("/searchInCluster")
//    public int searchInCluster() {
//        return milvusService.searchInCluster();
//    }

    @PostMapping("/insert")
    public void insert() {
        milvusService.insert();
    }

    @PostMapping("/delete")
    public void delete() {
        milvusService.delete();
    }

}
