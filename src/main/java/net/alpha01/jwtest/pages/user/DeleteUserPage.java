package net.alpha01.jwtest.pages.user;

import java.math.BigInteger;

import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.beans.User;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.UserMapper;
import net.alpha01.jwtest.pages.HomePage;
import net.alpha01.jwtest.pages.LayoutPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;

@AuthorizeInstantiation(value = Roles.ADMIN)
public class DeleteUserPage extends LayoutPage {
	private User user;

	public DeleteUserPage(final PageParameters params) {
		super(params);
		if (!params.containsKey("idUser")) {
			error("Parametro idUser non trovato");
			setResponsePage(HomePage.class);
		}
		SqlSessionMapper<UserMapper> sesUserMapper = SqlConnection.getSessionMapper(UserMapper.class);
		user = sesUserMapper.getMapper().getById(BigInteger.valueOf(params.getInt("idUser")));
		sesUserMapper.close();
		add(new Label("userName", user.getUsername()));
		Form<Project> delUserForm = new Form<Project>("delUserForm");
		delUserForm.add(new Button("YesBtn") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				SqlSessionMapper<UserMapper> sesDelTestMapper = SqlConnection.getSessionMapper(UserMapper.class);
				if (sesDelTestMapper.getMapper().delete(user).equals(1)) {
					info("User deleted");
					sesDelTestMapper.commit();
					sesDelTestMapper.close();
					setResponsePage(UsersPage.class);
				} else {
					sesDelTestMapper.rollback();
					sesDelTestMapper.close();
					error("ERROR: User not deleted SQL ERROR");
				}
			}
		});

		delUserForm.add(new Button("NoBtn") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit() {
				setResponsePage(UsersPage.class);
			}
		});
		add(delUserForm);
	}
}
