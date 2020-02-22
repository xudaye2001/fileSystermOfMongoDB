package com.example.mongodbfileserver.fileserver.repository;

import com.example.mongodbfileserver.fileserver.domain.File;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 储存库
 * @author xxf
 *
 */
public interface FileRepository  extends MongoRepository<File, String> {
}
