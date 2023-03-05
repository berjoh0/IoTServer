package iotserver.database;

import com.google.gson.JsonObject;

import iotserver.database.tables.TblSensorValues;
import simpleDB.database.SimpleDBDatabase;
import simpleDB.table.SimpleDBTable;

public class IoTDatabase extends SimpleDBDatabase {

    private static SimpleDBTable[] tables = { new TblSensorValues(null) };

    public IoTDatabase(JsonObject databaseProperties) {
        super(databaseProperties, tables);
    }

}
