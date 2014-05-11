<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html> 
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <link rel="stylesheet" type="text/css" href="/resources/block.css">
        <title>Greenbriar Membership Management</title>
    </head>
    <body>
        <%@include file="header.jsp" %>
        <sec:authorize access="hasRole('ROLE_REPORT')">
            Go look for the PDF file on the server.
        </sec:authorize>
    </body>
</html>