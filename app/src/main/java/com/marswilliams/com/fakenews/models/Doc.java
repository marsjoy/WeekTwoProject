
package com.marswilliams.com.fakenews.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Doc implements Parcelable {

    @SerializedName("web_url")
    @Expose
    private String webUrl;

    @SerializedName("multimedia")
    @Expose
    private List<Multimedium> multimedia = null;

    @SerializedName("headline")
    @Expose
    private Headline headline;

    @SerializedName("new_desk")
    @Expose
    private String newsDesk;

    @SerializedName("snippet")
    @Expose
    private String snippet;

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public List<Multimedium> getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(List<Multimedium> multimedia) {
        this.multimedia = multimedia;
    }

    public String getThumbnailURL(){

        for(Multimedium m:this.multimedia){
            Log.d("Model: Doc","Image subtype--> "+m.getSubtype());
            if(m.getSubtype().equalsIgnoreCase("thumbnail")){
                return m.getUrl();
            }
        }
        return null;
    }

    public Headline getHeadline() {
        return headline;
    }

    public void setHeadline(Headline headline) {
        this.headline = headline;
    }

    public String getNewsDesk() {
        return newsDesk;
    }

    public void setNewsDesk(String newsDesk) {
        this.newsDesk = newsDesk;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.webUrl);
        dest.writeList(this.multimedia);
        dest.writeParcelable(this.headline, flags);
        dest.writeString(this.newsDesk);
        dest.writeString(this.snippet);
    }

    public Doc() {
    }

    protected Doc(Parcel in) {
        this.webUrl = in.readString();
        this.multimedia = new ArrayList<Multimedium>();
        in.readList(this.multimedia, Multimedium.class.getClassLoader());
        this.headline = in.readParcelable(Headline.class.getClassLoader());
        this.newsDesk = in.readString();
        this.snippet = in.readString();
    }

    public static final Creator<Doc> CREATOR = new Creator<Doc>() {
        @Override
        public Doc createFromParcel(Parcel source) {
            return new Doc(source);
        }

        @Override
        public Doc[] newArray(int size) {
            return new Doc[size];
        }
    };
}
