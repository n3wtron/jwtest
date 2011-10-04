package net.alpha01.jwtest.util;

import net.alpha01.jwtest.JWTestSession;

import org.apache.wicket.Component;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

public class JWTestUtil {
	public static Model<Boolean> isAuthorized(String... roles ){
		Model<Boolean>  result=new Model<Boolean>(false);
		JWTestSession session=(JWTestSession) JWTestSession.get();
		if (session.getRoles()!=null){
			for (String role  : roles){
				result.setObject(result.getObject() || session.getRoles().contains(role));
				if (result.getObject()){
					return result;
				}
			}
		}
		return result;
	}

	public static String translate(String key,Component component) {
		StringResourceModel model = new StringResourceModel(key, component, null);
		return model.getObject();
	}
	
	public static void cleanDeadElements(){
		PlanUtil.cleanPlan();
		TestCaseUtil.cleanTestCase();
	}

}
