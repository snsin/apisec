create table app.item (
    id bigserial primary key,
    title varchar(255),
    price numeric(9,2),
    user_id bigint,
    foreign key (user_id) references app."user"
                      on delete set null
                      on update cascade
);
