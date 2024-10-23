package com.bremen.backend.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Response<T> {
	private int status;
	private String message;
	private T data;

	public Response(int status, String message) {
		this.status = status;
		this.message = message;
	}

}

