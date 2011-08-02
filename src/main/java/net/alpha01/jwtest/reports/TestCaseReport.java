package net.alpha01.jwtest.reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.beans.Step;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.StepMapper;

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

public class TestCaseReport {
	public static File generateReport(TestCase test) throws DocumentException {
		try {
			File outFile = File.createTempFile("jwtest_testcase", ".pdf");
			outFile.deleteOnExit();
			FileOutputStream fout = new FileOutputStream(outFile);
			Logger.getLogger(TestCaseReport.class).debug("PDF generato nel file: " + outFile.getAbsolutePath());
			Document report = new Document();
			PdfWriter.getInstance(report, fout);
			report.open();
			generate(new PdfAddableElement(report), test);
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

	public static void generate(PdfAddableElement rootPdf, TestCase test) throws DocumentException {
		SqlSessionMapper<StepMapper> sesMapper = SqlConnection.getSessionMapper(StepMapper.class);

		Paragraph title = new Paragraph("TestCase " + test.getName(), FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD));
		rootPdf.add(title);
		rootPdf.add(new Paragraph("Descrizione:",FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));
		List<Element> htmlElements;
		try {
			if (test.getDescription() != null) {
				htmlElements = HTMLWorker.parseToList(new StringReader(test.getDescription()), new StyleSheet());
				for (Element el : htmlElements) {
					rootPdf.add(el);
				}
			}
		} catch (IOException e) {
			rootPdf.add(new Paragraph("HTML Parsing Error"));
		}
		rootPdf.add(new Paragraph("Risultato Atteso:",FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD)));		
		try {
			if (test.getExpected_result() != null) {
				htmlElements = HTMLWorker.parseToList(new StringReader(test.getExpected_result()), new StyleSheet());
				for (Element el : htmlElements) {
					rootPdf.add(el);
				}
			}
		} catch (IOException e) {
			rootPdf.add(new Paragraph("HTML Parsing Error"));
		}
		rootPdf.add(new Paragraph(" "));
		List<Step> steps = sesMapper.getMapper().getAll(test.getId().intValue());
		if (steps.size() > 0) {
			Paragraph tableParagraph = new Paragraph("Steps");
			PdfPTable stepsTable = new PdfPTable(new float[] { 10, 45, 45 });
			stepsTable.setWidthPercentage(100);
			// Header
			stepsTable.addCell("Seq.");
			stepsTable.addCell("Input");
			stepsTable.addCell("Output");
			stepsTable.setHeaderRows(0);
			Iterator<Step> its = steps.iterator();
			while (its.hasNext()) {
				Step step = its.next();
				stepsTable.addCell(step.getSequence());
				try {
					for (Element htmlEl : HTMLWorker.parseToList(new StringReader(step.getDescription()!=null ?step.getDescription() : " "), new StyleSheet())) {
						PdfPCell cell = new PdfPCell();
						cell.addElement(htmlEl);
						stepsTable.addCell(cell);
					}
					for (Element htmlEl : HTMLWorker.parseToList(new StringReader(step.getExpected_result()!=null ? step.getExpected_result() : " " ), new StyleSheet())) {
						PdfPCell cell = new PdfPCell();
						cell.addElement(htmlEl);
						stepsTable.addCell(cell);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			tableParagraph.add(stepsTable);
			rootPdf.add(tableParagraph);
			rootPdf.add(new Paragraph(" "));
		}
		sesMapper.close();
	}
}
