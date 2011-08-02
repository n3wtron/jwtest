package net.alpha01.jwtest.exports;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import net.alpha01.jwtest.JWTestSession;
import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.beans.Requirement;
import net.alpha01.jwtest.dao.RequirementMapper;
import net.alpha01.jwtest.dao.RequirementMapper.RequirementSelectSort;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.exceptions.JWTestException;
import au.com.bytecode.opencsv.CSVWriter;

public class RequirementCSVExporter {
	public static File exportToCSV() throws JWTestException{
		File tmpFile;
		try {
			tmpFile = File.createTempFile("jwtest_export_requirement", ".csv");
			tmpFile.deleteOnExit();
			Project prj = ((JWTestSession)JWTestSession.get()).getCurrentProject();
			SqlSessionMapper<RequirementMapper> sesMapper = SqlConnection.getSessionMapper(RequirementMapper.class);
			Iterator<Requirement> itr = sesMapper.getMapper().getAll(new RequirementSelectSort(prj.getId(), "num", true)).iterator();
			sesMapper.close();
			CSVWriter csvWriter = new CSVWriter(new FileWriter(tmpFile),';');
			String[] columns = new String[]{"CodReq","Num","Titolo Requisito","Descrizione"};
			csvWriter.writeNext(columns);
			while (itr.hasNext()){
				Requirement req = itr.next();
				csvWriter.writeNext(new String[]{req.getType().toString(),req.getNum().toString(),req.getName(),req.getDescription()});
			}
			csvWriter.close();
			
			return tmpFile;
		} catch (IOException e) {
			throw new JWTestException(RequirementCSVExporter.class,e);
		}
	}
}
