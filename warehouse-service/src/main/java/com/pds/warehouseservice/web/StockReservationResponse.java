package com.pds.warehouseservice.web;

public class StockReservationResponse {
    private boolean success;
    private Long reservationId;

    public StockReservationResponse() {}

    public StockReservationResponse(boolean success) {
        this.success = success;
    }

    public StockReservationResponse(boolean success, Long reservationId) {
        this.success = success;
        this.reservationId = reservationId;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public Long getReservationId() { return reservationId; }
    public void setReservationId(Long reservationId) { this.reservationId = reservationId; }
}
