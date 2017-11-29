dir=$1

for file in $dir/*; do

base=$(basename $file)

echo $base

if [[ -f $file ]]; then

	echo 'processing ' $base

	curl -L -i -X PUT -T $file "127.0.0.1:5598/webhdfs/v1/finished/$base?op=CREATE&overwrite=true&blocksize=500&rf=1&noindirect=true"

	mvn exec:java -Dexec.mainClass="com.datastax.tika.ProcessFile"  -DcontactPoints=localhost -Dexec.args="$file"
	
fi	

done;
