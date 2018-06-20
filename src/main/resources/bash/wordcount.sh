#! /bin/bash

# source function library
#. /etc/rc.d/init.d/functions

DIALUP_PID=$GAME_X_HOME/bin/wordcount.pid
start()
{
    echo -n $"Starting $prog: "
    echo "Storm Stats Service check ..."
    
    if [ "$JAVA_HOME" = "" ]; then
  		echo "Error: JAVA_HOME is not set."
  		exit 1
	fi
	
	if [ "$GAME_X_HOME" = "" ]; then
  		echo "Error: GAME_X_HOME is not set."
  		exit 1
	fi

	bin=`dirname "$0"`
	export GAME_X_HOME=`cd $bin/../; pwd`

	GAME_X_HOME_CONF_DIR=$GAME_X_HOME/conf
	CLASSPATH="${GAME_X_HOME_CONF_DIR}"

	for f in $GAME_X_HOME/lib/*.jar; do
  		CLASSPATH=${CLASSPATH}:$f;
	done

	LOG_DIR=${GAME_X_HOME}/logs
	
	cd ${GAME_X_HOME}

	CLASS=org.smartloli.game.x.m.book._2_3.WordCountHA
	nohup ${JAVA_HOME}/bin/java -classpath "$CLASSPATH" $CLASS > ${LOG_DIR}/game-x-m.out 2>&1 < /dev/null & new_agent_pid=$!
	echo "$new_agent_pid" > $DIALUP_PID
    echo "Storm app service has started success!"
}

stop()
{

	 if [ -f $GAME_X_HOME/bin/wordcount.pid ];then
                    SPID=`cat $GAME_X_HOME/bin/wordcount.pid`
					  if [ "$SPID" != "" ];then
						 kill -9  $SPID

						 echo  > $DIALUP_PID
						 echo "stop success"
					  fi
	 fi
}

CheckProcessStata()
{
    CPS_PID=$1
    if [ "$CPS_PID" != "" ] ;then
        CPS_PIDLIST=`ps -ef|grep $CPS_PID|grep -v grep|awk -F" " '{print $2}'`
    else
        CPS_PIDLIST=`ps -ef|grep "$CPS_PNAME"|grep -v grep|awk -F" " '{print $2}'`
    fi

    for CPS_i in `echo $CPS_PIDLIST`
    do
        if [ "$CPS_PID" = "" ] ;then
            CPS_i1="$CPS_PID"
        else
            CPS_i1="$CPS_i"
        fi

        if [ "$CPS_i1" = "$CPS_PID" ] ;then
            #kill -s 0 $CPS_i
            kill -0 $CPS_i >/dev/null 2>&1
            if [ $? != 0 ] ;then
                echo "[`date`] MC-10500: Process $i have Dead"
                kill -9 $CPS_i >/dev/null 2>&1

                return 1
            else
                #echo "[`date`]: Process is alive"
                return 0
            fi
        fi
    done
    echo "[`date`] MC-10502: Process $CPS_i is not exists"
    return 1
}

status()
{
  SPID=`cat $GAME_X_HOME/bin/wordcount.pid`
  CheckProcessStata $SPID >/dev/null
  if [ $? != 0 ];then
	echo "unixdialup:{$SPID}  Stopped ...."
  else
	echo "unixdialup:{$SPID} Running Normal."
  fi

}

restart()
{
    echo "stoping ... "
    stop
    echo "staring ..."
    start
}

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    status)
         status
        ;;
    restart)
        restart
        ;;
    *)
        echo $"Usage: $0 {start|stop|restart}"
        RETVAL=1
esac
exit $RETVAL