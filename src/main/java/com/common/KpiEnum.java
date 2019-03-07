package com.common;

/**
 * 统计kpi的名称枚举类
 *
 *
 */
public enum KpiEnum {

	ADD_USER("add_user"), // 统计新用户的kpi
	BROWSER_ADD_USER("browser_add_user"), // 统计新用户kpi+浏览器
	ACTIVE_USER("active_user"), // 统计活跃用户kpi
	BROWSER_ACTIVE_USER("browser_active_user"), // 统计活跃用户kpi+浏览器
	ACTIVE_MEM("active_mem"), // 活跃会员kpi
	BROWSER_ACTIVE_MEM("browser_active_mem"), // 统计活跃会员kpi+浏览器
	LOCATION("location"), // 统计地域信息维度的kpi
	NEW_MEMBER("new_member"), // 统计新增会员kpi
	BROWSER_NEW_MEMBER("browser_new_member"), // 统计新增会员kpi+浏览器
	INSERT_MEMBER_INFO("insert_member_info"), // 插入会员信息kpi
	WEBSITE_PAGEVIEW("website_pageview"), // 统计浏览器维度的pv kpi
	 ;

	public final String name;

	private KpiEnum(String name) {
		this.name = name;
	}

	/**
	 * 根据kpiEnum的名称字符串值，获取对应的kpiEnum枚举对象
	 *
	 */
	public static KpiEnum valueOfName(String name) {
		for (KpiEnum type : values()) {
			if (type.name.equals(name)) {
				return type;
			}
		}
		throw new RuntimeException("不存在：" + name);
	}
}
