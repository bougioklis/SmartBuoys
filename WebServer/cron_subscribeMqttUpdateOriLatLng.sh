#!/bin/sh
mypid=$$
ps axuww | grep -v $mypid |  grep -v grep | grep crononce > /dev/null 2>&1
if [ $? -ne 0 ] ; then
	php -f subscribeMQTT.php &
	#php -f subscribeMqttUpdateOriLatLng.php &
else
	echo Already running
fi

