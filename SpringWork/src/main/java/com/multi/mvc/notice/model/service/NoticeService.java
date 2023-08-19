package com.multi.mvc.notice.model.service;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.multi.mvc.common.util.PageInfo;
import com.multi.mvc.notice.model.mapper.NoticeMapper;
import com.multi.mvc.notice.model.vo.Notice;
import com.multi.mvc.notice.model.vo.NoticeRead;

@Service
public class NoticeService {
	
	@Autowired
	private NoticeMapper mapper;
	
	public int getNoticeCount(Map<String, Object> param) {
		return mapper.selectNoticeCount(param);
	}

	public List<Notice> selectNoticeList(PageInfo pageInfo, Map<String, Object> param) {
		param.put("limit", pageInfo.getListLimit());
		param.put("offset", (pageInfo.getStartList() - 1));
		return mapper.selectNoticeList(param);
	}

	public Notice findByNo(int no) {
		Notice notice = mapper.selectNoticeByNo(no);
		notice.setReadCount(notice.getReadCount() + 1);
		mapper.updateReadCount(notice);
		return notice;
	}

	private static int count = 0;
	public String saveFile(MultipartFile upfile, String savePath) {
		File folder = new File(savePath);
		
		// 폴더가 없으면 경로채 폴더 만들어주는 코드
		if(folder.exists() == false) {
			folder.mkdirs();
		}
		System.out.println(savePath);
		
		// 파일 이름을 날짜시간 + 랜덤하게 바꾸는 코드, text.txt -> 20230522_14230230202.txt
		String originalFileName = upfile.getOriginalFilename();
		String renamedFileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS"));
		renamedFileName += (count++);									
		renamedFileName += originalFileName.substring(originalFileName.lastIndexOf("."));
		String renamedPath = savePath + "/" + renamedFileName;
		
		try {
			// 실제 파일이름을 변경하여 저장하는 코드
			upfile.transferTo(new File(renamedPath));
		} catch (Exception e) {
			return null;
		}
		
		return renamedFileName;
	}

	@Transactional(rollbackFor = Exception.class)
	public int saveNotice(Notice notice) {
		int result = 0;
		if(notice.getBno() == 0) {
			result = mapper.insertNotice(notice);
		} else {
			result = mapper.updateNotice(notice);
		}
		return result;
	}

	public void deleteFile(String path) {
		File file = new File(path);
		if(file.exists()) {
			file.delete();
		}
	}
	
	
	public List<NoticeRead> selectNoticeReadList(int bno) {
		return mapper.selectNoticeReadList(bno);
	}
	
	
	@Transactional(rollbackFor = Exception.class)
	public int deleteNotice(int no, String savePath) {
		Notice notice = mapper.selectNoticeByNo(no);
		deleteFile(savePath + "\\" + notice.getRenamedFileName());
		return mapper.deleteNotice(no);
	}

	@Transactional()
	public int insertNoticeRead(NoticeRead noticeRead) {
		return mapper.insertNoticeRead(noticeRead);
	}

}
