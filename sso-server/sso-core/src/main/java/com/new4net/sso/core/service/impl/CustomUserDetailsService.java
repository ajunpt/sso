package com.new4net.sso.core.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.new4net.jwt.client.configuration.JwtClientProperties;
import com.new4net.jwt.server.configuration.DaoUserDetailsService;
import com.new4net.sso.api.dto.UserInfo;
import com.new4net.sso.core.entity.Authority;
import com.new4net.sso.core.entity.User;
import com.new4net.sso.core.repo.UserReposity;
import com.new4net.sso.core.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@Transactional
public class CustomUserDetailsService implements DaoUserDetailsService {

    @Autowired
    private UserReposity userReposity;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private JwtClientProperties jwtClientProperties;

    @Override
    //@Cacheable
    public UserInfo loadUserByUsername(String username) throws UsernameNotFoundException {


        /**
         * @todo 从数据库或者缓存中取出jwt token生成时用的salt
         *
         */

        String[] ss = username.split(";");

        if (ss.length > 0) {
            username = ss[0];
        }

        User user = userReposity.findByUsername(username);


        UserInfo userInfo = user.getUserInfo();

        Set<Authority> an = user.getAuthorities();
        String moduleName = null;
        if (ss.length == 2) {
            moduleName = ss[1];

        } else {
            moduleName = jwtClientProperties.getModuleName();
        }

        if (an != null && !an.isEmpty()) {
            List<Authority> ans = authorityService.getSubAuthorities(moduleName, an.toArray(new Authority[0]));
            userInfo.setAuthorities(ans.stream().map((authority) -> {
                return authority.getAuth();
            }).collect(Collectors.toSet()));
        }
        if(userInfo.isEnable()){
            return userInfo;
        }else {
            throw new UsernameNotFoundException("用户不可用");
        }

    }

    @Override
    //@CachePut
    public String saveUserLoginInfo(UserInfo user) {
        String salt = BCrypt.gensalt();  //正式开发时可以调用该方法实时生成加密的salt
        /**
         * @todo 将salt保存到数据库或者缓存中
         *
         */
        redisTemplate.opsForValue().set("token:" + user.getUsername(), salt, 3600, TimeUnit.SECONDS);
        Algorithm algorithm = Algorithm.HMAC256(salt);
        Date date = new Date(System.currentTimeMillis() + 3600 * 1000);  //设置1小时后过期
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(date)
                .withIssuedAt(new Date())
                .sign(algorithm);
    }

    @Override
    //@CacheRemove
    public void deleteUserLoginInfo(String username) {
        /**
         *
         * @todo 清除数据库或者缓存中登录salt
         *
         */
        redisTemplate.delete("token:" + username);
    }
}
