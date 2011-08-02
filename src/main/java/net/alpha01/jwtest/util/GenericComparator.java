package net.alpha01.jwtest.util;

import java.lang.reflect.Method;
import java.util.Comparator;

public class GenericComparator<T> implements Comparator<T>{
	
	private String property;
	private boolean ascending;

	public GenericComparator(String property, boolean ascending) {
		super();
		this.property = property;
		this.ascending = ascending;
	}

	private String getMethodName(){
		return "get"+property.substring(0, 1).toUpperCase()+property.substring(1);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int compare(T o1, T o2) {
		try {
			Method mtd = o1.getClass().getMethod(getMethodName());
			if (isAscending()){
				return ((Comparable)mtd.invoke(o1)).compareTo(mtd.invoke(o2));
			}else{
				return ((Comparable)mtd.invoke(o2)).compareTo(mtd.invoke(o1));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}
}
