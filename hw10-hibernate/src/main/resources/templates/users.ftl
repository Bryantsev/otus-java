<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Users</title>
</head>
<body>
<div class="top">
    <p>Вы вошли под именем <b>${login}</b> <a href="/logout">Выйти</a></p>
</div>
<div>
    <h3>Пользователи</h3>
    <form action="/users/add" method="post">
        <#if error_msg!?length != 0 ><label style="color: red">${error_msg}</label><br/></#if>
        <label>Имя пользователя*:</label><input name="name" value="${name!""}"> <label>Возраст:</label><input name="age" value="${age!""}" size="3" maxlength="3"> <button type="submit">Добавить нового пользователя</button>
    </form><br/>

    <table border="1" cellspacing="0" cellpadding="0">
        <tr>
            <th>Ид-р</th>
            <th>Имя</th>
            <th>Возраст</th>
            <th>Удалить?</th>
        </tr>
        <#list users as user>
            <tr>
                <td>${user.id}</td>
                <td>${user.name}</td>
                <td>${user.age!""}</td>
                <td><a href="/users/delete?id=${user.id}">Удалить</a></td>
            </tr>
        </#list>
    </table>
</div>
</body>
</html>
