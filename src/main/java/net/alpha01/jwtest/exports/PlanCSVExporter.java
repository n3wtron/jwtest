package net.alpha01.jwtest.exports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;

import net.alpha01.jwtest.beans.Plan;
import net.alpha01.jwtest.beans.Step;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.dao.PlanMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.StepMapper;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.exceptions.JWTestException;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVWriter;

public class PlanCSVExporter {
	public static File exportToCSV(int idPlan) throws JWTestException {
		File tmpFile;
		try {
			tmpFile = File.createTempFile("jwtest_export_plan", ".csv");
			Logger.getLogger(PlanCSVExporter.class).debug("Exporting Plan on tmpFile:" + tmpFile.getAbsolutePath());
			tmpFile.deleteOnExit();

			SqlSessionMapper<PlanMapper> sesMapper = SqlConnection.getSessionMapper(PlanMapper.class);
			TestCaseMapper testMapper = sesMapper.getSqlSession().getMapper(TestCaseMapper.class);
			StepMapper stepMapper = sesMapper.getSqlSession().getMapper(StepMapper.class);
			Plan plan = sesMapper.getMapper().get(BigInteger.valueOf(idPlan));

			CSVWriter csvWriter = new CSVWriter(new FileWriter(tmpFile), ';');
			String[] columns = new String[] { "IdTest", "Titolo","X", "Input", "Output Atteso", "Note" };
			csvWriter.writeNext(columns);
			Iterator<TestCase> itt = testMapper.getAllByPlan(plan.getId()).iterator();
			while (itt.hasNext()) {
				TestCase tCase = itt.next();
				String description = tCase.getDescription();
				String expectedResult = tCase.getExpected_result();
				Iterator<Step> its = stepMapper.getAll(tCase.getId().intValue()).iterator();
				int i = 0;
				while (its.hasNext()) {
					i++;
					Step step = its.next();
					description += i + ") " + step.getDescription();
					expectedResult += i + ") " + step.getExpected_result();
				}
				csvWriter.writeNext(new String[] { tCase.getId().toString(), tCase.getName(), "", description, expectedResult, " " });
			}
			csvWriter.close();
			sesMapper.close();

			return tmpFile;
		} catch (IOException e) {
			throw new JWTestException(PlanCSVExporter.class, e);
		}
	}
}
