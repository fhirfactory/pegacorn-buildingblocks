/*
 * Copyright (c) 2021 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.pegacorn.internals.matrix.r061.events.common.contenttypes;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MVideoInfoType {
    private Integer duration;
    private Integer pixelHeight;
    private Integer pixelWidth;
    private String mimeType;
    private Integer size;
    private String videoThumbnailURL;
    private MEncryptedFileType videoThumbnailFile;
    private MThunmbnailInfoType videoThumbnailInfo;

    @JsonProperty("duration")
    public Integer getDuration() {
        return duration;
    }

    @JsonProperty("duration")
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @JsonProperty("h")
    public Integer getPixelHeight() {
        return pixelHeight;
    }

    @JsonProperty("h")
    public void setPixelHeight(Integer pixelHeight) {
        this.pixelHeight = pixelHeight;
    }

    @JsonProperty("w")
    public Integer getPixelWidth() {
        return pixelWidth;
    }

    @JsonProperty("w")
    public void setPixelWidth(Integer pixelWidth) {
        this.pixelWidth = pixelWidth;
    }

    @JsonProperty("mimetype")
    public String getMimeType() {
        return mimeType;
    }

    @JsonProperty("mimetype")
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @JsonProperty("size")
    public Integer getSize() {
        return size;
    }

    @JsonProperty("size")
    public void setSize(Integer size) {
        this.size = size;
    }

    @JsonProperty("thumbnail_url")
    public String getVideoThumbnailURL() {
        return videoThumbnailURL;
    }

    @JsonProperty("thumbnail_url")
    public void setVideoThumbnailURL(String videoThumbnailURL) {
        this.videoThumbnailURL = videoThumbnailURL;
    }

    @JsonProperty("thumbnail_file")
    public MEncryptedFileType getVideoThumbnailFile() {
        return videoThumbnailFile;
    }

    @JsonProperty("thumbnail_file")
    public void setVideoThumbnailFile(MEncryptedFileType videoThumbnailFile) {
        this.videoThumbnailFile = videoThumbnailFile;
    }

    @JsonProperty("thumbnail_info")
    public MThunmbnailInfoType getVideoThumbnailInfo() {
        return videoThumbnailInfo;
    }

    @JsonProperty("thumbnail_info")
    public void setVideoThumbnailInfo(MThunmbnailInfoType videoThumbnailInfo) {
        this.videoThumbnailInfo = videoThumbnailInfo;
    }
}
