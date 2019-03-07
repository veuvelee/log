package com.dataproducer.client;

import com.dataproducer.Util.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 分析引擎sdk java服务器端数据收集
 *
 *
 */
public class MkDataJDK {
	// 日志打印对象
	private static final Logger log = Logger.getGlobal();
	// 请求url的主体部分
	public static final String accessUrl = "http://linux001/log.gif";
	private static final String platformName = "javaServer";
	private static final String sdkName = "jdk";


	/**
	 * 触发订单支付成功事件，发送事件数据到nginx
	 *	orderId:订单支付id
	 *  memberId:订单支付会员id
	 *  如果发送数据成功(加入到发送队列中)，那么返回true
	 */
	public static boolean onChargeSuccess(String orderId, String memberId) {
		try {
			if (Utils.isEmpty(orderId) || Utils.isEmpty(memberId)) {
				// 订单id或者memberid为空
				log.log(Level.WARNING, "订单id和会员id不能为空");
				return false;
			}

			Map<String, String> data = new HashMap<String, String>();
			data.put("u_mid", memberId);
			data.put("oid", orderId);
			data.put("c_time", String.valueOf(System.currentTimeMillis()));
			data.put("en", "e_cs");
			data.put("pl", platformName);
			data.put("sdk", sdkName);
			// 创建url
			String url = Utils.buildUrl(data,accessUrl);
			// 发送url&将url加入到队列
			SendData.addSendUrl(url);
			return true;
		} catch (Throwable e) {
			log.log(Level.WARNING, "发送数据异常", e);
		}
		return false;
	}

	/**
	 * 触发订单退款事件，发送退款数据到nginx
	 * 
	 * orderId: 退款订单id
	 * memberId:退款会员id
	 *  如果发送数据成功，返回true。否则返回false。
	 */
	public static boolean onChargeRefund(String orderId, String memberId) {
		try {
			if (Utils.isEmpty(orderId) || Utils.isEmpty(memberId)) {
				// 订单id或者memberid为空
				log.log(Level.WARNING, "订单id和会员id不能为空");
				return false;
			}

			Map<String, String> data = new HashMap<String, String>();
			data.put("u_mid", memberId);
			data.put("oid", orderId);
			data.put("c_time", String.valueOf(System.currentTimeMillis()));
			data.put("en", "e_cr");
			data.put("pl", platformName);
			data.put("sdk", sdkName);
			// 构建url
			String url = Utils.buildUrl(data,accessUrl);
			// 将url添加到队列中
			SendData.addSendUrl(url);
			return true;
		} catch (Throwable e) {
			log.log(Level.WARNING, "发送数据异常");
		}
		return false;
	}


}
