package com.dataproducer.client;

import com.dataproducer.Util.HttpUtil;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 发送url数据的监控者，用于启动一个单独的线程来发送数据

 *
 */
public class SendData {
	// 日志记录对象
	private static final Logger log = Logger.getGlobal();
	// 队列，用户存储发送url
	private BlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	// 用于单列的一个类对象
	private static SendData my = null;

	private SendData() {
		// 私有构造方法，进行单列模式的创建
	}

	/**
	 * 获取单列的monitor对象实例
	 *
	 */
	public static SendData getSendData() {
		if (my == null) {
			synchronized (SendData.class) {
				if (my == null) {
					my = new SendData();

					Thread thread = new Thread(new Runnable() {

						@Override
						public void run() {

							SendData.my.run();
						}
					});
					thread.start();
				}
			}
		}
		return my;
	}

	/**
	 * 添加一个url到队列中去
	 *
	 */
	public static void addSendUrl(String url) throws InterruptedException {
		getSendData().queue.put(url);
	}

	/**
	 * 具体执行发送url的方法
	 * 
	 */
	private void run() {
		while (true) {
			try {
				String url = this.queue.take();
				// 正式的发送url
				HttpUtil.sendData(url);
			} catch (Throwable e) {
				log.log(Level.WARNING, "发送url异常", e);
			}
		}
	}


}
