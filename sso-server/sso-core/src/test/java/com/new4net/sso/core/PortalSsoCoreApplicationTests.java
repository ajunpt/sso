package com.new4net.sso.core;

import com.new4net.sso.core.repo.UserReposity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
public class PortalSsoCoreApplicationTests {
    @Autowired
    private UserReposity userReposity;

    @Test
    public void contextLoads() {

    }

    //    @Autowired
//    AuthorityServiceImpl authorityService;
//	@Test
//    public void test1(){
//	    long l = System.currentTimeMillis();
//        Authority authority = authorityService.findById("ROLE_SYSTEMADMIN");
//        List<Authority> authorityList = authorityService.getSubAuthorities("SSO_CORE",authority);
//        System.out.println(System.currentTimeMillis()-l);
//    }
//    @Autowired
//    private ModuleServiceImpl moduleService;
//    @Test
//    public void test2(){
//        long l = System.currentTimeMillis();
//        List<ModuleInfo> moduleInfos = moduleService.listAllModules();
//        System.out.println(System.currentTimeMillis()-l);
//    }
    @Test
    public void test() {

    }
}
