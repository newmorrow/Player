package com.example.firsttask;

public enum PlayerStateEnum {
	IDLE(R.string.idle_state, false),
	PREPARING(R.string.preparing_state, false),
	PLAYING(R.string.playing_state, true),
	PAUSED(R.string.paused_state, true);

	private final boolean readyToPlay;
	private final int nameId;

	private PlayerStateEnum(int nameId, boolean isReady) {
		this.readyToPlay = isReady;
		this.nameId = nameId;
	}

	public boolean isReadyToPlay() {
		return readyToPlay;
	}

	public int getNameId() {
		return nameId;
	}
}
