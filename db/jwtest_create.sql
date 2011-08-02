drop database jwtest;
create database jwtest character set = utf8;

use jwtest; 

create table roles (id int primary key auto_increment, name varchar(255) unique)TYPE=INNODB;

create table users (id int primary key auto_increment, username varchar(255) not null unique, password varchar(255), email varchar(255),name varchar(255),ldap bool default 0)TYPE=INNODB;

create table users_roles(id_user int not null, id_role int not null, primary key(id_user,id_role), constraint fk_users_roles_users foreign key (id_user) references users(id) on delete cascade on update cascade, constraint fk_users_roles_roles foreign key(id_role) references roles(id) on delete cascade on update cascade)TYPE=INNODB;

create table projects (id int primary key auto_increment, name varchar(255) unique,description text, mantis_url varchar(255)) TYPE=INNODB;

create table projects_users_roles(id_project int not null, id_user int not null, id_role int not null, primary key(id_project,id_user,id_role),constraint fk_projects_users_roles_users foreign key (id_user) references users(id) on delete cascade on update cascade, constraint fk_projects_users_roles_projects foreign key (id_project) references projects(id) on delete cascade on update cascade, constraint fk_projects_users_roles_roles foreign key(id_role) references roles(id) on delete cascade on update cascade)TYPE=INNODB;

create table projectversions (id_project int not null, version varchar(255) not null,released timestamp null, primary key(id_project,version), constraint fk_projectversions_projects foreign key (id_project) references projects(id) on delete cascade on update cascade) TYPE=INNODB;

create table requirementstype (id int primary key auto_increment, name varchar(255) unique, description text) TYPE=INNODB;

create table requirements (id int primary key auto_increment, id_project int not null, id_type int, num int not null, name varchar(255),description text, unique(id_project,id_type,num), constraint fk_requirements_projects foreign key (id_project) references projects(id) on delete cascade on update cascade, constraint fk_requirements_requirementstype foreign key(id_type) references requirementstype(id) on delete cascade on update cascade) TYPE=INNODB;

create table testcases(id int primary key auto_increment, id_requirement int not null, name varchar(255), description text, expected_result text, constraint fk_testcases_requirements foreign key(id_requirement) references requirements(id) on delete cascade on update cascade) TYPE=INNODB;

create table steps (id int primary key auto_increment, id_testcase int not null, description text, expected_result text, failed_result text,sequence varchar(255) not null default 0, constraint fk_steps_testcases foreign key (id_testcase) references  testcases(id) on delete cascade on update cascade) TYPE=INNODB;

create table plans (id int primary key auto_increment, id_project int not null, name varchar(255),creation_date timestamp, constraint fk_plans_projects foreign key (id_project) references projects(id) on delete cascade on update cascade) TYPE=INNODB;

create table plans_testcases (id_plan int not null, id_testcase int not null, primary key(id_plan, id_testcase), constraint fk_plans_testcases_plans foreign key (id_plan) references plans(id) on delete cascade on update cascade, constraint fk_plans_testcases_testcase foreign key(id_testcase) references testcases(id) on delete cascade on update cascade) TYPE=INNODB;

create table sessions (id int primary key auto_increment, id_plan int not null,id_user int,version varchar(255), start_date timestamp not null default CURRENT_TIMESTAMP, end_date timestamp, constraint fk_sessions_plans foreign key(id_plan) references plans(id) on delete cascade on update cascade, constraint fk_sessions_users foreign key(id_user) references users(id) on update cascade on delete set null ) TYPE=INNODB;

create table results (id int primary key auto_increment,id_parent int,  id_session int not null, id_testcase int not null,success bool,insert_date timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP, note text,recycles int default 0, constraint fk_results_sessions foreign key (id_session) references sessions(id) on delete cascade on update cascade, constraint fk_results_testcase foreign key(id_testcase) references testcases(id) on delete cascade on update cascade) TYPE=INNODB;

create table requirementsdependency (id_requirement int not null, id_dependency int not null, constraint fk_requirementsdependency_requiremens1 foreign key (id_requirement) references requirements(id) on delete cascade on update cascade, constraint fk_requirementsdependency_requirements2 foreign key (id_dependency) references requirements(id) on delete cascade on update cascade) TYPE=INNODB;

create table testcasesdependency (id_testcase int not null, id_dependency int not null, constraint fk_testcasesdependency_testcases1 foreign key (id_testcase) references testcases(id) on delete cascade on update cascade, constraint fk_testcasesdependency_testcases2 foreign key (id_dependency) references testcases(id) on delete cascade on update cascade) TYPE=INNODB;

create table attachments (id int primary key auto_increment, id_project int not null, name varchar(255) not null, description text,extension varchar(10) not null, constraint fk_attachments_projects foreign key(id_project) references projects(id) on delete cascade on update cascade) type=INNODB;

create table attachments_requirements (id_attachment int not null, id_requirement int not null,primary key(id_attachment,id_requirement), constraint fk_attachments_requirements_attachments foreign key(id_attachment) references attachments(id) on delete cascade on update cascade, constraint fk_attachments_requirements_requirements foreign key(id_requirement) references requirements(id) on delete cascade on update cascade) type=INNODB;

create table attachments_testcases (id_attachment int not null, id_testcase int not null,primary key(id_attachment,id_testcase), constraint fk_attachments_testcases_attachments foreign key(id_attachment) references attachments(id) on delete cascade on update cascade, constraint fk_attachments_testcases_testcases foreign key(id_testcase) references testcases(id) on delete cascade on update cascade) type=INNODB;

create table attachments_results (id_attachment int not null, id_result int not null,primary key(id_attachment,id_result), constraint fk_attachments_results_attachments foreign key(id_attachment) references attachments(id) on delete cascade on update cascade, constraint fk_attachments_results_results foreign key(id_result) references results(id) on delete cascade on update cascade) type=INNODB;

create table results_mantis (id_result int4 not null, id_mantis int not null,primary key(id_result,id_mantis), constraint fk_results_mantis_results foreign key (id_result) references results(id) on delete cascade)type=INNODB;