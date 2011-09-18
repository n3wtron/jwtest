package net.alpha01.jwtest.test;

import ooo.connector.BootstrapSocketConnector;

import com.sun.star.beans.PropertyValue;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XController;
import com.sun.star.frame.XModel;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.sheet.XSpreadsheet;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.sheet.XSpreadsheetView;
import com.sun.star.uno.Exception;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

public class TestOpenOffice {
	public static void main(String args[]) throws BootstrapException, Exception{
	    String loadUrl = "private:factory/scalc";
		String oooExeFolder="/usr/lib64/libreoffice/program/";
		XComponentContext context = BootstrapSocketConnector.bootstrap(oooExeFolder);
		System.out.println("Trying to connect to OOo service... ");
		 
		 
		// recupera il service manager
		XMultiComponentFactory factory = context.getServiceManager();
		String available = (factory != null ? "available" : "not available");
		
		System.out.println("… the service is '" +available +"'! ");
		
		XComponentLoader xcomponentloader = (XComponentLoader) 
				UnoRuntime.queryInterface(XComponentLoader.class,factory.createInstanceWithContext("com.sun.star.frame.Desktop", context));
		
		 
        PropertyValue[] loadProps = new PropertyValue[1];
        
		XComponent xComponent = xcomponentloader.loadComponentFromURL("file:///home/gigi/Documenti/PianoSessioneDiTest.ods", "_blank", 0,loadProps);
		XSpreadsheetDocument xSpreadsheetDocument=UnoRuntime.queryInterface(XSpreadsheetDocument.class,xComponent);
		

		XModel model = (XModel) UnoRuntime.queryInterface(XModel.class, xSpreadsheetDocument);
		XController controller = model.getCurrentController();
	    XSpreadsheetView view = (XSpreadsheetView) UnoRuntime.queryInterface(XSpreadsheetView.class, controller);
	    XSpreadsheet xSpreadsheet=view.getActiveSheet();
	    xSpreadsheet.getCellByPosition(0, 0).setFormula("CIAO");
	    
	}
}
