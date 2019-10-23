--20131206 game arpu---
create table if not exists t_game_arpu (
  time int(11) not null,
  game_id int(11) not null,
  user_type int(1) not null,
  pay_rate double default 0.00,
  arpu double default 0.00,
  primary key (time, game_id, user_type)
) default charset=utf8

