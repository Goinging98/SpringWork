<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="path" value="${pageContext.request.contextPath}"/>


<style>
    section>div#board-write-container{width:600px; margin:0 auto; text-align:center;}
    section>div#board-write-container h2{margin:10px 0;}
    table#tbl-board{width:500px; margin:0 auto; border:1px solid black; border-collapse:collapse; clear:both; }
    table#tbl-board th {width: 125px; border:1px solid; padding: 5px 0; text-align:center;} 
    table#tbl-board td {border:1px solid; padding: 5px 0 5px 10px; text-align:left;}
    div#comment-container button#btn-insert{width:60px;height:50px; color:white; background-color:#3300FF;position:relative;top:-20px;}
    
    /*댓글테이블*/
    table#tbl-comment{width:580px; margin:0 auto; border-collapse:collapse; clear:both; } 
    table#tbl-comment tr td{border-bottom:1px solid; border-top:1px solid; padding:5px; text-align:left; line-height:120%;}
    table#tbl-comment tr td:first-of-type{padding: 5px 5px 5px 50px;}
    table#tbl-comment tr td:last-of-type {text-align:right; width: 100px;}
    table#tbl-comment button.btn-delete{display:none;}
    table#tbl-comment tr:hover {background:lightgray;}
    table#tbl-comment tr:hover button.btn-delete{display:inline;}
    table#tbl-comment sub.comment-writer {color:navy; font-size:14px}
    table#tbl-comment sub.comment-date {color:tomato; font-size:10px}
</style>



<jsp:include page="/WEB-INF/views/common/header.jsp">
	<jsp:param value="공지사항 상세조회" name="title"/>
</jsp:include>


<section id="board-write-container">
	<h2 align="center">공지사항 상세조회</h2>
	
	<table id="tbl-board">
		<tr>
			<th>글번호</th>
			<td><c:out value="${notice.bno}"/></td>
		</tr>
		<tr>
			<th>제목</th>
			<td><c:out value="${notice.title}"/></td>
		</tr>
		<tr>
			<th>종류</th>
			<td>공지사항</td>
		</tr>
		<tr>
			<th>작성자</th>
			<td><c:out value="${notice.writerId}"/></td>
		</tr>
		<tr>
			<th>조회수</th>
			<td><c:out value="${notice.readCount}"/></td>
		</tr>
		<tr>
			<th>작성 시간</th>
			<td> <fmt:formatDate type="both" value="${notice.createDate}"/> </td>
		</tr>
		<tr>
			<th>수정 시간</th>
			<td> <fmt:formatDate type="both" value="${notice.modifyDate}"/> </td>
		</tr>
		<tr>
			<th>첨부파일</th>
			<td>
				<c:if test="${not empty notice.originalFileName}">
					<a href="javascript:fileDownload('${notice.originalFileName}',
																'${notice.renamedFileName}');">
						<img src="${path}/resources/images/file.png" width="20" height="20"/>
						<c:out value="${notice.originalFileName}"></c:out>		
					</a>
				</c:if>
				
				<c:if test="${empty notice.originalFileName}">
					<span> - </span>
				</c:if>
			</td>
		</tr>
		<tr>
			<th>첨부파일(이미지)</th>
			<td>
				<c:if test="${not empty notice.originalFileName 
								and (fn:contains(notice.originalFileName,'.jpg')
									 or fn:contains(notice.originalFileName,'.png')
									  or fn:contains(notice.originalFileName,'.jpeg'))}">
							<img src="${path}/resources/upload/notice/${notice.renamedFileName}"
								width="100%" height="100%"/>
				</c:if>
			</td>
		</tr>
		<tr>
			<th>내용</th>
			<td>
				<textarea rows="15" cols="50" readonly><c:out value="${notice.content}"/></textarea>
			</td>
		</tr>
		<!-- 수정 / 삭제 기능 -->
		<tr>
			<th colspan="2">
				<c:if test="${not empty loginMember && (loginMember.id == notice.writerId 
									|| loginMember.role == 'ROLE_ADMIN')}">
					<button type="button" id="btnUpdate">수정</button>
					<button type="button" id="btnDelete">삭제</button>
				</c:if>
			</th>
		</tr>
	</table>
	
   	<!-- 리플 출력 -->
	<table id="tbl-comment">
		
		<tr>
			<td colspan="3" style="text-align: center;"><h3>읽은 사람 리스트</h3></td>
		</tr>
		<c:if test="${!empty readList}">
			<c:forEach var="readInfo" items="${readList}">
				<tr>
					<td>
						<sub class="comment-writer">${readInfo.id}</sub>
						<sub class="comment-writer">${readInfo.name}</sub>
						<sub class="comment-date"><fmt:formatDate type="both" value="${readInfo.createDate}"/></sub>	
					</td>
					<td>
					</td>
				</tr>
			</c:forEach>
		</c:if>
	</table>
</section>
<br>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>



<script type="text/javascript">
	$(document).ready(() => {
		$("#btnUpdate").click((e) => {
			location.href = "${path}/notice/update?no=${notice.bno}";
		});
		
		$("#btnDelete").click((e) => {
			if(confirm("정말로 게시글을 삭제 하시겠습니까?")) {
				location.replace("${path}/notice/delete?no=${notice.bno}");
			}
		});
	});
	
	function deleteReply(replyNo, boardNo){
		var url = "${path}/board/replyDel?rno=";
		var requestURL = url + replyNo +"&bno=" + boardNo;
		location.replace(requestURL);
	}
	
	function fileDownload(originName, reName){
		const url = '${path}/board/fileDown';
		originName = encodeURIComponent(originName)
		reName = encodeURIComponent(reName)
		location.href = url + '?originName=' + originName + '&reName=' + reName;
	}
</script>
