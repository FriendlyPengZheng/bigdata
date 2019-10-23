#/bin/sh

logfile="./restart.log"
is_alive=`ps -ef | grep -v grep | grep "StatWriter" | wc -l`
if [ $is_alive -eq 0 ]; then
		echo "-----------------------------------------------------------------------" >> $logfile
		echo `date` "---- restarting StatWriter ----" >> $logfile
		./startup.sh
		echo `date` "---- StatWriter restarted ----" >> $logfile
fi
