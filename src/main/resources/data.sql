drop table if exists extracted_log;

create table extracted_log
(
    id  bigserial PRIMARY KEY,
    log VARCHAR(1000) NOT NULL
);


