package eu.bakici.imageprogressbar.utils;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

public class FlaggedRect implements Parcelable {

    public static final Creator<FlaggedRect> CREATOR = new Creator<FlaggedRect>() {
        @Override
        public FlaggedRect createFromParcel(Parcel in) {
            return new FlaggedRect(in);
        }

        @Override
        public FlaggedRect[] newArray(int size) {
            return new FlaggedRect[size];
        }
    };
    private Rect rect;
    private boolean flagged;

    protected FlaggedRect(Parcel in) {
        rect = in.readParcelable(Rect.class.getClassLoader());
        flagged = in.readByte() != 0;
    }

    public FlaggedRect(Rect rect, boolean flagged) {
        this.rect = rect;
        this.flagged = flagged;
    }

    public FlaggedRect(int left, int top, int right, int bottom) {
        this(new Rect(left, top, right, bottom), false);
    }

    public FlaggedRect(int left, int top, int right, int bottom, boolean flagged) {
        this(new Rect(left, top, right, bottom), flagged);
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(rect, flags);
        dest.writeByte((byte) (flagged ? 1 : 0));
    }

}
