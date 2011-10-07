package net.alpha01.jwtest.pages.project;

import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.dao.ProjectMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.pages.LayoutPage;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
@AuthorizeInstantiation(value={"ADMIN"})
public class AddProjectPage extends LayoutPage {
	private Project prj;

	public AddProjectPage() {
		prj = new Project();
		Form<Project> addProjectForm = new Form<Project>("addProjectForm") {
			private static final long serialVersionUID = 1L;

			protected void onSubmit() {
				SqlSessionMapper<ProjectMapper> sesMapper = SqlConnection.getSessionMapper(ProjectMapper.class);
				try {
					if (sesMapper.getMapper().add(prj).equals(1)) {
						info("Project Added");
						sesMapper.commit();
						sesMapper.close();
						Logger.getLogger(getClass()).debug("Project Added with new prjId:" + prj.getId());
						AddProjectPage.this.getSession().setCurrentProject(prj);
						setResponsePage(ProjectPage.class);
					} else {
						error("ERROR: AddProject SQL ERROR");
					}
				} catch (PersistenceException e) {
					e.printStackTrace();
					error("SQL ERROR: Duplicate PKEY");
				}
			};
		};
		addProjectForm.add(new TextField<String>("nameFld", new PropertyModel<String>(prj, "name")));
		addProjectForm.add(new TextField<String>("mantisUrlFld", new PropertyModel<String>(prj, "mantis_url")));
		addProjectForm.add(new TextArea<String>("descriptionFld", new PropertyModel<String>(prj, "description")));
		add(addProjectForm);
	}
}
