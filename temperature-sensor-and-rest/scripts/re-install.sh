#!/usr/bin/env bash

PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin
INSTALL_PATH=/usr/local/heating-controller/

. /lib/lsb/init-function

check_if_running_as_root() {
    if [ "$EUID" -ne 0 ]
      then echo "Please run as root with SUDO or something"
      exit -1
    fi
}


remove_old_installation() {
    echo "Removing old installation ..."
    if [ -f /etc/init.d/heating-controller ]; then
        echo "Removing service files"
        service stop heating-controller
        update-rc.d heating-controller remove
        rm -rf /etc/init.d/heating-controller
    fi

    if [ -d $INSTALL_PATH ]; then
        echo "Removing binaries"
        rm -rf $INSTALL_PATH
    fi

}

install() {
    echo "Installing new files ..."

    mkdir -p $INSTALL_PATH

    cp temperature-sensor-and-rest*.jar $INSTALL_PATH
    cp prod.json $INSTALL_PATH/prod.json
    cp schedule.json $INSTALL_PATH
    cp heating-controller /etc/init.d/

    chown root.root $INSTALL_PATH/ -R
    chmod 755 /etc/init.d/heating-controller && chown root.root /etc/init.d/heating-controller

    update-rc.d heating-controller defaults
    service start heating-controller
}

check_if_running_as_root

remove_old_installation

install