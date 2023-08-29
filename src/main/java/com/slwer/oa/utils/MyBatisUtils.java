package com.slwer.oa.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Function;

public class MyBatisUtils {
    // static 属于类不属于对象，且全局唯一
    private static final SqlSessionFactory sqlSessionFactory;

    // 静态块在类初始化时实例化 sqlSessionFactory
    static {
        Reader reader;
        try {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
    }

    /**
     * 执行 select 查询 SQL
     *
     * @param func 要执行查询语句的代码块
     * @return 查询结果
     */
    public static Object executeQuery(Function<SqlSession, Object> func) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
            return func.apply(sqlSession);
        }
    }

    /**
     * 执行INSERT/UPDATE/DELETE写操作SQL
     *
     * @param func 要执行的写操作代码块
     * @return 写操作后返回的结果
     */
    public static Object executeUpdate(Function<SqlSession, Object> func) {
        SqlSession sqlSession = sqlSessionFactory.openSession(false);
        try {
            Object obj = func.apply(sqlSession);
            sqlSession.commit();
            return obj;
        } catch (Exception e) {
            sqlSession.rollback();
            throw e;
        } finally {
            sqlSession.close();
        }
    }

}
