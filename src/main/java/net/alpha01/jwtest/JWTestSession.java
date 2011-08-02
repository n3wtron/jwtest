package net.alpha01.jwtest;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Iterator;

import net.alpha01.jwtest.beans.Role;
import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.beans.Session;
import net.alpha01.jwtest.beans.User;
import net.alpha01.jwtest.dao.ProjectMapper;
import net.alpha01.jwtest.dao.ProjectMapper.ProjectUserRoles;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.UserMapper;
import net.alpha01.jwtest.util.JWTestConfig;

import org.apache.log4j.Logger;
import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;

public class JWTestSession extends AuthenticatedWebSession {
	private static final long serialVersionUID = -625809801878692512L;

	private Project currentProject;
	private Session currentSession;
	private WebUser user;
	private HashSet<String> projectRoles = new HashSet<String>();

	private Roles currRoles;

	public JWTestSession(Request request) {
		super(request);
	}

	public void setCurrentProject(Project currentProject) {
		this.currentProject = currentProject;
		refreshProjectRoles();
	}

	public Project getCurrentProject() {
		return currentProject;
	}

	public void setCurrentSession(Session currentSession) {
		this.currentSession = currentSession;
	}

	public Session getCurrentSession() {
		return currentSession;
	}

	public WebUser getUser() {
		return user;
	}

	public void setUser(WebUser user) {
		this.user = user;
	}

	public void refreshProjectRoles() {
		projectRoles.clear();
		currRoles = null;
		if (getUser() != null && getCurrentProject() != null) {
			SqlSessionMapper<ProjectMapper> sesMapper = SqlConnection.getSessionMapper(ProjectMapper.class);
			for (Role grp : sesMapper.getMapper().getRoles(new ProjectUserRoles(getCurrentProject().getId(), getUser().getId()))) {
				projectRoles.add(grp.getName());
			}
			sesMapper.close();
		}
	}

	@Override
	public boolean authenticate(String username, String password) {
		// check if its admin user
		String adminUsername = JWTestConfig.getProp("security.admin.username");
		String adminPassword = JWTestConfig.getProp("security.admin.password");
		Logger.getLogger(getClass()).debug("use:" + username + " pw:" + password + " admUser:" + adminUsername + " adminPassword:" + adminPassword);
		if (username.equals(adminUsername) && password.equals(adminPassword)) {
			user = new WebUser();
			user.setId(BigInteger.ZERO);
			user.setEmail(JWTestConfig.getProp("security.admin.email"));
			user.setUsername(username);
			user.setRoles(new HashSet<String>());
			user.getRoles().add(Roles.ADMIN);
			refreshProjectRoles();
			signIn(true);
			return true;
		}

		// check if ldap is enabled and do the LDAP query
		User ldapUser = null;
		if (JWTestConfig.getPropAsBoolean("security.ldap.enabled")) {
			Logger.getLogger(getClass()).info("LDAP Enabled");
			ldapUser = LDAPAuth.authenticate(username, password);
			if (ldapUser!=null){
				Logger.getLogger(getClass()).info("LDAP User found");
			}else{
				Logger.getLogger(getClass()).info("LDAP User not found");
			}
		}else{
			Logger.getLogger(getClass()).info("LDAP Disabled");
		}
		// classic authentication
		SqlSessionMapper<UserMapper> sesMapper = SqlConnection.getSessionMapper(UserMapper.class);
		User dbUser;
		if (ldapUser!=null){
			dbUser=sesMapper.getMapper().get(new User(username, null,true));
		}else{
			dbUser=sesMapper.getMapper().get(new User(username, password,false));
		}
		if (ldapUser!=null && dbUser==null){
			//insert new ldap user on jwtest db
			if (!sesMapper.getMapper().add(ldapUser).equals(1)){
				sesMapper.rollback();
				sesMapper.close();
				return false;
			}else{
				sesMapper.commit();
				dbUser=ldapUser;
			}
		}
		if (dbUser == null) {
			sesMapper.close();
			signIn(false);
			return false;
		} else {
			user = new WebUser();
			user.setId(dbUser.getId());
			user.setEmail(dbUser.getEmail());
			user.setUsername(username);
			user.setName(dbUser.getName());
			Iterator<Role> itg = sesMapper.getMapper().getRoles(dbUser).iterator();
			user.setRoles(new HashSet<String>());
			while (itg.hasNext()) {
				user.getRoles().add(itg.next().getName());
			}
			sesMapper.close();
			refreshProjectRoles();
			signIn(true);
			return true;
		}

	}

	@Override
	public Roles getRoles() {
		if (user == null) {
			return null;
		} else {
			if (currRoles == null) {
				HashSet<String> allRoles = new HashSet<String>();
				allRoles.addAll(user.getRoles());
				allRoles.addAll(projectRoles);
				currRoles = new Roles(allRoles.toArray(new String[0]));
			}
			return currRoles;
		}
	}

	public void logout() {
		this.user = null;
		this.projectRoles.clear();
		this.currRoles = null;
		signOut();
	}

	public static Project getProject() {
		return ((JWTestSession)JWTestSession.get()).getCurrentProject();
	}

}
