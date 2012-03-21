
git clone $1 temp$3; 
if [ $? -ne 0 ]; then
	exit 2;
fi

cd temp$3;
if [ $? -ne 0 ]; then
	exit 8; 
fi

git log -p  >>../$2;
if [ $? -ne 0 ]
then
	exit 5;
fi

cd ..;
if [ $? -ne 0 ]; then
	exit 8; 
fi

rm -rf temp$3;
if [ $? -ne 0 ]; then
	exit 8;
fi
exit 0; 
