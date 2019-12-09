判断bean是否是proxy，来自EurekaClientAutoConfiguration#eurekaClient()
if (AopUtils.isAopProxy(manager)) {
				appManager = ProxyUtils.getTargetObject(manager);
			}