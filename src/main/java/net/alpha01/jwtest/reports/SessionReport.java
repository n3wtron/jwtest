package net.alpha01.jwtest.reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.JWTestSession;
import net.alpha01.jwtest.beans.Plan;
import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.beans.Result;
import net.alpha01.jwtest.beans.Session;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.dao.PlanMapper;
import net.alpha01.jwtest.dao.ResultMapper;
import net.alpha01.jwtest.dao.ResultMapper.ResultSort;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;

import org.apache.log4j.Logger;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.html.simpleparser.StyleSheet;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class SessionReport {
	public static File generateReport(Session ses) throws DocumentException {
		try {
			File outFile = File.createTempFile("jwtest_report", ".pdf");
			outFile.deleteOnExit();
			FileOutputStream fout = new FileOutputStream(outFile);
			Logger.getLogger(SessionReport.class).debug("PDF generato nel file: " + outFile.getAbsolutePath());
			Document report = new Document();
			PdfWriter.getInstance(report, fout);
			report.open();
			generate(new PdfAddableElement(report), ses);
			report.close();
			fout.close();
			return outFile;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void generate(PdfAddableElement rootPdf, Session ses) throws DocumentException {
		Project prj = ((JWTestSession) JWTestSession.get()).getCurrentProject();
		SqlSessionMapper<ResultMapper> sesMapper = SqlConnection.getSessionMapper(ResultMapper.class);
		Plan plan = sesMapper.getSqlSession().getMapper(PlanMapper.class).get(ses.getId_plan());
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

		Paragraph preface = new Paragraph();
		Paragraph title = new Paragraph("Report Session " + plan.getName() + " - " + sdf.format(ses.getStart_date()), FontFactory.getFont(FontFactory.HELVETICA, 18, Font.BOLD));
		title.setAlignment(Element.ALIGN_CENTER);
		preface.add(title);
		preface.add(new Paragraph(""));
		preface.add(new Paragraph("Project: " + prj.getName()));
		preface.add(new Paragraph("Version: " + ses.getVersion()));
		preface.add(new Paragraph("Plan: " + plan.getName()));
		preface.add(new Paragraph("Author: " + ses.getUser()));
		rootPdf.add(new Paragraph(""));
		preface.add(new Paragraph("Start Date: " + sdf.format(ses.getStart_date())));
		preface.add(new Paragraph("End Date: " + sdf.format(ses.getStart_date())));
		rootPdf.add(preface);
		rootPdf.add(new Paragraph(" "));
		Paragraph tableParagraph = new Paragraph("Tabella Risultati");

		PdfPTable resultsTable = new PdfPTable(3);
		resultsTable.setWidthPercentage(100);

		// Header
		resultsTable.addCell("TestCase");
		resultsTable.addCell("Result");
		resultsTable.addCell("Note");
		resultsTable.setHeaderRows(0);
		List<TestCase> tests = new ArrayList<TestCase>();
		Iterator<Result> itr = sesMapper.getMapper().getAll(new ResultSort(ses.getId().intValue())).iterator();
		while (itr.hasNext()) {
			Result res = itr.next();
			TestCase ts = res.getTestCase();
			tests.add(ts);
			resultsTable.addCell(ts.toString());
			if (res.getSuccess()) {
				resultsTable.addCell("SUCCESS");
			} else {
				resultsTable.addCell("FAILED");
			}
			if (res.getNote() == null) {
				resultsTable.addCell(" ");
			} else {
				try {
					List<Element> htmlElements;
					htmlElements = HTMLWorker.parseToList(new StringReader(res.getNote() != null ? res.getNote() : " "), new StyleSheet());
					for (Element htmlEl : htmlElements) {
						PdfPCell cell = new PdfPCell();
						cell.addElement(htmlEl);
						resultsTable.addCell(cell);
					}
				} catch (IOException e) {
					Logger.getLogger(SessionReport.class).error("PDF Reder result note", e);
				}
			}
		}
		tableParagraph.add(resultsTable);
		rootPdf.add(tableParagraph);

		// TestCases Details
		rootPdf.newPage();
		rootPdf.add(new Paragraph("Dettaglio dei Casi di Test",FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD)));
		Paragraph testCasesParagraph = new Paragraph(" ");
		testCasesParagraph.add(new Paragraph(" "));
		for (TestCase test : tests) {
			TestCaseReport.generate(new PdfAddableElement(rootPdf, testCasesParagraph), test);
		}
		rootPdf.add(testCasesParagraph);

		sesMapper.close();
	}
}
