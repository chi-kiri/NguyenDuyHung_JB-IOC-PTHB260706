create table ticket_bookings(
	booking_id serial primary key,
	movie_title varchar(150) not null,
	customer_name varchar(150) not null,
	show_time timestamp not null,
	booking_date timestamp not null,
	seat_quantity int not null,
	status varchar(30) not null
);

insert into ticket_bookings(booking_id,
	movie_title,
	customer_name,
	show_time,
	booking_date,
	seat_quantity,
	status)
values ('a', 'a a a', '2026-10-05 19:30:00', '2026-10-01 10:00:00', 2, 'Booked')
('b', 'b b b', '2026-10-06 20:00:00', '2026-10-01 11:00:00', 2, 'Booked')
('c', 'c c c', '2026-10-05 19:30:00', '2026-10-02 09:00:00', 2, 'CheckedIn')
('d', 'd d d', '2026-10-07 18:00:00', '2026-10-02 14:00:00', 2, 'Cancelled')

create  or replace function get_all_bookings()
returns setof ticket_bookings
language plpgsql
as $$
begin
	return query
	select * from ticket_bookings order by booking_id;
end;
$$;

create or replace procedure add_booking(
	p_booking_id int,
	p_movie_title varchar,
	p_customer_name varchar,
	p_show_time timestamp,
	p_booking_date timestamp,
	p_seat_quantity int,
	p_status varchar
)
language plpgsql
as $$
begin
	insert into ticket_bookings (booking_id,
	movie_title,
	customer_name,
	show_time,
	booking_date,
	seat_quantity,
	status)
	values (
			p_booking_id,
			p_movie_title,
			p_customer_name,
			p_show_time,
			p_booking_date,
			p_seat_quantity,
			p_status
	);
end;
$$;

create or replace function find_bookings_by_customer(p_movie_title varchar)
returns setof ticket_bookings
language plpgsql
as $$
begin
	return query
	select *
	from ticket_bookings tb
	where tb.movie_title ilike '%' || p_movie_title || '%'
	order by tb.booking_id;
end;
$$;


create or replace procedure update_booking(
	p_booking_id int,
	p_movie_title varchar,
	p_customer_name varchar,
	p_show_time timestamp,
	p_booking_date timestamp,
	p_seat_quantity int,
	p_status varchar
)
language plpgsql
as $$
begin
	update ticket_bookings
	set movie_title = p_movie_title,
		customer_name = p_customer_name,
		show_time = p_show_time,
		booking_date = p_booking_date,
		seat_quantity = p_seat_quantity,
		status = p_status
	where booking_id = p_booking_id;
end;
$$;

create or replace procedure delete_booking(p_booking_id int)
language plpgsql
as $$
begin
	delete from ticket_bookings where booking_id = p_booking_id;
end;
$$;

create or replace function find_bookings_by_movie(p_movie_title varchar)
returns setof ticket_bookings
language plpgsql
as $$
begin
	return query
	select * from ticket_bookings
	where movie_title ilike '%' || p_movie_title || '%'
	order by booking_id;
end;
$$;


