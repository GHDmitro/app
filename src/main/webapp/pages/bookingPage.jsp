<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%--
  Created by IntelliJ IDEA.
  User: macbookair
  Date: 12.05.16
  Time: 15:17
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<html>
<head>
    <title>bookingPage</title>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    <%--<link rel="icon" href="../../favicon.ico">--%>

    <title>Off Canvas Template for Bootstrap</title>

    <!-- Bootstrap core CSS -->
    <link href="<c:url value="../pages/css/bootstrap.min.css"/> " rel="stylesheet">

    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <link href="<c:url value="../pages/assets/css/ie10-viewport-bug-workaround.css"/> " rel="stylesheet">

    <!-- Custom pages.styles for this template -->
    <link href="<c:url value="../pages/styles/offcanvas.css"/> " rel="stylesheet">

    <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
    <!--[if lt IE 9]><![endif]-->
    <script src="<c:url value="../pages/assets/js/ie8-responsive-file-warning.js "/> "></script>
    <script src="<c:url value="../pages/assets/js/ie-emulation-modes-warning.js"/> "></script>

    <script type="text/javascript">
        function confirmBookAjax(bookingId,posId) {
            var methodurl = "/confirmBookAjax";
            $.ajax({
                type: "POST",
                url: methodurl,
                data: $("#confirmBookAjax" + bookingId+ posId).serialize(),
                success: function (booking) {

                    if (booking !== null) {

                        var bookingPositions = booking.bookingPositions;
                        var isExist = false;

                        for (var index1 in bookingPositions) {
                            if (posId == bookingPositions[index1].id){
                                isExist = true;
                            }
                                $('#' + bookingPositions[index1].id + 'capacity').text('').append("Колличество в заказе: ").append(bookingPositions[index1].capacity);
                                $('#' + bookingPositions[index1].id + 'number').text('').append("Колличество в заказе: ").append(bookingPositions[index1].capacity);
                        }

                        if (!isExist){
                            var element = document.getElementById(posId+'bookingPosition');
                            element.parentNode.removeChild(element);
                        }
                    }else{

                    }
                }, error : function (error){
                    if (error != null){
//                        alert(error);
                        var element1 = document.getElementById(bookingId + 'booking');
                        element1.parentNode.removeChild(element1);
                    }
                }
            });
        }
    </script>


