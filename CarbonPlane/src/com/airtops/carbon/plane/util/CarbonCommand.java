package com.airtops.carbon.plane.util;

/**
 * @ClassName CarbonCommand
 * @Description 飞行姿态指令类
 * @author DingJie
 * @date 2016-05-26
 * */
public class CarbonCommand {
	private static CarbonCommand INSTANCE = new CarbonCommand();
	private byte title = (byte) 0xCC;
	private byte lock = 0x00;
	private byte high_speed = (byte) (1000 / 256);
	private byte low_speed = (byte) (1000 & 0x000000FF);
	private byte high_direction = (byte) (1500 / 256);
	private byte low_direction = (byte) (1500 & 0x000000FF);
	private byte another = (byte) 0;
	private byte hand_mode = 0x02;
	private byte camera_mode = 0x03;
	private byte take_photo = 0x05;
	private byte start_video = 0x06;
	private byte stop_video = 0x07;
	private byte randoms = (byte) 1;
	private int trimming = 1500;
	public  int HEART_BEAT = 0;

	private byte high_left_acce = (byte) (1500 / 256);
	private byte low_left_acce = (byte) (1500 & 0x000000FF);

	private byte high_right_acce = (byte) (1500 / 256);
	private byte low_right_acce = (byte) (1500 & 0x000000FF);

	private byte param_mode = 0x04;
	private byte param_mode_raw = 0x01;
	private byte param_mode_pitch = 0x02;
	private byte param_mode_yaw = 0x03;

	private byte raw_first_high = (byte) (1500 / 256);
	private byte raw_first_low = (byte) (1500 & 0x000000FF);
	private byte raw_second_high = (byte) (1500 / 256);
	private byte raw_second_low = (byte) (1500 & 0x000000FF);
	private byte raw_third_high = (byte) (1500 / 256);
	private byte raw_third_low = (byte) (1500 & 0x000000FF);

	private byte pitch_first_high = (byte) (1500 / 256);
	private byte pitch_first_low = (byte) (1500 & 0x000000FF);
	private byte pitch_second_high = (byte) (1500 / 256);
	private byte pitch_second_low = (byte) (1500 & 0x000000FF);
	private byte pitch_third_high = (byte) (1500 / 256);
	private byte pitch_third_low = (byte) (1500 & 0x000000FF);

	private byte yaw_first_high = (byte) (1500 / 256);
	private byte yaw_first_low = (byte) (1500 & 0x000000FF);
	private byte yaw_second_high = (byte) (1500 / 256);
	private byte yaw_second_low = (byte) (1500 & 0x000000FF);
	private byte yaw_third_high = (byte) (1500 / 256);
	private byte yaw_third_low = (byte) (1500 & 0x000000FF);


