package net.alpha01.jwtest.dot;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import net.alpha01.jwtest.beans.Requirement;
import net.alpha01.jwtest.dao.RequirementMapper;
import net.alpha01.jwtest.dao.RequirementMapper.RequirementSelectSort;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;

import org.apache.wicket.protocol.http.WebApplication;

public class RequirementGraphResource extends AbstractGraphResource {
	private static final long serialVersionUID = 1L;
	private BigInteger idProject;

	public RequirementGraphResource(String name,BigInteger idProject) {
		super(name);
		this.idProject=idProject;
		generateImage();
	}
	
	@Override
	protected void generateDotCode(){
		SqlSessionMapper<RequirementMapper> sesMapper = SqlConnection.getSessionMapper(RequirementMapper.class);
		List<Requirement> reqs = sesMapper.getMapper().getAll(new RequirementSelectSort(idProject, "id", true));
		Iterator<Requirement> itr = reqs.iterator();
		try {
			FileWriter fout = new FileWriter(getTmpFile());
			fout.write(getDotPrefix());
			String rootContext = WebApplication.get().getServletContext().getServletContextName();
			while (itr.hasNext()) {
				Requirement req = itr.next();
				fout.write("\""+req.getName().replace('"', '\'')+"\" [URL=\"/"+rootContext+"/requirement/idReq/"+req.getId()+"\"];");
				Iterator<Requirement> itd = sesMapper.getMapper().getDependencies(req.getId()).iterator();
				while (itd.hasNext()) {
					Requirement dep = itd.next();
					fout.write("\""+req.getName().replace('"', '\'')+"\" -> \""+dep.getName().replace('"', '\'')+"\";\n");
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
