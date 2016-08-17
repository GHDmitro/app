<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%--
  Created by IntelliJ IDEA.
  User: macbookair
  Date: 16.07.16
  Time: 21:05
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link href="<c:url value="../pages/css/bootstrap.min.css"/> " rel="stylesheet">
  <link href="<c:url value="../pages/assets/css/ie10-viewport-bug-workaround.css"/> " rel="stylesheet">

  <!-- Custom pages.styles for this template -->
  <link href="<c:url value="../pages/styles/offcanvas.css"/> " rel="stylesheet">

  <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
  <!--[if lt IE 9]><![endif]-->
  <script src="<c:url value="../pages/assets/js/ie8-responsive-file-warning.js "/> "></script>
  <script src="<c:url value="../pages/assets/js/ie-emulation-modes-warning.js"/> "></script>
    <title></title>


  <script type="text/javascript">
    function sentest() {
      var methodurl = "/sentest";
      $.ajax({
        type: "POST",
        url: methodurl,
        data: $("#jsdhbjhsdv").serialize(),
        success: function (message) {
          $('#p').append(message.msg);  //"Товар заказан"
        }
      });
    }
  </script>
</head>
<body>
<%--<form enctype="multipart/form-data" action="/test" method="post">--%>
  <%--<input type="file" name="file" placeholder="file">--%>
<%--</form>--%>

<%--<button onclick="bookAjax('Dima Dima Dima 58768756')">Click</button>--%>


<form name="send" id="jsdhbjhsdv">
  <input type="number" min="1" required name="capacity" style="width: 50px"/>
  <a class="btn btn-success" type="button"
     onclick="sentest()">Заказать</a>


</form>
<p class="text" id="p"></p>

<%--<h1><c:out value="${text1}"/> fytrdrtdtrdtrd</h1>--%>
<%--<h1><c:out value="${text2}"/> fytrdrtdtrdtrd</h1>--%>
<%--<h1><c:out value="${text}"/> fytrdrtdtrdtrd</h1>--%>
<%--<h1><c:out value="${text3}"/> hvjhgvvhgjvhjv</h1>--%>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.pages.jsmy "></script>
<script>window.jQuery || document.write('<script src="/pages/assets/js/vendor/jquery.min.js"><\/script>')</script>
<script src="<c:url value="../pages/js/bootstrap.min.js"/> "></script>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="<c:url value="../pages/assets/js/ie10-viewport-bug-workaround.js"/> "></script>
<script src="<c:url value="../pages/jsmy/offcanvas.js"/> "></script>

</body>
</html>
