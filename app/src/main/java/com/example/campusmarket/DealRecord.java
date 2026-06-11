package com.example.campusmarket;

public class DealRecord {

    private int id;
    private int goodsId;
    private String goodsTitle;
    private String goodsPrice;
    private int type;          // 0=买, 1=卖
    private int userId;
    private String counterpartyName;
    private String dealTime;

    public DealRecord(int id, int goodsId, String goodsTitle, String goodsPrice,
                      int type, int userId, String counterpartyName, String dealTime) {
        this.id = id;
        this.goodsId = goodsId;
        this.goodsTitle = goodsTitle;
        this.goodsPrice = goodsPrice;
        this.type = type;
        this.userId = userId;
        this.counterpartyName = counterpartyName;
        this.dealTime = dealTime;
    }

    public int getId() { return id; }
    public int getGoodsId() { return goodsId; }
    public String getGoodsTitle() { return goodsTitle; }
    public String getGoodsPrice() { return goodsPrice; }
    public int getType() { return type; }
    public int getUserId() { return userId; }
    public String getCounterpartyName() { return counterpartyName; }
    public String getDealTime() { return dealTime; }

    public boolean isBuy() { return type == 0; }
    public boolean isSell() { return type == 1; }

    public String getTypeLabel() { return type == 0 ? "买入" : "卖出"; }
}