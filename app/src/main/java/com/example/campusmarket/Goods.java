package com.example.campusmarket;

public class Goods {

    private int id;
    private String title;
    private String price;
    private String description;
    private int userId;
    private String sellerName;
    private String contact;
    private String imagePath;
    private int status;       // 0=在售, 1=已售
    private String createTime;

    public Goods(int id, String title, String price, String description,
                 int userId, String sellerName, String contact,
                 String imagePath, int status, String createTime) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.userId = userId;
        this.sellerName = sellerName;
        this.contact = contact;
        this.imagePath = imagePath;
        this.status = status;
        this.createTime = createTime;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public boolean isSold() {
        return status == 1;
    }
}
