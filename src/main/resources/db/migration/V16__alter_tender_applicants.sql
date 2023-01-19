update tender_applicants set application_status = 'QUALIFIED' where application_status='SHORTLISTED';
update tender_applicants set application_status = 'NOT_QUALIFIED' where application_status='NOT_SHORTLISTED';
update tender_info set workflow_step = 'QUALIFIED' where workflow_step='SHORTLISTED';
update tender_info set workflow_step = 'NOT_QUALIFIED' where workflow_step='NOT_SHORTLISTED';