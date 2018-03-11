package com.moments_of_life.android.dto;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LorenWang on 2018/3/11.
 * 创建时间：2018/3/11 21:50
 * 创建人：王亮（Loren wang）
 * 功能作用：
 * 思路：
 * 修改人：
 * 修改时间：
 * 备注：
 */

public class AsrResultDto implements Parcelable {
    private int userId;
    private int asrId;
    private boolean asrState;
    private String asrContent;
    private long asrTime;

    public int getUserId() {
        return userId;
    }

    public AsrResultDto setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getAsrId() {
        return asrId;
    }

    public AsrResultDto setAsrId(int asrId) {
        this.asrId = asrId;
        return this;
    }

    public boolean isAsrState() {
        return asrState;
    }

    public AsrResultDto setAsrState(boolean asrState) {
        this.asrState = asrState;
        return this;
    }

    public String getAsrContent() {
        return asrContent;
    }

    public AsrResultDto setAsrContent(String asrContent) {
        this.asrContent = asrContent;
        return this;
    }

    public long getAsrTime() {
        return asrTime;
    }

    public AsrResultDto setAsrTime(long asrTime) {
        this.asrTime = asrTime;
        return this;
    }

    @Override
    public String toString() {
        return "AsrResultDto{" +
                "userId=" + userId +
                ", asrId=" + asrId +
                ", asrState=" + asrState +
                ", asrContent='" + asrContent + '\'' +
                ", asrTime=" + asrTime +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.userId);
        dest.writeInt(this.asrId);
        dest.writeByte(this.asrState ? (byte) 1 : (byte) 0);
        dest.writeString(this.asrContent);
        dest.writeLong(this.asrTime);
    }

    public AsrResultDto() {
    }

    protected AsrResultDto(Parcel in) {
        this.userId = in.readInt();
        this.asrId = in.readInt();
        this.asrState = in.readByte() != 0;
        this.asrContent = in.readString();
        this.asrTime = in.readLong();
    }

    public static final Creator<AsrResultDto> CREATOR = new Creator<AsrResultDto>() {
        @Override
        public AsrResultDto createFromParcel(Parcel source) {
            return new AsrResultDto(source);
        }

        @Override
        public AsrResultDto[] newArray(int size) {
            return new AsrResultDto[size];
        }
    };
}
