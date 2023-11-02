package com.example.webswipeservice.modal.video;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadedVideo {
    // 视频参数
    MultipartFile file;
    List<String> categories;
    String description;
    int isVertical;

    // 封面参数
    int coverH;
    int coverW;
    String coverImgType;
    int coverOffset;
}
