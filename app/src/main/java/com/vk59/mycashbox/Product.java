package com.vk59.mycashbox;

class Product {
    private long code;

    public Product(long barCode) {
        this.code = barCode;
    }

    public long getCode() {
        return code;
    }

    public void setCode(long code) {
        this.code = code;
    }
}
