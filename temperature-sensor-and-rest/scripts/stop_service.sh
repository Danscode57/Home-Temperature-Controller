#!/bin/bash


if [ -f service.pid ]; then
    echo "Seems that it is running, killing ..."
    OLD_PID=`cat service.pid`
    kill -9 $OLD_PID
    rm -rf service.pid
else
    echo "No Pid, nothing running, I suppose"
fi

exit 0