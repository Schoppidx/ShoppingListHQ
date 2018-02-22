package com.example.schoppenhauer.shoppinglisthq;


public class ShoppingMemo {

    private String product;
    private int quantity;
    private long id;
    private boolean checked;

    public ShoppingMemo(String product, int quantity, long id, boolean checked) {
        this.product = product;
        this.quantity = quantity;
        this.id = id;
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String output = quantity + " x " + product;

        return output;
    }

}
