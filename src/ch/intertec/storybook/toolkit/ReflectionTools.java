/*
Storybook: Scene-based software for novelists and authors.
Copyright (C) 2008 - 2011 Martin Mustun

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package ch.intertec.storybook.toolkit;

import java.lang.reflect.Method;

public class ReflectionTools {

	public static Object instance(String className) {
		return instanceAndInvoke(className, null);
	}

	@SuppressWarnings("unchecked")
	public static Object instanceAndInvoke(String className, String method, Object ... params) {
		Class<Object> c;
		try {
			c = (Class<Object>) Class.forName(
					"ch.intertec.storybook." + className);
			if (c != null) {
				Object o = c.newInstance();
				if (method != null) {
					// Class[] ca = new Class[params.length];
					// int counter = 0;
					// for(Object ob: params){
					// ca[counter] = ob.getClass();
					// ++counter;
					// }
					// Method m = o.getClass().getMethod(method, ca);
					// m.invoke(o, params);
					invoke(o, method, params);
				}
				return o;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static Object invoke(Object o, String method, Object ... params) {
		try {
			if(o == null || method == null || method.isEmpty()){
				return null;
			}
			Class[] ca = new Class[params.length];
			int counter = 0;
			for(Object ob: params){
				ca[counter] = ob.getClass();
				++counter;
			}
			Method m = o.getClass().getMethod(method, ca);
			return m.invoke(o, params);
			// Method m = o.getClass().getMethod(method);
			// return m.invoke(o);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Object cast(Object o, String castClassName){
		Class<Object> c;
		try {
			c = (Class<Object>) Class.forName(
					"ch.intertec.storybook.prof." + castClassName);			
			if (c != null) {
				return c.cast(o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
