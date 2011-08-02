package net.alpha01.jwtest.pages.project;

import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.dao.ProjectMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.pages.LayoutPage;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
@AuthorizeInstantiation(value={Roles.ADMIN,"PROJECT_ADMIN"})
public class UpdateProjectPage extends LayoutPage {
	private Project prj;

	public UpdateProjectPage() {
		prj = getSession().getCurrentProject();
		Form<Project> updProjectForm = new Form<Project>("updProjectForm") {
			private static final long serialVersionUID = 1L;

			protected void onSubmit() {
				SqlSessionMapper<ProjectMapper> mapper = SqlConnection.getSessionMapper(ProjectMapper.class);
				try {
					if (mapper.getMapper().update(prj).equals(1)) {
						mapper.commit();
						info("Project Updated");
						setResponsePage(ProjectPage.class);
					} else {
						mapper.rollback();
						error("ERROR: UpdateProject SQL ERROR");
					}
				} catch (PersistenceException e) {
					mapper.rollback();
					error("SQL ERROR: Duplicate PKEY");
				}finally{
					mapper.close();
				}
			};
		};
		updProjectForm.add(new TextField<String>("nameFld", new PropertyModel<String>(prj, "name")));
		updProjectForm.add(new TextField<String>("mantisUrlFld", new PropertyModel<String>(prj, "mantis_url")));
		updProjectForm.add(new TextArea<String>("descriptionFld", new PropertyModel<String>(prj, "description")));
		add(updProjectForm);
	}
}
