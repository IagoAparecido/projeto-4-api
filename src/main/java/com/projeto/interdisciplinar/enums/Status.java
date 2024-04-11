package com.projeto.interdisciplinar.enums;

public enum Status {
    AUTHORIZED("AUTHORIZED"),
    UNAUTHORIZED("UNAUTHORIZED");

    private String status;

    Status(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }
}
