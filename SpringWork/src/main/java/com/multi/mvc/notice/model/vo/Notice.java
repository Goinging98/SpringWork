package com.multi.mvc.notice.model.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notice {
	private int bno;
	private int mno;
	private String writerId;
	private String title;
	private String content;
	private String type;
	private String originalFileName;
	private String renamedFileName;
	private int readCount;
	private String status;
	private Date createDate;
	private Date modifyDate;
}
