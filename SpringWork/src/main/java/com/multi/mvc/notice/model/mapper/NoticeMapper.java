package com.multi.mvc.notice.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.multi.mvc.notice.model.vo.Notice;
import com.multi.mvc.notice.model.vo.NoticeRead;

@Mapper
public interface NoticeMapper {
	List<Notice> selectNoticeList(Map<String, Object> map);
	int selectNoticeCount(Map<String, Object> map);
	Notice selectNoticeByNo(int bno);
	int insertNotice(Notice board);
	int insertNoticeRead(NoticeRead noticeRead);
	int updateNotice(Notice board);
	int updateReadCount(Notice board);
	int deleteNotice(int bno);
	List<NoticeRead> selectNoticeReadList(int bno);
}
