package com.hualu.wifistart;

public enum PlayState {
	SINGLE_PLAY, SINGLE_REPEAT,ALL_REPEAT,RANDOM_PLAY;
	public static PlayState toPlayState (String state) {
        try {
            return valueOf(state);
        } catch (Exception ex) {
                // For error cases
            return ALL_REPEAT;
        }
    }
}
