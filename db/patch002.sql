alter table testcases add column version int default 0, drop primary key,add primary key (id,version);
alter table results add column testcase_version int default 0;
alter table results add CONSTRAINT fk_results_testcase_versioned FOREIGN KEY (id_testcase,testcase_version) REFERENCES testcases (id,version) ON DELETE CASCADE ON UPDATE CASCADE;
alter table results drop foreign key fk_results_testcase;
