package net.alpha01.jwtest.pages.requirement;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.beans.Requirement;
import net.alpha01.jwtest.beans.RequirementType;
import net.alpha01.jwtest.dao.RequirementMapper;
import net.alpha01.jwtest.dao.RequirementMapper.Dependency;
import net.alpha01.jwtest.dao.RequirementMapper.RequirementSelectSort;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.pages.project.ProjectPage;
import net.alpha01.jwtest.panels.attachment.AddAttachmentPanel;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
@AuthorizeInstantiation(value={Roles.ADMIN,"PROJECT_ADMIN","TESTER","MANAGER"})
public class UpdateRequirementPage extends LayoutPage implements Serializable {
	private static final long serialVersionUID = 1L;
	private Requirement req;
	private Model<RequirementType> typeModel = new Model<RequirementType>();
	private ArrayList<Requirement> origDependencies = new ArrayList<Requirement>();
	private ArrayList<Requirement> dependencies = new ArrayList<Requirement>();
	
	
	public UpdateRequirementPage(final PageParameters params) {
		super(params);
		if (params.get("idReq").isNull()) {
			error("Parametro idReq non trovato");
			setResponsePage(ProjectPage.class);
		}
		SqlSessionMapper<RequirementMapper> sesReqMapper = SqlConnection.getSessionMapper(RequirementMapper.class);
		req = sesReqMapper.getMapper().get(BigInteger.valueOf(params.get("idReq").toInt()));
		req.setOldId(req.getId());
		
		//recupero il tipo
		RequirementType type=sesReqMapper.getMapper().getType(req.getId_type());
		Logger.getLogger(getClass()).debug("Type Caricato:"+type);
		typeModel.setObject(type);
		
		//recupero le dipendenze
		dependencies.addAll(sesReqMapper.getMapper().getDependencies(req.getId()));
		origDependencies.addAll(dependencies);

		BookmarkablePageLink<Void> requirementLnk = new BookmarkablePageLink<Void>("requirementLnk",RequirementPage.class,new PageParameters().add("idReq",req.getId()));
		requirementLnk.add(new Label("requirementName", req.getName()));
		add(requirementLnk);

		Form<Requirement> updForm = new Form<Requirement>("updForm") {
			private static final long serialVersionUID = 1L;

			protected void onSubmit() {
				SqlSessionMapper<RequirementMapper> sesUpdReqMapper = SqlConnection.getSessionMapper(RequirementMapper.class);
				req.setId_type(typeModel.getObject().getId());
				try {
					if (sesUpdReqMapper.getMapper().update(req).equals(1)) {
						//rimuovo le dipendenze 
						sesUpdReqMapper.getMapper().deleteDependencies(req.getId());
						//ri-aggiungo le dipendenze
						Iterator<Requirement> itr = dependencies.iterator();
						while (itr.hasNext()) {
							Requirement r = itr.next();
							try {
								sesUpdReqMapper.getMapper().addDependency(new Dependency(req.getId(), r.getId()));
							} catch (PersistenceException e) {

							}
						}
						sesUpdReqMapper.commit();
						sesUpdReqMapper.close();
						info("Requirement added successfully");
						setResponsePage(RequirementPage.class,params);
					} else {
						sesUpdReqMapper.rollback();
						sesUpdReqMapper.close();
						error("SQL ERROR :Requirement not addedd");
					}
				} catch (PersistenceException e) {
					e.printStackTrace();
					sesUpdReqMapper.rollback();
					sesUpdReqMapper.close();
					error("SQL ERROR: Duplicate PKEY");
				}
			};
		};

		// recupero i requisiti per le dipendenze
		List<Requirement> allReq = sesReqMapper.getMapper().getAll(new RequirementSelectSort(getSession().getCurrentProject().getId(),req.getId_type(), "name", true));
		//tolgo se stesso dalla lista delle dipendenze
		allReq.remove(req);
		final ListMultipleChoice<Requirement> dependencyFld = new ListMultipleChoice<Requirement>("dependencyFld", new Model<ArrayList<Requirement>>(dependencies), allReq);
		dependencyFld.setOutputMarkupId(true);
		updForm.add(dependencyFld);

		List<RequirementType> types = sesReqMapper.getMapper().getTypes();
		sesReqMapper.close();
		
		DropDownChoice<RequirementType> typeFld =new DropDownChoice<RequirementType>("typeFld", typeModel, types);
		typeFld.setRequired(true);
		typeFld.add(new AjaxFormComponentUpdatingBehavior("onchange"){
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget ajaxTarget) {
				SqlSessionMapper<RequirementMapper> sesMapper = SqlConnection.getSessionMapper(RequirementMapper.class);
				List<Requirement> allReq = sesMapper.getMapper().getAll(new RequirementSelectSort(getSession().getCurrentProject().getId(),typeModel.getObject().getId(), "name", true));
				dependencyFld.setChoices(allReq);
				allReq.remove(req);
				if (origDependencies.size()>0 && allReq.contains(origDependencies.get(0))){
					dependencies.clear();
					dependencies.addAll(origDependencies);
				}
				sesMapper.close();
				ajaxTarget.add(dependencyFld);
			}
		});
		updForm.add(typeFld);
		updForm.add(new TextField<BigInteger>("numFld", new PropertyModel<BigInteger>(req, "num")));
		updForm.add(new TextField<String>("nameFld", new PropertyModel<String>(req, "name")));
		updForm.add(new TextArea<String>("descriptionFld", new PropertyModel<String>(req, "description")));
		add(updForm);
		
		add(new AddAttachmentPanel("attachmentPanel",req));
	}
}
