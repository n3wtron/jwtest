package net.alpha01.jwtest.panels;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class DoubleMultipleChoicePanel<T extends Serializable> extends Panel {
	private static final long serialVersionUID = 1L;
	private IModel<ArrayList<T>> addSel = new Model<ArrayList<T>>(new ArrayList<T>());
	private IModel<ArrayList<T>> remSel = new Model<ArrayList<T>>(new ArrayList<T>());
	private ListMultipleChoice<T> leftSel;
	private ListMultipleChoice<T> rightSel;
	private List<T> leftList=new ArrayList<T>();

	public DoubleMultipleChoicePanel(String id, final IModel<ArrayList<T>> model, List<T> lftList,IChoiceRenderer<T> listRenderer) {
		super(id, model);
		Form<String> selForm = new Form<String>("selForm");
		
		leftList.addAll(lftList);
		if (listRenderer!=null){
			leftSel = new ListMultipleChoice<T>("leftSel", addSel, leftList,listRenderer);
		}else{
			leftSel = new ListMultipleChoice<T>("leftSel", addSel, leftList);
		}
		leftSel.setOutputMarkupId(true);
		if (listRenderer!=null){
			rightSel = new ListMultipleChoice<T>("rightSel", remSel, model.getObject(),listRenderer);
		}else{
			rightSel = new ListMultipleChoice<T>("rightSel", remSel, model.getObject());	
		}
		rightSel.setOutputMarkupId(true);
		selForm.add(leftSel);
		selForm.add(rightSel);

		selForm.add(new AjaxButton("selBtn") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> arg1) {
				Logger.getLogger(getClass()).debug("added pressed");
				Iterator<T> itt = addSel.getObject().iterator();
				while (itt.hasNext()) {
					T el = itt.next();
					Logger.getLogger(getClass()).debug("added " + el);
					if (!model.getObject().contains(el)) {
						model.getObject().add(el);
						leftList.remove(el);
					}
				}
				target.addComponent(leftSel);
				target.addComponent(rightSel);
			}
		});

		selForm.add(new AjaxButton("unSelBtn") {
			private static final long serialVersionUID = 1L;
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> arg1) {
				Logger.getLogger(getClass()).debug("removed pressed");
				Iterator<T> itt = remSel.getObject().iterator();
				while (itt.hasNext()) {
					T el = itt.next();
					leftList.add(el);
					Logger.getLogger(getClass()).debug("removed " + el);
					model.getObject().remove(el);
				}
				target.addComponent(leftSel);
				target.addComponent(rightSel);
			}
		});
		add(selForm);
	}

}
