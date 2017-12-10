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

/**
 * Java 反射实现类.
 * 
 * @author smartloli.
 *
 *         Created by Dec 10, 2017
 */
public class JReflect {
	public static void main(String[] args) {
		Fruit f = Factory.getInstance(Orange.class.getName()); // 获取一个反射对象
		if (f != null) {
			f.eat(); // 调用反射类中的函数动作
		}
	}
}

interface Fruit {
	/** 抽象一个动作函数. */
	public abstract void eat();
}

/** 申明一个实物类实现接口中的抽象动作. */
class Apple implements Fruit {

	/** 打印具体动作. */
	@Override
	public void eat() {
		System.out.println("apple");
	}

}

/** 申明一个实物类实现接口中的抽象动作. */
class Orange implements Fruit {

	/** 打印具体动作. */
	@Override
	public void eat() {
		System.out.println("orange");
	}

}

/** 封装接口. */
class Factory {
	public static Fruit getInstance(String className) {
		Fruit f = null;
		try {
			f = (Fruit) Class.forName(className).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}
}
