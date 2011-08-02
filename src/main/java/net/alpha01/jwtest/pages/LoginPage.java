package net.alpha01.jwtest.pages;

import net.alpha01.jwtest.WebUser;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

public class LoginPage extends LayoutPage {
	private WebUser user = new WebUser();

	public LoginPage() {
		Form<String> loginForm = new Form<String>("loginForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				if (!LoginPage.this.getSession().authenticate(user.getUsername(), user.getPassword())) {
					error("Authentication Failed");
				} else {
					if (!continueToOriginalDestination()) {
						setResponsePage(HomePage.class);
					}
				}
			}
		};
		loginForm.add(new TextField<String>("usernameFld", new PropertyModel<String>(user, "username")));
		loginForm.add(new PasswordTextField("passwordFld", new PropertyModel<String>(user, "password")));
		add(loginForm);
	}
}
