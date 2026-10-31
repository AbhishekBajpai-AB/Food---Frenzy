package com.example.demo.entities;

import java.util.EnumSet;

public enum OrderStatus {
	PLACED,
	CONFIRMED,
	PREPARING,
	READY,
	COMPLETED,
	CANCELLED;

	public boolean canMoveTo(OrderStatus next) {
		return switch (this) {
			case PLACED -> EnumSet.of(CONFIRMED, CANCELLED).contains(next);
			case CONFIRMED -> EnumSet.of(PREPARING, CANCELLED).contains(next);
			case PREPARING -> EnumSet.of(READY, CANCELLED).contains(next);
			case READY -> next == COMPLETED;
			case COMPLETED, CANCELLED -> false;
		};
	}
}
