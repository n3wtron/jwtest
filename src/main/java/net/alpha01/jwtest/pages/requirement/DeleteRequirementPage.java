package net.alpha01.jwtest.pages.requirement;

import java.math.BigInteger;

import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.beans.Requirement;
import net.alpha01.jwtest.dao.RequirementMapper;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.pages.project.ProjectPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
@AuthorizeInstantiation(value={Roles.ADMIN,"PROJECT_ADMIN","TESTER","MANAGER"})
public class DeleteRequirementPage extends LayoutPage {
	private Requirement req;
	public DeleteRequirementPage(final PageParameters params){
		super(params);
		if (!params.containsKey("idReq")) {
			error("Parametro idReq non trovato");
			setResponsePage(ProjectPage.class);
		}
		 SqlSessionMapper<RequirementMapper> sesReqMapper = SqlConnection.getSessionMapper(RequirementMapper.class);
		req= sesReqMapper.getMapper().get(BigInteger.valueOf(params.getInt("idReq")));
		sesReqMapper.close();
		add (new Label("requirementName",req.getName()));
		Form<Project> delRequirementForm = new Form<Project>("delRequirementForm");
		delRequirementForm.add(new Button("YesBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				 SqlSessionMapper<RequirementMapper> sesDelReqMapper = SqlConnection.getSessionMapper(RequirementMapper.class);
				if (sesDelReqMapper.getMapper().delete(req).equals(1)){
					info("Requirement deleted");
					sesDelReqMapper.commit();
					sesDelReqMapper.close();
					setResponsePage(ProjectPage.class);
				}else{
					sesDelReqMapper.rollback();
					sesDelReqMapper.close();
					error("ERROR: Requirement not deleted SQL ERROR");
				}
			}
		});
		
		delRequirementForm.add(new Button("NoBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				setResponsePage(RequirementPage.class,params);
			}
		});
		add(delRequirementForm);
	}
}
