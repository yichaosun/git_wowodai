package com.example.common;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableTools implements Parcelable {
	

	private byte[] b;
	private Bitmap bitmap;
	
	public ParcelableTools(){}

	
	
	public Bitmap getBitmap() {
		return bitmap;
	}



	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}



	public byte[] getB() {
		return b;
	}

	public void setB(byte[] b) {
		this.b = b;
	}

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {

		out.writeByteArray(b);
	}

	public static final Parcelable.Creator<ParcelableTools> CREATOR = new Parcelable.Creator<ParcelableTools>() {
		public ParcelableTools createFromParcel(Parcel in) {
			ParcelableTools tools = new ParcelableTools();
			//tools.b = in.read
			return new ParcelableTools(in);
		}

		public ParcelableTools[] newArray(int size) {
			return new ParcelableTools[size];
		}
	};

	private ParcelableTools(Parcel in) {

	}
}
