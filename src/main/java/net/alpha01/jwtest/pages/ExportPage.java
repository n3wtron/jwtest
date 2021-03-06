package net.alpha01.jwtest.pages;

import java.io.File;
import java.util.List;

import net.alpha01.jwtest.JWTestSession;
import net.alpha01.jwtest.beans.Plan;
import net.alpha01.jwtest.beans.Session;
import net.alpha01.jwtest.component.TmpFileDownloadModel;
import net.alpha01.jwtest.dao.PlanMapper;
import net.alpha01.jwtest.dao.SessionMapper;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.exceptions.JWTestException;
import net.alpha01.jwtest.exports.PlanCSVExporter;
import net.alpha01.jwtest.exports.PlanODSExporter;
import net.alpha01.jwtest.exports.RequirementCSVExporter;
import net.alpha01.jwtest.exports.RequirementODSExporter;
import net.alpha01.jwtest.exports.ResultCSVExporter;
import net.alpha01.jwtest.exports.ResultODSExporter;
import net.alpha01.jwtest.exports.TestCaseCSVExporter;
import net.alpha01.jwtest.exports.TestCaseODSExporter;
import net.alpha01.jwtest.util.JWTestConfig;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.IRequestCycle;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.FileResourceStream;

public class ExportPage extends LayoutPage {
	private static final long serialVersionUID = 1L;
	private Model<Session> selectedSession = new Model<Session>();
	private Model<Plan> selectedPlan = new Model<Plan>();
	private String type;
	private String ext;
	
	public ExportPage() {
		type=JWTestConfig.getProp("export.type");
		ext="ODS".equals(type)?".ods":"csv";
		
		IModel<File> requirementCVSfileModel = new TmpFileDownloadModel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected File getFile() {
				try {
					if ("ODS".equals(type)){
						return RequirementODSExporter.exportToODS();
					}
					else{
						return RequirementCSVExporter.exportToCSV();
					}
				} catch (JWTestException e) {
					return null;
				}
			}
		};
		add(new DownloadLink("requirementsExportLnk", requirementCVSfileModel, getSession().getCurrentProject().getName() + "_requirements."+ext).add(new ContextImage("exportRequirementsImg", "images/export_requirements.png")));

		IModel<File> testCaseCVSfileModel = new TmpFileDownloadModel() {
			private static final long serialVersionUID = 1L;

			@Override
			protected File getFile() {
				try {
					if ("ODS".equals(type)){
						return TestCaseODSExporter.exportToODS();
					}
					else{
						return TestCaseCSVExporter.exportToCSV();
					}
				} catch (JWTestException e) {
					return null;
				}
			}
		};
		add(new DownloadLink("testcasesExportLnk", testCaseCVSfileModel, getSession().getCurrentProject().getName() + "_testcases."+ext).add(new ContextImage("exportTestcasesImg", "images/export_testcases.png")));
		 
		Form<String> planForm = new Form<String>("planForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				type=JWTestConfig.getProp("export.type");
				ext="ODS".equals(type)?".ods":"csv";
				try {
					final File csvFile ;
					if ("ODS".equals(type)){
						csvFile = PlanODSExporter.exportToODS(selectedPlan.getObject().getId().intValue());
					}
					else{
						csvFile = PlanCSVExporter.exportToCSV(selectedPlan.getObject().getId().intValue());
					}
					
					RequestCycle.get().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(new FileResourceStream(csvFile),JWTestSession.getProject().getName() +"_"+selectedPlan.getObject().getName()+ "_plan."+ext) {
						@Override
						public void detach(IRequestCycle requestCycle) {
							Logger.getLogger(getPageClass()).debug("Temporary file deleted");
							csvFile.delete();
							super.detach(requestCycle);
						}
					});
				} catch (JWTestException e) {
					error("FATAL: Cannot export session");
				}
			}
		};
		
		SqlSessionMapper<SessionMapper> sesMapper = SqlConnection.getSessionMapper(SessionMapper.class);
		PlanMapper planMapper = sesMapper.getSqlSession().getMapper(PlanMapper.class);
		List<Plan> plans = planMapper.getAll(getSession().getCurrentProject().getId().intValue());
		planForm.add(new DropDownChoice<Plan>("planFld", selectedPlan, plans).setRequired(true));
		
		add(planForm);
		
		Form<String> sessionForm = new Form<String>("sessionForm") {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit() {
				type=JWTestConfig.getProp("export.type");
				ext="ODS".equals(type)?".ods":"csv";
				try {
					final File csvFile;
					if ("ODS".equals(type)){
						csvFile = ResultODSExporter.exportToODS(selectedSession.getObject().getId().intValue());
					}
					else{
						csvFile = ResultCSVExporter.exportToCSV(selectedSession.getObject().getId().intValue());
					}
					
					
					RequestCycle.get().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(new FileResourceStream(csvFile),JWTestSession.getProject().getName() + "_"+selectedSession.getObject().getStart_date()+"_sessionresult."+ext) {
						@Override
						public void detach(IRequestCycle requestCycle) {
							Logger.getLogger(getPageClass()).debug("Temporary file deleted");
							csvFile.delete();
							super.detach(requestCycle);
						}
					});
				} catch (JWTestException e) {
					error("FATAL: Cannot export session");
				}
			}
		};
		
		List<Session> sessions = sesMapper.getMapper().getAllByProject(getSession().getCurrentProject().getId());
		sessionForm.add(new DropDownChoice<Session>("sessionFld", selectedSession, sessions).setRequired(true));
		sesMapper.close();
		add(sessionForm);

	}
}
