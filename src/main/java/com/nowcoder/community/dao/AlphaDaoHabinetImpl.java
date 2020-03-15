package com.nowcoder.community.dao;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("A")
public class AlphaDaoHabinetImpl implements AlphaDao {



    @Override
    public String select() {
        return "你好";
    }
}
