package net.alpha01.jwtest.pages.profiles;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import net.alpha01.jwtest.beans.Profile;
import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.dao.ProfileMapper;
import net.alpha01.jwtest.dao.ProjectMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.pages.HomePage;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.util.JWTestUtil;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class UpdateProfilePage extends LayoutPage {

	private Profile profile;
	private Model<Project> selectedProject = new Model<Project>();

	public UpdateProfilePage(PageParameters params) {
		super();
		if (!params.containsKey("idProfile")){
			error("Parameter idProfile not found");
			setResponsePage(HomePage.class);
			return;
		}
		SqlSessionMapper<ProjectMapper> sesMapper = SqlConnection.getSessionMapper(ProjectMapper.class);
		ProfileMapper profileMapper =sesMapper.getSqlSession().getMapper(ProfileMapper.class);
		profile = profileMapper.get(BigInteger.valueOf(params.getAsInteger("idProfile")));
		add(new Label("profileName", profile.getName()));
		
		if (profile.getId_project()!=null){
			selectedProject.setObject(sesMapper.getMapper().get(profile.getId_project().intValue()));
		}
		
		Form<Void> updForm = new Form<Void>("addForm") {
			private static final long serialVersionUID = 1L;

			protected void onSubmit() {
				SqlSessionMapper<ProfileMapper> sesMapper = SqlConnection.getSessionMapper(ProfileMapper.class);
				profile.setId_project(selectedProject.getObject() != null ? selectedProject.getObject().getId() : null);
				try {
					if (sesMapper.getMapper().update(profile).equals(1)) {
						info("Profile Added");
						sesMapper.commit();
						setResponsePage(ProfilesPage.class);
					} else {
						error("SQLError");
						sesMapper.rollback();
					}
				} catch (PersistenceException e) {
					error("SQL Error:" + e.getMessage());
					Logger.getLogger(getClass()).error("SQL Error", e);
				} finally {
					sesMapper.close();
				}
			};
		};
		updForm.add(new TextField<String>("nameFld", new PropertyModel<String>(profile, "name")).setRequired(true));
		updForm.add(new TextArea<String>("descriptionFld", new PropertyModel<String>(profile, "description")));
		
		List<Project> allProjects = new ArrayList<Project>();
		
		DropDownChoice<Project> projectFld;
		updForm.add(projectFld=new DropDownChoice<Project>("projectFld", selectedProject, allProjects, new IChoiceRenderer<Project>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Object getDisplayValue(Project object) {
				return object == null ? "Tutti i progetti" : object.getName();
			}

			@Override
			public String getIdValue(Project object, int index) {
				return object == null ? "0" : object.getId().toString();
			}
		}));
		if (!JWTestUtil.isAuthorized(Roles.ADMIN).getObject()){
			projectFld.setRequired(true);
		}else{
			allProjects.add(null);
		}
		allProjects.addAll(sesMapper.getMapper().getAll());
		add(updForm);
		sesMapper.close();
	}

}
