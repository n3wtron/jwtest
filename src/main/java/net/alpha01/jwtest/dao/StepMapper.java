package net.alpha01.jwtest.dao;

import java.math.BigInteger;
import java.util.List;

import net.alpha01.jwtest.beans.Step;

public interface StepMapper {
	Step get(BigInteger bigInteger);
	List<Step> getAll(int idTestCase);
	Integer add(Step step);
	Integer delete(Step key);
	Integer update(Step step);
}
