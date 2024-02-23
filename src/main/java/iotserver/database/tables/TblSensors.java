/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver.database.tables;

import java.util.HashMap;

import iotserver.database.IoTDatabase;
import simpleDB.database.SimpleDBDatabase;
import simpleDB.field.SimpleDBField;
import simpleDB.table.SimpleDBTable;

/**
 *
 * @author johanbergman
 */
public class TblSensors extends SimpleDBTable {

    private static String tableName = "SensorValue";

    private static SimpleDBField[] tableFields = {
            new SimpleDBField("sensorID", "INTEGER PRIMARY KEY AUTOINCREMENT", null, false, false),
            new SimpleDBField("serial", "TEXT", "", true, false),
            new SimpleDBField("siteID", "INT", "0", true, false),
            new SimpleDBField("timestamp", "TEXT", "", true, false)
    };

    public TblSensors(IoTDatabase iotDatabase) {
        super((SimpleDBDatabase) iotDatabase, tableFields, tableName);
    }

}
