package com.new4net.util;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class MysqlUpperCaseStrategy extends PhysicalNamingStrategyStandardImpl {
    public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
        if(name==null){
            return name;
        }
        String tbName = name.getText()==null?name.getText():name.getText().toUpperCase();
        return Identifier.toIdentifier(tbName);
    }

    public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment context) {
        if(name==null){
            return name;
        }
        String tbName = name.getText()==null?name.getText():name.getText().toUpperCase();
        return Identifier.toIdentifier(tbName);
    }

    public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment context) {
        if(name==null){
            return name;
        }
        String tbName = name.getText()==null?name.getText():name.getText().toUpperCase();
        return Identifier.toIdentifier(tbName);
    }


    public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) {
        if(name==null){
            return name;
        }
        String tbName = name.getText()==null?name.getText():name.getText().toUpperCase();
        return Identifier.toIdentifier(tbName);
    }

    public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
        if(name==null){
            return name;
        }
        String tbName = name.getText()==null?name.getText():name.getText().toUpperCase();
        return Identifier.toIdentifier(tbName);
    }
}
