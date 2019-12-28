package com.new4net.sso.core;

import com.new4net.sso.api.dto.ModuleInfo;
import com.new4net.sso.core.entity.Authority;

import com.new4net.sso.core.repo.UserReposity;
import com.new4net.sso.core.service.impl.AuthorityServiceImpl;
import com.new4net.sso.core.service.impl.ModuleServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = {PortalSsoCoreApplicationTests.class})
public class PortalSsoCoreApplicationTests {
//    @Autowired
//    private UserReposity userReposity;
	@Test
    void contextLoads() {

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
}
