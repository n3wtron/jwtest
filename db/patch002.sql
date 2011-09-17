alter table profiles add column id_project int  not null;
alter table profiles add constraint fk_profiles_projects foreign key(id_project) references projects(id) on delete cascade;
