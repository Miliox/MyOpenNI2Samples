package br.com.emilianofirmino.openni2.samples;

import java.util.ArrayList;
import org.openni.*;

public class OpenNI2Info {
	public static void main(String[] args) {
		OpenNI.initialize();

		println("OpenNI Info:");
		for(DeviceInfo deviceInfo : OpenNI.enumerateDevices()) {
			printDeviceDetails(deviceInfo);

			Device device = Device.open(deviceInfo.getUri());
			
			ArrayList<SensorInfo> sensors = new ArrayList<SensorInfo>(3);
			
			if (device.hasSensor(SensorType.COLOR))
				sensors.add(device.getSensorInfo(SensorType.COLOR));
			if (device.hasSensor(SensorType.DEPTH))
				sensors.add(device.getSensorInfo(SensorType.DEPTH));
			if (device.hasSensor(SensorType.IR))
				sensors.add( device.getSensorInfo(SensorType.IR));

			for (SensorInfo sensorInfo : sensors)
			{
				println("  Sensor: " + sensorInfo.getSensorType());
				for (VideoMode videoMode : sensorInfo.getSupportedVideoModes()) {
					printSensorDetails(videoMode);
				}
			}

			device.close();
		}
		
		OpenNI.shutdown();
	}

	private static void printSensorDetails(VideoMode videoMode) {
		println("    FramePerSecond: " + videoMode.getFps());
		println("    Height: " + videoMode.getResolutionY());
		println("    Width: " + videoMode.getResolutionX());
		println("    PixelFormat: " + videoMode.getPixelFormat());
		println("");
	}

	private static void printDeviceDetails(DeviceInfo deviceInfo) {
		println("Device");
		println("  Name: " + deviceInfo.getName());
		println("  Uri: " + deviceInfo.getUri());
		println("  Usb Product Id: " + deviceInfo.getUsbProductId());
		println("  Usb Vendor Id: " + deviceInfo.getUsbVendorId());
		println("");
	}
	
	private static void println(String s)
	{
		System.out.println(s);
	}

}
