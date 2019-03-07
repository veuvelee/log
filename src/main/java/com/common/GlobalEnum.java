package com.common;

/**
 * 全局常量类
 *
 *
 */
public class GlobalEnum {
	
	/**
	 * 一天的毫秒数
	 */
	public static final int DAY_OF_MILLISECONDS = 86400000;
	
	/**
	 * 定义的运行时间变量名
	 */
	public static final String RUN_DATE_NAME = "RUN_DATE";
	
	/**
	 * 某些地方默认值
	 */
	public static final String DEFAULT_VALUE = "unknown";
	
	/**
	 * 指定全部列值
	 */
	public static final String VALUE_OF_ALL = "all";
	


	/**
	 * 指定连接表配置为lzy
	 */
	public static final String WAREHOUSE_OF_REPORT = "lzy";

	/**
	 * 批量执行的key
	 */
	public static final String JDBC_BATCH_NUMBER = "mysql.batch.number";


	/**
	 * driver 名称
	 */
	public static final String JDBC_DRIVER = "mysql.%s.driver";

	/**
	 * JDBC URL
	 */
	public static final String JDBC_URL = "mysql.%s.url";

	/**
	 * username名称
	 */
	public static final String JDBC_USERNAME = "mysql.%s.username";

	/**
	 * password名称
	 */
	public static final String JDBC_PASSWORD = "mysql.%s.password";

}
