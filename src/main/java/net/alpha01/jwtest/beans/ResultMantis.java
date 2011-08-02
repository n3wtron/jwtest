package net.alpha01.jwtest.beans;

import java.io.Serializable;
import java.math.BigInteger;

public class ResultMantis implements Serializable {
	private static final long serialVersionUID = 1L;
	private BigInteger id_result;
	private BigInteger id_mantis;

	public ResultMantis(BigInteger id_result, BigInteger id_mantis) {
		super();
		this.id_result = id_result;
		this.id_mantis = id_mantis;
	}

	public ResultMantis() {

	}

	public BigInteger getId_result() {
		return id_result;
	}

	public void setId_result(BigInteger id_result) {
		this.id_result = id_result;
	}

	public BigInteger getId_mantis() {
		return id_mantis;
	}

	public void setId_mantis(BigInteger id_mantis) {
		this.id_mantis = id_mantis;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id_mantis == null) ? 0 : id_mantis.hashCode());
		result = prime * result + ((id_result == null) ? 0 : id_result.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ResultMantis))
			return false;
		ResultMantis other = (ResultMantis) obj;
		if (id_mantis == null) {
			if (other.id_mantis != null)
				return false;
		} else if (!id_mantis.equals(other.id_mantis))
			return false;
		if (id_result == null) {
			if (other.id_result != null)
				return false;
		} else if (!id_result.equals(other.id_result))
			return false;
		return true;
	}

}
