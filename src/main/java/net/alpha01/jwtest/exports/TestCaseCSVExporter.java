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

import au.com.bytecode.opencsv.CSVWriter;

public class TestCaseCSVExporter {
	public static File exportToCSV() throws JWTestException{
		File tmpFile;
		try {
			tmpFile = File.createTempFile("jwtest_export_testcase", ".csv");
			Logger.getLogger(TestCaseCSVExporter.class).debug("Exporting TestCase on tmpFile:"+tmpFile.getAbsolutePath());
			tmpFile.deleteOnExit();
			Project prj = ((JWTestSession)JWTestSession.get()).getCurrentProject();
			SqlSessionMapper<RequirementMapper> sesMapper = SqlConnection.getSessionMapper(RequirementMapper.class);
			Iterator<Requirement> itr = sesMapper.getMapper().getAll(new RequirementSelectSort(prj.getId(), "num", true)).iterator();
			TestCaseMapper testMapper = sesMapper.getSqlSession().getMapper(TestCaseMapper.class);
			StepMapper stepMapper = sesMapper.getSqlSession().getMapper(StepMapper.class);
			CSVWriter csvWriter = new CSVWriter(new FileWriter(tmpFile),';');
			String[] columns = new String[]{"IDReq","Num","Titolo Caso di Prova","Input","Output Atteso","Note"};
			csvWriter.writeNext(columns);
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
					csvWriter.writeNext(new String[]{req.getType().toString()+req.getNum().toString(),tCase.getId().toString(),tCase.getName(),description,expectedResult," "});
				}
			}
			csvWriter.close();
			sesMapper.close();
			
			return tmpFile;
		} catch (IOException e) {
			throw new JWTestException(TestCaseCSVExporter.class,e);
		}
	}
}
