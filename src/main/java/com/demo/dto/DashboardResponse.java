package com.demo.dto;

import java.util.List;

public class DashboardResponse {
  private long totalUsers;
  private long totalOrders;
  private long totalProducts;
  private double totalRevenue;
  private List<RevenueByDay> revenueByDays;
  public long getTotalUsers() {
    return totalUsers;
  }
  public void setTotalUsers(long totalUsers) {
    this.totalUsers = totalUsers;
  }
  public long getTotalOrders() {
    return totalOrders;
  }
  public void setTotalOrders(long totalOrders) {
    this.totalOrders = totalOrders;
  }
  public long getTotalProducts() {
    return totalProducts;
  }
  public void setTotalProducts(long totalProducts) {
    this.totalProducts = totalProducts;
  }
  public double getTotalRevenue() {
    return totalRevenue;
  }
  public void setTotalRevenue(double totalRevenue) {
    this.totalRevenue = totalRevenue;
  } 
  public List<RevenueByDay> getRevenueByDays() {
    return revenueByDays;
  }
  public void setRevenueByDays(List<RevenueByDay> revenueByDays) {
    this.revenueByDays = revenueByDays;
  }
   
}
