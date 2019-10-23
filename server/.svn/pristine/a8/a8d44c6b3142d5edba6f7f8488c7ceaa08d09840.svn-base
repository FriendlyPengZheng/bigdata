WORKDIR=`dirname $0`
WORKDIR=`cd $WORKDIR && pwd`
cd $WORKDIR
echo workdir = $WORKDIR

source config.sh

path=$1

if [[ $path == "" ]]; then
    echo invalid param: path
    exit
fi

path=${path##hdfs://192.168.11.128}
echo "process $path"

HADOOP=${HADOOP_PATH}hadoop

$HADOOP fs -test -e $path
if [[ $? -ne 0 ]]; then
	echo $path not exist
	exit;
fi
$HADOOP fs -rmr -skipTrash $path/_*

if [[ $2 == "" ]]; then
	for type in `$HADOOP fs -ls $path | grep -v Found | awk '{print $8}' | awk -F'/' '{print $NF}' | awk -F '-' '{print $1}' | sort | uniq`
	do
		size=`$HADOOP fs -du $path/$type-* | grep -v Found | awk '{sum+=$1/1024} END {printf "%f\n",sum}'`
		if [[ `awk -v size=$size 'BEGIN {print (size<=100?1:0)}'` -eq 1 && \
			`$HADOOP fs -ls $path/$type-* | grep -v Found | wc -l` -lt 4 ]]; then
			echo "ignore $path/$type-*"
			continue;
		fi
		if [[ `awk -v size=$size 'BEGIN {print (size<=262144?1:0)}'` -eq 1 ]]; then #小于256M的文件用gzip压缩，并合并成1个文件
			cmp="-D mapred.output.compression.codec=org.apache.hadoop.io.compress.GzipCodec -D mapred.reduce.tasks=1"
		else #大于256M的文件，用bz2压缩，并根据大小控制reducer数量
			size=`echo $size | awk -F'\.' '{print $1}'`
			n=`expr \( $size - 1 \) \/ 262144 + 1`
			if [[ $n -lt 16 ]]; then
				cmp="-D mapred.output.compression.codec=org.apache.hadoop.io.compress.BZip2Codec -D mapred.reduce.tasks=$n"
			else
				cmp="-D mapred.output.compression.codec=org.apache.hadoop.io.compress.BZip2Codec"
			fi
		fi
		#echo $cmp
		if [[ $type"x" == "partx" ]]; then
			$HADOOP jar ${HADOOP_JAR_PATH} compression.SimpleJobDriver \
				-conf ${HADOOP_CONF} \
				-D mapred.output.compress=true \
				$cmp \
				-jobName "compress $path/$type-*" \
				-input  $path/$type-* \
				-output $OUTPUT/$path
		else
			$HADOOP jar ${HADOOP_JAR_PATH} compression.SimpleJobDriver \
				-conf ${HADOOP_CONF} \
				-D type=$type \
				-D mapred.output.compress=true \
				$cmp \
				-jobName "compress $path/$type-*" \
				-input  $path/$type-* \
				-output $OUTPUT/$path \
				-addMos $type
		fi
		if [[ $? == 0 ]]; then
			$HADOOP fs -rm -skipTrash $path/$type-*
			$HADOOP fs -mv $OUTPUT/$path/$type-* $path/
		fi
	done
else #所有文件合并在一起
	#-D mapred.output.compress=true \
	#-D mapred.output.compression.codec=org.apache.hadoop.io.compress.BZip2Codec \
	#-D mapred.reduce.tasks=30 \
	size=`$HADOOP fs -du $path/* | grep -v Found | awk '{sum+=$1/1024} END {printf "%f\n",sum}'`
	if [[ `awk -v size=$size 'BEGIN {print (size<=262144?1:0)}'` -eq 1 ]]; then #小于256M的文件用gzip压缩，并合并成1个文件
		cmp="-D mapred.output.compression.codec=org.apache.hadoop.io.compress.GzipCodec -D mapred.reduce.tasks=1"
	else #大于256M的文件，用bz2压缩，并根据大小控制reducer数量
		size=`echo $size | awk -F'\.' '{print $1}'`
		n=`expr \( $size - 1 \) \/ 262144 + 1`
		if [[ $n -lt 16 ]]; then
			cmp="-D mapred.output.compression.codec=org.apache.hadoop.io.compress.BZip2Codec -D mapred.reduce.tasks=$n"
		else
			cmp="-D mapred.output.compression.codec=org.apache.hadoop.io.compress.BZip2Codec"
		fi
	fi
	$HADOOP jar ${HADOOP_JAR_PATH} compression.SimpleJobDriver \
		-conf ${HADOOP_CONF} \
		-D mapred.output.compress=true \
		$cmp \
		-jobName "compress $path" \
		-input  $path \
		-output $OUTPUT/$path
	if [[ $? == 0 ]]; then
		$HADOOP fs -rm -skipTrash $path/*
		$hadoop fs -mv $OUTPUT/$path/part* $path/
	fi
fi
$HADOOP fs -rmr -skipTrash $OUTPUT/$path/
