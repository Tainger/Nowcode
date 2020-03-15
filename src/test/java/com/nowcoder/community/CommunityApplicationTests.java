package com.nowcoder.community;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommunityApplicationTests implements ApplicationContextAware {

	private  ApplicationContext applicationContext;

	@Autowired
	private UserMapper userMapper;
	/**
	 * 会自己把他放进去
	 * @param applicationContext
	 * @throws BeansException
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void TestApplication(){
		System.out.println(applicationContext);
		AlphaDao alphaDao = applicationContext.getBean("A",AlphaDao.class);
		System.out.println(alphaDao.select());
	}

	@Test
	public void TestApplication1(){
		SimpleDateFormat alphaServiceImpl =  applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(alphaServiceImpl.format(new Date()));
	}

	@Test
	public void TestConfig(){
		SimpleDateFormat simpleDateFormat = (SimpleDateFormat) applicationContext.getBean("mySimpleDateFormat");
		System.out.println(simpleDateFormat.format(new Date()));
	}

	@Autowired
	private AlphaService alphaService;
	@Autowired
	private  AlphaDao alphaDao;
	@Autowired
	private SimpleDateFormat simpleDateFormat;

	@Test
	public void  TestDI(){
		System.out.println(alphaDao);
		System.out.println(alphaService);
		System.out.println(simpleDateFormat);
		System.out.println(alphaDao.select());
	}

	@Test
	public void TestSelectByid(){
		User user = userMapper.selectById(1);
		System.out.println(user);
	}
	@Test
	public void Save(){
		alphaService.save();
	}

	@Test
	public void Save1(){
		alphaService.save1();
	}
}
