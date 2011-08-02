package net.alpha01.jwtest.panels.attachment;

import java.io.File;
import java.io.IOException;

import net.alpha01.jwtest.JWTestSession;
import net.alpha01.jwtest.beans.Attachment;
import net.alpha01.jwtest.beans.IdBean;
import net.alpha01.jwtest.beans.Project;
import net.alpha01.jwtest.beans.Requirement;
import net.alpha01.jwtest.beans.Result;
import net.alpha01.jwtest.beans.TestCase;
import net.alpha01.jwtest.dao.AttachmentMapper;
import net.alpha01.jwtest.dao.AttachmentMapper.AttachmentAssociation;
import net.alpha01.jwtest.dao.SqlConnection;
import net.alpha01.jwtest.dao.SqlSessionMapper;
import net.alpha01.jwtest.exceptions.JWTestException;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.lang.Bytes;

public class AddAttachmentPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private Project prj = JWTestSession.getProject();
	private Attachment attachment = new Attachment(prj);
	private FileUploadField fileFld;
	private Requirement req;
	private TestCase test;
	private Result res;

	public AddAttachmentPanel(String id, Requirement req) {
		super(id);
		this.req = req;
		createUploadForm();
	}

	public AddAttachmentPanel(String id, TestCase test) {
		super(id);
		this.test = test;
		createUploadForm();
	}

	public AddAttachmentPanel(String id, Result res) {
		super(id);
		this.res = res;
		createUploadForm();
	}

	private void createUploadForm() {
		Form<String> addForm = new Form<String>("addForm") {
			private static final long serialVersionUID = 1L;

			protected void onSubmit() {
				FileUpload fileUpload = fileFld.getFileUpload();
				IdBean bean=null;
				if (res!=null){
					bean=res;
				}
				if (test!=null){
					bean=test;
				}
				if (req!=null){
					bean=req;
				}
				if (bean==null){
					error("association failed element cannot be null");
					return;
				}
				try {
					uploadAttachment(fileUpload, attachment, bean);
					info("File added");
					attachment.setId(null);
					attachment.setDescription(null);
					attachment.setName(null);
					this.clearInput();
				} catch (JWTestException e) {
					error(e.getMessage());
				}
			};
		};
		addForm.add(new TextField<String>("descriptionFld", new PropertyModel<String>(attachment, "description")));
		addForm.add(fileFld = new FileUploadField("fileFld"));
		fileFld.setRequired(true);
		addForm.setMultiPart(true);
		addForm.setMaxSize(Bytes.megabytes(2));
		addForm.add(new FeedbackPanel("fbPanel"));
		add(addForm);
		
	}
	
	
	
	public static void uploadAttachment(FileUpload fileUpload,Attachment attachment,IdBean bean) throws JWTestException{
		boolean fileError = false;
		boolean sqlError = false;
		SqlSessionMapper<AttachmentMapper> sesMapper = SqlConnection.getSessionMapper(AttachmentMapper.class);
		int lstPointPos = fileUpload.getClientFileName().lastIndexOf('.');
		if (lstPointPos > 0) {
			attachment.setExtension(fileUpload.getClientFileName().substring(lstPointPos + 1));
		}
		attachment.setName(fileUpload.getClientFileName());
		try {
			if (sesMapper.getMapper().add(attachment).equals(1)) {
				File attachmentFile = new File(AttachmentUtil.getAttachmentDir(), attachment.getId() + "." + attachment.getExtension());
				if (!attachmentFile.createNewFile()) {
					fileError = true;
				} else {
					fileUpload.writeTo(attachmentFile);
					if(bean instanceof Requirement){
						//associate to requirement
						sqlError = !sesMapper.getMapper().associateRequirement(new AttachmentAssociation(attachment.getId(), bean.getId())).equals(1);
					}
					if(bean instanceof TestCase){
						//associate to testcase
						sqlError = !sesMapper.getMapper().associateTestCase(new AttachmentAssociation(attachment.getId(), bean.getId())).equals(1);
					}
					if (bean instanceof Result){
						//associate to result
						sqlError = !sesMapper.getMapper().associateResult(new AttachmentAssociation(attachment.getId(), bean.getId())).equals(1);
					}
					if (sqlError){
						sesMapper.rollback();
						throw new JWTestException(AddAttachmentPanel.class,"SQL Error associating attachment");
					}
					sesMapper.commit();
				}
				if (fileError){
					sesMapper.rollback();
					throw new JWTestException(AddAttachmentPanel.class,"File creation error");
				}
			} else {
				sesMapper.rollback();
				throw new JWTestException(AddAttachmentPanel.class,"SQL Error");
			}
		} catch (IOException e) {
			sesMapper.rollback();
			throw new JWTestException(AddAttachmentPanel.class,e);
		} finally {
			sesMapper.close();
		}
	}

}
