package net.alpha01.jwtest.panels;

import java.io.File;

import net.alpha01.jwtest.pages.LayoutPage;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.panel.Panel;

public class PanelLink extends Panel{
	private static final long serialVersionUID = 1L;

	public PanelLink(String id, String msg,Class<? extends LayoutPage> pageClass, PageParameters params) {
		super(id);
		BookmarkablePageLink<String> lnk = new BookmarkablePageLink<String>("lnk", pageClass,params);
		lnk.add(new Label("lbl",msg));
		add(lnk);
		
	}
	
	public PanelLink(String id, String msg, File file,String fileName){
		super(id);
		DownloadLink lnk = new DownloadLink("lnk", file,fileName);
		lnk.add(new Label("lbl",msg));
		add(lnk);
	}

}
