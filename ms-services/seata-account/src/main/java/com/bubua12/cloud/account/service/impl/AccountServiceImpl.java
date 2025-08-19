package com.bubua12.cloud.account.service.impl;

import com.bubua12.cloud.account.mapper.AccountTblMapper;
import com.bubua12.cloud.account.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
public class AccountServiceImpl implements AccountService {

    @Resource
    private AccountTblMapper accountTblMapper;

    @Override
    public void debit(String userId, int money) {
        // 扣减账户余额
        accountTblMapper.debit(userId, money);
    }
}
