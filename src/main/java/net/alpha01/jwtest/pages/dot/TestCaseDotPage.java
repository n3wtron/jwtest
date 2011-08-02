package net.alpha01.jwtest.pages.dot;

import java.math.BigInteger;

import net.alpha01.jwtest.dot.TestCaseGraphResource;
import net.alpha01.jwtest.panels.DotPanel;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;

public class TestCaseDotPage extends WebPage{
	public TestCaseDotPage(PageParameters params){
		//DOT GRAPH
		TestCaseGraphResource resource= new TestCaseGraphResource("testGraphPanel", BigInteger.valueOf(params.getAsInteger("idReq")));
		add(new DotPanel("testcaseDotGraph", resource));
	}
}
