package com.example.webswipeservice.modal.video;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@TableName("category_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryInfo {
    @TableId
    private long id;
    private String categoryKey;
    private String text;
}
