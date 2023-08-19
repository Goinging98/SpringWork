package com.multi.mvc.notice.model.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRead {
	private int bno;
	private int mno;
	private Date createDate;
	private String id;
	private String name;
}
