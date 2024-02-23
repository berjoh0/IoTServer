/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iotserver.database.tables;

import java.util.HashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import iotserver.database.IoTDatabase;
import simpleDB.database.SimpleDBDatabase;
import simpleDB.field.SimpleDBField;
import simpleDB.table.SimpleDBTable;

/**
 *
 * @author johanbergman
 */
public class TblSensorValues extends SimpleDBTable {

    private static String tableName = "SensorValue";

    private static SimpleDBField[] tableFields = {
            new SimpleDBField("sensorValueID", "INTEGER PRIMARY KEY AUTOINCREMENT", null, false, false),
            new SimpleDBField("siteID", "INT", "0", true, false),
            new SimpleDBField("sensorID", "INT", "0", true, false),
            new SimpleDBField("value", "REAL", "0", true, false),
            new SimpleDBField("timestamp", "TEXT", "", true, false)
    };

    public TblSensorValues(IoTDatabase iotDatabase) {
        super((SimpleDBDatabase) iotDatabase, tableFields, tableName);
    }

    public JsonObject getLatestValues() {
        String sql = "select sv1.siteID,sv1.sensorID,sv1.timestamp,sv1.value from " +
                "(SELECT \"siteID\",\"sensorID\", \"value\",timestamp," +
                " row_number() over (partition by \"siteID\", \"sensorID\" order by timestamp desc) as _rn" +
                "     FROM \"SensorValue\" " +
                ") as sv1" +
                " where sv1._rn = 1";
        JsonObject retValues = new JsonObject();
        JsonArray tArray = selectValues(sql);
        retValues.add("sensor", tArray);

        return retValues;
    }

}
