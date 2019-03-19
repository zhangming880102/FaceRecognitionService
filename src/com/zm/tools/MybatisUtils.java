package com.zm.tools;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;


public class MybatisUtils {
	 
    private static final SqlSessionFactory sqlSessionFactory;
 
    static{
        String resource="mybatis.xml";
        Reader reader=null;
        try {
            reader=Resources.getResourceAsReader(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sqlSessionFactory=new SqlSessionFactoryBuilder().build(reader);
    }
 
    public static SqlSessionFactory getSqlSessionFactory(){
        return sqlSessionFactory;
    }
 
}
