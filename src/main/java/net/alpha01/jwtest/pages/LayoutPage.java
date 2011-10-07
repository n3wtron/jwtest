package net.alpha01.jwtest.pages;

import java.util.List;

import net.alpha01.jwtest.JWTestSession;
import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.dao.ProjectMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.pages.project.ProjectPage;
import net.alpha01.jwtest.panels.LoginPanel;
import net.alpha01.jwtest.panels.LogoutPanel;
import net.alpha01.jwtest.panels.MainMenuPanel;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;

public class LayoutPage extends WebPage{
	private static final long serialVersionUID = 1L;
	private Model<Project> currPrj=new Model<Project>(getSession().getCurrentProject());
	private Label currProjectNameLabel;
	public LayoutPage(){
		super();
		generateLayout();
	}
	
	public LayoutPage(PageParameters params){
		super(params);
		generateLayout();
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		String contextPath = getRequestCycle().getRequest().getContextPath();
		response.renderCSSReference(new PackageResourceReference(LayoutPage.class,"style.css"));
		response.renderString("<link rel=\"icon\" href=\""+contextPath+"/images/icon.png\" type=\"image/png\"/>");
	}
	
	public void generateLayout(){
		add(new ContextImage("icon", "images/icon.png"));
		// Form per la scelta del progetto
		Form<Project> projectForm = new Form<Project>("projectForm");
		SqlSessionMapper<ProjectMapper> sesMapper=SqlConnection.getSessionMapper(ProjectMapper.class);
		List<Project> allProjects = sesMapper.getMapper().getAll();
		
		
		DropDownChoice<Project> projectFld = new DropDownChoice<Project>("projectFld",currPrj,allProjects);
		projectFld.add(new AjaxFormComponentUpdatingBehavior("onchange"){
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				getSession().setCurrentProject(currPrj.getObject());
				getSession().setCurrentSession(null);
				Logger.getLogger(getClass()).debug("selezionato progetto:"+currPrj.getObject());
				target.add(currProjectNameLabel);
				setResponsePage(ProjectPage.class);
			}
		});
		projectForm.add(projectFld);
		add(projectForm);
		refreshProjectName(getSession().getCurrentProject());
		
		sesMapper.close();
		
		//TOP MENU
		//USER Panel
		if (getSession().isSignedIn()){
			add(new LogoutPanel("logoutPanel"));
		}else{
			add(new LoginPanel("logoutPanel"));
		}
		
		//MENU
		add(new MainMenuPanel("mainMenuPanel"));
		
		//FeedBackPanel
		add(new FeedbackPanel("fbPanel"));
	}
	
	
	private void refreshProjectName(Project project){
		currProjectNameLabel = new Label("currProjectName",new PropertyModel<String>(currPrj, "name"));
		Logger.getLogger(getClass()).debug("aggiorno nome progetto "+currProjectNameLabel.getDefaultModelObject());
		currProjectNameLabel.setOutputMarkupId(true);
		addOrReplace(currProjectNameLabel);
	}
	
	@Override
	public JWTestSession getSession(){
		return ((JWTestSession)super.getSession());
	}
}
