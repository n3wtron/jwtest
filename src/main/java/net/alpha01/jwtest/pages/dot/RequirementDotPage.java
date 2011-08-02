package net.alpha01.jwtest.pages.dot;

import net.alpha01.jwtest.JWTestSession;
import net.alpha01.jwtest.dot.RequirementGraphResource;
import net.alpha01.jwtest.panels.DotPanel;

import org.apache.wicket.markup.html.WebPage;

public class RequirementDotPage extends WebPage{
	public RequirementDotPage(){
		//DOT GRAPH
		RequirementGraphResource resource= new RequirementGraphResource("reqName",((JWTestSession)JWTestSession.get()).getCurrentProject().getId());
		add(new DotPanel("reqDotGraph", resource));
	}
}
