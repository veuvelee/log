package com.analysis.output;

import com.pojo.Base;

import java.io.IOException;



/**
 * 操作（mysql查询、插入）表的接口
 *
 */
public interface IDConverter {
    /**
     * 根据value获取id
     * 如果数据库中有，那么直接返回。如果没有，那么进行插入后返回新的id值
     *
     */
    public int getId(Base base) throws IOException;
}
