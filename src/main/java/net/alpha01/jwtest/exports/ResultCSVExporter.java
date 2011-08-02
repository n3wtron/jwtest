package net.alpha01.jwtest.exports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import net.alpha01.jwtest.beans.Requirement;
import net.alpha01.jwtest.beans.Result;
import net.alpha01.jwtest.beans.Step;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.dao.RequirementMapper;
import net.alpha01.jwtest.dao.ResultMapper;
import net.alpha01.jwtest.dao.ResultMapper.ResultSort;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.StepMapper;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.exceptions.JWTestException;
import au.com.bytecode.opencsv.CSVWriter;

public class ResultCSVExporter {
	public static File exportToCSV(int idSession) throws JWTestException{
		File tmpFile;
		try {
			tmpFile = File.createTempFile("jwtest_export_results", ".csv");
			tmpFile.deleteOnExit();
			SqlSessionMapper<ResultMapper> sesMapper = SqlConnection.getSessionMapper(ResultMapper.class);
			TestCaseMapper testMapper = sesMapper.getSqlSession().getMapper(TestCaseMapper.class);
			RequirementMapper reqMapper = sesMapper.getSqlSession().getMapper(RequirementMapper.class);
			StepMapper stepMapper = sesMapper.getSqlSession().getMapper(StepMapper.class);
			
			Iterator<Result> itr = sesMapper.getMapper().getAll(new ResultSort(idSession)).iterator();
			CSVWriter csvWriter = new CSVWriter(new FileWriter(tmpFile),';');
			String[] columns = new String[]{"IdReq","Titolo","Input","Output Atteso","Risultato","Note"};
			csvWriter.writeNext(columns);
			while (itr.hasNext()){
				Result res = itr.next();
				TestCase test = testMapper.get(res.getId_testcase());
				Requirement req= reqMapper.get(test.getId_requirement());
				String description=test.getDescription();
				String expectedResult=test.getExpected_result();
				
				Iterator<Step> its = stepMapper.getAll(test.getId().intValue()).iterator();
				int i=0;
				while (its.hasNext()){
					i++;
					Step step = its.next();
					description+=i+") "+step.getDescription();
					expectedResult+=i+") "+step.getExpected_result();
				}
				String result;
				if (res.getSuccess()){
					result="OK";
				}else{
					result="C";
				}
				csvWriter.writeNext(new String[]{req.getType()+"-"+req.getNum(),test.getName(),description,expectedResult,result,res.getNote()});
			}
			csvWriter.close();
			sesMapper.close();
			return tmpFile;
		} catch (IOException e) {
			throw new JWTestException(ResultCSVExporter.class,e);
		}
	}
}
