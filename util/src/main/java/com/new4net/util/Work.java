package com.new4net.util;

import org.hibernate.Session;

import java.sql.Connection;

public interface Work{
    Object run(Session session);

    default int getIsolationLevel(){
        return Connection.TRANSACTION_READ_COMMITTED;
    }
}