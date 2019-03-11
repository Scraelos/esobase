<html>
<head>
    <style type="text/css">
        body {
            height: 100%;
        }

        body > table {
            width: 100%;
            height: 100%;
        }

        body > table > tbody > tr > td
        {
            text-align: center;
        }

        form > table
        {
            margin-left:auto;
            margin-right:auto;
        }

        .error
        {
            font-weight: bold;
            color: red;
        }
    </style>
</head>
<body>
<table>
    <tr>
        <td>
            <form method="post" action="../j_spring_security_check">
                <table>
                    <tr>
                        <td>Логин</td>
                        <td><input type="text" name="j_username"/></td>
                    </tr>
                    <tr>
                        <td>Пароль</td>
                        <td><input type="password" name="j_password"/></td>
                    </tr>
                    <tr>
                        <td>Запомнить</td>
                        <td><input type="checkbox" name="remember-me"/></td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                        <td><input type="submit" value="Вход"></td>
                    </tr>
                </table>
            </form>

            <#if isError?? && isError?string == "true">
                <div class="error">User not found</div>
            </#if>

        </td>
    </tr>
</table>
</body>
</html>