<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ page import="java.util.Set" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html> 
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <script src="//ajax.googleapis.com/ajax/libs/jquery/2.1.0/jquery.min.js"></script>
        <link href="//cdnjs.cloudflare.com/ajax/libs/x-editable/1.5.0/jquery-editable/css/jquery-editable.css" rel="stylesheet"/>
        <script src="//cdnjs.cloudflare.com/ajax/libs/x-editable/1.5.0/jquery-editable/js/jquery-editable-poshytip.min.js"></script>
        <link rel="stylesheet" type="text/css" href="/resources/block.css">
        <title>District Membership Report</title>
    </head>
    <body>
<sec:authorize access="hasRole('ROLE_ADMIN')">  
<script>
$.fn.editable.defaults.mode = 'inline';
$(document).ready(function() {
    <c:forEach items="${districts}" var="district">
        $('#representative_<c:out value="${district.getName()}"/>').editable();
    </c:forEach>
});            
</script>
</sec:authorize>
    <%@include file="header.jsp" %>

        <table cellpadding="0" cellspacing="0" border="0">
            <tr>
                <td valign="top">
        <table cellpadding="5" cellspacing="0" border="1" style="margin-top: 15px; margin-left: 15px;">
            <tr>
                <td>Houses</td>
                <td><fmt:formatNumber value="${houses.size()}" /></td>
            </tr>
            <tr>
                <td>Total Membership</td>
                <td>
                    <table cellpadding="5" cellspacing="0" border="1" style="margin-top: 15px; margin-left: 15px;">
                        <tr>
                            <th>2013</th>
                            <th>2014</th>
                            <th>2015</th>
                            <th>2016</th>
                        </tr>
                        <tr>
                            <td align="right"><c:out value='${districtService.getPercentMembership("2013")}' />%</td>
                            <td align="right"><c:out value='${districtService.getPercentMembership("2014")}' />%</td>
                            <td align="right"><c:out value='${districtService.getPercentMembership("2015")}' />%</td>
                            <td align="right"><c:out value='${districtService.getPercentMembership("2016")}' />%</td>
                        </tr>
                        <tr>
                            <td align="right"><fmt:formatNumber value='${districtService.getMembershipCount("2013")}' /></td>
                            <td align="right"><fmt:formatNumber value='${districtService.getMembershipCount("2014")}' /></td>
                            <td align="right"><fmt:formatNumber value='${districtService.getMembershipCount("2015")}' /></td>
                            <td align="right"><fmt:formatNumber value='${districtService.getMembershipCount("2016")}' /></td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
                </td>
                <td>
            
        <table cellpadding="5" cellspacing="0" border="1" style="margin-top: 15px; margin-left: 15px;">
            <tr>
                <th rowspan="2">Name</th>
                <th rowspan="2">Representative</th>
                <th colspan="4" valign="top">Membership</th>
            </tr>
            <tr>
                <th>2013</th>
                <th>2014</th>
                <th>2015</th>
                <th>2016</th>
            </tr>
            <c:forEach items="${districts}" var="district">
                <tr>
                    <td>
                        <a href='/district/${district.name}'><c:out value="${district.name}"/></a>
                    </td>
                    <td>
                        <div class='value editable' id='representative_<c:out value="${district.getName()}"/>' data-type="text" data-url='/district/update_representative' data-pk='<c:out value="${district.getName()}"/>' data-name='representative'>
                            <c:out value="${officierService.getDistrictRepresentative(district.name)}" />
                        </div>
                    </td>
                    <td align="right"><c:out value='${districtService.getPercentMembership(district.name, "2013")}' />%</td>
                    <td align="right"><c:out value='${districtService.getPercentMembership(district.name, "2014")}' />%</td>
                    <td align="right"><c:out value='${districtService.getPercentMembership(district.name, "2015")}' />%</td>
                    <td align="right"><c:out value='${districtService.getPercentMembership(district.name, "2016")}' />%</td>
                </tr>
            </c:forEach>
        </table>
                </td>
            </tr>
        </table>
    </body>
</html>