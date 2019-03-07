package com.common;

/**
 * 定义
 * 		日志收集客户端收集得到的用户数据参数的name名称
 * 		hbase中logs表的结构信息
 * 		用户数据参数的name为logs的列名
 */
public class LogEnum {
	
	/**
	 * 事件枚举类。指定事件的名称
	 */
	public static enum EventEnum {
		LAUNCH(1, "launch event", "e_l"), // launch事件，表示第一次访问
		PAGEVIEW(2, "page view event", "e_pv"), // 页面浏览事件
		CHARGEREQUEST(3, "charge request event", "e_crt"), // 订单生产事件
		CHARGESUCCESS(4, "charge success event", "e_cs"), // 订单成功支付事件
		CHARGEREFUND(5, "charge refund event", "e_cr"), // 订单退款事件
		EVENT(6, "event duration event", "e_e") // 事件
		;

		public final int id; // id 唯一标识
		public final String name; // 名称
		public final String bieName; // 别名，数据收集简写

		private EventEnum(int id, String name, String bieName) {
			this.id = id;
			this.name = name;
			this.bieName = bieName;
		}

		/**
		 * 获取匹配别名的event枚举对象，没有匹配的值，返回null。
		 *
		 */
		public static EventEnum valueOfBieName(String bieName) {
			for (EventEnum event : values()) {
				if (event.bieName.equals(bieName)) {
					return event;
				}
			}
			return null;
		}
	}

	/**
	 * 表名称
	 */
	public static final String HBASE_NAME_LOGS = "mylogs";

	/**
	 * mylogs表的列簇名称
	 */
	public static final String EVENT_LOGS_FAMILY_NAME = "log";

	/**
	 * 日志分隔符
	 */
	public static final String LOG_SEPARTIOR = "\t";

	/**
	 * 用户ip地址
	 */
	public static final String LOG_IP = "ip";

	/**
	 * 服务器时间
	 */
	public static final String LOG_SERVER_TIME = "stime";

	/**
	 * 事件名称
	 */
	public static final String LOG_EVENT_NAME = "en";


	/**
	 * 用户标识
	 */
	public static final String LOG_UUID = "uid";

	/**
	 * 会员标识
	 */
	public static final String LOG_MEMBER_ID = "mid";

	/**
	 * 会话id
	 */
	public static final String LOG_SESSION_ID = "sid";
	/**
	 * 客户端时间
	 */
	public static final String LOG_CLIENT_TIME = "ctime";

	/**
	 * 浏览器信息
	 */
	public static final String LOG_USER_AGENT = "biev";


	/**
	 * 平台
	 */
	public static final String LOG_PLATFORM = "pl";
	/**
	 * 当前url
	 */
	public static final String LOG_URL = "url";
	/**
	 * 前url
	 */
	public static final String LOG_REFERRER_URL = "pref";
	/**
	 * title
	 */
	public static final String LOG_TITLE = "tt";
	/**
	 * 订单id
	 */
	public static final String LOG_ORDER_ID = "oid";
	/**
	 * 订单名称
	 */
	public static final String LOG_ORDER_NAME = "on";
	/**
	 * 订单金额
	 */
	public static final String LOG_ORDER_MONEY = "om";
	/**
	 * 货币类型
	 */
	public static final String LOG_ORDER_MONEY_TYPE = "omt";
	/**
	 * 支付方式
	 */
	public static final String LOG_ORDER_PAYMENT_TYPE = "opt";
	/**
	 * category名称
	 */
	public static final String LOG_EVENT_CATEGORY = "eca";
	/**
	 * action名称
	 */
	public static final String LOG_EVENT_ACTION = "eac";
	/**
	 * kv_前缀
	 */
	public static final String LOG_EVENT_KV = "kv_";
	/**
	 * 持续时间
	 */
	public static final String LOG_EVENT_DURATION = "edu";
	/**
	 * 操作系统名称
	 */
	public static final String LOG_OS_NAME = "osn";
	/**
	 * 操作系统版本
	 */
	public static final String LOG_OS_VERSION = "osv";
	/**
	 * 浏览器名称
	 */
	public static final String LOG_BROWSER_NAME = "bn";

	/**
	 * 国家:根据ip解析
	 */
	public static final String LOG_COUNTRY = "country";
	/**
	 * 省:根据ip解析
	 */
	public static final String LOG_PROVINCE = "province";
	/**
	 * 市:根据ip解析
	 */
	public static final String LOG_CITY = "city";
}
