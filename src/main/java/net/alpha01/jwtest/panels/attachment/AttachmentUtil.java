package net.alpha01.jwtest.panels.attachment;

import java.io.File;

import net.alpha01.jwtest.beans.Attachment;
import net.alpha01.jwtest.util.JWTestConfig;

public class AttachmentUtil {
	public static String getAttachmentDir() {
		File attachmentDir = new File(JWTestConfig.getProp("attachment.directory"));
		if (!attachmentDir.exists()) {
			attachmentDir.mkdirs();
		}
		return attachmentDir.getAbsolutePath();
	}
	
	public static File getFile(Attachment attachment){
		return new File(getAttachmentDir()+File.separator+attachment.getId().toString()+"."+attachment.getExtension());
	}

	public static void remove(Attachment attachment) {
		getFile(attachment).delete();
	}
}
