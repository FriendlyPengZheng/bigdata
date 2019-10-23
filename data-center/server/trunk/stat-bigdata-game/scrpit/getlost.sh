date=$1
HADOOP='/opt/taomee/hadoop/hadoop/bin/hadoop'
$HADOOP fs -text /bigdata/output/day/$date/active-lost-14/p*  | grep '^16'  | grep '14$' | awk '{print $5}' | sed 's/--1//g' > getlost/$date
