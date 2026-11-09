-- Run once on an existing MySQL database before deploying this version.
UPDATE accounts SET role_id = 2 WHERE role_id = 3;
DELETE FROM roles WHERE id = 3 AND role_name = 'COUNCIL';

ALTER TABLE councils ADD COLUMN status varchar(20) NOT NULL DEFAULT 'ACTIVE';
ALTER TABLE papers ADD COLUMN rejection_reason varchar(500) NULL;
ALTER TABLE projects ADD COLUMN rejection_reason varchar(500) NULL;

ALTER TABLE council_evaluations DROP INDEX project_id;
ALTER TABLE council_evaluations ADD COLUMN evaluator_id int NULL AFTER project_id;
UPDATE council_evaluations ce
SET evaluator_id = (SELECT MIN(cm.lecturer_id) FROM council_members cm WHERE cm.council_id = ce.council_id)
WHERE evaluator_id IS NULL;
ALTER TABLE council_evaluations MODIFY evaluator_id int NOT NULL;
ALTER TABLE council_evaluations ADD CONSTRAINT fk_evaluation_evaluator
  FOREIGN KEY (evaluator_id) REFERENCES lecturers(id);
ALTER TABLE council_evaluations ADD UNIQUE KEY uk_evaluation_member (council_id, project_id, evaluator_id);
