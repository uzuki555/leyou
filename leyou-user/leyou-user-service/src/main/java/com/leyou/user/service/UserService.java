package com.leyou.user.service;

import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import com.leyou.utils.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;
    private static  final String KEY_PREFIX = "USER:VERIFY:";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    public Boolean checkUser(String data, Integer type) {
        User user = new User();
        if (data==null){
            return null;
        }
        if(type==1){
            user.setUsername(data);
        }if(type==2){
            user.setPhone(data);
        }else {
            return  null;
        }
        return userMapper.selectCount(user)==0;
    }

    public void sendVerifyCode(String phone) {
        if(StringUtils.isBlank(phone)){
            return;
        }
        Map<String,String> map = new HashMap<>();
        map.put("phone",phone);
        String code = NumberUtils.generateCode(6);
        map.put("code",code);
        this.amqpTemplate.convertAndSend("LEYOU.SMS.EXCHANGE","verify.code",map);
        stringRedisTemplate.opsForValue().set(KEY_PREFIX+phone,code,5, TimeUnit.MINUTES);
    }
    @Transactional
    public void register(@Valid User user, String code) {
        String verify = stringRedisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if(!StringUtils.equals(verify,code)){
            return;
        }
        user.setId(null);
        user.setCreated(new Date());
        String salt = CodecUtils.generateSalt();
        String password = CodecUtils.md5Hex(user.getPassword(), salt);
        user.setPassword(password);
        userMapper.insertSelective(user);
    }

    public User queryByUser(String username, String password) {
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        if(!StringUtils.equals(user.getPassword(),CodecUtils.md5Hex(password,user.getSalt()))){
            return null;
        }
        if(user!=null){
            return user;
        }
        return null;
    }
}
