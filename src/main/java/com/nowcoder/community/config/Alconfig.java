package com.nowcoder.community.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class Alconfig {

    @Bean
    public SimpleDateFormat mySimpleDateFormat(){

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    /*
    *    <beans>
    *        <bean  id="mySimpleDateFormat"  class="java.text.SimpleDateFormat"></bean>
    *    </beans>
    * */
}
