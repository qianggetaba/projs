判断bean是否被aop proxy，获取直接对象，来自EurekaClientAutoConfiguration#eurekaClient()
if (AopUtils.isAopProxy(manager)) {
				appManager = ProxyUtils.getTargetObject(manager);
			}

当service被aop切面，但是被调用函数里调用当前类的其他方法没有被aop，把this.callMethodB()改为下面的调用方法
((Service) AopContext.currentProxy()).callMethodB();

