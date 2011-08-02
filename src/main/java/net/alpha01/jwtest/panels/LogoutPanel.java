package net.alpha01.jwtest.panels;

import net.alpha01.jwtest.JWTestSession;
import net.alpha01.jwtest.pages.HomePage;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

public class LogoutPanel extends Panel{
	private static final long serialVersionUID = 1L;
	public LogoutPanel(String id) {
		super(id);
		final JWTestSession session = (JWTestSession) JWTestSession.get();
		Form<String> logoutForm = new Form<String>("logoutForm"){
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit() {
				session.logout();
				setResponsePage(HomePage.class);
			}
		};
		logoutForm.add(new Label("userName",session.getUser().toString()));
		add(logoutForm);
	}

	

}
