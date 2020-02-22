package com.example.mongodbfileserver.fileserver.controller;

import com.example.mongodbfileserver.fileserver.domain.File;
import com.example.mongodbfileserver.fileserver.service.FileService;
import com.example.mongodbfileserver.fileserver.util.MD5Util;
import lombok.Getter;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.lang.model.util.Elements;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;


@Controller
@CrossOrigin(origins = "*", maxAge = 3600)
public class FileController {
    @Autowired
    private FileService fileService;

    @Value("${server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String serverPort;

    /**
     * 首页展示20条数据
     * @param model
     * @return
     */
    @RequestMapping(value = "/")
    public String index(Model model) {
        model.addAttribute("files", fileService.listFileByPage(0,20));
        return "index";
    }

    /**
     * 分页查询文件
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @GetMapping(value = "files/{pageIndex}/{pageSize}")
    @ResponseBody
    public List<File> listFIlesByPage(
            @PathVariable int pageIndex,
            @PathVariable int pageSize) {
        return fileService.listFileByPage(pageIndex, pageSize);
    }

    /**
     * 根据id获取文件片信息
     * @param id
     * @return
     */
    @GetMapping(value = "files/{id}")
    @ResponseBody
    public ResponseEntity<Object> serveFile(
            @PathVariable String id) {
        Optional<File> file = fileService.getFileById(id);

        if (file.isPresent()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attach-ment; fileName=\"" +
                            file.get().getName()+"\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .header(HttpHeaders.CONTENT_LENGTH, file.get().getSize()+ "")
                    .header("Connection", "close")
                    .body(file.get().getContent().getData());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not fount");
        }
    }

    /**
     * 在线显示文件
     * @param id
     * @return
     */
    @GetMapping("/views/{id}")
    @ResponseBody
    public ResponseEntity<Object> serveFileOnLine(@PathVariable String id) {
        Optional<File> file = fileService.getFileById(id);

        if (file.isPresent()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "fileName=\"" + file.get().getName()+ "\"")
                    .header(HttpHeaders.CONTENT_TYPE, file.get().getSize()+"").header("Connection", "close")
                    .body(file.get().getContent().getData());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File was not found");
        }
    }

    /**
     * 上传文件
     * @param file
     * @param redirectAttributes
     * @return
     */
    @PostMapping(value = "/")
    public String handleFileUpload(@RequestParam("file")MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            File f = new File(file.getOriginalFilename(), file.getContentType(), file.getSize(),
                    new Binary(file.getBytes()));
            f.setMd5(MD5Util.getMD5(file.getInputStream()));
            fileService.saveFile(f);
        } catch (IOException | NoSuchAlgorithmException | NoSuchFieldException ex) {
            ex.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Your" +
                    file.getOriginalFilename() + "is wrong!");
            return "redirect:/";
        }

        redirectAttributes.addFlashAttribute("message", "You successfully uploaded" +
                file.getOriginalFilename() + "!");
        return "redirect:/";
    }

    /**
     * 上传接口
     * @param file
     * @return
     */
    @PostMapping(value = "/upload")
    @ResponseBody
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        File retuinFile = null;
        try {
            File f = new File(file.getOriginalFilename(), file.getContentType(), file.getSize(), new Binary(file.getBytes()));
            f.setMd5(MD5Util.getMD5(file.getInputStream()));
            retuinFile = fileService.saveFile(f);
            String path = "//" + serverAddress + ":" + serverPort + "/view" + retuinFile.getId();
            return ResponseEntity.status(HttpStatus.OK).body(path);
        } catch (IOException | NoSuchAlgorithmException | NoSuchFieldException ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    @DeleteMapping("/id")
    @ResponseBody
    public ResponseEntity<String> deletFile(@PathVariable String id) {
        try {
            fileService.removeFile(id);
            return ResponseEntity.status(HttpStatus.OK).body("DELETE Success!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
