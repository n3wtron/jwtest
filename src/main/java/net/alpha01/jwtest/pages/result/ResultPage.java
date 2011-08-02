package net.alpha01.jwtest.pages.result;

import java.math.BigInteger;
import java.util.List;

import net.alpha01.jwtest.beans.Result;
import net.alpha01.jwtest.beans.ResultMantis;
import net.alpha01.jwtest.component.AjaxLinkSecure;
import net.alpha01.jwtest.component.BookmarkablePageLinkSecure;
import net.alpha01.jwtest.dao.ResultMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.pages.session.SessionsPage;
import net.alpha01.jwtest.pages.testcase.TestCasePage;
import net.alpha01.jwtest.panels.CloseablePanel;
import net.alpha01.jwtest.panels.attachment.AttachmentPanel;
import net.alpha01.jwtest.panels.result.ResultsTablePanel;
import net.alpha01.jwtest.util.JWTestConfig;
import net.alpha01.jwtest.util.JWTestUtil;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class ResultPage extends LayoutPage {
	private ResultMantis resMantis = new ResultMantis();
	private ListView<ResultMantis> mantisRow;

	public ResultPage(final PageParameters params) {
		if (!params.containsKey("idResult")) {
			error("parameter idResult not found");
			return;
		}
		boolean isAuthorized = JWTestUtil.isAuthorized(Roles.ADMIN, "PROJECT_ADMIN", "MANAGER").getObject();
		
		SqlSessionMapper<ResultMapper> sesMapper = SqlConnection.getSessionMapper(ResultMapper.class);
		final Result res = sesMapper.getMapper().get(BigInteger.valueOf(params.getAsInteger("idResult")));
		
		add(new BookmarkablePageLinkSecure<String>("addRecycleLnk",AddResultPage.class,new PageParameters("idParent="+res.getId()),Roles.ADMIN,"PROJECT_ADMIN","MANAGER"){
			private static final long serialVersionUID = 1L;
			public boolean isVisible() {
				return super.isVisible() && res.getId_parent()==null;
			};
		}.add(new ContextImage("addRecycleImg", "images/add_recycle.png")));
		
		BookmarkablePageLink<String> testcasePageLnk = new BookmarkablePageLink<String>("testcasePageLnk", TestCasePage.class, new PageParameters("idTest=" + res.getId_testcase()));
		testcasePageLnk.add(new Label("testcaseName", res.getTestCase().toString()));
		add(testcasePageLnk);

		BookmarkablePageLink<String> sesPageLnk = new BookmarkablePageLink<String>("sesPageLnk", SessionsPage.class, new PageParameters("idSession=" + res.getId_session()));
		sesPageLnk.add(new Label("sessionName", res.getSession().toString()));
		add(sesPageLnk);
		
		add(new Label("result",res.getSuccess()==true ? "Success":"Failed"));
		
		add(new Label("note", res.getNote()));

		// Mantis List
		final List<ResultMantis> mantis = sesMapper.getMapper().getAllMantis(res);
		mantisRow = new ListView<ResultMantis>("mantisRow", mantis) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<ResultMantis> item) {
				ExternalLink mantisLnk = new ExternalLink("mantisLnk", JWTestConfig.getProp("mantis.url") + "/view.php?id=" + item.getModel().getObject().getId_mantis().toString());
				mantisLnk.add(new Label("mantisId", item.getModel().getObject().getId_mantis().toString()));
				item.add(mantisLnk);
				item.add(new AjaxLinkSecure<String>("delMantisLnk",Roles.ADMIN, "PROJECT_ADMIN", "MANAGER") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onClick(AjaxRequestTarget target) {
						SqlSessionMapper<ResultMapper> sesMapper = SqlConnection.getSessionMapper(ResultMapper.class);
						if (sesMapper.getMapper().delMantis(new ResultMantis(item.getModel().getObject().getId_result(), item.getModel().getObject().getId_mantis())).equals(1)) {
							sesMapper.commit();
							setResponsePage(ResultPage.class, params);
						} else {
							sesMapper.rollback();
							error("SQL Error deleting mantis link");
						}
						sesMapper.close();
					}
				});
			}
		};
		add(mantisRow);

		// Mantis Add Form
		Form<String> addMantisForm = new Form<String>("addMantisForm") {
			private static final long serialVersionUID = 1L;

			protected void onSubmit() {
				resMantis.setId_result(res.getId());
				SqlSessionMapper<ResultMapper> sesMapper = SqlConnection.getSessionMapper(ResultMapper.class);
				if (sesMapper.getMapper().addMantis(resMantis).equals(1)) {
					sesMapper.commit();
					setResponsePage(ResultPage.class, params);
				} else {
					sesMapper.rollback();
					error("SQL Error adding mantis id");
				}
				sesMapper.close();
			};
		};
		addMantisForm.add(new TextField<BigInteger>("mantidId", new PropertyModel<BigInteger>(resMantis, "id_mantis")));
		add(addMantisForm);
		if (!isAuthorized) {
			addMantisForm.setVisible(false);
		}
		
		//Parent
		WebMarkupContainer parentContainer=new WebMarkupContainer("parentContainer");
		BookmarkablePageLink<String>parentLnk;
		if (res.getId_parent()!=null){
			parentLnk=new BookmarkablePageLink<String>("parentLnk", ResultPage.class,new PageParameters("idResult="+res.getId_parent()));
			parentLnk.add(new Label("parentName",res.getId_parent().toString()));
		}else{
			parentLnk=new BookmarkablePageLink<String>("parentLnk", ResultPage.class);
			parentLnk.add(new Label("parentName"));
		}
		parentContainer.add(parentLnk);
		if (res.getId_parent()==null){
			parentContainer.setVisible(false);
		}
		add(parentContainer);
		
		//recycles
		List<Result> recycles =sesMapper.getMapper().getRecycles(res.getId().intValue());
		if (recycles.size()>0){
			add(new ResultsTablePanel("recyclesTable",JWTestUtil.translate("recycles", this), recycles, 10, isAuthorized));
		}else{
			add(new EmptyPanel("recyclesTable"));
		}
		sesMapper.close();
		
		//ATTACHMENTS TABLE
		final Model<AttachmentPanel> attachPanelModel=new Model<AttachmentPanel>();
		CloseablePanel attachmentsPanel;
		add(attachmentsPanel = new CloseablePanel("attachmentsPanel",JWTestUtil.translate("attachments",this),false){
			private static final long serialVersionUID = 1L;
			@Override
			public Panel getContentPanel(String id) {
				boolean isAuth = JWTestUtil.isAuthorized(Roles.ADMIN, "PROJECT_ADMIN", "MANAGER","TESTER").getObject();
				attachPanelModel.setObject(new AttachmentPanel(id, res, false, isAuth, isAuth));
				return attachPanelModel.getObject();
			}
		});
		if (attachPanelModel.getObject().getSize()==0){
			attachmentsPanel.setVisible(false);
		}
		
		
	}

}
