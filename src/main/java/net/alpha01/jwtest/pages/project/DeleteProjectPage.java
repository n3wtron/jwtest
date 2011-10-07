package net.alpha01.jwtest.pages.project;

import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.dao.ProjectMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.pages.HomePage;
import net.alpha01.jwtest.pages.LayoutPage;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;

@AuthorizeInstantiation(value={Roles.ADMIN,"PROJECT_ADMIN"})
public class DeleteProjectPage extends LayoutPage {
	private Project prj;
	public DeleteProjectPage(){
		prj=getSession().getCurrentProject();
		add (new Label("projectName",prj.getName()));
		Form<Project> delProjectForm = new Form<Project>("delProjectForm");
		delProjectForm.add(new Button("YesBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				SqlSessionMapper<ProjectMapper> mapper = SqlConnection.getSessionMapper(ProjectMapper.class);
				if (mapper.getMapper().delete(prj).equals(1)){
					info("Project deleted");
					mapper.commit();
					mapper.close();
					DeleteProjectPage.this.getSession().setCurrentProject(null);
					setResponsePage(HomePage.class);
				}else{
					mapper.rollback();
					mapper.close();
					error("ERROR: Project not deleted SQL ERROR");
				}
			}
		});
		
		delProjectForm.add(new Button("NoBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				setResponsePage(ProjectPage.class);
			}
		});
		add(delProjectForm);
	}
}
