<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="path" value="${pageContext.request.contextPath}"/>

<style>
	section#board-list-container{width:600px; margin:0 auto; text-align:center;}
	section#board-list-container h2{margin:10px 0;}
	table#tbl-board{width:100%; margin:0 auto; border:1px solid black; border-collapse:collapse; clear:both; }
	table#tbl-board th, table#tbl-board td {border:1px solid; padding: 5px 0; text-align:center;} 
	input#btn-add{float:right; margin: 0 0 15px;}
	div#pageBar{margin-top:10px; text-align:center; background-color:rgba(0, 188, 212, 0.3);}
    .blue_window { display: inline-block; width: 366px;  border: 3px solid #6ccaf5; }
    .input_text { width: calc( 100% - 14px); margin: 6px 7px; border: 0;  background: #007dd100; font-weight: bold;font-size: 16px; outline: none;}
    .sch_smit {width: 54px; height: 40px; margin: 0; border: 0; vertical-align: top; background: #007dd1; color: white; font-weight: bold; border-radius: 1px; cursor: pointer;}
    .sch_smit:hover {background: #007dd1;}
    #board-list-container h2{text-align: center; margin-top: 5px; margin-bottom: 5px}
</style>


<jsp:include page="/WEB-INF/views/common/header.jsp">
	<jsp:param value="회원관리" name="title"/>
</jsp:include>


<c:set var="searchType" value="${param.searchType}"/>
<c:if test="${empty searchType}">
	<c:set var="searchType" value="${'title'}"/>
</c:if>

<section id="content">
	<div id="board-list-container">
		<h2>회원 관리</h2>
			
		<!-- 회원 리스트 시작 -->	
		<table id="tbl-board">
			<tr>
				<th>번호</th>
				<th>id</th>
				<th>이름</th>
				<th>가입일</th>
			</tr>
			
			<c:if test="${empty list}">
				<tr>
					<td colspan="6">조회된 회원이 없습니다.</td>
				</tr>
			</c:if>
			
			<c:if test="${not empty list}">
				<c:forEach var="item" items="${list}">
					<tr>
						<td><c:out value="${item.mno}"/></td>
						<td><c:out value="${item.id}"/></td>
						<td><c:out value="${item.name}"/></td>
						<td><c:out value="${item.enrollDate}"/></td>
					</tr>
				</c:forEach>
			</c:if>
		</table>
		<!-- 게시판 리스트 끝 -->	
		
		<!-- page부 시작 -->
		<div align="center">
			<%-- 
			순수 페이지만 이동하는 코드
			<!-- 처음 페이지 -->
			<button onclick="location.href='${path}/board/list?page=1'">&lt;&lt;</button>
			<!-- 이전 페이지 -->
			<button onclick="location.href='${path}/board/list?page=${pageInfo.prevPage}'">&lt;</button>
			--%>
			
			<!-- 처음 페이지 -->
			<button onclick="movePage(1)">&lt;&lt;</button>
			<!-- 이전 페이지 -->
			<button onclick="movePage(${pageInfo.prevPage})">&lt;</button>
		
			<!-- 10개 페이지가 보여지는 부분 -->
			<c:forEach begin="${pageInfo.startPage}" end="${pageInfo.endPage}" varStatus="status" step="1">
				<c:if test="${status.current == pageInfo.currentPage}">
					<button disabled>${status.current}</button>
				</c:if>
				<c:if test="${status.current != pageInfo.currentPage}">
					<button onclick="movePage(${status.current})">
						${status.current}
					</button>
				</c:if>
			</c:forEach>
			
			<!-- 다음 페이지 -->
			<button onclick="movePage(${pageInfo.nextPage})">&gt;</button>
			<!-- 마지막 페이지 -->
			<button onclick="movePage(${pageInfo.maxPage})">&gt;&gt;</button>
		
		</div>
		<!-- page부 끝 -->
	</div>
	<br>
</section>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

<script type="text/javascript">
	function movePage(page){
		searchForm.page.value = page;
		searchForm.submit();
	}
</script>

