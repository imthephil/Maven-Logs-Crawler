
./get_cvs_log_loggin.sh $1
if [ $? -ne 0 ]; then 
	exit 2; 
fi

cvs -d $1 checkout -P $3;
if [ $? -ne 0 ]; then
        exit 4;
fi

cvs -d $1 log>>$2
if [ $? -ne 0 ]; then
        exit 5;
fi

