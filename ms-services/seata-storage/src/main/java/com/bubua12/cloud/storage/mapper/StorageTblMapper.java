package com.bubua12.cloud.storage.mapper;

import com.bubua12.cloud.storage.bean.StorageTbl;
import org.apache.ibatis.annotations.Param;

public interface StorageTblMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StorageTbl record);

    int insertSelective(StorageTbl record);

    StorageTbl selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(StorageTbl record);

    int updateByPrimaryKey(StorageTbl record);

    void deduct(@Param("commodityCode") String commodityCode,
                @Param("count") int count);
}
