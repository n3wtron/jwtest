package net.alpha01.jwtest.dot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import net.alpha01.jwtest.util.JWTestConfig;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;

public abstract class AbstractGraphResource extends DynamicImageResource {
	private static final long serialVersionUID = 1L;
	private File tmpFile;
	File tmpOutFile;
	private String map = new String();
	private String name;
	private String size="8,5";
	private byte[] content=null;

	public AbstractGraphResource(String name) {
		this.setName(name);
		try {
			tmpFile = File.createTempFile("jwtest", ".dot");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	protected void generateImage() {
		generateDotCode();
		try {
			Runtime runtime = Runtime.getRuntime();
			tmpOutFile = File.createTempFile("jwtest", ".png");
			tmpOutFile.deleteOnExit();
			File tmpMapOutFile = File.createTempFile("jwtest", ".map");
			tmpMapOutFile.deleteOnExit();
			String dotPath = JWTestConfig.getProp("dot.path");
			String dotCommand = dotPath+" -Tcmapx -o " + tmpMapOutFile.getAbsolutePath() + " -Tpng " + getTmpFile().getAbsolutePath() + " -o " + tmpOutFile.getAbsolutePath();
			Logger.getLogger(getClass()).debug("dot Command:" + dotCommand);
			Process pr = runtime.exec(dotCommand);
			int rc=0;
			if ((rc=pr.waitFor()) != 0) {
				Logger.getLogger(getClass()).error("Dot Process exited with "+rc+" RetCode");
			}
			// map loading
			FileReader mapReader = new FileReader(tmpMapOutFile);
			char[] buffer = new char[1024];
			int readed=0;
			StringWriter writer=new StringWriter();
			while ((readed=mapReader.read(buffer)) > 0) {
				writer.write(buffer, 0, readed);
			}
			writer.close();
			map=writer.toString();
			mapReader.close();
			tmpMapOutFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected abstract void generateDotCode();

	protected String getDotPrefix() {
		String prefix="";
		prefix+=JWTestConfig.getProp("dot.rankdir")!=null ? "rankdir="+JWTestConfig.getProp("dot.rankdir")+";\n" : "";
		prefix+=JWTestConfig.getProp("dot.shape")!=null ? "node [shape="+JWTestConfig.getProp("dot.shape")+"];\n" : "";
		return "digraph "+getName()+"{\n"+prefix;
	}

	protected String getDotEnd() {
		return "}";
	}

	@Override
	protected byte[] getImageData() {
		if (content!=null){
			return content;
		}
		try {
			content= new byte[(int) tmpOutFile.length()];
			FileInputStream in;
			in = new FileInputStream(tmpOutFile);
			in.read(content);
			in.close();
			tmpOutFile.delete();
			return content;
		} catch (FileNotFoundException e) {
			return new byte[0];
		} catch (IOException e) {
			return new byte[0];
		}

	}

	public File getTmpFile() {
		return tmpFile;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public String getMap() {
		return map;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}


	public void setSize(String size) {
		this.size = size;
	}


	public String getSize() {
		return size;
	}
	
	public String getUseMapName(){
		return "#"+getName();
	}
	
	

}
