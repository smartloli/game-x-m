/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartloli.game.x.m.book_9_2_2;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Java 动态代理实现类.
 * 
 * @author smartloli.
 *
 *         Created by Dec 10, 2017
 */
public class JProxy {
	public static void main(String[] args) {
		JInvocationHandler ji = new JInvocationHandler();	// 创建对象
		Subject sub = (Subject) ji.bind(new RealSubject());	// 调用绑定
		System.out.println(sub.say("邓杰", "男"));			// 打印动作返回结果
	}

}

interface Subject {
	/** 创建一个动作接口, 负责打印姓名和性别. */
	public String say(String name, String sex);
}

class RealSubject implements Subject {

	/** 实现动作接口中的函数, 并返回姓名和性别结果. */
	@Override
	public String say(String name, String sex) {
		return name + "," + sex;
	}

}

class JInvocationHandler implements InvocationHandler {

	/** 申明一个对象类. */
	private Object object = null;

	/** 动态代理绑定对象 */
	public Object bind(Object object) {
		this.object = object;
		return Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(), this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object tmp = method.invoke(this.object, args);
		return tmp;
	}

}
