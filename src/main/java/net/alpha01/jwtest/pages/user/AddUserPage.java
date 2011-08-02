package net.alpha01.jwtest.pages.user;

import java.util.ArrayList;
import java.util.List;

import net.alpha01.jwtest.beans.Role;
import net.alpha01.jwtest.beans.User;
import net.alpha01.jwtest.dao.RoleMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.UserMapper;
import net.alpha01.jwtest.dao.UserMapper.GroupUserAssociation;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.util.JWTestConfig;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.EmailAddressValidator;
import org.apache.wicket.validation.validator.StringValidator;

@AuthorizeInstantiation(value = Roles.ADMIN)
public class AddUserPage extends LayoutPage {
	private User user = new User();
	private ArrayList<Role> selectedGroups = new ArrayList<Role>();

	public AddUserPage() {
		Form<String> addForm = new Form<String>("addForm") {
			private static final long serialVersionUID = 1L;

			protected void onSubmit() {
				SqlSessionMapper<UserMapper> sesMapper = SqlConnection.getSessionMapper(UserMapper.class);
				try {
					if (sesMapper.getMapper().add(user).equals(1)) {
						info("User Added");
						boolean sqlOk = true;
						for (Role grp : selectedGroups){
							if (!sesMapper.getMapper().associateRole(new GroupUserAssociation(user.getId(), grp.getId())).equals(1)){
								sqlOk=false;
							}
						}
						if (sqlOk){
							sesMapper.commit();
						}else{
							sesMapper.rollback();
							error("SQL Error while associate groups");
						}
						setResponsePage(UsersPage.class);
					} else {
						sesMapper.rollback();
						error("SQL Error");
					}
				} catch (PersistenceException e) {
					Logger.getLogger(getClass()).error("User not added", e);
					error("User already present");
					sesMapper.rollback();
				}
				sesMapper.close();
			};
		};

		addForm.add(new TextField<String>("usernameFld", new PropertyModel<String>(user, "username")).setRequired(true).add(new StringValidator() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onValidate(IValidatable<String> validatable) {
				if (validatable.getValue().equals(JWTestConfig.getProp("security.admin.username"))){
					validatable.error(new ValidationError().setMessage("Cannot add User "+validatable.getValue()+" : is the administrator user"));
				}
			}
		}));
		
		addForm.add(new TextField<String>("nameFld", new PropertyModel<String>(user, "name")));
		final PasswordTextField passwordFld = new PasswordTextField("passwordFld", new PropertyModel<String>(user, "password"));
		passwordFld.setOutputMarkupId(true);
		addForm.add(passwordFld);
		addForm.add(new TextField<String>("emailFld", new PropertyModel<String>(user, "email")).add(EmailAddressValidator.getInstance()));
		CheckBox ldapFld =new CheckBox("ldapFld", new PropertyModel<Boolean>(user,"ldap"));
		ldapFld.setOutputMarkupId(true);
		ldapFld.add(new AjaxFormComponentUpdatingBehavior("onchange"){
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if (user.getLdap()){
					passwordFld.clearInput();
					passwordFld.setEnabled(false);
					passwordFld.setRequired(false);
					target.addComponent(passwordFld);
				}else{
					passwordFld.setEnabled(true);
					passwordFld.setRequired(true);
				}
				target.addComponent(passwordFld);
			}
		});
		if (!JWTestConfig.getPropAsBoolean("security.ldap.enabled")){
			ldapFld.add(new AttributeModifier("title",true, new Model<String>("LDAP is disabled")));
			ldapFld.setEnabled(false);
		}
		addForm.add(ldapFld);
		
		SqlSessionMapper<RoleMapper> sesMapper = SqlConnection.getSessionMapper(RoleMapper.class);
		List<Role> roles = sesMapper.getMapper().getAll();
		addForm.add(new ListMultipleChoice<Role>("groupsFld", new Model<ArrayList<Role>>(selectedGroups),roles));
		
		add(addForm);
	}
}
