<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html> 
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <link rel="stylesheet" type="text/css" href="/resources/block.css">
        <title>Greenbriar Membership Management</title>
    </head>
    <body>
        <%@include file="header.jsp" %>
        <h1>Bad Email Values</h1>

        <ul>
            <c:forEach items="${peopleService.getPeopleWithBadEmails()}" var="person">
                <a href="/person/editform/<c:out value='${person.getPk()}' />">
                    <c:out value='${person.getEmail()}' />
                </a><br/>
            </c:forEach>
        </ul>
    </body>
</html>
