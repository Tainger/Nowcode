package com.nowcoder.community.service;

import com.nowcoder.community.dao.AlphaDao;
import com.nowcoder.community.dao.DiscussionPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PostMapping;

@Service
public class AlphaService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussionPostMapper discussionPostMapper;
    @Autowired
    private TransactionTemplate transactionTemplate;


    @Transactional(isolation =Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public void save(){

        User user = new User();
        user.setUsername("GG");
        user.setActivationCode("123456");
        user.setStatus(0);
        user.setHeaderUrl("???");
        user.setType(0);
        userMapper.insertUser(user);

        DiscussPost discussPost = new DiscussPost();
        discussPost.setType(0);
        discussPost.setContent("131321");
        discussionPostMapper.insertDiscussPost(discussPost);

    }

    public Object save1(){
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        return  transactionTemplate.execute(
                new TransactionCallback<Object>() {

                    @Override
                    public Object doInTransaction(TransactionStatus transactionStatus) {
                        User user = new User();
                        user.setUsername("MM");
                        user.setActivationCode("123456");
                        user.setStatus(0);
                        user.setHeaderUrl("???");
                        user.setType(0);
                        userMapper.insertUser(user);

                        DiscussPost discussPost = new DiscussPost();
                        discussPost.setType(0);
                        discussPost.setContent("fdfdfd1");
                        discussionPostMapper.insertDiscussPost(discussPost);
                        Integer.valueOf("fdkdf");
                    return "ok";
                    }
                });
    }


}
