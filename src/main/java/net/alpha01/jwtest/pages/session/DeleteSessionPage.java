package net.alpha01.jwtest.pages.session;

import java.math.BigInteger;

import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.beans.Session;
import net.alpha01.jwtest.dao.SessionMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.util.JWTestUtil;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

@AuthorizeInstantiation(value={Roles.ADMIN,"PROJECT_ADMIN","MANAGER"})
public class DeleteSessionPage extends LayoutPage {
	private static final long serialVersionUID = 1L;
	private Session ses;
	public DeleteSessionPage(PageParameters params){
		super(params);
		if (params.get("idSession").isNull()) {
			error("Parametro idSession non trovato");
			setResponsePage(SessionsPage.class);
		}
		SqlSessionMapper<SessionMapper> sesTestMapper = SqlConnection.getSessionMapper(SessionMapper.class);
		ses=sesTestMapper.getMapper().get(BigInteger.valueOf(params.get("idSession").toLong()));
		add (new Label("sessionName",ses.toString()));
		Form<Project> delSessionForm = new Form<Project>("delSessionForm");
		delSessionForm.add(new Button("YesBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				SqlSessionMapper<SessionMapper> mapper = SqlConnection.getSessionMapper(SessionMapper.class);
				if (mapper.getMapper().delete(ses).equals(1)){
					info("Session deleted");
					mapper.commit();
					mapper.close();
					DeleteSessionPage.this.getSession().setCurrentSession(null);
					JWTestUtil.cleanDeadElements();
					setResponsePage(SessionsPage.class);
				}else{
					mapper.rollback();
					mapper.close();
					error("ERROR: Session not deleted SQL ERROR");
				}
			}
		});
		
		delSessionForm.add(new Button("NoBtn"){
			private static final long serialVersionUID = 1L;
			@Override
			public void onSubmit() {
				setResponsePage(SessionsPage.class);
			}
		});
		add(delSessionForm);
	}
}