</head>
<body>
<sec:authorize access="hasRole('customer')">
    <nav class="navbar navbar-static-top navbar-inverse">
        <div class="container">
            <div class="navbar-header">
                <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar"
                        aria-expanded="false" aria-controls="navbar">
                    <span class="sr-only">Toggle navigation</span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <!-- <a class="active" href="#">Прайс лист</a> -->
            </div>

            <div id="navbar" class="collapse navbar-collapse">
                <ul class="nav navbar-nav navbar-right">
                        <%--<li><p class="text"><sec:authentication property="principal.username"/></p></li>--%>
                    <li><a href="<c:url value="/home"/>">На главную</a></li>

                    <li><a href="<c:url value="/deleteAllBooking"/>">Удалить все</a></li>
                    <!-- <li><a href="#">Войти</a></li> -->
                </ul>
            </div>

        </div>
        <!-- /.container -->
    </nav>


    <%--<div id="lol">---------</div>--%>
    <c:if test="${bookingList == null}">
        <h3 class="text-center">Заказов пока что нет</h3>
    </c:if>
    <c:forEach items="${bookingList}" var="booking">
        <div class="container marketing" id="${booking.id}booking">
            <h2 class="text-center"><p>Заказы от <c:out value="${booking.accountClient.login}"/></p></h2>
            <hr class="featurette-divider">
                <%--<form enctype="multipart/form-data" action="<c:url value="/changePositionPost"/> " method="post">--%>
            <div class="row featurette">

                <div class="col-xs-12 col-sm-6 col-md-6 col-md-5 col-lg-5">
                    <c:if test="${booking.accountClient.photoAccount != null}">
                        <img class="featurette-image img-responsive center-block" width="350" height="350"
                             src="<c:url value="/givePhoto/${booking.accountClient.photoAccount}"/>"
                             alt="Изображение загружается">
                        <%----%>
                    </c:if>
                    <c:if test="${booking.accountClient.photoAccount == null}">
                        <img class="featurette-image img-responsive center-block" width="350" height="350"
                             src="<c:url value="/img/defaultPhotoToScreen.png"/>"
                             alt="Изображение загружается">
                    </c:if>

                    <br><br>
                    <ul class="text-center">
                        <li><p class="lead">Email : <c:out value="${booking.accountClient.email}"/></p></li>
                        <li><p class="lead">Телефон :<c:out value="${booking.accountClient.telNumber}"/></p></li>
                    </ul>
                </div>

                <div class="col-xs-12 col-sm-6 col-md-6 col-md-7 col-lg-5" id="${booking.id}positions">
                        <%--<div class="col-md-7 col-md-push-5 col-sm-10 col-xs-12">--%>
                    <c:forEach items="${booking.bookingPositions}" var="bookingPosition">


                        <%--Here will name of positioin from product--%>


                        <%--<ul class="text-center">--%>
                            <%--<li style="list-style-type: none"><h3 class="featurette-heading">Название продукта: <c:out--%>
                                    <%--value="${bookingPosition.product.name}"/></h3></li>--%>
                            <%--<li style="list-style-type: none"><h4 class="text-muted">Модель продукта: <c:out--%>
                                    <%--value="${bookingPosition.product.codeOfModel}"/></h4></li>--%>
                            <%--<li style="list-style-type: none"><h4 class="text-muted">Количество в заказе: <c:out--%>
                                    <%--value="${bookingPosition.capacity}"/></h4></li>--%>
                            <%--<li style="list-style-type: none ">--%>
                                <%--<form action="<c:url value="/confirmBooking"/>" method="post">--%>
                                    <%--<input type="hidden" name="positionID" value="${bookingPosition.id}">--%>
                                    <%--<input type="number" required min="0" name="capacity" style="width: 50px"--%>
                                           <%--value="${bookingPosition.capacity}"/>--%>
                                    <%--<button type="submit" class="btn btn-success">Подтвердить продажу</button>--%>
                                <%--</form>--%>
                            <%--</li>--%>
                        <%--</ul>--%>

                        <ul class="text-center" id="${bookingPosition.id}bookingPosition">
                            <li style="list-style-type: none">
                                <h3 class="featurette-heading" id="${bookingPosition.id}name">Название: <c:out
                                        value="${bookingPosition.product.name}"/></h3></li>
                            <li style="list-style-type: none"><h4 class="text-muted" id="${bookingPosition.id}model">
                                Модель: <c:out value="${bookingPosition.product.codeOfModel}"/></h4></li>
                            <li style="list-style-type: none"><h4 class="text-muted" id="${bookingPosition.id}capacity">
                                Количество в заказе: <c:out value="${bookingPosition.capacity}"/></h4></li>
                            <li style="list-style-type: none ">
                                <form id="confirmBookAjax${booking.id}${bookingPosition.id}">
                                    <input type="hidden" name="positionID" value="${bookingPosition.id}">
                                    <input type="number" required min="0" name="capacity" style="width: 50px"
                                           id="${bookingPosition.id}number" value="${bookingPosition.capacity}"/>
                                    <a type="button" class="btn btn-success"
                                       onclick="confirmBookAjax(${booking.id},${bookingPosition.id})">Подтвердить
                                        продажу</a>
                                </form>
                            </li>
                        </ul>

                        <%--<form name="confirm" id="confirmBookAjax${bookingPosition.id}">--%>
                        <%--<input type="hidden" name="positionID" value="${bookingPosition.id}">--%>
                        <%--<input type="number" required min="0" name="capacity" style="width: 50px"--%>
                        <%--value="${bookingPosition.capacity}"/>--%>

                        <%--<p>--%>
                        <%--<a type="button" class="btn btn-success" onclick="">Подтвердить продажу</a>--%>
                        <%--<div class="text-area" id="confirmResponse${bookingPosition.id}"></div>--%>
                        <%--</p>--%>
                        <%--</form>--%>
                        <%--<br><br>--%>

                    </c:forEach>
                </div>
                <br>


            </div>
                <%--</form>--%>

            <hr class="featurette-divider">

            <!-- /END THE FEATURETTES -->


            <!-- FOOTER -->
            <footer>
                <p class="pull-right"><a href="#">Back to top</a></p>
                    <%--<p>&copy; 2015 Company, Inc. &middot; <a href="#">Privacy</a> &middot; <a href="#">Terms</a></p>--%>
            </footer>
        </div>
    </c:forEach>
    <h3 class="text-center" id="notBooking"></h3>

</sec:authorize>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.pages.jsmy "></script>
<script>window.jQuery || document.write('<script src="/pages/assets/js/vendor/jquery.min.js"><\/script>')</script>
<script src="<c:url value="../pages/js/bootstrap.min.js"/> "></script>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="<c:url value="../pages/assets/js/ie10-viewport-bug-workaround.js"/> "></script>
<script src="<c:url value="../pages/jsmy/offcanvas.js"/> "></script>

</body>
</html>
