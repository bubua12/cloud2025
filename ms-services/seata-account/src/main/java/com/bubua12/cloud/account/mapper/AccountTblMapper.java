package com.bubua12.cloud.account.mapper;

import com.bubua12.cloud.account.bean.AccountTbl;
import org.apache.ibatis.annotations.Param;

public interface AccountTblMapper {

    int deleteByPrimaryKey(Long id);

    int insert(AccountTbl record);

    int insertSelective(AccountTbl record);

    AccountTbl selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(AccountTbl record);

    int updateByPrimaryKey(AccountTbl record);

    void debit(@Param("userId") String userId,
               @Param("money") int money);
}
