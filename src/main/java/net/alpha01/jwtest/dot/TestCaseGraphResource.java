package net.alpha01.jwtest.dot;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.TestCaseMapper;
import net.alpha01.jwtest.dao.TestCaseMapper.TestCaseSelectSort;

import org.apache.wicket.protocol.http.WebApplication;

public class TestCaseGraphResource extends AbstractGraphResource {
	private static final long serialVersionUID = 1L;
	private BigInteger idRequirement;

	public TestCaseGraphResource(String name,BigInteger idRequirement) {
		super(name);
		this.idRequirement=idRequirement;
		generateImage();
	}
	
	@Override
	protected void generateDotCode(){
		SqlSessionMapper<TestCaseMapper> sesMapper = SqlConnection.getSessionMapper(TestCaseMapper.class);
		List<TestCase> reqs = sesMapper.getMapper().getAll(new TestCaseSelectSort(idRequirement, "name", true));
		Iterator<TestCase> itt = reqs.iterator();
		try {
			FileWriter fout = new FileWriter(getTmpFile());
			fout.write(getDotPrefix());
			String rootContext = WebApplication.get().getServletContext().getServletContextName();
			while (itt.hasNext()) {
				TestCase test = itt.next();
				fout.write("\""+test.getName().replace('"', '\'')+"\" [URL=\"/"+rootContext+"/test/idTest/"+test.getId()+"\"];");
				Iterator<TestCase> itd = sesMapper.getMapper().getDependencies(test.getId()).iterator();
				while (itd.hasNext()) {
					TestCase dep = itd.next();
					fout.write("\""+test.getName().replace('"','\'')+"\" -> \""+dep.getName().replace('"', '\'')+"\";\n");
				}
			}
			fout.write(getDotEnd());
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sesMapper.close();
	}

}
