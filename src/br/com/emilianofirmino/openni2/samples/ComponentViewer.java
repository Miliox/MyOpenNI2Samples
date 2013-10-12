package br.com.emilianofirmino.openni2.samples;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.openni.VideoFrameRef;
import org.openni.VideoStream;
import org.openni.VideoStream.NewFrameListener;

public class ComponentViewer extends Component implements NewFrameListener {
	private static final long serialVersionUID = 4862271264097409060L;

	private BufferedImage bufferedImage;
	private VideoFrameRef frame;
	private VideoStream   stream;

	private float[]	      histogram;
	private int[]         imagePixels;

	public void setVideoStream(VideoStream stream) {
		if (this.stream != null) {
			this.stream.removeNewFrameListener(this);
		}

		if (frame != null) {
			frame.release();
		}

		this.stream = stream;

		if (stream != null) {
			stream.addNewFrameListener(this);
			setSize(stream.getVideoMode().getResolutionX(),
			stream.getVideoMode().getResolutionY());
		}
	}

	@Override
	public void onFrameReady(VideoStream stream) {
		if (frame != null)
			frame.release();

		frame = stream.readFrame();

		if (imagePixels == null ||
				imagePixels.length < frame.getWidth() * frame.getHeight()) {
			imagePixels = new int[frame.getWidth() * frame.getHeight()];
		}

		ByteBuffer data = frame.getData().order(ByteOrder.LITTLE_ENDIAN);
		int pos = 0;
		switch (frame.getVideoMode().getPixelFormat()) {
			case DEPTH_1_MM:
			case DEPTH_100_UM:
			case SHIFT_9_2:
			case SHIFT_9_3:
				calcHist(data);
				data.rewind();
				while (data.remaining() > 0) {
					short depth = data.getShort();
					short pixel = (short) histogram[depth];
					imagePixels[pos++] = (0xFF << 24) | pixel << 8;
				}
				break;
			case RGB888:
				while (data.remaining() > 0) {
					int red = (int) data.get() & 0xFF;
					int green = (int) data.get() & 0xFF;
					int blue = (int) data.get() & 0xFF;
					imagePixels[pos++] = (0xFF << 24) | (red << 16) | (green << 8) | blue;
				}
				break;
			default:
				frame.release();
				frame = null;
		}

		repaint();
	}

	@Override
	public void paint(Graphics g)
	{
		if (frame == null) {
			return;
		}

		int width = frame.getWidth();
		int height = frame.getHeight();

		if (bufferedImage == null || bufferedImage.getWidth() != width || bufferedImage.getHeight() != height) {
			bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		}

		bufferedImage.setRGB(0, 0, width, height, imagePixels, 0, width);

		int framePosX = (getWidth() - width) / 2;
		int framePosY = (getHeight() - height) / 2;
		g.drawImage(bufferedImage, framePosX, framePosY, null);
	}

	private void calcHist(ByteBuffer depthBuffer) {
		if (histogram == null || histogram.length < stream.getMaxPixelValue()) {
			histogram = new float[stream.getMaxPixelValue()];
		}

		// reset
		for (int i = 0; i < histogram.length; ++i) {
			histogram[i] = 0;
		}

		int points = 0;
		while (depthBuffer.remaining() > 0) {
			int depth = depthBuffer.getShort() & 0xFFFF;
			if (depth != 0) {
				histogram[depth]++;
				points++;
			}
		}

		for (int i = 1; i < histogram.length; i++) {
			histogram[i] += histogram[i - 1];
		}

		if (points > 0) {
			for (int i = 1; i < histogram.length; i++) {
				histogram[i] = (int) (256 * (1.0f - (histogram[i] / (float) points)));
			}
		}
	}

}
