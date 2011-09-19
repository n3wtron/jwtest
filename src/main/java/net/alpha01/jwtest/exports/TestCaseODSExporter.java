package net.alpha01.jwtest.exports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import net.alpha01.jwtest.JWTestSession;
import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.beans.Requirement;
import net.alpha01.jwtest.beans.Step;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.dao.RequirementMapper;
import net.alpha01.jwtest.dao.RequirementMapper.RequirementSelectSort;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.StepMapper;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.dao.TestCaseMapper.TestCaseSelectSort;
import net.alpha01.jwtest.exceptions.JWTestException;

import org.apache.log4j.Logger;

import com.sun.star.lang.XComponent;
import com.sun.star.sheet.XSpreadsheet;

import au.com.bytecode.opencsv.CSVWriter;

public class TestCaseODSExporter {
	public static File exportToODS() throws JWTestException{
		File tmpFile;
		try {
			tmpFile = File.createTempFile("jwtest_export_testcase", ".ods");
			Logger.getLogger(TestCaseODSExporter.class).debug("Exporting TestCase on tmpFile:"+tmpFile.getAbsolutePath());
			tmpFile.deleteOnExit();
			Project prj = ((JWTestSession)JWTestSession.get()).getCurrentProject();
			SqlSessionMapper<RequirementMapper> sesMapper = SqlConnection.getSessionMapper(RequirementMapper.class);
			Iterator<Requirement> itr = sesMapper.getMapper().getAll(new RequirementSelectSort(prj.getId(), "num", true)).iterator();
			TestCaseMapper testMapper = sesMapper.getSqlSession().getMapper(TestCaseMapper.class);
			StepMapper stepMapper = sesMapper.getSqlSession().getMapper(StepMapper.class);
			
			
			XComponent xComponent=RequirementODSExporter.exportToXComponent();
			XSpreadsheet xSpreadsheet=RequirementODSExporter.getSpreadSheet(xComponent, "CasiProva");
			
			int y=10;
			
			Iterator<TestCase> itt;
			while (itr.hasNext()){
				Requirement req = itr.next();
				itt=testMapper.getAll(new TestCaseSelectSort(req.getId(), "id", true)).iterator();
				while (itt.hasNext()){
					TestCase tCase = itt.next();
					String description=tCase.getDescription();
					String expectedResult=tCase.getExpected_result();
					Iterator<Step> its = stepMapper.getAll(tCase.getId().intValue()).iterator();
					int i=0;
					while (its.hasNext()){
						i++;
						Step step = its.next();
						description+=i+") "+step.getDescription();
						expectedResult+=i+") "+step.getExpected_result();
					}
					
					xSpreadsheet.getCellByPosition(1, y).setFormula(req.getType().toString());
					xSpreadsheet.getCellByPosition(2, y).setFormula(req.getNum().toString());
					xSpreadsheet.getCellByPosition(4, y).setFormula(tCase.getId().toString());
					xSpreadsheet.getCellByPosition(5, y).setFormula(tCase.getName());
					xSpreadsheet.getCellByPosition(6, y).setFormula(description);
					xSpreadsheet.getCellByPosition(7, y++).setFormula(expectedResult);
					
				}
			}
			RequirementODSExporter.storeDocComponent(xComponent, "file://"+tmpFile.getAbsolutePath());
			RequirementODSExporter.closeDocComponent(xComponent);
			return tmpFile;
		
		} catch (java.lang.Exception e) {
			throw new JWTestException(TestCaseODSExporter.class,e);
		}
	}
}
