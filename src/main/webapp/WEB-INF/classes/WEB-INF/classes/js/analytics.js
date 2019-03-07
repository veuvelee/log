(function() {
	//cookie工具类，包括获取，设置等方法
	var CookieUtil = {
		//根据名找到值
		get : function(name) {
			//名=
			var cookieName = encodeURIComponent(name) + "=";
			//cookie在系统中以这样形式保存：cookie名1 = cookie值; cookie名2= cookie值
			var cookieStart = document.cookie.indexOf(cookieName);
			var cookieValue = null;
			if (cookieStart > -1) {
				var cookieEnd = document.cookie.indexOf(";", cookieStart);
				//没有“；”说明此name为最后一个cookie
				if (cookieEnd == -1) {
					cookieEnd = document.cookie.length;
				}
				//截取值
				cookieValue = decodeURIComponent(document.cookie.substring(
						cookieStart + cookieName.length, cookieEnd));
			}
			return cookieValue;
		},
		// 设置浏览器cookie
		set : function(name, value, expires, path, domain, secure) {
			//设置name=value键值对
			var cookieText = encodeURIComponent(name) + "="
					+ encodeURIComponent(value);

			if (expires) {
				// 设置过期时间
				var expiresTime = new Date();
				expiresTime.setTime(expires);
				cookieText += ";expires=" + expiresTime.toGMTString();
			}

			if (path) {
				cookieText += ";path=" + path;
			}

			if (domain) {
				cookieText += ";domain=" + domain;
			}

			if (secure) {
				cookieText += ";secure";
			}

			//设置cookie
			document.cookie = cookieText;
		},
		//只根据name，value设置cookie
		setExt : function(name, value) {
			this.set(name, value, new Date().getTime() + 315360000000, "/");
		}
	};

	// 主体
	var tracker = {
		// 各种配置项
		clientConfig : {
			//nginx服务器要匹配log.gif以此来记录日志
			serverUrl : "http://linxu001/log.gif",
			//session过期时间
			sessionTimeout : 360, // 360s -> 6min
			//最大等待时间
			maxWaitTime : 3600, // 3600s -> 60min -> 1h
		},

		cookieExpiresTime : 315360000000, // cookie过期时间，10年,不设置永不过期，除非自己删了

		//所需数据的各个字段
		columns : {
			//事件名
			eventName : "en",
			//访问平台
			platform : "pl",
			//数据源：java或js
			sdk : "sdk",
			//用户id
			uuid : "u_ud",
			//会员id（登录了才是会员，不登录就是用户）
			memberId : "u_mid",
			//会话id
			sessionId : "u_sd",
			//数据日期
			clientTime : "c_time",
			//浏览器信息
			userAgent : "b_iev",
			//当前网址
			currentUrl : "p_url",
			//上一个网址
			referrerUrl : "p_ref",
			//当前标签
			title : "tt",
			//订单编号
			orderId : "oid",
			//订单名
			orderName : "on",
			//支付金额
			currencyAmount : "cua",
			//货币类型
			currencyType : "cut",
			//支付方式
			paymentType : "pt",
			//Event事件的类别名称
			category : "ca",
			//Event事件的动作名称
			action : "ac",
			//Event事件的自定义属性,也就是在其他字段前拼接kv_来表示event时间
			kv : "kv_",
			//event持续时间
			duration : "du"
		},

		keys : {
			//各个事件的标识
			pageView : "e_pv",
			chargeRequestEvent : "e_crt",
			launch : "e_l",
			eventDurationEvent : "e_e",
			sid : "bftrack_sid",
			uuid : "bftrack_uuid",
			mid : "bftrack_mid",
			preVisitTime : "bftrack_previsit"
		},

		/**
		 * 获取会话id
		 */
		getSid : function() {
			return CookieUtil.get(this.keys.sid);
		},

		/**
		 * 保存会话id到cookie
		 */
		setSid : function(sid) {
			if (sid) {
				CookieUtil.setExt(this.keys.sid, sid);
			}
		},

		/**
		 * 获取uuid，从cookie中
		 */
		getUuid : function() {
			return CookieUtil.get(this.keys.uuid);
		},

		/**
		 * 保存uuid到cookie
		 */
		setUuid : function(uuid) {
			if (uuid) {
				CookieUtil.setExt(this.keys.uuid, uuid);
			}
		},

		/**
		 * 获取mid
		 */
		getMemberId : function() {
			return CookieUtil.get(this.keys.mid);
		},

		/**
		 * 设置mid
		 */
		setMemberId : function(mid) {
			if (mid) {
				CookieUtil.setExt(this.keys.mid, mid);
			}
		},

		//开始
		startSession : function() {
			// 加载js就触发的方法
			if (this.getSid()) {
				// 会话id存在，表示uuid也存在
				if (this.isSessionTimeout()) {
					// 会话过期,产生新的会话
					this.createNewSession();
				} else {
					// 会话没有过期，更新最近访问时间
					this.updatePreVisitTime(new Date().getTime());
				}
			} else {
				// 会话id不存在，表示uuid也不存在
				this.createNewSession();
			}
			//触发pv事件
			this.onPageView();
		},

		onLaunch : function() {
			// 触发launch事件
			var launch = {};
			launch[this.columns.eventName] = this.keys.launch; // 设置事件名称
			this.setCommonColumns(launch); // 设置公用columns
			this.sendDataToServer(this.parseParam(launch)); // 最终发送编码后的数据
		},

		onPageView : function() {
			// 触发page view事件
			if (this.preCallApi()) {
				var time = new Date().getTime();
				var pageviewEvent = {};
				pageviewEvent[this.columns.eventName] = this.keys.pageView;
				pageviewEvent[this.columns.currentUrl] = window.location.href; // 设置当前url
				pageviewEvent[this.columns.referrerUrl] = document.referrer; // 设置前一个页面的url
				pageviewEvent[this.columns.title] = document.title; // 设置title
				this.setCommonColumns(pageviewEvent); // 设置公用columns
				this.sendDataToServer(this.parseParam(pageviewEvent)); // 最终发送编码后的数据
				this.updatePreVisitTime(time);
			}
		},

		onChargeRequest : function(orderId, name, currencyAmount, currencyType, paymentType) {
			// 触发订单产生事件
			if (this.preCallApi()) {
				if (!orderId || !currencyType || !paymentType) {
					this.log("订单id、货币类型以及支付方式不能为空");
					return;
				}
				// 金额必须是数字
				if (typeof (currencyAmount) == "number") {

					var time = new Date().getTime();
					var chargeRequestEvent = {};
					//设置各种kv键值对
					chargeRequestEvent[this.columns.eventName] = this.keys.chargeRequestEvent;
					chargeRequestEvent[this.columns.orderId] = orderId;
					chargeRequestEvent[this.columns.orderName] = name;
					chargeRequestEvent[this.columns.currencyAmount] = currencyAmount;
					chargeRequestEvent[this.columns.currencyType] = currencyType;
					chargeRequestEvent[this.columns.paymentType] = paymentType;
					this.setCommonColumns(chargeRequestEvent); // 设置公用columns
					this.sendDataToServer(this.parseParam(chargeRequestEvent)); // 最终发送编码后的数据
					this.updatePreVisitTime(time);
				} else {
					this.log("订单金额必须是数字");
					return;
				}
			}
		},

		onEventDuration : function(category, action, map, duration) {
			// 触发event事件
			if (this.preCallApi()) {
				if (category && action) {
					var time = new Date().getTime();
					var event = {};
					event[this.columns.eventName] = this.keys.eventDurationEvent;
					event[this.columns.category] = category;
					event[this.columns.action] = action;
					if (map) {
						for ( var k in map) {
							if (k && map[k]) {
								//把字段名前边拼上kv_
								event[this.columns.kv + k] = map[k];
							}
						}
					}
					if (duration) {
						event[this.columns.duration] = duration;
					}
					this.setCommonColumns(event); // 设置公用columns
					this.sendDataToServer(this.parseParam(event)); // 最终发送编码后的数据
					this.updatePreVisitTime(time);
				} else {
					this.log("category和action不能为空");
				}
			}
		},

		/**
		 * 执行对外方法前必须执行的方法
		 * 		根据是否过期执行不同方法
		 */
		preCallApi : function() {
			if (this.isSessionTimeout()) {
				// 如果为true，表示需要新建
				this.startSession();
			} else {
				this.updatePreVisitTime(new Date().getTime());
			}
			return true;
		},

		//发送数据到服务器
		sendDataToServer : function(data) {
			//测试环境，先在浏览器看看数据啥样
			alert(data);
			
			//用图片方式，不影响页面正常功能，不需要返回结果
			var img = new Image(1, 1);// <img src="url"></img>
			img.onerror = function() {
				// 这里可以进行重试操作
				//算了不重试了
			};
			//https://linux001/log.gif?k=v&k=v&k=v......
			img.src = this.clientConfig.serverUrl + "?" + data;
		},

		/**
		 * 往data中添加发送到nginx服务器的公用部分
		 */
		setCommonColumns : function(data) {
			data[this.columns.platform] = "website";
			data[this.columns.sdk] = "js";
			data[this.columns.uuid] = this.getUuid(); // 设置用户id
			data[this.columns.memberId] = this.getMemberId(); // 设置会员id
			data[this.columns.sessionId] = this.getSid(); // 设置sid
			data[this.columns.clientTime] = new Date().getTime(); // 设置客户端时间
			data[this.columns.userAgent] = window.navigator.userAgent; // 设置浏览器类型
		},

		/**
		 * 创建新的会员，并判断是否是第一次访问页面，如果是，进行launch事件的发送。
		 */
		createNewSession : function() {
			var time = new Date().getTime(); // 获取当前操作时间
			// 1. 进行会话更新操作
			var sid = this.generateId(); // 产生一个session id
			this.setSid(sid);
			this.updatePreVisitTime(time); // 更新最近访问时间
			// 2. 进行uuid查看操作
			if (!this.getUuid()) {
				// uuid不存在，先创建uuid，然后保存到cookie，最后触发launch事件
				var uuid = this.generateId(); //uuid
				this.setUuid(uuid);
				//触发
				this.onLaunch();
			}
		},

		/**
		 * 参数编码返回字符串
		 * 		格式化数据
		 */
		parseParam : function(data) {
			var params = "";
			for ( var e in data) {
				if (e && data[e]) {
					params += encodeURIComponent(e) + "="
							+ encodeURIComponent(data[e]) + "&";
				}
			}
			if (params) {
				//把最后一个&去掉
				return params.substring(0, params.length - 1);
			} else {
				return params;
			}
		},

		/**
		 * 产生uuid
		 */
		generateId : function() {
			var chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz';
			var tmpid = [];
			var r;


			for (i = 0; i < 36; i++) {
				//第i各不存在
				if (!tmpid[i]) {
					r = Math.floor(Math.random() * chars.length);
					tmpid[i] = chars[r];
				}
			}
			//相当于转换为字符串形式
			return tmpid.join('');
		},

		/**
		 * 判断这个会话是否过期，
		 * 如果是过期，返回false;否则返回true。
		 */
		isSessionTimeout : function() {
			var time = new Date().getTime();
			var preTime = CookieUtil.get(this.keys.preVisitTime);
			if (preTime) {
				// 最近访问时间存在,那么进行区间判断，做差
				return time - preTime > this.clientConfig.sessionTimeout * 1000;
			}
			return true;
		},

		/**
		 * 更新最近访问时间
		 */
		updatePreVisitTime : function(time) {
			CookieUtil.setExt(this.keys.preVisitTime, time);
		},

		/**
		 * 打印日志
		 */
		log : function(msg) {
			console.log(msg);
		},

	};

	// 对外暴露的方法名称，可以在jsp页面中直接使用该放发
	window.__AE__ = {
		startSession : function() {
			tracker.startSession();
		},
		onPageView : function() {
			tracker.onPageView();
		},
		onChargeRequest : function(orderId, name, currencyAmount, currencyType, paymentType) {
			tracker.onChargeRequest(orderId, name, currencyAmount, currencyType, paymentType);
		},
		onEventDuration : function(category, action, map, duration) {
			tracker.onEventDuration(category, action, map, duration);
		},
		setMemberId : function(mid) {
			tracker.setMemberId(mid);
		}
	};

	// 自动加载方法
	var autoLoad = function() {
		// 进行参数设置
		var _aelog_ = _aelog_ || window._aelog_ || [];
		var memberId = null;
		for (i = 0; i < _aelog_.length; i++) {
			_aelog_[i][0] === "memberId" && (memberId = _aelog_[i][1]);
		}
		// 根据是给定memberid，设置memberid的值
		memberId && __AE__.setMemberId(memberId);
		// 启动session
		__AE__.startSession();
	};

	//执行
	autoLoad();
})();