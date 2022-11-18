create table album_photos
(
    album_id bigint not null,
    assignment_ts timestamp default CURRENT_TIMESTAMP,
    photo_id bigint not null,
    position integer not null,

    constraint pk_album_photos primary key (album_id, position)
);

create table albums
(
    id bigint not null,
    version integer not null,
    creation_ts timestamp default CURRENT_TIMESTAMP,
    album_name varchar(64) not null,
    restricted boolean not null default false,

    constraint pk_albums primary key (id)
);

create table countries
(
    id bigint not null,
    version int not null,
    iso2code varchar(2) not null,
    name varchar(64),

    constraint pk_countries primary key (id)
);

create table persons
(
    id bigint not null,
    version integer not null,
    first_name varchar(64) not null,
    last_name varchar(64) not null,
    user_name varchar(255) not null,
    nick_name varchar(64) not null,

    constraint pk_persons primary key (id),
    constraint uk_persons_nick_name unique (nick_name)
);

create table photographer_emails
(
    photographer_id bigint not null,
    email_address varchar(255) not null,
    email_type char(1) constraint ck_email_type check (email_type in ('B', 'P'))
);

create table photographers
(
    id bigint not null,
    version integer not null,
    first_name varchar(64) not null,
    last_name varchar(64) not null,
    user_name varchar(255) not null,
    billing_city varchar(64),
    billing_street_number varchar(64),
    billing_zip_code varchar(16),
    business_area_code integer
        constraint ck_business_area_code
        check (business_area_code between 0 and 9999),
    business_country_code integer
        constraint ck_business_country_code
        check (business_country_code between 0 and 999),
    business_serial_number varchar(16),
    mobile_area_code integer
        constraint ck_mobile_area_code
        check (mobile_area_code between 0 and 9999),
    mobile_country_code integer
        constraint ck_mobile_country_code
        check (mobile_country_code between 0 and 999),
    mobile_serial_number varchar(16),
    studio_city varchar(64),
    studio_street_number varchar(64),
    studio_zip_code varchar(16),
    billing_country_id bigint,
    studio_country_id bigint,

    constraint pk_photographers primary key (id)
);

create table photos
(
    id bigint not null,
    version integer not null,
    creation_ts timestamp not null default CURRENT_TIMESTAMP,
    file_name varchar(64) not null,
    height integer not null check (height between 0 and 32767),
    photo_name varchar(64) not null,
    orientation varchar(1) not null,
    width integer not null check (width between 0 and 32767),
    photographer_id bigint,

    constraint pk_photos primary key (id)
);

create table tagged_persons
(
    id bigint not null,
    person_id bigint not null,
    photo_id bigint not null,
    rating integer,

    constraint pk_tagged_persons primary key (id)
);

alter table album_photos
add constraint fk_album_photos_2_photos foreign key (photo_id) references photos;

alter table album_photos
add constraint fk_album_photos_2_album foreign key (album_id) references albums;

alter table photographer_emails
add constraint fk_photographer_emails foreign key (photographer_id) references photographers;

alter table photographers
add constraint fk_photographers_billing_country foreign key (billing_country_id) references countries;

comment on constraint fk_photographers_billing_country on photographers is 'billing country';

alter table photographers
add constraint fk_photographers_studio_country foreign key (studio_country_id) references countries;

comment on constraint fk_photographers_studio_country on photographers is 'studio country';

alter table photos
add constraint fk_photo_photographer foreign key (photographer_id) references photographers;

alter table tagged_persons
add constraint fk_tagged_persons_2_person foreign key (person_id) references persons;

alter table tagged_persons
add constraint fk_tagged_persons_2_photo foreign key (photo_id) references photos;

create index ix_photos_photo_name on photos(photo_name);

create sequence albums_seq start with 1 increment by 50;

create sequence countries_seq start with 1 increment by 50;

create sequence persons_seq start with 1 increment by 50;

create sequence photographers_seq start with 1 increment by 50;

create sequence photos_seq start with 1 increment by 50;

create sequence tagged_persons_seq start with 1 increment by 50;

create view photo_details as
select
    photos.id,
    photos.photo_name,
    photos.height,
    photos.width,
    photographer.first_name || ' ' || photographer.last_name as photographer_name,
    (
        select count(*)
        from album_photos
        where album_photos.photo_id = photos.id
    ) as album_occurrences,
    (
        select coalesce(array_agg(persons.first_name || ' ' || persons.last_name), array[]::varchar[])
        from tagged_persons
        join persons
        on tagged_persons.person_id = persons.id
        where tagged_persons.photo_id = photos.id
    ) as tagged_people,
    (
        select round(avg(tagged_persons.rating), 2)
        from tagged_persons
        join persons
        on tagged_persons.person_id = persons.id
        where tagged_persons.photo_id = photos.id
    ) as average_rating
from photos
join photographers as photographer
on photos.photographer_id = photographer.id;

-- [jooq ignore start]
-- Ignore this function when using jOOQ's DDLDatabase generator, since parsing stored procedures is a pro feature.
--
-- This will still be picked up when using Flyway + Testcontainers for generation,
-- because there, the jOOQ parser is not used.
create or replace function rating_statistics_for_person(
    in the_person_id persons.id%type,
    out avg_rating_in_good_photos integer,
    out number_of_good_photos integer
)
returns record
as $$
begin
    with photos_where_person_is_tagged (photo_id, person_rating) as (
        select tagged_persons.photo_id, tagged_persons.rating
        from tagged_persons
        where tagged_persons.person_id = the_person_id
    ),
    ratings_of_photos_where_person_is_tagged (photo_id, person_rating, avg_rating) as (
        select
            p.photo_id,
            p.person_rating,
            avg(tagged_persons.rating)
        from tagged_persons
        join photos_where_person_is_tagged as p
        on p.photo_id = tagged_persons.photo_id
        group by p.photo_id, p.person_rating
    )
    select avg(ratings.person_rating), count(ratings.person_rating)
    into avg_rating_in_good_photos, number_of_good_photos
    from ratings_of_photos_where_person_is_tagged as ratings
    where ratings.avg_rating >= 6;
end
$$ language plpgsql;
-- [jooq ignore stop]
