export LANG=en_US.UTF-8
WORKDIR=`dirname $0`
WORKDIR=`cd $WORKDIR && pwd`
cd $WORKDIR
echo workdir = $WORKDIR

source config.sh

#for d in {20131108..20131130}
#do
#	php migrate.php $d | grep -v 'UCOUNT[[:space:]]16[[:space:]]-1[[:space:]]-1[[:space:]]-1[[:space:]]精灵获得' | grep -v 'UCOUNT[[:space:]]16[[:space:]]-1[[:space:]]-1[[:space:]]-1[[:space:]]精灵出战' > $d
#done
#for d in {20131201..20131212}
#do
#	php migrate.php $d | grep -v 'UCOUNT[[:space:]]16[[:space:]]-1[[:space:]]-1[[:space:]]-1[[:space:]]精灵获得' | grep -v 'UCOUNT[[:space:]]16[[:space:]]-1[[:space:]]-1[[:space:]]-1[[:space:]]精灵出战' > $d
#done
#for d in {20131213..20131231}
#do
#	php migrate.php $d | grep -v 'UCOUNT[[:space:]]16[[:space:]]-1[[:space:]]-1[[:space:]]-1[[:space:]]精灵获得' | grep -v 'UCOUNT[[:space:]]16[[:space:]]-1[[:space:]]-1[[:space:]]-1[[:space:]]精灵出战' > $d
#done
#for d in {20140101..20140131}
#do
#	php migrate.php $d | grep -v 'UCOUNT[[:space:]]16[[:space:]]-1[[:space:]]-1[[:space:]]-1[[:space:]]精灵获得' | grep -v 'UCOUNT[[:space:]]16[[:space:]]-1[[:space:]]-1[[:space:]]-1[[:space:]]精灵出战' > $d
#done
#for d in {20140201..20140225}
#do
#	php migrate.php $d | grep -v 'UCOUNT[[:space:]]16[[:space:]]-1[[:space:]]-1[[:space:]]-1[[:space:]]精灵获得' | grep -v 'UCOUNT[[:space:]]16[[:space:]]-1[[:space:]]-1[[:space:]]-1[[:space:]]精灵出战' > $d
#done

##upload by db_server
#for d in {20131108..20131130}
#do
#	$DB_UPLOAD -type 2 -date $d -path /bigdata/migrate/cplan/$d
#done
#for d in {20131201..20131231}
#do
#	$DB_UPLOAD -type 2 -date $d -path /bigdata/migrate/cplan/$d
#done
#for d in {20140101..20140131}
#do
#	$DB_UPLOAD -type 2 -date $d -path /bigdata/migrate/cplan/$d
#done
#for d in {20140201..20140225}
#do
#	$DB_UPLOAD -type 2 -date $d -path /bigdata/migrate/cplan/$d
#done

d=$1
if [[ $d == "" ]]; then
	d=`date -d "-1 day" +%Y%m%d`
fi
php migrate.php $d | grep -v 'UCOUNT[[:space:]]16[[:space:]]-1[[:space:]]-1[[:space:]]-1[[:space:]]精灵获得' | grep -v 'UCOUNT[[:space:]]16[[:space:]]-1[[:space:]]-1[[:space:]]-1[[:space:]]精灵出战' > $d
/opt/taomee/hadoop/hadoop/bin/hadoop fs -copyFromLocal $d /bigdata/migrate/cplan/$d
$DB_UPLOAD -type 2 -date $d -path /bigdata/migrate/cplan/$d
