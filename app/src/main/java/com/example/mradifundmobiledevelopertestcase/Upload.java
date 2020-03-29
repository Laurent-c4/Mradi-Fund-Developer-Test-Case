package com.example.mradifundmobiledevelopertestcase;

public class Upload {
    private String mName;
    private String mPDFUrl;

    public Upload(){

    }

    public Upload(String name, String PDFUrl ) {
        mName = name;
        mPDFUrl = PDFUrl;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPDFUrl() {
        return mPDFUrl;
    }

    public void setmPDFUrl(String mPDFUrl) {
        this.mPDFUrl = mPDFUrl;
    }
}
