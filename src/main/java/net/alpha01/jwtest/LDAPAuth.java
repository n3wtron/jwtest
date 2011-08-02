package net.alpha01.jwtest;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import net.alpha01.jwtest.beans.User;
import net.alpha01.jwtest.util.JWTestConfig;

import org.apache.log4j.Logger;

public class LDAPAuth {
	private static String baseSearch = JWTestConfig.getProp("security.ldap.searchBase");
	private static String userSearchString = JWTestConfig.getProp("security.ldap.users.searchString");
	private static String usersOU = "ou=" + JWTestConfig.getProp("security.ldap.users.ou");
	
	private static DirContext getContext(String username, String password) throws NamingException {
		Properties ldapProperties = new Properties();
		ldapProperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		ldapProperties.put(Context.PROVIDER_URL, "ldap://" + JWTestConfig.getProp("security.ldap.server") + ":" + JWTestConfig.getProp("security.ldap.port"));
		ldapProperties.put(Context.SECURITY_AUTHENTICATION, "simple");
		ldapProperties.put(Context.SECURITY_PRINCIPAL, userSearchString.replace("${username}", username)+","+usersOU + "," + baseSearch);
		ldapProperties.put(Context.SECURITY_CREDENTIALS, password);
		return new InitialDirContext(ldapProperties);
	}

	public static User authenticate(String username, String password) {
		try {
			DirContext ctx = getContext(username, password);
			if (ctx==null){
				Logger.getLogger(LDAPAuth.class).error("authentication failure");
				return null;
			}
			User ldapUser=new User(username,password,true);
			SearchControls sctrl = new SearchControls();
			sctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> results = ctx.search(usersOU + "," + baseSearch, userSearchString.replace("${username}", username),sctrl);
			if (results.hasMoreElements()){
				SearchResult res = results.next();
				Attributes attrs= res.getAttributes();
				String cn  = attrs.get("cn")!=null ? (String)(attrs.get("cn").get()) : null;
				if (cn!=null){
					ldapUser.setName(cn);
				}
				String email  = attrs.get("mail")!=null ? (String)(attrs.get("mail").get()) : null;
				if (email!=null){
					ldapUser.setEmail(email);
				}
			}
			return ldapUser;
		} catch (NamingException e) {
			Logger.getLogger(LDAPAuth.class).error("LDAPAuthentication failure",e);
		}
		return null;
	}
}
