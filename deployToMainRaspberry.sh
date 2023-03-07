#! /bin/bash
scp target/iotserver-1.0-SNAPSHOT.jar pi@mainraspberry:server/IoTServer
scp -r properties pi@mainraspberry:server/IoTServer
scp ../simpleDB/target/simpledb-1.0-SNAPSHOT.jar pi@mainraspberry:server/IoTServer/lib
