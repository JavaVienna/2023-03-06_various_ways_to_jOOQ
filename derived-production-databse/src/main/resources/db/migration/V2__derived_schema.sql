create table categories(
    id bigint not null,
    category_name varchar(64) not null,

    constraint pk_categories primary key (id)
);

create table photo_categories(
     id bigint not null,
     category_id bigint not null,
     photo_id bigint not null,

     constraint pk_photo_categories primary key (id)
);

alter table photo_categories
add constraint fk_photo_categories_2_categories foreign key (category_id) references categories;

alter table photo_categories
add constraint fk_photo_categories_2_photos foreign key (photo_id) references photos;

alter table persons
rename column user_name to user_email;
