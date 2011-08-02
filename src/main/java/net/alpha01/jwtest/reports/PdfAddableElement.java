package net.alpha01.jwtest.reports;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;

public class PdfAddableElement {
	private Document doc;
	private Paragraph par;
	
	
	public PdfAddableElement(Document doc,Paragraph par){
		this.doc=doc;
		this.par=par;
	}
	
	public PdfAddableElement(Document doc){
		this.doc=doc;
	}
	
	public PdfAddableElement(PdfAddableElement rootPdf, Paragraph par) {
		this.doc=rootPdf.getDocument();
		this.par=par;
	}

	public void add(Element el) throws DocumentException{
		if (par!=null){
			par.add(el);
		}else{
			doc.add(el);
		}
	}
	
	public void newPage(){
		doc.newPage();
	}
	
	public Document getDocument(){
		return this.doc;
	}
}
