package net.alpha01.jwtest.pages.project;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.beans.ProjectVersion;
import net.alpha01.jwtest.dao.ProjectVersionMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.pages.LayoutPage;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
@AuthorizeInstantiation(value={Roles.ADMIN,"PROJECT_ADMIN"})
public class ProjectVersionPage extends LayoutPage {
	private static final long serialVersionUID = 1L;
	private ProjectVersion nPv = new ProjectVersion(getSession().getCurrentProject());
	private ArrayList<ProjectVersion> selectedVersion = new ArrayList<ProjectVersion>();
	private ProjectVersion uPv = new ProjectVersion();
	private TextField<String> updVersionFld;
	private DateTimeField releasedFld; 
	private AjaxButton updateBtn;

	public ProjectVersionPage() {
		final Project prj = getSession().getCurrentProject();
		Form<String> versionsForm = new Form<String>("versionsForm");
		SqlSessionMapper<ProjectVersionMapper> sesMapper = SqlConnection.getSessionMapper(ProjectVersionMapper.class);
		final ArrayList<ProjectVersion> versions = new ArrayList<ProjectVersion>();
		versions.addAll(sesMapper.getMapper().getAll(prj.getId()));
		sesMapper.close();

		final ListMultipleChoice<ProjectVersion> versionList = new ListMultipleChoice<ProjectVersion>("versionsFld", new Model<ArrayList<ProjectVersion>>(selectedVersion), versions);
		versionList.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				refreshUpdateForm(target);
			}
		});
		versionList.setOutputMarkupId(true);
		versionsForm.add(versionList);

		// DELETE
		versionsForm.add(new AjaxButton("delBtn") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				SqlSessionMapper<ProjectVersionMapper> sesMapper = SqlConnection.getSessionMapper(ProjectVersionMapper.class);
				Iterator<ProjectVersion> itp = selectedVersion.iterator();
				boolean sqlOk = true;
				while (itp.hasNext() && sqlOk) {
					sqlOk = sqlOk && sesMapper.getMapper().delete(itp.next()).equals(1);
				}
				if (!sqlOk) {
					error("SQL Error");
					sesMapper.rollback();
				} else {
					sesMapper.commit();
					selectedVersion.clear();
					versions.clear();
					versions.addAll(sesMapper.getMapper().getAll(prj.getId()));
					sesMapper.close();
					target.add(versionList);
					refreshUpdateForm(target);
				}
			}

			@Override
			protected void onError(AjaxRequestTarget arg0, Form<?> arg1) {
				// TODO Auto-generated method stub
				
			}
		});

		// UPDATE
		updVersionFld = new TextField<String>("updVersionFld", new PropertyModel<String>(uPv, "version"));
		updVersionFld.setOutputMarkupId(true);
		versionsForm.add(updVersionFld);
		releasedFld = new DateTimeField("releasedFld", new PropertyModel<Date>(uPv, "released"));
		releasedFld.setOutputMarkupId(true);
		versionsForm.add(releasedFld);
		
		updateBtn=new AjaxButton("updateBtn") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				SqlSessionMapper<ProjectVersionMapper> sesMapper = SqlConnection.getSessionMapper(ProjectVersionMapper.class);
				if (sesMapper.getMapper().update(uPv).equals(1)){
					info("Version updated");
					sesMapper.commit();
					setResponsePage(ProjectVersionPage.class);
				}else{
					error("SQL Error");
					sesMapper.rollback();
				}
				sesMapper.close();
			}

			@Override
			protected void onError(AjaxRequestTarget arg0, Form<?> arg1) {
				// TODO Auto-generated method stub	
			}
		};
		updateBtn.setOutputMarkupId(true);
		versionsForm.add(updateBtn);
		
		// INSERT
		final TextField<String> versionFld = new TextField<String>("versionFld", new PropertyModel<String>(nPv, "version"));
		versionFld.setOutputMarkupId(true);
		versionsForm.add(versionFld);
		versionsForm.add(new AjaxButton("addVersionBtn") {
			private static final long serialVersionUID = 1L;

			@Override
			public void onSubmit(AjaxRequestTarget target, Form<?> form) {
				SqlSessionMapper<ProjectVersionMapper> sesMapper = SqlConnection.getSessionMapper(ProjectVersionMapper.class);
				if (sesMapper.getMapper().add(nPv).equals(1)) {
					sesMapper.commit();
					versions.add(nPv);
					nPv = new ProjectVersion(prj);
					target.add(versionList);
					versionFld.setModel(new PropertyModel<String>(nPv, "version"));
					target.add(versionFld);
				} else {
					error("SQL Error");
					sesMapper.rollback();
				}
				sesMapper.close();
			}

			@Override
			protected void onError(AjaxRequestTarget arg0, Form<?> arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		add(versionsForm);
	}
	
	
	private void refreshUpdateForm(AjaxRequestTarget target){
		boolean enabled;
		Logger.getLogger(getClass()).debug("numSelected "+selectedVersion.size());
		if (selectedVersion.size()==1){
			Logger.getLogger(getClass()).debug("set visible true");
			uPv=selectedVersion.get(0);
			uPv.setOldVersion(uPv.getVersion());
			enabled=true;
		}else{
			uPv=new ProjectVersion();
			Logger.getLogger(getClass()).debug("set visible false");
			enabled=false;
		}
		updVersionFld.setModel(new PropertyModel<String>(uPv,"version"));
		releasedFld.setModel(new PropertyModel<Date>(uPv,"released"));
		updVersionFld.setEnabled(enabled);
		releasedFld.setEnabled(enabled);
		updateBtn.setEnabled(enabled);
		target.add(updVersionFld);
		target.add(updateBtn);
		target.add(releasedFld);
	}
}
