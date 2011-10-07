package net.alpha01.jwtest.pages.dot;

import java.math.BigInteger;

import net.alpha01.jwtest.dot.TestCaseGraphResource;
import net.alpha01.jwtest.panels.DotPanel;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class TestCaseDotPage extends WebPage{
	private static final long serialVersionUID = 1L;

	public TestCaseDotPage(PageParameters params){
		//DOT GRAPH
		TestCaseGraphResource resource= new TestCaseGraphResource("testGraphPanel", BigInteger.valueOf(params.get("idReq").toLong()));
		add(new DotPanel("testcaseDotGraph", resource));
	}
}
