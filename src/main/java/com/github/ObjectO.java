package com.github;

/**
 * Based Object.
 * 
 * @author qiaozhy
 *
 */
public class ObjectO {
	
	public static final Object NULL = null;
	
	private Object object = NULL;
	
	public ObjectO(Object object) {
		this.object = object;
	}
	
	public boolean isNull() {
		if (NULL == object) return true;
		else return false;
	}
	
	public boolean isNotNull() {
		if (NULL == object) return false;
		else return true;
	}
	
	public static boolean isNull(Object object) {
		if (NULL == object) return true;
		else return false;
	}
	
	public static boolean isNotNull(Object object) {
		if (NULL == object) return false;
		else return true;
	}

	public Object get() {
		return object;
	}

	public void set(Object object) {
		this.object = object;
	}

}
