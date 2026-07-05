package com.kagemusha.backend.controller.response;

public class ApiResponse<T> {
    private final boolean success;
    private final T data;

    private ApiResponse(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }
}
