<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">
<head>
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

    <!-- HTML5 shim and Respond.pages.jsmymy for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <!--<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>-->
    <!--<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>-->
    <%--<![endif]-->--%>
    <script type="text/javascript">
        function bookAjax(posId) {
            var methodurl = "/bookAjax";
            $.ajax({
                type: "POST",
                url: methodurl,
                data: $("#" + posId).serialize(),
                success: function (product) {
                    $('#p' + posId).text('').append("Заказан ").append(product.name).append(" ").append(product.codeOfModel);   //"Товар заказан"
                }
            });
        }

    </script>
</head>

<body>
<sec:authorize access="isAuthenticated()">
    <nav class="navbar navbar-inverse navbar-static-top " id="navBarhead">
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
                <ul class="nav navbar-nav">
                        <%--<li class="active"><a href="#">Прайс лист</a></li>--%>
                    <sec:authorize access="hasRole('customer')">
                        <li><a href="<c:url value="/addPricePosition"/> ">Добавить позицию</a></li>
                        <li><a href="<c:url value="/bookingPage"/> ">Заказы</a></li>
                        <li><a href="<c:url value="/ownData"/> ">Личные данные пользователя</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasRole('client')">
                        <li><a href="<c:url value="/ownData/${login}"/> ">Личные данные ${login}</a></li>
                    </sec:authorize>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                        <%--<li><p class="text"><sec:authentication property="principal.username"/></p></li>--%>
                    <sec:authorize access="hasRole('client')">
                        <li><a href="<c:url value="/home"/> ">К списку продавцов</a></li>
                    </sec:authorize>
                    <sec:authorize access="hasRole('customer')">
                        <c:if test="${account.photoBackground1 == null or account.photoBackground2 == null or account.photoBackground3 == null}">
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Добавить фон<b
                                        class="caret"></b></a>
                                <ul class="dropdown-menu">
                                    <li class="text-center">Загрузить изображение</li>
                                    <li class="divider"></li>
                                    <form enctype="multipart/form-data" method="post"
                                          action="<c:url value="/changeBackground"/>">
                                            <%--<ul>--%>
                                        <li><input type="file" name="photoBackground" required></li>
                                        <li class="divider"></li>
                                            <%--<li><input type="file" name="photoBackground2"></li>--%>
                                            <%--<li class="divider"></li>--%>
                                            <%--<li><input type="file" name="photoBackground3" placeholder="Photo3"></li>--%>
                                            <%--<li class="divider"></li>--%>
                                        <li class="text-center" style="list-style-type: none ">
                                            <button class="btn btn-success" type="submit">Подтвердить</button>
                                        </li>
                                        <li class="divider"></li>
                                    </form>
                                </ul>

                            </li>
                        </c:if>
                        <c:if test="${account.photoBackground1 != null or account.photoBackground2 != null or account.photoBackground3 != null}">
                            <li class="dropdown">
                                <a href="#" class="dropdown-toggle" data-toggle="dropdown">Удалить фон<b
                                        class="caret"></b></a>
                                    <%--<c:if test="${account.photoBackground1 != null or account.photoBackground2 != null or account.photoBackground3 != null}">--%>
                                <ul class="dropdown-menu">
                                    <li class="text-center">Отметьте изображения для удаления</li>
                                    <li class="divider"></li>
                                    <form method="post" action="<c:url value="/deleteBackground"/>">
                                        <c:if test="${account.photoBackground1 != null}">
                                            <li>
                                                <label for="photoBackground1">Изображение № 1</label>
                                                <input type="checkbox" name="photoBackground1" id="photoBackground1"/>
                                            </li>
                                            <li class="divider"></li>
                                        </c:if>
                                        <c:if test="${account.photoBackground2 != null}">
                                            <li>
                                                <label for="photoBackground2">Изображение № 2</label>
                                                <input type="checkbox" name="photoBackground2" id="photoBackground2">
                                            </li>
                                            <li class="divider"></li>
                                        </c:if>
                                        <c:if test="${account.photoBackground3 != null}">
                                            <li>
                                                <label for="photoBackground3">Изображение № 3</label>
                                                <input type="checkbox" name="photoBackground3" id="photoBackground3"/>
                                            </li>
                                            <li class="divider"></li>
                                        </c:if>

                                        <li class="text-center" style="list-style-type: none ">
                                            <button class="btn btn-success" type="submit">Подтвердить</button>
                                        </li>
                                        <li class="divider"></li>
                                    </form>
                                </ul>
                                    <%--</c:if>--%>
                            </li>
                        </c:if>
                    </sec:authorize>
                    <li><a href="<c:url value="/logout"/>">Вийти</a></li>
                    <!-- <li><a href="#">Войти</a></li> -->
                </ul>
            </div>
            <!-- /.nav-collapse -->
        </div>
        <!-- /.container -->
    </nav>
    <%--<div class="carousel slide" id="carousel">--%>
    <%--<div class="carousel-inner">--%>
    <%--<div class="item active">--%>
    <%--&lt;%&ndash;id="pageHeader1"&ndash;%&gt;--%>
    <%--<img src="<c:url value="/img/heades/header1.jpg"/>">--%>
    <%--</div>--%>
    <%--</div>--%>
    <%--<!-- индикаторы слайдов -->--%>
    <%--</div>--%>

    <div class="carousel slide" id="carousel">

        <!-- индикаторы слайдов -->
        <ol class="carousel-indicators">

            <li class="active" data-target="#carousel" data-slide="0"></li>

            <c:if test="${account.photoBackground2 != null}">
                <li data-target="#carousel" data-slide="1"></li>
            </c:if>
            <c:if test="${account.photoBackground3 != null}">
                <li data-target="#carousel" data-slide="2"></li>
            </c:if>
        </ol>
        <!-- сдайды -->
        <div class="carousel-inner">

            <div class="item active">
                <c:if test="${account.photoBackground1 == null}">
                    <img src="<c:url value="/img/heades/header1.jpg"/>" alt="Пример фонового изображения">
                </c:if>
                <c:if test="${account.photoBackground1 != null}">
                    <img src="<c:url value="/givePhoto/${account.photoBackground1}"/>" alt="">
                </c:if>
            </div>
            <c:if test="${account.photoBackground2 != null}">
                <div class="item">
                    <img src="<c:url value="/givePhoto/${account.photoBackground2}"/>" alt="">
                        <%--<div class="carousel-caption">--%>
                        <%--<h3>Второй слайд</h3>--%>
                        <%--<p>Описание второго слайда</p>--%>
                        <%--</div>--%>
                </div>
            </c:if>
            <c:if test="${account.photoBackground3 != null}">
                <div class="item">
                    <img src="<c:url value="/givePhoto/${account.photoBackground3}"/>" alt="">
                </div>
            </c:if>
        </div>

        <!-- стрелки переключения слайдов -->
        <a href="#carousel" class="left carousel-control" data-slide="prev">
            <span class="glyphcon glyphcon-chevron-left"></span>
        </a>
        <a href="#carousel" class="right carousel-control" data-slide="next">
            <span class="glyphcon glyphcon-chevron-right"></span>
        </a>
    </div>

    <br>
    <c:if test="${error != null}">
        <a class="btn-primary" href="<c:url value="/home"/>">Если прайс лист не появился нажмите на кнопку</a>
    </c:if>
    <c:forEach items="${listPositions}" var="position">
        <div class="container marketing">
                <%--<hr class="featurette-divider">--%>
            <div class="row featurette" id="left-right-side">
                <div class="col-lg-1"></div>

                <div class="col-xs-12 col-sm-6 col-md-6 col-md-7 col-lg-5">
                        <%--<img class="featurette-image img-responsive center-block" width="350" height="350" src="<c:url value="/givePhoto/${position.product.photo}"/>"--%>
                        <%--alt="Изображение загружается">--%>
                    <c:if test="${position.product.photo != null}">
                        <img class="featurette-image img-responsive center-block" width="350" height="350"
                             src="<c:url value="/givePhoto/${position.product.photo}"/>"
                             alt="Изображение загружается">
                        <%----%>
                    </c:if>
                    <c:if test="${position.product.photo == null}">
                        <img class="featurette-image img-responsive center-block" width="350" height="350"
                             src="<c:url value="/img/defaultPhotoToScreen.png"/>"
                             alt="Изображение загружается">
                    </c:if>
                        <%-- src="<c:url value="/givePhoto/${position.product.photo}"/>"--%>
                        <%--src="<spring:url value="/img/${position.product.photo}"/>"--%>
                </div>


                <div class="col-xs-12 col-sm-6 col-md-6 col-md-5 col-lg-5">
                    <ul>
                            <%--Here will name of positioin from product--%>
                        <li><h3 class="featurette-heading"><c:out value="Название:  ${position.product.name}"/></h3>
                        </li>
                            <%--Here will be code of position from product--%>
                        <li><h4 class="text-muted"><c:out value="Модель:  ${position.product.codeOfModel}"/></h4></li>
                        <li><h4 class="text-muted"><c:out value="Цена:  ${position.cost}"/></h4></li>
                            <%--Here will be decription from position--%>
                        <li><p class="lead"><c:out value="Описание:   ${position.product.description}"/></p></li>
                        <c:if test="${position.bookingCondition != null}">
                            <li><p class="lead"><c:out
                                    value="Уточнения для заказов:   ${position.bookingCondition}"/></p></li>
                        </c:if>
                        <c:if test="${position.deliveryCondition != null}">
                            <li><p class="lead"><c:out
                                    value="Уточнение для доставки:   ${position.deliveryCondition}"/></p></li>
                        </c:if>
                        <c:if test="${position.id != 0}">
                            <sec:authorize access="hasRole('customer')">
                                <li><p class="lead"><c:out value="Колличество:   ${position.product.amount}"/></p></li>
                                <div class="btn-group btn-group-justified">
                                    <a class="btn btn-success" href="<c:url value="/changePosition/${position.id}"/> "
                                       role="button" style="width: 110px">Изменить &raquo;</a>
                                    <a class="btn btn-warning" href="<c:url value="/deletePosition/${position.id}"/> "
                                       role="button" style="width: 110px">Удалить &raquo;</a>
                                </div>
                            </sec:authorize>
                            <sec:authorize access="hasRole('client')">
                                <%--<form action="<c:url value="/bookingPosition"/> " method="post">--%>
                                <%--<input type="hidden" name="positionID" value="${position.id}"/>--%>
                                <%--<input type="number" min="1" required name="capacity" style="width: 50px"/>--%>
                                <%--<button type="submit" class="btn btn-success">Заказать</button>--%>
                                <%--</form>--%>

                                <p>

                                <form name="send" id="${position.id}">
                                    <input type="hidden" name="positionID" value="${position.id}"/>
                                    <input type="number" min="1" required name="capacity" style="width: 50px"/>
                                    <a class="btn btn-success" type="button"
                                       onclick="bookAjax(${position.id})">Заказать</a>


                                </form>
                                <p class="text" id="p${position.id}"></p>
                                <%--<span class="glyphicon glyphicon-shopping-cart" id="glyph${position.id}"></span>--%>
                            </sec:authorize>
                        </c:if>
                            <%--<li><form action="<c:url value="/changePosition"/>" method="post">--%>
                            <%--&lt;%&ndash;<input type="hidden" name="">&ndash;%&gt;--%>
                            <%--</form> </li>--%>
                            <%--<li><form action="<c:url value="/deletePosition"/>" method="post">--%>
                            <%--<input type="hidden" name="positionId" value="${}">--%>
                            <%--</form>  </li>--%>
                    </ul>
                </div>
                <div class="col-lg-1"></div>

                <br>

            </div>

            <hr class="featurette-divider">
            <!-- /END THE FEATURETTES -->
            <!-- FOOTER -->
            <footer>
                <p class="pull-right"><a href="#">Back to top</a></p>
                    <%--<p>&copy; 2015 Company, Inc. &middot; <a href="#">Privacy</a> &middot; <a href="#">Terms</a></p>--%>
            </footer>
        </div>
    </c:forEach>
</sec:authorize>
<sec:authorize access="!isAuthenticated()">
    <p class="text-center">
    <ul>
        <li><h2>Вы не зарегистрированны, попробуйте ввести пароль снова либо зарегистрируйтесь!</h2></li>
        <li><a class="btn btn-lg btn-success" href="<c:url value="/login"/>" role="button">Войти</a></li>
    </ul>
    </p>
</sec:authorize>
<!-- </div> --><!-- /.container -->

<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.pages.jsmy "></script>
<script>window.jQuery || document.write('<script src="/pages/assets/js/vendor/jquery.min.js"><\/script>')</script>
<script src="<c:url value="../pages/js/bootstrap.min.js"/> "></script>
<!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
<script src="<c:url value="../pages/assets/js/ie10-viewport-bug-workaround.js"/> "></script>
<script src="<c:url value="../pages/jsmy/offcanvas.js"/> "></script>

</body>
</html>
