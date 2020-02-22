package com.example.mongodbfileserver.fileserver.domain;

import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.Date;

/**
 * @author xxf
 */
@Document
@lombok.Data
public class File {

    @Id
    private String id;

    /** 文件名 */
    private String name;

    /** 文件类型 */
    private String contentType;
    private long size;
    private Date uploadDate;
    private String md5;

    /** 文件内容 */
    private Binary content;

    /** 文件路径 */
    private String path;


    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        File fileInfo = (File) object;
        return java.util.Objects.equals(size, fileInfo.size)
                && java.util.Objects.equals(name, fileInfo.name)
                && java.util.Objects.equals(contentType, fileInfo.contentType)
                && java.util.Objects.equals(uploadDate, fileInfo.uploadDate)
                && java.util.Objects.equals(md5, fileInfo.md5)
                && java.util.Objects.equals(id, fileInfo.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, contentType, uploadDate, md5, id, size);
    }

    @Override
    public String toString() {
        return "File{"
                + "name='" + name + '\''
                + ", contentType='" + contentType + '\''
                + ", size=" + size
                + ", uploadDate=" + uploadDate
                + ", md5='" + md5 + '\''
                + ", id='" + id + '\''
                + '}';
    }

    public File(String name, String contentType, long size,Binary content) {
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.content = content;
        this.uploadDate =new Date();
    }
}
