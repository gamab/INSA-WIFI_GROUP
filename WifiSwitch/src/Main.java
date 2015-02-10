
public class Main {
	
	
	public static void main(String[] args) {
		String networkSSID = "freebox_ZERUIO";
		String networkPass = "pass12345__";

		WifiConfiguration wifiConfig = new WifiConfiguration();
		wifiConfig.SSID = String.format("\"%s\"", networkSSID);
		wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

		WifiManager wifiManager = (WifiManager).getSystemService(WIFI_SERVICE);
		//remember id
		int netId = wifiManager.addNetwork(wifiConfig);
		wifiManager.disconnect();
		wifiManager.enableNetwork(netId, true);
		wifiManager.reconnect();
	}
}
