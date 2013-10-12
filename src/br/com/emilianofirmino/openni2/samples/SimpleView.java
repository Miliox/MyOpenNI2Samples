package br.com.emilianofirmino.openni2.samples;


import javax.swing.JFrame;

import org.openni.Device;
import org.openni.OpenNI;
import org.openni.SensorType;
import org.openni.VideoMode;
import org.openni.VideoStream;

public class SimpleView {
	private static JFrame          frame;
	private static ComponentViewer viewer;

	private static Device      device;
	private static VideoStream videoStream;

	public static void main(String[] args) {
		OpenNI.initialize();

		SensorType sensor = SensorType.COLOR;
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("color"))
				sensor = SensorType.COLOR;
			if (args[0].equalsIgnoreCase("depth"))
				sensor = SensorType.DEPTH;
			if (args[0].equalsIgnoreCase("ir"))
				sensor = SensorType.IR;
		}

		int index = 0;
		if (args.length > 1) {
			index = Integer.parseInt(args[1]);
		}

		device = Device.open();
		videoStream = VideoStream.create(device, sensor);

		VideoMode mode = videoStream.getSensorInfo().getSupportedVideoModes().get(index);
		videoStream.setVideoMode(mode);

		viewer = new ComponentViewer();
		viewer.setVideoStream(videoStream);
		viewer.setSize(mode.getResolutionX(), mode.getResolutionY());

		frame = new JFrame("RGB Test");
		frame.setSize(viewer.getWidth() + 20, viewer.getHeight() + 20);
		frame.add(viewer);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.out.println("hide window");
				frame.setVisible(false);
				viewer.setVideoStream(null);
				System.out.println("stopping video stream...");
				videoStream.stop();
				System.out.println("closing device...");
				device.close();
				System.out.println("shutting down OpenNI...");
				OpenNI.shutdown();
				System.out.println("bye!");
			}
		});

		videoStream.start();
	}

}
