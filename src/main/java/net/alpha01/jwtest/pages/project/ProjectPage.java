package net.alpha01.jwtest.pages.project;

import net.alpha01.jwtest.component.BookmarkablePageLinkSecure;
import net.alpha01.jwtest.component.HtmlLabel;
import net.alpha01.jwtest.pages.ExportPage;
import net.alpha01.jwtest.pages.HomePage;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.pages.dot.RequirementDotPage;
import net.alpha01.jwtest.pages.plan.AddPlanPage;
import net.alpha01.jwtest.pages.requirement.AddRequirementPage;
import net.alpha01.jwtest.pages.session.StartSessionPage;
import net.alpha01.jwtest.panels.plan.PlansTablePanel;
import net.alpha01.jwtest.panels.requirement.RequirementsTablePanel;

import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.PropertyModel;

public class ProjectPage extends LayoutPage{
	
	public ProjectPage(){
		if (getSession().getCurrentProject()==null){
			error("Nessun progetto selezionato");
			setResponsePage(HomePage.class);
		}else{
			
			add (new Label("projectName",getSession().getCurrentProject().getName()));
			add (new HtmlLabel("projectDescription",getSession().getCurrentProject().getDescription()));
			ExternalLink mantisLnk;
			if (getSession().getCurrentProject().getMantis_url()!=null && !getSession().getCurrentProject().getMantis_url().isEmpty()){
				mantisLnk = new ExternalLink("mantisLnk", new PropertyModel<String>(getSession().getCurrentProject(),"mantis_url"));
			}else{
				mantisLnk=new ExternalLink("mantisLnk", "");
				mantisLnk.setVisible(false);
			}
			add(mantisLnk);
			
			//LINK
			add(new BookmarkablePageLinkSecure<String>("addRequirementLnk", AddRequirementPage.class, Roles.ADMIN,"PROJECT_ADMIN","MANAGER").add(new ContextImage("addRequirementImg", "images/add_requirement.png")));
			add(new BookmarkablePageLinkSecure<String>("addPlanLnk", AddPlanPage.class, Roles.ADMIN,"PROJECT_ADMIN","MANAGER").add(new ContextImage("addPlanImg", "images/add_plan.png")));
			add(new BookmarkablePageLinkSecure<String>("startSessionLnk", StartSessionPage.class, Roles.ADMIN,"PROJECT_ADMIN","MANAGER","TESTER").add(new ContextImage("addSessionImg", "images/add_session.png")));
			add(new BookmarkablePageLinkSecure<String>("projectRolesLnk", ProjectRolesPage.class, Roles.ADMIN,"PROJECT_ADMIN").add(new ContextImage("rolesProjectImg", "images/roles_folder.png")));
			add(new BookmarkablePageLinkSecure<String>("updateProjectLnk", UpdateProjectPage.class, Roles.ADMIN,"PROJECT_ADMIN").add(new ContextImage("updateProjectImg", "images/update_folder.png")));
			add(new BookmarkablePageLinkSecure<String>("versionsProjectLnk", ProjectVersionPage.class, Roles.ADMIN,"PROJECT_ADMIN").add(new ContextImage("versionsProjectImg", "images/versions_folder.png")));
			add(new BookmarkablePageLinkSecure<String>("deleteProjectLnk", DeleteProjectPage.class, Roles.ADMIN,"PROJECT_ADMIN").add(new ContextImage("deleteRequirementImg", "images/delete_folder.png")));
			add(new BookmarkablePageLink<String>("exportLnk", ExportPage.class).add(new ContextImage("exportImg", "images/export.png")));
			
			
			add(new RequirementsTablePanel("requirementsTable", getSession().getCurrentProject().getId().intValue()));
			//PLANS Table
			add (new PlansTablePanel("plansTable",getSession().getCurrentProject().getId().intValue(),5));
			add(new BookmarkablePageLink<String>("reqGraphLnk",RequirementDotPage.class));
		}
	}
}
