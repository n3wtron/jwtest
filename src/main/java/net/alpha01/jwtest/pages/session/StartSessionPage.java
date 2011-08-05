package net.alpha01.jwtest.pages.session;

import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.List;

import net.alpha01.jwtest.JWTestSession;
import net.alpha01.jwtest.beans.Plan;
import net.alpha01.jwtest.beans.Profile;
import net.alpha01.jwtest.beans.ProjectVersion;
import net.alpha01.jwtest.beans.Session;
import net.alpha01.jwtest.dao.PlanMapper;
import net.alpha01.jwtest.dao.ProfileMapper;
import net.alpha01.jwtest.dao.ProjectVersionMapper;
import net.alpha01.jwtest.dao.SessionMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.pages.LayoutPage;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

@AuthorizeInstantiation(value={Roles.ADMIN,"PROJECT_ADMIN","TESTER","MANAGER"})
public class StartSessionPage extends LayoutPage {
	private Session session = new Session();
	private Model<Plan> planModel= new Model<Plan>(new Plan());
	private Model<ProjectVersion> versionModel= new Model<ProjectVersion>(new ProjectVersion(getSession().getCurrentProject()));
	private Model<Profile> profileModel = new Model<Profile>();

	public StartSessionPage() {
		SqlSessionMapper<PlanMapper> sesPlanMapper = SqlConnection.getSessionMapper(PlanMapper.class);
		List<Plan> plans = sesPlanMapper.getMapper().getAll(getSession().getCurrentProject().getId().intValue());
		List<ProjectVersion> versions = sesPlanMapper.getSqlSession().getMapper(ProjectVersionMapper.class).getAll(getSession().getCurrentProject().getId());
		List<Profile> profiles = sesPlanMapper.getSqlSession().getMapper(ProfileMapper.class).getAllByProject(JWTestSession.getProject().getId());
		sesPlanMapper.close();
		Form<String> addForm = new Form<String>("addForm") {
			private static final long serialVersionUID = 1L;

			protected void onSubmit() {
				if  (StartSessionPage.this.getSession().getUser().getId().equals(BigInteger.ZERO)){
					error("Admin is not a tester user please re-login with the correct user");
					return;
				}
				
				SqlSessionMapper<SessionMapper> sesSessionMapper = SqlConnection.getSessionMapper(SessionMapper.class);
				session.setId_user(StartSessionPage.this.getSession().getUser().getId());
				session.setStart_date(GregorianCalendar.getInstance().getTime());
				session.setId_plan(planModel.getObject().getId());
				session.setVersion(versionModel.getObject().getVersion());
				session.setId_profile(profileModel.getObject()!=null ?profileModel.getObject().getId():null);
				try {
					if (sesSessionMapper.getMapper().add(session).equals(1)) {
						sesSessionMapper.commit();
						StartSessionPage.this.getSession().setCurrentSession(session);
						info("Session started");
						setResponsePage(SessionsPage.class);
					} else {
						error("ERROR Session not started");
						sesSessionMapper.rollback();
					}
				} catch (PersistenceException e) {
					sesSessionMapper.rollback();
					Logger.getLogger(getClass()).error("SQL ERROR",e);
					error("SQL ERROR");
				}
				sesSessionMapper.close();
			};
		};
		addForm.add(new DropDownChoice<Plan>("planFld", planModel, plans).setRequired(true));
		addForm.add(new DropDownChoice<ProjectVersion>("versionFld", versionModel, versions).setRequired(true));
		addForm.add(new DropDownChoice<Profile>("profileFld", profileModel, profiles).setRequired(false));
		add(addForm);
	}
}
