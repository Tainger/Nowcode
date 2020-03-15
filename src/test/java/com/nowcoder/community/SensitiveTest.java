package com.nowcoder.community;


import com.nowcoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {

    @Autowired
    private  SensitiveFilter sensitiveFilter;

    @Test
    public void TestSensitive(){
        String str="你好尼玛我去大保健我去开票";
        String str1 = sensitiveFilter.filter(str);
        System.out.println(str1);
    }

}
