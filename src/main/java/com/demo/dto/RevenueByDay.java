package com.demo.dto;


public class RevenueByDay {
  private String date;
  public String getDate() {
    return date;
  }
  public void setDate(String date) {
    this.date = date;
  }
  public double getTotal() {
    return total;
  }
  public void setTotal(double total) {
    this.total = total;
  }
  private double total;
  public RevenueByDay(String date, double total){
    this.date = date;
    this.total = total;
  }
}
