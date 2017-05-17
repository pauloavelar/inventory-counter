package com.pauloavelar.inventory.model;

import android.os.Parcel;
import android.os.Parcelable;

public class InventoryItem implements Parcelable {

    private long id;
    private String dateTime;
    private String product;
    private String lotCode;
    private int bagCount;

    public InventoryItem() { }

    public InventoryItem(long id, String dateTime, String product, String lotCode, int bagCount) {
        this.id = id;
        this.dateTime = dateTime;
        this.product = product;
        this.lotCode = lotCode;
        this.bagCount = bagCount;
    }

    protected InventoryItem(Parcel in) {
        id = in.readLong();
        product = in.readString();
        lotCode = in.readString();
        bagCount = in.readInt();
    }

    public static final Creator<InventoryItem> CREATOR = new Creator<InventoryItem>() {
        @Override
        public InventoryItem createFromParcel(Parcel in) {
            return new InventoryItem(in);
        }

        @Override
        public InventoryItem[] newArray(int size) {
            return new InventoryItem[size];
        }
    };

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getLotCode() {
        return lotCode;
    }

    public void setLotCode(String lotCode) {
        this.lotCode = lotCode;
    }

    public int getBagCount() {
        return bagCount;
    }

    public void setBagCount(int bagCount) {
        this.bagCount = bagCount;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String datetime) {
        this.dateTime = datetime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(product);
        parcel.writeString(lotCode);
        parcel.writeInt(bagCount);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
