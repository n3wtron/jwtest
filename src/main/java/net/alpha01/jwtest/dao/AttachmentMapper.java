package net.alpha01.jwtest.dao;

import java.math.BigInteger;
import java.util.List;

import net.alpha01.jwtest.beans.Attachment;

public interface AttachmentMapper {
	class AttachmentAssociation{
		private BigInteger idAttachment;
		private BigInteger idAssociation;
		public AttachmentAssociation(){
			
		}
		public AttachmentAssociation(BigInteger idAttachment, BigInteger idAssociation) {
			super();
			this.idAttachment = idAttachment;
			this.idAssociation = idAssociation;
		}
		public BigInteger getIdAttachment() {
			return idAttachment;
		}
		public void setIdAttachment(BigInteger idAttachment) {
			this.idAttachment = idAttachment;
		}
		public BigInteger getIdAssociation() {
			return idAssociation;
		}
		public void setIdAssociation(BigInteger idAssociation) {
			this.idAssociation = idAssociation;
		}
	}
	
	
	Attachment get(BigInteger bigInteger);
	Integer add(Attachment attachment);
	Integer delete(Attachment attachment);
	Integer update(Attachment attachment);
	
	Integer associateRequirement(AttachmentAssociation association);
	Integer associateTestCase(AttachmentAssociation association);
	Integer associateResult(AttachmentAssociation association);
	List<Attachment> getByRequirement(BigInteger idRequirement);
	List<Attachment> getByTestcase(BigInteger idTestcase);
	List<Attachment> getByResult(BigInteger idResult);
	
}
