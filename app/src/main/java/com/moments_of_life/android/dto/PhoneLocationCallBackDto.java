package com.moments_of_life.android.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by wangliang on 0007/2017/3/7.
 */

public class PhoneLocationCallBackDto implements Parcelable {
    public String city;//城市
    public String address = "";//详细地址
    public String addressName = "";//详细地址名称
    public Double lat = 4.9e-324;//纬度
    public Double lng = 4.9e-324;//经度
    public String locationFromType = "";//定位来源
    public Long locationTime = 0l;//定位时间

    @Override
    public String toString() {
        return "PhoneLocationCallBackDto{" +
                "city='" + city + '\'' +
                ", address='" + address + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                ", locationFromType='" + locationFromType + '\'' +
                ", locationTime='" + locationTime + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.city);
        dest.writeString(this.address);
        dest.writeString(this.addressName);
        dest.writeValue(this.lat);
        dest.writeValue(this.lng);
        dest.writeString(this.locationFromType);
        dest.writeValue(this.locationTime);
    }

    public PhoneLocationCallBackDto() {
    }

    protected PhoneLocationCallBackDto(Parcel in) {
        this.city = in.readString();
        this.address = in.readString();
        this.addressName = in.readString();
        this.lat = (Double) in.readValue(Double.class.getClassLoader());
        this.lng = (Double) in.readValue(Double.class.getClassLoader());
        this.locationFromType = in.readString();
        this.locationTime = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Creator<PhoneLocationCallBackDto> CREATOR = new Creator<PhoneLocationCallBackDto>() {
        @Override
        public PhoneLocationCallBackDto createFromParcel(Parcel source) {
            return new PhoneLocationCallBackDto(source);
        }

        @Override
        public PhoneLocationCallBackDto[] newArray(int size) {
            return new PhoneLocationCallBackDto[size];
        }
    };
}
