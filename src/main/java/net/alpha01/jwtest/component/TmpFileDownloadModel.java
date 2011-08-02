package net.alpha01.jwtest.component;

import java.io.File;

import org.apache.wicket.model.LoadableDetachableModel;

public abstract class TmpFileDownloadModel extends LoadableDetachableModel<File>{
	private static final long serialVersionUID = 1L;
	private File file;
	
	protected abstract File getFile();
	
	@Override
	protected  File load(){
		file=getFile();
		return file;
	}
	
	@Override
	protected void onDetach() {
		if (getFile()!=null){
			getFile().delete();
		}
	}


}
