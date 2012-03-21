
hg clone $1 temp_$3
if [ $? -ne 0 ]; then
        exit 2;
fi


hg log temp_$3>>$2;
if [ $? -ne 0 ]; then
        exit 5;
fi

rm -r temp_$3;
if [ $? -ne 0 ]; then
        exit 99;
fi
exit 0;
