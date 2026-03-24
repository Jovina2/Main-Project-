package com.example.project;

public class RecommendItem {
public String title;
public String subtitle;
public int confidence;
public boolean isSwap;

public RecommendItem(String title, String subtitle, int confidence, boolean isSwap) {
    this.title = title;
    this.subtitle = subtitle;
    this.confidence = confidence;
    this.isSwap = isSwap;
}
}