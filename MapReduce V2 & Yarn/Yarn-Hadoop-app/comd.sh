#!/bin/bash
echo "`hostname` : I AM A Container $1" >> /media/psf/Bureau/Yarn/Yarn-Hadoop-resultat/check.txt
for (( i=$2; i <= $3; ++i))
	do
	for (( j=0; j <= 9; ++j))
	   do
	     echo "$i$j" >> /media/psf/Bureau/Yarn/Yarn-Hadoop-resultat/"`hostname`-$1".txt;
	done
done