MYSQL="mysql -utdconfig -ptdconfig@mysql -h192.168.71.76 db_td_config"
games=(1 2 5 6 10 16)
for game in ${games[*]}
do
	sql="update t_gpzs_info set status=1 where game_id=$game and (platform_id!=-1 or zone_id!=-1 or server_id!=-1);"
	$MYSQL -e "$sql"
done
