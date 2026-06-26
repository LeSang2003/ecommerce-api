package com.demo.dto.lookbook;
import java.util.List;
import lombok.*;

@Getter
@Setter
public class LookbookSectionResponse {

    private Long id;

    private Integer displayOrder;

    private String type;

    private String title;

    private String content;

    private String videoUrl;

    private List<LookbookImageResponse> images;


}