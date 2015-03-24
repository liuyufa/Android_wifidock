package com.hualu.wifistart.wifiset;

public class WiFiSetMessages {
	//add for 热点设置
	public static final int MSG_GET_HOTSPOTS=0x01;
	public static final int MSG_GET_HOTSPOTS_XML=0x02;
	public static final int MSG_PARSER_HOTSPOTS=0x03;
	public static final int MSG_PARSER_HOTSPOTS_SUCCESS=0x04;
	public static final int MSG_GET_HOTSPOTS_FAIL=0x05;
	public static final int MSG_CONNECT_HOTSPOTS=0x06;
	public static final int MSG_SEND_CONNECT_HOTSPOTS_SUCCESS=0x07;
	public static final int MSG_SEND_CONNECT_HOTSPOTS_FAIL=0x08;
	public static final int MSG_CHECK_HOTSPOTS_STATUS=0x09;
	public static final int MSG_CONNECT_HOTSPOTS_SUCCESS=0x0A;
	public static final int MSG_CHK_CONNECT_HOTSPOTS_FAIL=0x0B;
	public static final int MSG_CONNECT_HOTSPOTS_FAIL=0x0C;
	public static final int MSG_GET_PRE_AP=0x0D;
	public static final int MSG_DISCONNECT_AP=0x0F;
	public static final int MSG_SEND_DISCONNECT_SUCCESS=0x10;
	public static final int MSG_SEND_DISCONNECT_FAIL=0x11;
	public static final int MSG_SEND_3G=0x12;
	public static final int MSG_SEND_3G_FAIL=0x13;
	public static final int MSG_SEND_3G_SUCCESS=0x14;
	public static final int MSG_GET_3G_DETAILS=0x15;
	public static final int MSG_GET_3G_DETAIL_SUCCESS=0x16;
	public static final int MSG_CHECK_CONNECTED_AP=0x17;
	public static final int MSG_CHECK_AP_SUCCESS=0x18;
	public static final int MSG_CHECK_AP_FAIL=0x19;
	public static final int MSG_PARSER_HOTSPOTS_FAIL=0x1A;
	public static final int MSG_CHK_CMD_HOTSPOTS_FAIL=0x1B;
	public static final int MSG_GROUTER_INFO=0x1C;
	public static final int MSG_GROUTER_INFO_SUCCESS=0x1D;
	public static final int MSG_GROUTER_INFO_FAIL=0x1F;
	public static final int MSG_CHK_CONNECT_HOTSPOTS_RUN=0x20;
	public static final int MSG_ERROR_FOR_AP=0x21;
	public static final int MSG_SEND_CONNECT_HOTSPOTS_SUCCESS_WAIT=0x22;
	//add for 模式选择
	public static final int SUCCESS=0x01;
	public static final int FAIL=0x02;
	public static final int MODE_3G=0x03;
	public static final int MODE_REPEATER=0x04;
	public static final int MODE_ROUTER=0x05;
	public static final int MODE_ERROR=0x06;
	public static final int REPEATER_ENABLE=0x07;
	//add for 设置向导
	public static final int SET_ROUTER_PPPOE=0x01;
	public static final int SET_ROUTER_DHCP=0x02;
	public static final int SET_ROUTER_STATIC=0x03;
	public static final int SET_3G_3G=0x04;
	public static final int SET_PPPOE_CMD=0x05;
	public static final int SET_STATIC_CMD=0x06;
	public static final int SET_ROUTER_PPPOE_SUCCESS=0x07;
	public static final int SET_ROUTER_STATIC_SUCCESS=0x08;
	public static final int SET_ROUTER_DHCP_SUCCESS=0x09;
	public static final int SET_3G_3G_SUCCESS=0x0A;
	public static final int SET_PPPOE_CMD_SUCCESS=0x0B;
	public static final int SET_STATIC_CMD_SUCCESS=0x0C;
	public static final int MSG_SEND_CMD_FAIL=0x0D;
	public static final int SET_ROUTER_PPPOE_FAIL=0x0F;
}
