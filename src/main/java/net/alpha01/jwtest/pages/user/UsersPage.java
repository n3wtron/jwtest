package net.alpha01.jwtest.pages.user;

import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.beans.User;
import net.alpha01.jwtest.component.BookmarkablePageLinkSecure;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.UserMapper;
import net.alpha01.jwtest.pages.LayoutPage;
import net.alpha01.jwtest.panels.PanelLink;

import org.apache.wicket.PageParameters;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
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

@AuthorizeInstantiation(value=Roles.ADMIN)
public class UsersPage extends LayoutPage{
	
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
		@SuppressWarnings("unchecked")
		IColumn<User>[] columns = new IColumn[6];
		columns[0] = new PropertyColumn<User>(new Model<String>("ID"),"id");
		columns[1] = new PropertyColumn<User>(new Model<String>("Username"),"username");
		columns[2] = new PropertyColumn<User>(new Model<String>("Name"),"name");
		columns[3] = new PropertyColumn<User>(new Model<String>("Email"),"email");
		columns[4] = new AbstractColumn<User>(new Model<String>("Update")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<User>> item, String contentId, IModel<User> model) {
				item.add(new PanelLink(contentId,"update",UpdateUserPage.class,new PageParameters("idUser="+model.getObject().getId())));
			}
		};
		columns[5] = new AbstractColumn<User>(new Model<String>("Delete")) {
			private static final long serialVersionUID = 1L;

			@Override
			public void populateItem(Item<ICellPopulator<User>> item, String contentId, IModel<User> model) {
				item.add(new PanelLink(contentId,"delete",DeleteUserPage.class,new PageParameters("idUser="+model.getObject().getId())));
			}
		};
		DataTable<User> usersTable = new DataTable<User>("usersTable", columns, dataProvider, 15);
		usersTable.addTopToolbar(new HeadersToolbar(usersTable, dataProvider));
		usersTable.addBottomToolbar(new NavigationToolbar(usersTable));
		add(usersTable);
	}
}
