package com.example._30_itemprocessor_customize;

import org.springframework.batch.item.ItemProcessor;

public class CustomizeItemProcessor implements ItemProcessor<User, User> {

    @Override
    public User process(User item) throws Exception {
        // id 為偶數的用戶不要
        // 返回 null 的時候，讀入的 item 會放棄，不會進入 itemwriter
        return item.getId() % 2 == 0 ? null : item;
    }
}