	public static CarbonCommand getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CarbonCommand();
		}
		return INSTANCE;
	}

	/**
	 * 飞行姿态操控数据
	 * */
	public byte[] carbon_plane_normal_command() {
		return  new byte[] { title, lock, high_speed, low_speed, another, another, another, another,
				high_direction, low_direction, randoms };
	}

	/**
	 * hand mode
	 * */
	public byte[] carbon_hand_mode() {
		return new byte[] { title, hand_mode, high_left_acce, low_left_acce, high_right_acce, low_right_acce,
				another, another, another, another, randoms };
	}

	/**
	 * 修改参数raw
	 * */
	public byte[] carbon_param_raw() {
		byte[] data = new byte[] { title, param_mode, param_mode_raw, raw_first_high, raw_first_low,
				raw_second_high, raw_second_low, raw_third_high, raw_third_low, randoms};
		return  data;
	}

	/**
	 * 修改参数pitch
	 * */
	public byte[] carbon_param_pitch() {
		byte[] data = new byte[] { title, param_mode, param_mode_pitch, pitch_first_high, pitch_first_low,
				pitch_second_high, pitch_second_low, pitch_third_high, pitch_third_low, randoms};
		return data;
	}

	/**
	 * 修改参数yaw
	 * */
	public byte[] carbon_param_yaw() {
		byte[] data = new byte[] { title, param_mode, param_mode_yaw, yaw_first_high, yaw_first_low,
				yaw_second_high, yaw_second_low, yaw_third_high, yaw_third_low, randoms};
		return data;
	}

	/**
	 * 拍照指令
	 * */
	public byte[] carbon_take_photo() {
		byte[] data = new byte[] { title, camera_mode, high_speed, low_speed, another, another, another,
				take_photo, high_direction, low_direction, randoms };
		return data;
	}

	/**
	 * 开始摄像指令
	 * */
	public byte[] carbon_take_video() {
		byte[] data = new byte[] { title, camera_mode, high_speed, low_speed, another, another, another,
				start_video, high_direction, low_direction, randoms };
		return data;
	}
	
	/**
	 * 停止摄像指令
	 * */
	public byte[] carbon_stop_video() {
		byte[] data = new byte[] { title, camera_mode, high_speed, low_speed, another, another, another,
				stop_video, high_direction, low_direction, randoms };
		return data;
	}
	
	public void setLock(byte lock) {
		this.lock = lock;
	}

	public void setHspeed(byte hspeed) {
		this.high_speed = hspeed;
	}

	public void setLspeed(byte lspeed) {
		this.low_speed = lspeed;
	}

	public void setHdirection(byte hdirection) {
		this.high_direction = hdirection;
	}

	public void setLdirection(byte ldirection) {
		this.low_direction = ldirection;
	}

	public void setAnother(byte another) {
		this.another = another;
	}

	public void setTitle(byte title) {
		this.title = title;
	}
	
	public void setRandoms(byte randoms) {
		this.randoms = randoms;
	}
	
	public void setTrimming(int trimming) {
		this.trimming = trimming;
	}
	
	public int getTrimming() {
		return trimming;
	}

	public int getHeartBeat() {
		return HEART_BEAT;
	}

	public void setHeartBeat(int HEART_BEAT) {
		this.HEART_BEAT = HEART_BEAT;
	}

	public byte getYaw_third_low() {
		return yaw_third_low;
	}

	public void setYaw_third_low(byte yaw_third_low) {
		this.yaw_third_low = yaw_third_low;
	}

	public byte getRaw_first_high() {
		return raw_first_high;
	}

	public void setRaw_first_high(byte raw_first_high) {
		this.raw_first_high = raw_first_high;
	}

	public byte getRaw_first_low() {
		return raw_first_low;
	}

	public void setRaw_first_low(byte raw_first_low) {
		this.raw_first_low = raw_first_low;
	}

	public byte getRaw_second_high() {
		return raw_second_high;
	}

	public void setRaw_second_high(byte raw_second_high) {
		this.raw_second_high = raw_second_high;
	}

	public byte getRaw_second_low() {
		return raw_second_low;
	}

	public void setRaw_second_low(byte raw_second_low) {
		this.raw_second_low = raw_second_low;
	}

	public byte getRaw_third_high() {
		return raw_third_high;
	}

	public void setRaw_third_high(byte raw_third_high) {
		this.raw_third_high = raw_third_high;
	}

	public byte getRaw_third_low() {
		return raw_third_low;
	}

	public void setRaw_third_low(byte raw_third_low) {
		this.raw_third_low = raw_third_low;
	}

	public byte getPitch_first_high() {
		return pitch_first_high;
	}

	public void setPitch_first_high(byte pitch_first_high) {
		this.pitch_first_high = pitch_first_high;
	}

	public byte getPitch_first_low() {
		return pitch_first_low;
	}

	public void setPitch_first_low(byte pitch_first_low) {
		this.pitch_first_low = pitch_first_low;
	}

	public byte getPitch_second_high() {
		return pitch_second_high;
	}

	public void setPitch_second_high(byte pitch_second_high) {
		this.pitch_second_high = pitch_second_high;
	}

	public byte getPitch_second_low() {
		return pitch_second_low;
	}

	public void setPitch_second_low(byte pitch_second_low) {
		this.pitch_second_low = pitch_second_low;
	}

	public byte getPitch_third_high() {
		return pitch_third_high;
	}

	public void setPitch_third_high(byte pitch_third_high) {
		this.pitch_third_high = pitch_third_high;
	}

	public byte getPitch_third_low() {
		return pitch_third_low;
	}

	public void setPitch_third_low(byte pitch_third_low) {
		this.pitch_third_low = pitch_third_low;
	}

	public byte getYaw_first_high() {
		return yaw_first_high;
	}

	public void setYaw_first_high(byte yaw_first_high) {
		this.yaw_first_high = yaw_first_high;
	}

	public byte getYaw_first_low() {
		return yaw_first_low;
	}

	public void setYaw_first_low(byte yaw_first_low) {
		this.yaw_first_low = yaw_first_low;
	}

	public byte getYaw_second_high() {
		return yaw_second_high;
	}

	public void setYaw_second_high(byte yaw_second_high) {
		this.yaw_second_high = yaw_second_high;
	}

	public byte getYaw_second_low() {
		return yaw_second_low;
	}

	public void setYaw_second_low(byte yaw_second_low) {
		this.yaw_second_low = yaw_second_low;
	}

	public byte getYaw_third_high() {
		return yaw_third_high;
	}

	public void setYaw_third_high(byte yaw_third_high) {
		this.yaw_third_high = yaw_third_high;
	}

	public byte getLow_left_acce() {
		return low_left_acce;
	}

	public void setLow_left_acce(byte low_left_acce) {
		this.low_left_acce = low_left_acce;
	}

	public byte getHigh_left_acce() {
		return high_left_acce;
	}

	public void setHigh_left_acce(byte high_left_acce) {
		this.high_left_acce = high_left_acce;
	}

	public byte getHigh_right_acce() {
		return high_right_acce;
	}

	public void setHigh_right_acce(byte high_right_acce) {
		this.high_right_acce = high_right_acce;
	}

	public byte getLow_right_acce() {
		return low_right_acce;
	}

	public void setLow_right_acce(byte low_right_acce) {
		this.low_right_acce = low_right_acce;
	}
}
