bash bundle.sh

if [ "$#" -eq 2 ]
then
	echo "Running Master"
	java -cp src/migrate.jar proj1/Main $1 $2 
elif [ "$#" -eq 3 ]
then
	echo "Running worker"
	java -cp src/migrate.jar proj1/Main $1 $2 $3
else
	echo "Invalid number of arguments supplied- read the documentation for detail"
fi
	