package com.bubua12.cloud.storage.service.impl;

import com.bubua12.cloud.storage.mapper.StorageTblMapper;
import com.bubua12.cloud.storage.service.StorageService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class StorageServiceImpl implements StorageService {

    @Resource
    private StorageTblMapper storageTblMapper;

    @Transactional
    @Override
    public void deduct(String commodityCode, int count) {
        storageTblMapper.deduct(commodityCode, count);
        if (count == 5) {
            throw new RuntimeException("库存不足");
        }
    }
}
