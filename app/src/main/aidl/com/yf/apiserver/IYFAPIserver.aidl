package com.yf.apiserver;

// Declare any non-default types here with import statements

interface IYFAPIserver {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void setEthDhcp();
     String getEthIpAdressInfo();
     String getWifiIpAdressInfo();
     boolean setWifiDhcpConect(String ssid, int security, String password);
     boolean setWifiStaticConect(String ssid, int security,String password, String IPaddr, String gateWay, String mask,String dns1, String dns2);
     boolean yfsetEthIPAddress(String IP, String Mask, String Gateway,String DNS, String DNS2);
     String getSerialNumber();
     boolean setNetworkEnable(int netWorkType, boolean enable);
     String getOsVersion();
     void updateSystemOs(String osPath);
     boolean setNtpTimeServer(String server);
     String getNtpTimeServer();
     void enableAdb(boolean enable);
     void yfslientinstallapk(String apkpath, boolean openApp);
     void startForegroundMonitor(boolean start, String packagename,int time);
     void setMeetLedColor(int color);
     void yfsetHumanSensor(int time);
     String yfgetUartPath(String uartnum);
     String yfgetCurrentNetType();
     String yfgetUSBPath();
     String yfgetSDPath();
     String yfgetInternalSDPath();
     boolean yfsetEthonoff(boolean enable);
     String yfgetIpAddress();
     String yfgetEthMacAddress();
     void yfsetStatusBarVisibility(boolean enable);
     void yfsetNavigationBarCanSwap(boolean enable);
     void yfsetNavigationBarVisibility(boolean enable);
     int yfgetScreenWidth();
     int yfgetScreenHeight();
     void yfTakeScreenshot(String path, String name);
     void yfSetLCDOff();
     void yfSetLCDOn();
     void yfReboot();
     void yfShutDown();
     String yfgetAvailableInternalMemorySize();
     String yfgetInternalStorageMemory();
     String yfgetRAMSize();
     String yfgetBuildDate();
     String yfgetFirmwareVersion();
     String yfgetKernelVersion();
     String yfgetAndroidVersion();
     String yfgetAndroidDeviceModel();
     void yfsetHdmiOutOnOff(boolean enable);
	 void ShellRootCommand(String Command);
}