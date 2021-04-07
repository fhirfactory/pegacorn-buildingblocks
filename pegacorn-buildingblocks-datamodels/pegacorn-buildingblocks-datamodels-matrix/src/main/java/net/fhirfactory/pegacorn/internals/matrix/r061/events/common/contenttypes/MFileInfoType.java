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

public class MFileInfoType {
    private String mimeType;
    private Integer size;
    private String thumbnailURL;
    private MEncryptedFileType thumbnailFile;
    private MThunmbnailInfoType thumbnailInfo;

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
    public String getThumbnailURL() {
        return thumbnailURL;
    }

    @JsonProperty("thumbnail_url")
    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    @JsonProperty("thumbnail_file")
    public MEncryptedFileType getThumbnailFile() {
        return thumbnailFile;
    }

    @JsonProperty("thumbnail_file")
    public void setThumbnailFile(MEncryptedFileType thumbnailFile) {
        this.thumbnailFile = thumbnailFile;
    }

    @JsonProperty("thumbnail_info")
    public MThunmbnailInfoType getThumbnailInfo() {
        return thumbnailInfo;
    }

    @JsonProperty("thumbnail_info")
    public void setThumbnailInfo(MThunmbnailInfoType thumbnailInfo) {
        this.thumbnailInfo = thumbnailInfo;
    }
}
