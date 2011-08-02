package net.alpha01.jwtest.panels;

import net.alpha01.jwtest.JWTestSession;
import net.alpha01.jwtest.WebUser;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

public class LoginPanel extends Panel{
	private static final long serialVersionUID = 1L;
	private WebUser user = new WebUser();
	public LoginPanel(String id) {
		super(id);
		Form<String> loginForm = new Form<String>("loginForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				if (!JWTestSession.get().authenticate(user.getUsername(), user.getPassword())) {
					error("Authentication Failed");
				} else {
					setResponsePage(LoginPanel.this.getPage().getClass(),LoginPanel.this.getPage().getPageParameters());
				}
			}
		};
		loginForm.add(new TextField<String>("usernameFld", new PropertyModel<String>(user, "username")));
		loginForm.add(new PasswordTextField("passwordFld", new PropertyModel<String>(user, "password")));
		add(loginForm);
	}

	

}
