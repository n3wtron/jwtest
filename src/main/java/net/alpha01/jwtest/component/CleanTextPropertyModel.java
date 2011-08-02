package net.alpha01.jwtest.component;

import java.lang.reflect.InvocationTargetException;

import org.apache.wicket.model.PropertyModel;

public class CleanTextPropertyModel extends PropertyModel<String>{
	private static final long serialVersionUID = -5620456500329456036L;
	
	public CleanTextPropertyModel(Object modelObject, String expression) {
		super(modelObject, expression);
	
	}

	@Override
	public String getObject() {
		String res;
		try {
			res = (String) getPropertyGetter().invoke(getChainedModel().getObject());
		} catch (IllegalArgumentException e) {
			return "";
		} catch (IllegalAccessException e) {
			return "";
		} catch (InvocationTargetException e) {
			return "";
		}
		if (res==null){
			return "";
		}else{
			return res.replaceAll("\\<[^>]*>","");
		}
	}
}
