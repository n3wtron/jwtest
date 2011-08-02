package net.alpha01.jwtest.component;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class DatePropertyColumn<T> extends PropertyColumn<T>{
	private static final long serialVersionUID = 9087832091533454608L;
	private String dateFormat;
	public DatePropertyColumn(IModel<String> displayModel, String property,String format) {
		super(displayModel, property);
		this.dateFormat=format;
	}
	
	public DatePropertyColumn(IModel<String> displayModel, String property,String sortProperty,String format) {
		super(displayModel, property,sortProperty);
		this.dateFormat=format;
	}
	
	@Override
	public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		PropertyModel<T> model = new PropertyModel<T>(rowModel, getPropertyExpression());
		if (model.getObject()!= null && ((model.getObject() instanceof Date))){
			item.add(new Label(componentId,sdf.format(model.getObject())));
		}else{
			item.add(new Label(componentId,model));
		}
	}

}
