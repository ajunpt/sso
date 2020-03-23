package com.new4net.sso.core.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.new4net.sso.core.RabbitConfig;
import com.new4net.sso.core.dao.BaseDao;
import com.new4net.sso.core.entity.User;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.amqp.core.Message;
import javax.annotation.Resource;

@Component
@Transactional
public class UserService extends BaseServiceImpl<User> {
    public UserService(){
    }
    @Resource(name="userDao")
    public void setBaseDao(BaseDao<User> baseDao) {
        super.setBaseDao(baseDao);
    }

    @RabbitListener(queues = {RabbitConfig.USER_SAVE_QUEUE})
    public void save(String msg, Message message , Channel channel){
        save(JSONObject.parseObject(msg,User.class));
    }
}
