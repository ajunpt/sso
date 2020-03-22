package com.new4net.sso.core;

import com.alibaba.fastjson.JSONObject;
import com.new4net.sso.api.dto.UserInfo;
import com.new4net.sso.core.controller.UserController;
import com.new4net.sso.core.entity.User;
import com.new4net.sso.core.service.impl.UserService;
import com.new4net.util.Page;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SsoCoreApplication.class)
@WebAppConfiguration("src/main/java")
public class UserServiceTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private MockHttpSession session;
    @Autowired
    private UserService userService;

    @Before
    public void setup() {
        // 初始化构建
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        session = new MockHttpSession();
        User user = userService.findById("402881a170ddc52a0170ddc53ef00001");
        session.setAttribute("user", user); //拦截器那边会判断用户是否登录，所以这里注入一个用户
    }

    /**
     * 测试同步
     */
    @Test
    public void testGetUserId() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("pageNo", 1);
        map.put("pageSize", 1);
        map.put("enable", true);
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.post("/user/listUsers")
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                        .content(JSONObject.toJSONString(map))
        )

                // .andExpect(MockMvcResultMatchers.status().isOk())             //等同于Assert.assertEquals(200,status);
                // .andExpect(MockMvcResultMatchers.content().string("hello lvgang"))    //等同于 Assert.assertEquals("hello lvgang",content);
                .andDo(MockMvcResultHandlers.print())

                .andReturn();
        int status = mvcResult.getResponse().getStatus();                 //得到返回代码
        String content = mvcResult.getResponse().getContentAsString();    //得到返回结果

        Assert.assertEquals(200, status);                        //断言，判断返回代码是否正确
        System.out.println(content);
    }

    @Autowired
    private UserController userController;

    @Test
    public void test2() {
        Map<String, Object> map = new HashMap<>();
        map.put("pageNo", 1);
        map.put("pageSize", 1);
        map.put("enable", true);
        Page<UserInfo> userPage = userController.listUsers(map);
        System.out.println("xx:" + JSONObject.toJSONString(userPage));
    }
}
