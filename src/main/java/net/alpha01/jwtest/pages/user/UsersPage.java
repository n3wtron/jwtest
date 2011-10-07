package net.alpha01.jwtest.pages.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.beans.User;
import net.alpha01.jwtest.component.BookmarkablePageLinkSecure;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.UserMapper;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.panels.PanelLink;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

@AuthorizeInstantiation(value=Roles.ADMIN)
public class UsersPage extends LayoutPage{
	private static final long serialVersionUID = 1L;

	class UsersDataProvider extends SortableDataProvider<User>{
		private static final long serialVersionUID = 1L;
		private List<User> users;
		@Override
		public Iterator<? extends User> iterator(int first, int count) {
			return users.subList(first, first+count).iterator();
		}

		@Override
		public int size() {
			SqlSessionMapper<UserMapper> sesMapper = SqlConnection.getSessionMapper(UserMapper.class);
			users=sesMapper.getMapper().getAll();
			sesMapper.close();
			return users.size();
		}

		@Override
		public IModel<User> model(User object) {
			Model<User> userModel = new Model<User>(object);
			userModel.detach();
			return userModel;
		}
	}
	
	public UsersPage(){
		
		//LINK
		add(new BookmarkablePageLinkSecure<String>("addUserLnk",AddUserPage.class,Roles.ADMIN).add(new ContextImage("addUserImg", "images/add_user.png")));
		
		
		UsersDataProvider dataProvider = new UsersDataProvider();
		List<IColumn<User>> columns = new ArrayList<IColumn<User>>();
		columns.add( new PropertyColumn<User>(new Model<String>("ID"),"id"));
		columns.add( new PropertyColumn<User>(new Model<String>("Username"),"username"));
		columns.add( new PropertyColumn<User>(new Model<String>("Name"),"name"));
		columns.add( new PropertyColumn<User>(new Model<String>("Email"),"email"));
		columns.add( new AbstractColumn<User>(new Model<String>("Update")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<User>> item, String contentId, IModel<User> model) {
				item.add(new PanelLink(contentId,"update",UpdateUserPage.class,new PageParameters().add("idUser",model.getObject().getId())));
			}
		});
		columns.add(new AbstractColumn<User>(new Model<String>("Delete")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<User>> item, String contentId, IModel<User> model) {
				item.add(new PanelLink(contentId,"delete",DeleteUserPage.class,new PageParameters().add("idUser",model.getObject().getId())));
			}
		});
		DataTable<User> usersTable = new DataTable<User>("usersTable", columns, dataProvider, 15);
		usersTable.addTopToolbar(new HeadersToolbar(usersTable, dataProvider));
		usersTable.addBottomToolbar(new NavigationToolbar(usersTable));
		add(usersTable);
	}
}
