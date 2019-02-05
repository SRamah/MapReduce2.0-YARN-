#!/bin/bash
echo "`hostname` : I AM A Container $1" >> /media/psf/Bureau/Yarn/Yarn-Hadoop-resultat/check.txt


for (( i=$2; i <= $3; ++i))
do
   nb = "$i"
   for (( j=$2; j <= $3; ++j))
   do
     nb = "$nb$j";
     echo "$nb">> /media/psf/Bureau/Yarn/Yarn-Hadoop-resultat/check.txt
   done
done


