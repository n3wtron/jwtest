package net.alpha01.jwtest.panels.profiles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.beans.Profile;
import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.component.DataTableAlternatedRows;
import net.alpha01.jwtest.component.HtmlPropertyColumn;
import net.alpha01.jwtest.dao.ProfileMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.pages.profiles.ProfilesPage;
import net.alpha01.jwtest.pages.profiles.UpdateProfilePage;
import net.alpha01.jwtest.panels.PanelLabel;
import net.alpha01.jwtest.panels.PanelLinkAjaxSecure;
import net.alpha01.jwtest.panels.PanelLinkSecure;
import net.alpha01.jwtest.util.JWTestUtil;

import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

public class ProfileTablePanel extends Panel {
	private static final long serialVersionUID = 1L;
	class ProfileDataTable extends SortableDataProvider<Profile> {
		private static final long serialVersionUID = 1L;
		private Project project;
		private List<Profile> profiles;

		public ProfileDataTable(Project project) {
			this.project = project;
		}

		@Override
		public Iterator<? extends Profile> iterator(int index, int length) {
			return profiles.subList(index, index + length).iterator();
		}

		@Override
		public IModel<Profile> model(Profile profile) {
			IModel<Profile> result = new Model<Profile>(profile);
			result.detach();
			return result;
		}

		@Override
		public int size() {
			SqlSessionMapper<ProfileMapper> sesMapper = SqlConnection.getSessionMapper(ProfileMapper.class);
			if (project!=null){
				profiles = sesMapper.getMapper().getAllByProject(project.getId());
			}else{
				profiles = sesMapper.getMapper().getAll();
			}
			sesMapper.close();
			return profiles.size();
		}

	}


	@SuppressWarnings("unchecked")
	public ProfileTablePanel(String id, Project project, boolean modLink) {
		super(id);
		ProfileDataTable dataProvider = new ProfileDataTable(project);
		ArrayList<IColumn<Profile>> columns=new ArrayList<IColumn<Profile>>();
		columns.add(new AbstractColumn<Profile>(new StringResourceModel("name",this,null)) {
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<Profile>> item, String contentId, IModel<Profile> model) {
				item.add(new PanelLabel(contentId, model.getObject().getName()));	
			}
		});
		columns.add(new PropertyColumn<Profile>(new StringResourceModel("project",this,null), "projectName"));
		columns.add(new HtmlPropertyColumn<Profile>(new StringResourceModel("description",this,null), "description"));
		
		if (modLink){
			columns.add(new AbstractColumn<Profile>(new StringResourceModel("update",this,null)) {
				private static final long serialVersionUID = 1L;

				@Override
				public void populateItem(Item<ICellPopulator<Profile>> item, String contentId, IModel<Profile> model) {
					if (model.getObject().getId_project()==null){
						//only ADMIN
						item.add(new PanelLinkSecure(contentId,JWTestUtil.translate("update", ProfileTablePanel.this),UpdateProfilePage.class, new PageParameters("idProfile="+model.getObject().getId().toString()),Roles.ADMIN));
					}else{
						item.add(new PanelLinkSecure(contentId,JWTestUtil.translate("update", ProfileTablePanel.this),UpdateProfilePage.class, new PageParameters("idProfile="+model.getObject().getId().toString()),Roles.ADMIN,"PROJECT_ADMIN"));
					}
				}
			});
			columns.add(new AbstractColumn<Profile>(new StringResourceModel("delete",this,null)) {
				private static final long serialVersionUID = 1L;

				@Override
				public void populateItem(Item<ICellPopulator<Profile>> item, String contentId, final IModel<Profile> model) {
					if (model.getObject().getId_project()==null){
						//only ADMIN
						item.add(new PanelLinkAjaxSecure(contentId,JWTestUtil.translate("delete", ProfileTablePanel.this),Roles.ADMIN){
							private static final long serialVersionUID = 1L;
							@Override
							protected void onAjaxClick(AjaxRequestTarget target) {
								deleteProfile(model.getObject());
							}
						});
					}else{
						item.add(new PanelLinkAjaxSecure(contentId,JWTestUtil.translate("delete", ProfileTablePanel.this),Roles.ADMIN,"PROJECT_ADMIN"){
							private static final long serialVersionUID = 1L;
							@Override
							protected void onAjaxClick(AjaxRequestTarget target) {
								deleteProfile(model.getObject());
							}
						});
					}
				}
			});
		}
		
		DataTable<Profile> dTable = new DataTableAlternatedRows<Profile>("profileDataTable", columns.toArray(new IColumn[0]), dataProvider, 20);
		dTable.addTopToolbar(new HeadersToolbar(dTable, dataProvider));
		dTable.addBottomToolbar(new NavigationToolbar(dTable));
		add(dTable);
	}
	
	private void deleteProfile(Profile profile){
		SqlSessionMapper<ProfileMapper> sesMapper  = SqlConnection.getSessionMapper(ProfileMapper.class);
		if (sesMapper.getMapper().delete(profile).equals(1)){
			sesMapper.commit();
			info("Profile deleted");
			setResponsePage(ProfilesPage.class);
		}else{
			error("SQL Error");
			sesMapper.rollback();
		}
		sesMapper.close();
	}

}
