package com.gocam.goscamdemopro.vphoto.data;

import static com.gos.platform.api.result.PlatResult.PlatCmd.uploadFileVPhoto;

import com.gos.platform.api.contact.PlatCode;
import com.gos.platform.api.result.PlatResult;

public class UploadFileResult extends PlatResult {
    private final String TAG = this.getClass().getSimpleName();

    private String url;

    private String message;

    public UploadFileResult(int code, okhttp3.Response response){
        super(uploadFileVPhoto, 0, code, null);
        if (response == null) {
            return;
        }
        if (response.code() == 200) {
            responseCode = PlatCode.SUCCESS;
        } else {
            responseCode = response.code();
        }
        this.message = response.message();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
