package com.new4net.sso.core.service.impl;

import com.new4net.jwt.client.configuration.JwtClientProperties;
import com.new4net.sso.core.dao.BaseDao;
import com.new4net.sso.core.entity.Authority;
import com.new4net.sso.core.entity.AuthorityRelation;
import com.new4net.sso.core.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service

public  class AuthorityServiceImpl extends BaseServiceImpl<Authority> implements AuthorityService {
    @Resource(name="authorityDao")
    public void setBaseDao(BaseDao<Authority> baseDao) {
        super.setBaseDao(baseDao);
    }
    @Autowired
    private JwtClientProperties jwtClientProperties;
    public AuthorityServiceImpl(){
    }

    @Override
    @Transactional
    public List<Authority> getSubAuthorities(String moduleName,Authority ...superAuthoritys) {
        if(moduleName==null){
            moduleName = jwtClientProperties.getModuleName();
        }
        Set<String> set = new HashSet<>();
        for(Authority superAuthority:superAuthoritys){
            String submoduleName = superAuthority.getModule().getModuleName();

            if(moduleName.startsWith(submoduleName)){

                String m = submoduleName;

                if(moduleName.equals(submoduleName)){
                    set.add(m);
                }else{
                    m = moduleName.substring(submoduleName.length()+1);
                    String[] strs = m.split(".");

                    for(String s:strs){
                        set.add(m);
                        m +="."+s;
                    }
                }
            }

        }
        List<Authority> authorities = getAuthorities(superAuthoritys,select(set.toArray(new String[0])),moduleName);
        if(authorities==null){
            return new ArrayList<Authority>(Arrays.asList(superAuthoritys));
        }
        authorities.addAll(Arrays.asList(superAuthoritys));
        return authorities;
    }

    private List<Authority> select( String[] m) {
        if(m!=null&&m.length>0){
            List<Authority> authorities1 =findByHQL("From Authority a Where a.module.moduleName in (?0)",m);

            return authorities1;
        }

        return null;
    }

    private List<Authority> getAuthorities(Authority[] superAuthoritys, List<Authority> list,String moduleName) {

        List<Authority> list1 =  get(superAuthoritys,list);
        if(list1!=null){
            return list1.stream().filter(authority -> {
                return moduleName.equals(authority.getModule().getModuleName());
            }).collect(Collectors.toList());
        }
        return null;

    }

    private List<Authority> get(Authority[] superAuthoritys, List<Authority> list) {


        Set<AuthorityRelation> set = new HashSet<>();


        for(Authority superAuthority:superAuthoritys){
            Set<AuthorityRelation> authorityRelations =getAuthorityRelations(superAuthority,list);
            if(authorityRelations!=null){
                set.addAll(authorityRelations);

            }
        }


        List<Authority> authorities  = list.stream().filter(authority -> {
            for(AuthorityRelation authorityRelation:set){
                if(authorityRelation.getSubAuthCode()!=null
                        &&authorityRelation.getSubAuthCode().equals(authority.getAuthorityCode())){
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());

        return authorities;
    }



    private Set<AuthorityRelation> getAuthorityRelations(Authority superAuthority, List<Authority> authorities1) {
        Set<AuthorityRelation> set = new HashSet<>();
        Set<AuthorityRelation> set1 = superAuthority.getAuthorityRelations();
        if(set1!=null&&!set1.isEmpty()){

            for(Authority authority:authorities1){
                AuthorityRelation relation =AuthorityRelation.builder()
                        .superAuthCode(superAuthority.getAuthorityCode()).subAuthCode(authority.getAuthorityCode()).build();
                if(set1.contains(relation)){
                    set.add(relation);

                    Set<AuthorityRelation> set2 = getAuthorityRelations(authority,authorities1);
                    if(set2!=null){
                        set.addAll(set2);
                    }

                }
            }
        }

        return set;

    }
}
