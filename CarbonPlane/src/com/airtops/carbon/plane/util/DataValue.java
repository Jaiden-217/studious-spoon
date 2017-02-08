package com.airtops.carbon.plane.util;

public class DataValue {

    public static final String TAG="airtops.carbon.plane";

    public static final String DEVICE_NAME="SerialCom";
    
    public static final String UUID="0000FFF6-0000-1000-8000-00805f9b34fb";
    
    public static final String UUID_RECEIVE = "0000FFF7-0000-1000-8000-00805f9b34fb";
    
    public static final String UUID_BATTERY = "00002A19-0000-1000-8000-00805f9b34fb";

    public static final int SCAN_PERIOD = 5000;

    public static final String DATA_ACTION="com.airtops.action.DATA";

    public static final String LINK_ACTION="com.airtops.action.LINK";

    public static final String RSSI_ACTION="com.airtops.action.RSSI";
    
    public static final int MSG_SEARCHING = 1001;
    
    public static final int MSG_NO_BLE_FOUND = 1003;
    
    public static final int MSG_QUIT = 1004;
    
    public static final String BROAD_FINISH = "com.carbon.flier.finish";

    public static final String BROAD_CONNECTION_BROKEN = "com.carbon.plane.connection.broken";

    public static final int ACCE_MAX = 2000;

    public static final int ACCE_MIN = 1000;

    public static final int ACCE_RATE_DEFAULT = 0;

    public static final int ACCE_DENOMINATOR = 200000;

    public static final int ACCE_COUNT_DEFAULT = 1000;

    public static final int DIRECTION_FACTORING = 30;

    public static final int SENSOR_DATA_DEFAULT = 0;

    public static final int SENSOR_DATA_LEVEL_MAX = 3;

    public static final int SENSOR_DATA_LEVEL_MIN = -3;

    public static final int DIRECTION_LEFT = 1700;

    public static final int DIRECTION_RIGHT = 1300;

}
